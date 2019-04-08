package com.brunel.group30.fitnessapp.Custom

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import com.appizona.yehiahd.fastsave.FastSave
import com.brunel.group30.fitnessapp.DashboardActivity.stepCountCircularProgressIndicator
import com.brunel.group30.fitnessapp.DashboardActivity.userInfo
import com.brunel.group30.fitnessapp.Fragments.CaloriesPageAdapter
import com.brunel.group30.fitnessapp.Fragments.HydrationPageAdapter
import com.brunel.group30.fitnessapp.Models.UserInfo
import com.brunel.group30.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth


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

/**
 * Responsible for inflating the step count target dialog
 * and in charge of the behaviour of the elements within it.
 */
class CustomStepCountTargetDialog : CustomDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_insert_step_target, container, false)
        val numberPicker = v.findViewById<CustomNumberPicker>(R.id.number_picker_step_target)

        v.findViewById<View>(R.id.button_confirm).setOnClickListener {
            userInfo.goals.stepsTarget = (numberPicker.value + 1) * 1000
            userInfo.goals.updateDB(FirebaseAuth.getInstance().currentUser!!.uid)

            // Re-save the object in device's SharedPreferences
            FastSave.getInstance().saveObject<UserInfo>(UserInfo.COLLECTION_NAME, userInfo)
            stepCountCircularProgressIndicator.setProgress(
                    stepCountCircularProgressIndicator.progress,
                    userInfo.goals.stepsTarget.toDouble())

            (parentFragment as DialogFragment).dismiss()
        }

        val incrementAmount = 1000
        numberPicker.displayedValues = numberPicker.getArrayWithSteps(incrementAmount, "")
        numberPicker.value = stepCountCircularProgressIndicator.maxProgress.toInt() / incrementAmount - 1

        return v
    }
}

/**
 * Responsible for inflating the hydration intake/target dialog
 * and in charge of the behaviour of the elements within it.
 */
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

/**
 * Responsible for inflating the calorie target dialog
 * and in charge of the behaviour of the elements within it.
 */
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

/**
 * Responsible for inflating the weight target dialog
 * and in charge of the behaviour of the elements within it.
 */
class CustomWeightTargetDialog : CustomDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_insert_weight_target, container, false)
        val numberPicker = v.findViewById<CustomNumberPicker>(R.id.number_picker_weight_target)

        val minWeight = userInfo.weight - 50
        val maxWeight = userInfo.weight + 50
        val targetWeightIndex = userInfo.goals.weightTarget - minWeight

        numberPicker.maxValue = maxWeight
        numberPicker.minValue = minWeight

        numberPicker.displayedValues = numberPicker.getArrayWithSteps(1, "kg")
        numberPicker.value = targetWeightIndex

        v.findViewById<View>(R.id.button_confirm).setOnClickListener {
            userInfo.goals.weightTarget = userInfo.goals.weightTarget + (numberPicker.value - targetWeightIndex)
            userInfo.goals.updateDB(FirebaseAuth.getInstance().currentUser!!.uid)

            // Re-save the object in device's SharedPreferences
            FastSave.getInstance().saveObject(UserInfo.COLLECTION_NAME, userInfo)

            val weightCircularProgressIndicator = activity!!.findViewById<CircularProgressIndicator>(R.id.circular_progress_insights_weight)

            if (userInfo.goals.weightTarget > userInfo.weight) {
                weightCircularProgressIndicator.setProgress(
                        userInfo.weight.toDouble(),
                        userInfo.goals.weightTarget.toDouble()
                )
            } else {
                weightCircularProgressIndicator.setProgress(
                        userInfo.goals.weightTarget.toDouble(),
                        userInfo.weight.toDouble()
                )
            }

            this.dismiss()
        }

        return v
    }
}