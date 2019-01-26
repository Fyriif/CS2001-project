package com.brunel.group30.fitnessapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.NumberPicker;

public class CustomNumberPicker extends NumberPicker {
    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttributeSet(attrs);
    }

    private void processAttributeSet(AttributeSet attrs) {
        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.CustomNumberPicker, 0, 0);

        try {
            this.setMinValue(attributes.getInt(R.styleable.CustomNumberPicker_minValue, 0));
            this.setMaxValue(attributes.getInt(R.styleable.CustomNumberPicker_maxValue, 0));
            this.setValue(attributes.getInt(R.styleable.CustomNumberPicker_defaultValue,
                    attributes.getInt(R.styleable.CustomNumberPicker_minValue, 0)));
        } finally {
            attributes.recycle();
        }
    }
}
