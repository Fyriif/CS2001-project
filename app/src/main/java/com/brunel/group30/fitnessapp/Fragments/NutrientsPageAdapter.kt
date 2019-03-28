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
import com.brunel.group30.fitnessapp.R

class NutrientsPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> WeeklyNutrientsSummary.newInstance()
        1 -> DailyNutrients.newInstance()
        else -> null
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Weekly Summary"
        1 -> "Daily Nutrients"
        else -> ""
    }

    override fun getCount(): Int = 2
}

class DailyNutrients : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_nutrients_daily, container, false)
        view.findViewById<CircularProgressIndicator>(R.id.circular_progress_daily_calorie_intake).setCurrentProgress(
                DashboardActivity.userInfo.dailyNutriments.calories
        )

        return view
    }

    companion object {
        fun newInstance(): DailyNutrients = DailyNutrients()
    }
}

class WeeklyNutrientsSummary : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_nutrients_summary, container, false)
        view.findViewById<TextView>(R.id.text_view_weekly_fat).text = String.format(
                getString(R.string.val_with_unit),
                DashboardActivity.userInfo.weeklyNutriments.fat,
                "mg"
        )

        view.findViewById<TextView>(R.id.text_view_weekly_fibre).text = String.format(
                getString(R.string.val_with_unit),
                DashboardActivity.userInfo.weeklyNutriments.fiber,
                "mg"
        )

        view.findViewById<TextView>(R.id.text_view_weekly_protein).text = String.format(
                getString(R.string.val_with_unit),
                DashboardActivity.userInfo.weeklyNutriments.protein,
                "mg"
        )

        view.findViewById<TextView>(R.id.text_view_weekly_saturated_fat).text = String.format(
                getString(R.string.val_with_unit),
                DashboardActivity.userInfo.weeklyNutriments.saturatedFat,
                "mg"
        )

        view.findViewById<TextView>(R.id.text_view_weekly_sodium).text = String.format(
                getString(R.string.val_with_unit),
                DashboardActivity.userInfo.weeklyNutriments.sodium,
                "mg"
        )

        view.findViewById<TextView>(R.id.text_view_weekly_sugar).text = String.format(
                getString(R.string.val_with_unit),
                DashboardActivity.userInfo.weeklyNutriments.sugar,
                "mg"
        )

        view.findViewById<TextView>(R.id.text_view_weekly_total_carbs).text = String.format(
                getString(R.string.val_with_unit),
                DashboardActivity.userInfo.weeklyNutriments.carbohydrates,
                "mg"
        )

        return view
    }

    companion object {
        fun newInstance(): WeeklyNutrientsSummary = WeeklyNutrientsSummary()
    }
}