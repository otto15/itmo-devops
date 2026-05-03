# itmo-devops

Монорепозиторий проекта Garage: бэкенд, фронтенд, инфраструктура (Yandex Cloud).

```
garage-backend/    — Spring Boot REST API (Kotlin)
garage-frontend/   — React SPA (Vite + nginx)
cloud-terraform/   — Terraform (Yandex Cloud VM)
ansible/           — Ansible (установка Docker + деплой на VM)
docker-compose.yml — Production compose (backend + frontend + db)
```

---

## Порядок развёртывания

1. [Terraform](#terraform-yandex-cloud) — поднять VM в Yandex Cloud
2. [Docker](#docker--сборка-и-публикация-образов) — собрать и запушить образы в registry
3. [Ansible](#ansible-установка-docker--деплой-на-vm) — установить Docker на VM и запустить приложение

---

## Backend

### Пререквизиты

- JDK 21+
- Docker + Docker Compose (для запуска с БД)

### Запуск через Docker Compose (рекомендуется)

```bash
cd garage-backend
docker compose up --build
```

API будет доступно на `http://localhost:8080`.  
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Запуск локально

Требует PostgreSQL на `localhost:5432` (БД `garage`, пользователь `garage`, пароль `garage`).

```bash
cd garage-backend
./gradlew bootRun
```

### Тесты

```bash
./gradlew test
```

---

## Frontend

### Пререквизиты

- Node.js 18+
- Java 17+ (требуется `openapi-generator-cli` при `npm install`)

### Запуск

```bash
cd garage-frontend
npm install
npm run dev
```

Приложение будет доступно на `http://localhost:5173`.  
Бэкенд должен быть запущен на `http://localhost:8080`.

---

## Terraform (Yandex Cloud)

### Пререквизиты

1. **Установить Terraform** — [terraform.io/downloads](https://developer.hashicorp.com/terraform/downloads)

2. **Установить Yandex Cloud CLI (`yc`)** — инструкция: https://yandex.cloud/ru/docs/cli/quickstart#install

3. **Сгенерировать SSH-ключ** (если ещё нет):

```bash
ssh-keygen -t ed25519 -f ~/.ssh/id_ed25519_dev_ops -C "dev-ops"
```

4. **Добавить публичный ключ** в файл `cloud-terraform/meta.txt`:

```yaml
ssh_authorized_keys:
  - 'ssh-ed25519 AAAAC3... dev-ops'   # ← вставьте содержимое ~/.ssh/id_ed25519_dev_ops.pub
```

Посмотреть публичный ключ: `cat ~/.ssh/id_ed25519_dev_ops.pub`

### Переменные окружения

Экспортировать один раз перед работой с Terraform, Docker и Ansible:

```bash
export YC_TOKEN=$(yc iam create-token --impersonate-service-account-id ajebfl57u2ghg0l5n4so)
export YC_CLOUD_ID=$(yc config get cloud-id)
export YC_FOLDER_ID=$(yc config get folder-id)
```

### Команды

```bash
cd cloud-terraform

terraform init
terraform validate
terraform plan
terraform apply
```

После `apply` Terraform выведет IP-адреса созданной VM и автоматически создаст файл `ansible/inventory.ini`:

```
internal_ip_address_vm_1 = "192.168.10.x"
external_ip_address_vm_1 = "1.2.3.4"
```

---

## Docker — сборка и публикация образов

Registry: `cr.yandex/crp73fh09ihdevr61ugo`

> Требует установленного `YC_TOKEN` (см. раздел Terraform).

### Авторизация

```bash
echo $YC_TOKEN | docker login cr.yandex --username iam --password-stdin
```

### Сборка и push

```bash
docker build --platform linux/amd64 -t cr.yandex/crp73fh09ihdevr61ugo/garage-backend:latest ./garage-backend
docker build --platform linux/amd64 -t cr.yandex/crp73fh09ihdevr61ugo/garage-frontend:latest ./garage-frontend

docker push cr.yandex/crp73fh09ihdevr61ugo/garage-backend:latest
docker push cr.yandex/crp73fh09ihdevr61ugo/garage-frontend:latest
```

---

## Ansible (установка Docker + деплой на VM)

### Пререквизиты

- Ansible (`pip install ansible` или `brew install ansible`)
- Выполнен `terraform apply` — VM поднята, файл `ansible/inventory.ini` создан автоматически
- Образы собраны и запушены в registry (см. раздел выше)
- Установлен `YC_TOKEN` (используется для авторизации в registry на VM)

### Запуск

```bash
cd ansible
ansible-playbook playbook.yml
```

Плейбук:
1. Устанавливает Docker Engine на VM
2. Логинится в Yandex Container Registry
3. Копирует `docker-compose.yml` на VM
4. Запускает все сервисы: `docker compose up -d`

После успешного деплоя приложение доступно на `http://<external_ip>`.

### Проверка

```bash
ssh -i ~/.ssh/id_ed25519_dev_ops devops@<external_ip> docker compose ps
```
