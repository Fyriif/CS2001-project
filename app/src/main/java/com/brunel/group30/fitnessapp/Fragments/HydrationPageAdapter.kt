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
import android.widget.TextView
import com.appizona.yehiahd.fastsave.FastSave
import com.brunel.group30.fitnessapp.Custom.CustomNumberPicker
import com.brunel.group30.fitnessapp.DashboardActivity
import com.brunel.group30.fitnessapp.Models.UserInfo
import com.brunel.group30.fitnessapp.R
import com.brunel.group30.fitnessapp.Services.GoogleFitApi
import com.google.firebase.auth.FirebaseAuth


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

        val v = inflater.inflate(R.layout.dialog_insert_water_target, container, false)
        val numberPicker: CustomNumberPicker = v.findViewById(R.id.number_picker_water_target)

        val incrementAmount = 250
        numberPicker.displayedValues = numberPicker.getArrayWithSteps(incrementAmount, "ml")
        numberPicker.value = (DashboardActivity.userInfo.goals.hydrationTarget / incrementAmount) - 1

        v.findViewById<Button>(R.id.button_submit_daily_water_target).setOnClickListener {
            val newVal = (incrementAmount * numberPicker.value) + incrementAmount
            DashboardActivity.userInfo.goals.hydrationTarget = (incrementAmount * numberPicker.value) + incrementAmount
            DashboardActivity.userInfo.goals.updateDB(FirebaseAuth.getInstance().currentUser!!.uid)

            FastSave.getInstance().saveObject<UserInfo>(UserInfo.COLLECTION_NAME, DashboardActivity.userInfo)
            DashboardActivity.hydrationCircularProgressIndicator.maxProgress = DashboardActivity.userInfo.goals.hydrationTarget.toDouble()

            activity!!.findViewById<TextView>(R.id.text_view_target_hydration).text = getString(
                    R.string.msg_target_with_val,
                    newVal.toString(),
                    "ml"
            )

            (parentFragment as DialogFragment).dismiss()
        }

        return v
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

        val incrementAmount = 250
        numberPicker.displayedValues = numberPicker.getArrayWithSteps(incrementAmount, "ml")
        numberPicker.value = 0

        v.findViewById<Button>(R.id.button_submit_daily_water_intake).setOnClickListener {
            GoogleFitApi.sendHydrationData(numberPicker.value + 1 * incrementAmount)
            (parentFragment as DialogFragment).dismiss()
        }

        return v
    }

    companion object {
        fun newInstance(): HydrationInsertData = HydrationInsertData()
    }
}