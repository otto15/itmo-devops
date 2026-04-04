package com.garage.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.garage.generated.model.CarRequest
import com.garage.repository.CarRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class CarControllerIntegrationTest {

    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:16").apply {
            withDatabaseName("garage_test")
            withUsername("test")
            withPassword("test")
            waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureDataSource(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @Autowired lateinit var carRepository: CarRepository

    @BeforeEach
    fun cleanUp() {
        carRepository.deleteAll()
    }

    private fun carRequest(
        brand: String = "Toyota",
        model: String = "Corolla",
        year: Int = 2020,
        color: String = "White",
        licensePlate: String = "AB123CD",
    ) = CarRequest(brand = brand, model = model, year = year, color = color, licensePlate = licensePlate)

    private fun postCar(licensePlate: String = "AB123CD"): Long {
        val result = mockMvc.post("/cars") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(carRequest(licensePlate = licensePlate))
        }.andReturn()
        return objectMapper.readTree(result.response.contentAsString)["id"].asLong()
    }

    @Test
    fun `POST cars - creates car and returns 201 with body`() {
        mockMvc.post("/cars") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(carRequest())
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { exists() }
            jsonPath("$.brand") { value("Toyota") }
            jsonPath("$.model") { value("Corolla") }
            jsonPath("$.year") { value(2020) }
            jsonPath("$.color") { value("White") }
            jsonPath("$.licensePlate") { value("AB123CD") }
        }
    }

    @Test
    fun `GET cars - returns list of all cars`() {
        postCar("AA001BB")
        postCar("AA002BB")

        mockMvc.get("/cars").andExpect {
            status { isOk() }
            jsonPath("$") { isArray() }
            jsonPath("$.length()") { value(2) }
        }
    }

    @Test
    fun `GET cars - returns empty list when garage is empty`() {
        mockMvc.get("/cars").andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(0) }
        }
    }

    @Test
    fun `GET cars by id - returns car when exists`() {
        val id = postCar("ID001")

        mockMvc.get("/cars/$id").andExpect {
            status { isOk() }
            jsonPath("$.id") { value(id) }
            jsonPath("$.licensePlate") { value("ID001") }
        }
    }

    @Test
    fun `GET cars by id - returns 404 when not found`() {
        mockMvc.get("/cars/99999").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `PUT cars by id - updates car and returns updated data`() {
        val id = postCar("UPD001")
        val updated = carRequest(licensePlate = "UPD001", color = "Black", brand = "Honda", model = "Civic")

        mockMvc.put("/cars/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updated)
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(id) }
            jsonPath("$.color") { value("Black") }
            jsonPath("$.brand") { value("Honda") }
            jsonPath("$.model") { value("Civic") }
        }
    }

    @Test
    fun `PUT cars by id - returns 404 when car does not exist`() {
        mockMvc.put("/cars/99999") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(carRequest())
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `DELETE cars by id - removes car and returns 204`() {
        val id = postCar("DEL001")

        mockMvc.delete("/cars/$id").andExpect {
            status { isNoContent() }
        }

        mockMvc.get("/cars/$id").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `DELETE cars by id - returns 404 when car does not exist`() {
        mockMvc.delete("/cars/99999").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `POST cars - returns 400 when brand is blank`() {
        mockMvc.post("/cars") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(carRequest(brand = ""))
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.errors.brand") { exists() }
        }
    }

    @Test
    fun `POST cars - returns 400 when year is below minimum`() {
        mockMvc.post("/cars") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(carRequest(year = 1800))
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.errors.year") { exists() }
        }
    }

    @Test
    fun `POST cars - returns 400 when license plate has invalid characters`() {
        mockMvc.post("/cars") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(carRequest(licensePlate = "ab-123"))
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.errors.licensePlate") { exists() }
        }
    }

    @Test
    fun `PUT cars by id - returns 400 when model exceeds max length`() {
        val id = postCar("VAL001")
        mockMvc.put("/cars/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(carRequest(licensePlate = "VAL001", model = "A".repeat(101)))
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.errors.model") { exists() }
        }
    }
}
