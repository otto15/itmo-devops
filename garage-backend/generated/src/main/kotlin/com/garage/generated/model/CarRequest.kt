package com.garage.generated.model

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import jakarta.validation.Valid
import io.swagger.v3.oas.annotations.media.Schema

/**
 * 
 * @param brand 
 * @param model 
 * @param year 
 * @param color 
 * @param licensePlate 
 */
data class CarRequest(

    @get:Size(min=1,max=100)
    @Schema(example = "Toyota", required = true, description = "")
    @get:JsonProperty("brand", required = true) val brand: kotlin.String,

    @get:Size(min=1,max=100)
    @Schema(example = "Corolla", required = true, description = "")
    @get:JsonProperty("model", required = true) val model: kotlin.String,

    @get:Min(1886)
    @get:Max(2100)
    @Schema(example = "2020", required = true, description = "")
    @get:JsonProperty("year", required = true) val year: kotlin.Int,

    @get:Size(min=1,max=50)
    @Schema(example = "White", required = true, description = "")
    @get:JsonProperty("color", required = true) val color: kotlin.String,

    @get:Pattern(regexp="^[A-Z0-9]+$")
    @get:Size(min=1,max=20)
    @Schema(example = "AB123CD", required = true, description = "")
    @get:JsonProperty("licensePlate", required = true) val licensePlate: kotlin.String
    ) {

}

