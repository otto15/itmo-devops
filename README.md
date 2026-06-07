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

Для работы terraform в России настроить зеркало яндекса, скопировав в ~/.terraformrc следующие строки:
provider_installation {
  network_mirror {
    url = "https://terraform-mirror.yandexcloud.net/"
    include = ["registry.terraform.io/*/*"]
  }
  direct {
    exclude = ["registry.terraform.io/*/*"]
  }
}


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
---

## Лабораторная работа №3 — Kubernetes (Minikube) на локальной VM

Приложение разворачивается в кластере **Minikube** (docker-driver), поднятом на
виртуальной машине (KVM/любой хост). Образы тянутся из Yandex Container Registry,
бэкенд масштабируется через **HPA по CPU (порог 15%)**, метрики собирает
**Prometheus**, дашборды показывает **Grafana** — всё внутри кластера.

Приложение деплоится в отдельный namespace **`garage-app`** (а не в `default`);
стек мониторинга — в namespace `monitoring`.

```
k8s/
  namespace.yaml — namespace garage-app для приложения
  database/    — StatefulSet Postgres + headless Service (db)
  backend/     — Deployment + Service + HPA (CPU 15%) + ServiceMonitor
  frontend/    — Deployment + Service (nginx + SPA)
  grafana-dashboard.json — дашборд «Garage — Pods, App & HPA»
loadtest/                — нагрузка с твоей машины (Locust в Docker, вне кластера)
ansible/
  playbook-install.yml   — Docker, kubectl, minikube, helm, рост диска на VM
  playbook-deploy.yml     — minikube + helm (Prometheus/Grafana) + манифесты + port-forward
  files/                  — systemd-юнит port-forward для доступа по LAN
```

### Требования к VM

Полный стек (minikube + kube-prometheus-stack + Postgres + JVM-бэкенд) требует
**≥ 5 GiB RAM**, 2–4 CPU и **≥ 30 GiB диска** (образы + minikube + данные Prometheus;
на 19 GiB диск переполняется). `playbook-install.yml` автоматически расширяет корневой
LV на всё свободное место VG (типичный установщик Ubuntu отдаёт LV лишь половину).

### 0. Подготовка VM (один раз)

`ansible/inventory.ini` — IP виртуалки и пользователь. Установка инструментов:

```bash
ansible-playbook -i ansible/inventory.ini ansible/playbook-install.yml --ask-become-pass
```

### 1. Сборка и публикация образов в registry

```bash
export YC_TOKEN=$(yc iam create-token)
echo "$YC_TOKEN" | docker login cr.yandex --username iam --password-stdin

docker build --platform linux/amd64 -t cr.yandex/crp73fh09ihdevr61ugo/garage-backend:latest ./garage-backend
docker build --platform linux/amd64 -f garage-frontend/Dockerfile -t cr.yandex/crp73fh09ihdevr61ugo/garage-frontend:latest .
docker push cr.yandex/crp73fh09ihdevr61ugo/garage-backend:latest
docker push cr.yandex/crp73fh09ihdevr61ugo/garage-frontend:latest
```

> В CI публикация образов вынесена в отдельные job'ы `backend-docker` / `frontend-docker`
> (см. `.github/workflows/ci.yml`) — образы пушатся в cr.yandex по тегам `latest` и `${{ github.sha }}`.

### 2. Деплой кластера и приложения

Идемпотентный плейбук: пересоздаёт minikube, ставит kube-prometheus-stack через
`helm upgrade --install`, грузит образы, применяет манифесты, поднимает port-forward.

```bash
export YC_TOKEN=$(yc iam create-token)
ansible-playbook -i ansible/inventory.ini ansible/playbook-deploy.yml \
  -e "yc_token=$YC_TOKEN" --ask-become-pass
```

`--ask-become-pass` нужен только для установки systemd-юнита `garage-portforward`
(копируется в `/etc/systemd/system`). Без него запустите без root и пропустите этот шаг:
`... --skip-tags portforward`.

### 3. Доступ к сервисам по локальной сети

systemd-сервис `garage-portforward` пробрасывает сервисы кластера на интерфейс VM
(`0.0.0.0`), автоперезапуск при сбое. С любого хоста в локальной сети:

| Сервис      | URL                              |
|-------------|----------------------------------|
| Frontend    | `http://<IP_VM>:8080`            |
| Backend API | `http://<IP_VM>:8081/cars`       |
| Grafana     | `http://<IP_VM>:3000` (admin/admin) |
| Prometheus  | `http://<IP_VM>:9090`            |

Управление: `sudo systemctl status|restart garage-portforward` на VM.

### 4. Нагрузка и автомасштабирование (HPA, порог 15% CPU)

HPA: `minReplicas: 1`, `maxReplicas: 5`, `averageUtilization: 15` (% от CPU request 200m).

Нагрузка генерируется **с твоей машины** (Locust в Docker, вне кластера) — так генератор
не отнимает CPU у самого приложения. Сценарий сохранён в `loadtest/locustfile.py`.

```bash
./loadtest/run-load.sh        # запускает Locust в Docker, цель — frontend http://<IP_VM>:8080
```

Дальше «одна кнопка»: открыть **`http://localhost:8089`** → поля уже заполнены (200 users,
20/s) → нажать **Start**. CPU бэкенда переваливает за 15%, HPA создаёт новые поды (**1 → 5**).
Нажать **Stop** (или Ctrl-C в терминале) — реплики возвращаются к 1.

> Цель — frontend (`:8080`), а не backend (`:8081`): nginx проксирует `/cars` на backend
> **Service**, и kube-proxy раскидывает запросы по всем подам → HPA масштабирует ровно.
> Это не бенчмарк: на одном узле (4 CPU) приложение + мониторинг делят ресурсы, поэтому
> абсолютные времена ответа не репрезентативны — цель демонстрации в реакции HPA.

### 5. Мониторинг (Grafana + Prometheus)

Бэкенд отдаёт метрики на `/actuator/prometheus` (Spring Boot Actuator + Micrometer,
включены histogram-бакеты для перцентилей), Prometheus скрейпит их через `ServiceMonitor`
(`k8s/backend/monitor.yaml`). Пароль Grafana: **admin / admin**.

Дашборд **«Garage — System & garage_app»** (`k8s/grafana-dashboard.json`) разделён
по идейному принципу на **системные ресурсы** и **метрики приложения**:

- **System — системные ресурсы**: CPU и память узла, CPU и память по подам
  (селектор `namespace`/`pod`: `garage-app` — приложение, `kube-system` — системные поды).
- **garage_app — JVM**: heap used / committed / max, live threads, паузы GC по подам.
- **garage_app — HTTP-эндпоинты**: **RPS по эндпоинтам и по подам**, **перцентили времени
  ответа (p90/p95/p99 + max)**, p99 по каждому эндпоинту, ошибки 5xx.
- **garage_app — HPA**: реплики и CPU-утилизация vs порог 15%.

Плюс ~27 встроенных дашбордов kube-prometheus-stack (*Kubernetes / Compute Resources*,
*Node Exporter* и др.).

> Метрики эндпоинтов и перцентили наполняются под нагрузкой — запустите Locust (шаг 4).