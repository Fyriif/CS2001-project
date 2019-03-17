package com.brunel.group30.fitnessapp.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brunel.group30.fitnessapp.R

class NutrientsSummary : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nutrients_summary, container, false)
    }

    companion object {
        fun newInstance(): NutrientsSummary = NutrientsSummary()
    }
}