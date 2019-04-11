package com.brunel.group30.fitnessapp.Enums

enum class BMI {
    UNDERWEIGHT, NORMAL, OVERWEIGHT;

    companion object {
        fun getString(bmiVal: Double): BMI? {
            if (bmiVal < 18.5) {
                return UNDERWEIGHT
            } else if (bmiVal > 18.5 && bmiVal < 24.9) {
                return NORMAL
            } else if (bmiVal > 25.0) {
                return OVERWEIGHT
            }

            return null
        }
    }
}
