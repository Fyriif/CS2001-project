package com.brunel.group30.fitnessapp.Custom

import android.content.Context
import android.util.AttributeSet
import android.widget.NumberPicker
import com.brunel.group30.fitnessapp.R

class CustomNumberPicker(context: Context, attrs: AttributeSet) : NumberPicker(context, attrs) {
    init {
        processAttributeSet(attrs)
    }

    private fun processAttributeSet(attrs: AttributeSet) {
        val attributes = context.theme.obtainStyledAttributes(attrs,
                R.styleable.CustomNumberPicker, 0, 0)

        try {
            this.minValue = attributes.getInt(R.styleable.CustomNumberPicker_minValue, 0)
            this.maxValue = attributes.getInt(R.styleable.CustomNumberPicker_maxValue, 0)
            this.value = attributes.getInt(R.styleable.CustomNumberPicker_defaultValue,
                    attributes.getInt(R.styleable.CustomNumberPicker_minValue, 0))
        } finally {
            attributes.recycle()
        }
    }
}
