import random
import string

from locust import HttpUser, task, between


def _plate():
    letters = "".join(random.choices(string.ascii_uppercase, k=3))
    return f"{letters}{random.randint(100, 999)}"


class GarageUser(HttpUser):
    # small think-time between requests
    wait_time = between(0.05, 0.2)

    @task(20)
    def list_cars(self):
        self.client.get("/cars", name="GET /cars")

    # Low weight on purpose: GET /cars returns the whole table (no pagination),
    # so a write-heavy test bloats the response and chokes the kubectl
    # port-forward. Keep writes rare; truncate the table if /cars grows big.
    @task(1)
    def create_car(self):
        self.client.post(
            "/cars",
            name="POST /cars",
            json={
                "brand": random.choice(["Lada", "Toyota", "BMW", "Kia"]),
                "model": random.choice(["Vesta", "Corolla", "X5", "Rio"]),
                "year": random.randint(2000, 2024),
                "color": random.choice(["White", "Black", "Red", "Blue"]),
                "licensePlate": _plate(),
            },
        )
