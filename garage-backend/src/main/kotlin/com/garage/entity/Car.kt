package com.garage.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "cars")
data class Car(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var brand: String,

    @Column(nullable = false)
    var model: String,

    @Column(nullable = false)
    var year: Int,

    @Column(nullable = false)
    var color: String,

    @Column(name = "license_plate", nullable = false, unique = true)
    var licensePlate: String,
)
