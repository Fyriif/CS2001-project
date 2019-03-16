package com.brunel.group30.fitnessapp.Fragments

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class NutrientsPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> NutrientsSummary.newInstance()
        1 -> NutrientsData.newInstance()
        else -> null
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Summary"
        1 -> "Nutrients Data"
        else -> ""
    }

    override fun getCount(): Int = 2
}