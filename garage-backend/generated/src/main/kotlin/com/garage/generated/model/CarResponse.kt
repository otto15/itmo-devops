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
 * @param id 
 * @param brand 
 * @param model 
 * @param year 
 * @param color 
 * @param licensePlate 
 */
data class CarResponse(

    @Schema(example = "1", description = "")
    @get:JsonProperty("id") val id: kotlin.Long? = null,

    @Schema(example = "Toyota", description = "")
    @get:JsonProperty("brand") val brand: kotlin.String? = null,

    @Schema(example = "Corolla", description = "")
    @get:JsonProperty("model") val model: kotlin.String? = null,

    @Schema(example = "2020", description = "")
    @get:JsonProperty("year") val year: kotlin.Int? = null,

    @Schema(example = "White", description = "")
    @get:JsonProperty("color") val color: kotlin.String? = null,

    @Schema(example = "AB123CD", description = "")
    @get:JsonProperty("licensePlate") val licensePlate: kotlin.String? = null
    ) {

}

