package com.garage.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.garage.entity.Car
import com.garage.exception.CarNotFoundException
import com.garage.generated.model.CarRequest
import com.garage.generated.model.CarResponse
import com.garage.mapper.CarMapper
import com.garage.service.CarService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@WebMvcTest(CarController::class)
class CarControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @MockkBean lateinit var carService: CarService
    @MockkBean lateinit var carMapper: CarMapper

    private fun car(id: Long = 1L) = Car(
        id = id, brand = "Toyota", model = "Corolla",
        year = 2020, color = "White", licensePlate = "AB123CD"
    )

    private fun carResponse(id: Long = 1L) = CarResponse(
        id = id, brand = "Toyota", model = "Corolla",
        year = 2020, color = "White", licensePlate = "AB123CD"
    )

    private fun carRequest() = CarRequest(
        brand = "Toyota", model = "Corolla",
        year = 2020, color = "White", licensePlate = "AB123CD"
    )

    @Test
    fun `GET cars - returns list of cars`() {
        every { carService.findAll() } returns listOf(car())
        every { carMapper.toResponse(any()) } returns carResponse()

        mockMvc.get("/cars").andExpect {
            status { isOk() }
            jsonPath("$[0].brand") { value("Toyota") }
        }
    }

    @Test
    fun `POST cars - creates car and returns 201`() {
        every { carMapper.toEntity(any()) } returns car(0L)
        every { carService.create(any()) } returns car()
        every { carMapper.toResponse(any()) } returns carResponse()

        mockMvc.post("/cars") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(carRequest())
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(1) }
        }
    }

    @Test
    fun `GET cars by id - returns car`() {
        every { carService.findById(1L) } returns car()
        every { carMapper.toResponse(any()) } returns carResponse()

        mockMvc.get("/cars/1").andExpect {
            status { isOk() }
            jsonPath("$.brand") { value("Toyota") }
        }
    }

    @Test
    fun `GET cars by id - returns 404 when not found`() {
        every { carService.findById(99L) } throws CarNotFoundException(99L)

        mockMvc.get("/cars/99").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `PUT cars by id - updates car`() {
        every { carMapper.toEntity(any()) } returns car()
        every { carService.update(1L, any()) } returns car()
        every { carMapper.toResponse(any()) } returns carResponse()

        mockMvc.put("/cars/1") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(carRequest())
        }.andExpect {
            status { isOk() }
            jsonPath("$.brand") { value("Toyota") }
        }
    }

    @Test
    fun `PUT cars by id - returns 404 when not found`() {
        every { carMapper.toEntity(any()) } returns car()
        every { carService.update(99L, any()) } throws CarNotFoundException(99L)

        mockMvc.put("/cars/99") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(carRequest())
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `DELETE cars by id - returns 204`() {
        justRun { carService.delete(1L) }

        mockMvc.delete("/cars/1").andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `DELETE cars by id - returns 404 when not found`() {
        every { carService.delete(99L) } throws CarNotFoundException(99L)

        mockMvc.delete("/cars/99").andExpect {
            status { isNotFound() }
        }
    }
}
