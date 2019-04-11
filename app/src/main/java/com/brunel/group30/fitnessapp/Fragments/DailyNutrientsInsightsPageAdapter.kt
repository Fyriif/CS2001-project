package com.brunel.group30.fitnessapp.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.brunel.group30.fitnessapp.DashboardActivity
import com.brunel.group30.fitnessapp.R

class DailyNutrientsInsightsPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> FatStat.newInstance()
        1 -> SaturatedStat.newInstance()
        2 -> SodiumStat.newInstance()
        3 -> ProteinStat.newInstance()
        4 -> TotalCarbsStat.newInstance()
        5 -> SugarStat.newInstance()
        6 -> FibreStat.newInstance()
        else -> null
    }

    override fun getCount(): Int = 7
}

class FatStat : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.view_fat_stat, container, false)
        view.findViewById<TextView>(R.id.text_view_fat_daily).text = String.format(
                getString(R.string.val_with_unit),
                DashboardActivity.userInfo.dailyNutriments.fat,
                "mg"
        )

        return view
    }

    companion object {
        fun newInstance(): FatStat = FatStat()
    }
}

class SaturatedStat : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.view_saturated_fat_stat, container, false)
        view.findViewById<TextView>(R.id.text_view_sat_fat_daily).text = String.format(
                getString(R.string.val_with_unit),
                DashboardActivity.userInfo.dailyNutriments.saturatedFat,
                "mg"
        )

        return view
    }

    companion object {
        fun newInstance(): SaturatedStat = SaturatedStat()
    }
}

class SodiumStat : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.view_sodium_stat, container, false)
        view.findViewById<TextView>(R.id.text_view_sodium_daily).text = String.format(
                getString(R.string.val_with_unit),
                DashboardActivity.userInfo.dailyNutriments.sodium,
                "mg"
        )

        return view
    }

    companion object {
        fun newInstance(): SodiumStat = SodiumStat()
    }
}

class ProteinStat : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.view_protein_stat, container, false)
        view.findViewById<TextView>(R.id.text_view_protein_daily).text = String.format(
                getString(R.string.val_with_unit),
                DashboardActivity.userInfo.dailyNutriments.protein,
                "mg"
        )

        return view
    }

    companion object {
        fun newInstance(): ProteinStat = ProteinStat()
    }
}

class TotalCarbsStat : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.view_total_carbs_stat, container, false)
        view.findViewById<TextView>(R.id.text_view_carbs_daily).text = String.format(
                getString(R.string.val_with_unit),
                DashboardActivity.userInfo.dailyNutriments.carbohydrates,
                "mg"
        )

        return view
    }

    companion object {
        fun newInstance(): TotalCarbsStat = TotalCarbsStat()
    }
}

class SugarStat : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.view_sugar_stat, container, false)
        view.findViewById<TextView>(R.id.text_view_sugar_daily).text = String.format(
                getString(R.string.val_with_unit),
                DashboardActivity.userInfo.dailyNutriments.sugar,
                "mg"
        )

        return view
    }

    companion object {
        fun newInstance(): SugarStat = SugarStat()
    }
}

class FibreStat : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.view_fibre_stat, container, false)
        view.findViewById<TextView>(R.id.text_view_fibre_daily).text = String.format(
                getString(R.string.val_with_unit),
                DashboardActivity.userInfo.dailyNutriments.fiber,
                "mg"
        )

        return view
    }

    companion object {
        fun newInstance(): FibreStat = FibreStat()
    }
}