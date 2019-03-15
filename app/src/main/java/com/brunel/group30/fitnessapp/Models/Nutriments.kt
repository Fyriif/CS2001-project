package com.brunel.group30.fitnessapp.Models

import com.google.gson.annotations.SerializedName

class Nutriments {
    @SerializedName("proteins")
    var protein: Float = 0.toFloat()

    @SerializedName("fat")
    var fat: Float = 0.toFloat()

    @SerializedName("sodium")
    var sodium: Float = 0.toFloat()

    @SerializedName("saturated-fat")
    var saturatedFat: Float = 0.toFloat()

    @SerializedName("carbohydrates")
    var carbohydrates: Float = 0.toFloat()

    @SerializedName("energy_value")
    var calories: Float = 0.toFloat()

    @SerializedName("sugars")
    var sugar: Float = 0.toFloat()

    @SerializedName("fiber")
    var fiber: Float = 0.toFloat()
}
