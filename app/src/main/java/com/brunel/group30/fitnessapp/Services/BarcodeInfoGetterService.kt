package com.brunel.group30.fitnessapp.Services

import android.os.AsyncTask

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response

import org.json.JSONException
import org.json.JSONObject

import java.io.IOException

class BarcodeInfoGetterService : AsyncTask<String, Void, JSONObject>() {
    private val client = OkHttpClient()

    override fun doInBackground(vararg strings: String): JSONObject? {
        val URL = "https://world.openfoodfacts.org/api/v0/product/"
        val request = Request.Builder()
                .url(URL + strings[0] + ".json")
                .get()
                .build()

        try {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                return JSONObject(response.body().string())
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }
}
