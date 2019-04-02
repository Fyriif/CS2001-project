package com.brunel.group30.fitnessapp.Custom

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brunel.group30.fitnessapp.Fragments.CaloriesPageAdapter
import com.brunel.group30.fitnessapp.Fragments.HydrationPageAdapter
import com.brunel.group30.fitnessapp.R


open class CustomDialogFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val d = super.onCreateDialog(savedInstanceState)
        d.setCanceledOnTouchOutside(true)

        return d
    }

    override fun onResume() {
        super.onResume()
        dialog.window!!.setLayout(
                (resources.displayMetrics.widthPixels * 0.75).toInt(),
                (resources.displayMetrics.heightPixels * 0.65).toInt()
        )
    }
}

class CustomHydrationDialog : CustomDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_tabs_hydration, container, false)

        val hydrationTabLayout = v.findViewById<TabLayout>(R.id.tabs_hydration_dialog_fragment)
        val customViewPager = v.findViewById<CustomViewPager>(R.id.view_pager_hydration_dialog_fragment)
        val hydrationPageAdapter = HydrationPageAdapter(childFragmentManager)

        customViewPager.adapter = hydrationPageAdapter
        hydrationTabLayout.setupWithViewPager(customViewPager)

        return v
    }
}

class CustomCaloriesDialog : CustomDialogFragment() {
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
