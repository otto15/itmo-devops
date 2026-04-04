package com.garage.service

import com.garage.entity.Car
import com.garage.exception.CarNotFoundException
import com.garage.repository.CarRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class CarServiceTest {

    private val carRepository: CarRepository = mockk()
    private val carService = CarService(carRepository)

    private fun car(id: Long = 1L, licensePlate: String = "AB123CD") = Car(
        id = id,
        brand = "Toyota",
        model = "Corolla",
        year = 2020,
        color = "White",
        licensePlate = licensePlate,
    )

    @Test
    fun `findAll returns all cars from repository`() {
        val cars = listOf(car(1L, "AB123CD"), car(2L, "XY456ZW"))
        every { carRepository.findAll() } returns cars

        val result = carService.findAll()

        assertEquals(2, result.size)
        verify(exactly = 1) { carRepository.findAll() }
    }

    @Test
    fun `findAll returns empty list when garage is empty`() {
        every { carRepository.findAll() } returns emptyList()

        val result = carService.findAll()

        assertEquals(0, result.size)
    }

    @Test
    fun `findById returns car when it exists`() {
        val c = car()
        every { carRepository.findById(1L) } returns Optional.of(c)

        val result = carService.findById(1L)

        assertEquals(c, result)
    }

    @Test
    fun `findById throws CarNotFoundException when car does not exist`() {
        every { carRepository.findById(99L) } returns Optional.empty()

        assertThrows<CarNotFoundException> { carService.findById(99L) }
    }

    @Test
    fun `create saves and returns the car`() {
        val newCar = car(id = 0L)
        val savedCar = car(id = 1L)
        every { carRepository.save(newCar) } returns savedCar

        val result = carService.create(newCar)

        assertEquals(savedCar, result)
        verify(exactly = 1) { carRepository.save(newCar) }
    }

    @Test
    fun `update modifies fields of existing car and saves it`() {
        val existing = car(1L)
        val updatePayload = car(0L).copy(color = "Black", brand = "Honda", model = "Civic")
        val savedResult = existing.copy(color = "Black", brand = "Honda", model = "Civic")

        every { carRepository.findById(1L) } returns Optional.of(existing)
        every { carRepository.save(any()) } returns savedResult

        val result = carService.update(1L, updatePayload)

        assertEquals("Black", result.color)
        assertEquals("Honda", result.brand)
        assertEquals("Civic", result.model)
        verify(exactly = 1) { carRepository.save(any()) }
    }

    @Test
    fun `update throws CarNotFoundException when car does not exist`() {
        every { carRepository.findById(99L) } returns Optional.empty()

        assertThrows<CarNotFoundException> { carService.update(99L, car()) }
    }

    @Test
    fun `delete removes existing car`() {
        every { carRepository.existsById(1L) } returns true
        justRun { carRepository.deleteById(1L) }

        carService.delete(1L)

        verify(exactly = 1) { carRepository.deleteById(1L) }
    }

    @Test
    fun `delete throws CarNotFoundException when car does not exist`() {
        every { carRepository.existsById(99L) } returns false

        assertThrows<CarNotFoundException> { carService.delete(99L) }
    }
}
