package com.brunel.group30.fitnessapp.Models

import com.google.gson.annotations.SerializedName

class Nutriments {
    @SerializedName("proteins")
    var protein: Double = 0.toDouble()

    @SerializedName("fat")
    var fat: Double = 0.toDouble()

    @SerializedName("sodium")
    var sodium: Double = 0.toDouble()

    @SerializedName("saturated-fat")
    var saturatedFat: Double = 0.toDouble()

    @SerializedName("carbohydrates")
    var carbohydrates: Double = 0.toDouble()

    @SerializedName("energy_value")
    var calories: Double = 0.toDouble()

    @SerializedName("sugars")
    var sugar: Double = 0.toDouble()

    @SerializedName("fiber")
    var fiber: Double = 0.toDouble()
}
