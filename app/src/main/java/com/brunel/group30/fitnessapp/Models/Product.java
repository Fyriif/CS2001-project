package com.brunel.group30.fitnessapp.Models;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("product_name")
    private String name;

    private Nutriments nutriments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Nutriments getNutriments() {
        return nutriments;
    }

    public void setNutriments(Nutriments nutriments) {
        this.nutriments = nutriments;
    }
}
