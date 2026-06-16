package com.garage.service

import com.garage.entity.Car
import com.garage.generated.model.CarRequest
import com.garage.mapper.CarMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CarMapperTest {

    private val mapper = CarMapper()

    @Test
    fun `toEntity maps CarRequest to Car`() {
        val request = CarRequest(
            brand = "Toyota",
            model = "Corolla",
            year = 2020,
            color = "White",
            licensePlate = "AB123CD",
        )

        val car = mapper.toEntity(request)

        assertEquals("Toyota", car.brand)
        assertEquals("Corolla", car.model)
        assertEquals(2020, car.year)
        assertEquals("White", car.color)
        assertEquals("AB123CD", car.licensePlate)
    }

    @Test
    fun `toResponse maps Car to CarResponse`() {
        val car = Car(
            id = 1L,
            brand = "BMW",
            model = "X5",
            year = 2023,
            color = "Black",
            licensePlate = "XY456ZW",
        )

        val response = mapper.toResponse(car)

        assertEquals(1L, response.id)
        assertEquals("BMW", response.brand)
        assertEquals("X5", response.model)
        assertEquals(2023, response.year)
        assertEquals("Black", response.color)
        assertEquals("XY456ZW_RU", response.licensePlate)
    }
}
