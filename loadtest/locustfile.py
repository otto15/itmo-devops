"""Load scenario for the Garage backend"""
import random
import string

from locust import HttpUser, task, between


def _plate():
    letters = "".join(random.choices(string.ascii_uppercase, k=3))
    return f"{letters}{random.randint(100, 999)}"


class GarageUser(HttpUser):
    wait_time = between(0.05, 0.2)

    @task(20)
    def list_cars(self):
        self.client.get("/cars", name="GET /cars")
        
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
