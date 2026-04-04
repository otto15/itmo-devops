package com.garage.mapper

import com.garage.entity.Car
import com.garage.generated.model.CarRequest
import com.garage.generated.model.CarResponse
import org.springframework.stereotype.Component

@Component
class CarMapper {

    fun toEntity(request: CarRequest): Car = Car(
        brand = request.brand,
        model = request.model,
        year = request.year,
        color = request.color,
        licensePlate = request.licensePlate,
    )

    fun toResponse(car: Car): CarResponse = CarResponse(
        id = car.id,
        brand = car.brand,
        model = car.model,
        year = car.year,
        color = car.color,
        licensePlate = car.licensePlate,
    )
}
