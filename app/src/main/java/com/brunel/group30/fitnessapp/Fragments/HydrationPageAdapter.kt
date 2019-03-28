package com.brunel.group30.fitnessapp.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brunel.group30.fitnessapp.R

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_insert_water_data, container, false)
    }

    companion object {
        fun newInstance(): HydrationInsertData = HydrationInsertData()
    }
}