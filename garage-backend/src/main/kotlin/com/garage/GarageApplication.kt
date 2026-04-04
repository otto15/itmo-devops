package com.garage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GarageApplication

fun main(args: Array<String>) {
    runApplication<GarageApplication>(*args)
}
