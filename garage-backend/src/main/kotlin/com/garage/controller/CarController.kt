package com.garage.controller

import com.garage.generated.api.CarsApi
import com.garage.generated.model.CarRequest
import com.garage.generated.model.CarResponse
import com.garage.mapper.CarMapper
import com.garage.service.CarService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CarController(
    private val carService: CarService,
    private val carMapper: CarMapper,
) : CarsApi {

    override fun listCars(): ResponseEntity<List<CarResponse>> =
        ResponseEntity.ok(carService.findAll().map(carMapper::toResponse))

    override fun createCar(carRequest: CarRequest): ResponseEntity<CarResponse> {
        val created = carService.create(carMapper.toEntity(carRequest))

        return ResponseEntity.status(HttpStatus.CREATED).body(carMapper.toResponse(created))
    }

    override fun getCar(id: Long): ResponseEntity<CarResponse> =
        ResponseEntity.ok(carMapper.toResponse(carService.findById(id)))

    override fun updateCar(id: Long, carRequest: CarRequest): ResponseEntity<CarResponse> {
        val updated = carService.update(id, carMapper.toEntity(carRequest))

        return ResponseEntity.ok(carMapper.toResponse(updated))
    }

    override fun deleteCar(id: Long): ResponseEntity<Unit> {
        carService.delete(id)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
