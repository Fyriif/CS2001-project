package com.brunel.group30.fitnessapp.Custom

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TimePicker

import com.brunel.group30.fitnessapp.R

import java.util.Calendar

class CustomTimeRangePicker(context: Context) : Dialog(context) {
    lateinit var setTimeRangeBtn: Button
    lateinit var startTimePicker: TimePicker
    lateinit var endTimePicker: TimePicker

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_custom_time_range_picker)

        this.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        this.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val cancelBtn = findViewById<Button>(R.id.button_cancel)
        setTimeRangeBtn = findViewById(R.id.button_set_time)

        cancelBtn.setOnClickListener { cancel() }
        setTimeRangeBtn.setOnClickListener { dismiss() }

        startTimePicker = findViewById(R.id.time_picker_start)
        endTimePicker = findViewById(R.id.time_picker_end)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        startTimePicker.visibility = View.VISIBLE
                        endTimePicker.visibility = View.GONE
                    }
                    1 -> {
                        startTimePicker.visibility = View.GONE
                        endTimePicker.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, 1)

        if (Build.VERSION.SDK_INT >= 23) {
            endTimePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
        }
    }
}