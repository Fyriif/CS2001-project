package com.brunel.group30.fitnessapp.Fragments

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.brunel.group30.fitnessapp.Custom.CustomNumberPicker
import com.brunel.group30.fitnessapp.R
import com.brunel.group30.fitnessapp.Services.GoogleFitApi

class HydrationPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> HydrationTargetData.newInstance()
        1 -> HydrationInsertData.newInstance()
        else -> null
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Set Target"
        1 -> "Add Data"
        else -> ""
    }

    override fun getCount(): Int = 2
}

class HydrationTargetData : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_insert_water_target, container, false)
    }

    companion object {
        fun newInstance(): HydrationTargetData = HydrationTargetData()
    }
}

class HydrationInsertData : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.dialog_insert_water_data, container, false)
        val numberPicker: CustomNumberPicker = v.findViewById(R.id.number_picker_water_daily)
        numberPicker.displayedValues = numberPicker.getArrayWithSteps(250, "ml")
        numberPicker.value = 0

        v.findViewById<Button>(R.id.button_submit_daily_water_intake).setOnClickListener(View.OnClickListener {
            GoogleFitApi.sendHydrationData(numberPicker.value + 1 * 250)
            (parentFragment as DialogFragment).dismiss()
        })


        return v
    }

    companion object {
        fun newInstance(): HydrationInsertData = HydrationInsertData()
    }
}