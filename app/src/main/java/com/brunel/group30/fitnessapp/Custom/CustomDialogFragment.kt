package com.brunel.group30.fitnessapp.Custom

import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.brunel.group30.fitnessapp.R

open class CustomDialogFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }
}