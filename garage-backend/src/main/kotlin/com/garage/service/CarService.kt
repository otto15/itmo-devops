package com.garage.service

import com.garage.entity.Car
import com.garage.exception.CarNotFoundException
import com.garage.repository.CarRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CarService(private val carRepository: CarRepository) {

    @Transactional(readOnly = true)
    fun findAll(): List<Car> = carRepository.findAll()

    @Transactional(readOnly = true)
    fun findById(id: Long): Car =
        carRepository.findById(id).orElseThrow { CarNotFoundException(id) }

    fun create(car: Car): Car = carRepository.save(car)

    fun update(id: Long, updated: Car): Car {
        val existing = findById(id)
        existing.brand = updated.brand
        existing.model = updated.model
        existing.year = updated.year
        existing.color = updated.color
        existing.licensePlate = updated.licensePlate
        return carRepository.save(existing)
    }

    fun delete(id: Long) {
        if (!carRepository.existsById(id)) throw CarNotFoundException(id)
        carRepository.deleteById(id)
    }
}
