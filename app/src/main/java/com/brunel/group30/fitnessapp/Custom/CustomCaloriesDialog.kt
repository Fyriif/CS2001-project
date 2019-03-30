package com.brunel.group30.fitnessapp.Custom

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.brunel.group30.fitnessapp.Fragments.CaloriesPageAdapter
import com.brunel.group30.fitnessapp.R

class CustomCaloriesDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_tabs_calories, container, false)

        val caloriesTabLayout = v.findViewById<TabLayout>(R.id.tabs_calories_dialog_fragment)
        val customViewPager = v.findViewById<CustomViewPager>(R.id.view_pager_calories_dialog_fragment)
        val caloriesPageAdapter = CaloriesPageAdapter(childFragmentManager)

        customViewPager.adapter = caloriesPageAdapter
        caloriesTabLayout.setupWithViewPager(customViewPager)

        return v
    }
}
