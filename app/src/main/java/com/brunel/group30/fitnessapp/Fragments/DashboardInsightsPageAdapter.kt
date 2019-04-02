package com.brunel.group30.fitnessapp.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import com.brunel.group30.fitnessapp.DashboardActivity
import com.brunel.group30.fitnessapp.Enums.BMI
import com.brunel.group30.fitnessapp.R


class DashboardInsightsPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> WeightData.newInstance()
        1 -> BMIData.newInstance()
        else -> null
    }

    override fun getCount(): Int = 2
}

class WeightData : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.view_weight_kg_stat, container, false)
        val weightProgressIndicator: CircularProgressIndicator = view.findViewById(R.id.circular_progress_insights_weight)

        weightProgressIndicator.setProgressTextAdapter {
            PatternProgressTextAdapter(getString(R.string.msg_progress_weight_pattern)).formatText(DashboardActivity.userInfo.weight.toDouble())
        }

        weightProgressIndicator.setCurrentProgress(
                DashboardActivity.userInfo.weight.toDouble()
        )

        return view
    }

    companion object {
        fun newInstance(): WeightData = WeightData()
    }
}

class BMIData : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.view_bmi_stat, container, false)
        view.findViewById<TextView>(R.id.text_view_insights_bmi).text = String.format(
                getString(R.string.`val`),
                DashboardActivity.userInfo.calculateBMI().toString()
        )

        view.findViewById<TextView>(R.id.text_view_insights_bmi_val).text = String.format(
                getString(R.string.`val`),
                BMI.getString(DashboardActivity.userInfo.calculateBMI())
        )

        return view
    }

    companion object {
        fun newInstance(): BMIData = BMIData()
    }
}

class PatternProgressTextAdapter(private val pattern: String = "%.0f") : CircularProgressIndicator.ProgressTextAdapter {
    override fun formatText(currentProgress: Double): String {
        return String.format(pattern, currentProgress)
    }
}