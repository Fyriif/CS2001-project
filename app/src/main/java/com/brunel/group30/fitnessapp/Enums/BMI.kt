package com.brunel.group30.fitnessapp.Enums

enum class BMI {
    UNDERWEIGHT, NORMAL, OVERWEIGHT;

    companion object {
        fun getString(bmiNum: Double): BMI? {
            if (bmiNum < 18.5) {
                return UNDERWEIGHT
            } else if (bmiNum > 18.5 && bmiNum < 24.9) {
                return NORMAL
            } else if (bmiNum > 25.0 && bmiNum < 29.0) {
                return OVERWEIGHT
            }

            return null
        }
    }
}
