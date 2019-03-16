package com.brunel.group30.fitnessapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.brunel.group30.fitnessapp.BarcodeScannerActivity
import com.brunel.group30.fitnessapp.R

class NutrientsData : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nutrients_data, container, false)

        /*val barcodeScannerBtn = view.findViewById<LinearLayout>(R.id.button_barcode_scanner)
        barcodeScannerBtn.setOnClickListener { startActivity(Intent(activity, BarcodeScannerActivity::class.java)) }*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val barcodeScannerBtn = view.findViewById<LinearLayout>(R.id.button_barcode_scanner)
        barcodeScannerBtn.setOnClickListener { startActivity(Intent(activity, BarcodeScannerActivity::class.java)) }
    }

    companion object {
        fun newInstance(): NutrientsData = NutrientsData()
    }
}