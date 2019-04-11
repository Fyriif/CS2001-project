package com.brunel.group30.fitnessapp.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brunel.group30.fitnessapp.R

class CaloriesPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> CaloriesInsertData.newInstance()
        1 -> CaloriesTargetData.newInstance()
        else -> null
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Add Data"
        1 -> "Set Target"
        else -> ""
    }

    override fun getCount(): Int = 2
}

class CaloriesTargetData : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_insert_calories_target, container, false)
    }

    companion object {
        fun newInstance(): CaloriesTargetData = CaloriesTargetData()
    }
}

class CaloriesInsertData : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_insert_calories_data, container, false)
    }

    companion object {
        fun newInstance(): CaloriesInsertData = CaloriesInsertData()
    }
}