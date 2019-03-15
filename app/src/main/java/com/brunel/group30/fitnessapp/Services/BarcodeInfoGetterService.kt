package com.brunel.group30.fitnessapp.Services

import android.os.AsyncTask
import com.brunel.group30.fitnessapp.Models.Nutriments
import com.brunel.group30.fitnessapp.Models.Product
import com.google.gson.Gson
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.json.JSONException
import org.json.JSONObject


class BarcodeInfoGetterService : AsyncTask<String, Void, Product>() {
    private val client = OkHttpClient()

    override fun doInBackground(vararg strings: String): Product? {
        val URL = "https://world.openfoodfacts.org/api/v0/product/"
        val request = Request.Builder()
                .url(URL + strings[0] + ".json")
                .get()
                .build()

        try {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val result: String = response.body().string()
                val resultsProductJson: JSONObject = JSONObject(result).get("product") as JSONObject

                val nutriments: Nutriments = Gson().fromJson(resultsProductJson.get("nutriments").toString(),
                        Nutriments::class.java)

                val product: Product = Gson().fromJson(resultsProductJson.toString(),
                        Product::class.java)

                product.nutriments = nutriments

                return product
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }
}