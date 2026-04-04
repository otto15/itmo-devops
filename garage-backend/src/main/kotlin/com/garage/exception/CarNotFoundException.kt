package com.garage.exception

class CarNotFoundException(id: Long) : RuntimeException("Car with id=$id not found")
