package com.brunel.group30.fitnessapp.Models;

import android.annotation.SuppressLint;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.List;

public class UserInfo {

    public static final String COLLECTION_NAME = "user-info";

    private String dob;
    private boolean prefersMetric;
    private boolean isMale;
    public int height;
    private int weight;
    private boolean isDisabled;
    private HashMap<String, Boolean> locations;
    private HashMap<String, List<String>> workOutDays;
    private Nutriments dailyNutriments;
    private Nutriments weeklyNutriments;

    @Exclude
    private Goals goals;

    public UserInfo() {

    }

    public UserInfo(String dob) {
        this.dob = dob;
    }

    @PropertyName("dob")
    public String getDob() {
        return dob;
    }

    @PropertyName("dob")
    public void setDob(String dob) {
        this.dob = dob;
    }

    @PropertyName("prefers_metric")
    public boolean isPrefersMetric() {
        return prefersMetric;
    }

    @PropertyName("prefers_metric")
    public void setPrefersMetric(boolean prefersMetric) {
        this.prefersMetric = prefersMetric;
    }

    @PropertyName("is_male")
    public boolean isMale() {
        return isMale;
    }

    @PropertyName("is_male")
    public void setIsMale(boolean isMale) {
        isMale = isMale;
    }

    @PropertyName("height")
    public int getHeight() {
        return height;
    }

    @PropertyName("height")
    public void setHeight(int height) {
        this.height = height;
    }

    @PropertyName("weight")
    public int getWeight() {
        return weight;
    }

    @PropertyName("weight")
    public void setWeight(int weight) {
        this.weight = weight;
    }

    @PropertyName("is_disabled")
    public boolean isDisabled() {
        return isDisabled;
    }

    @PropertyName("is_disabled")
    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    @PropertyName("location")
    public HashMap<String, Boolean> getLocations() {
        return locations;
    }

    @PropertyName("location")
    public void setLocations(HashMap<String, Boolean> locations) {
        this.locations = locations;
    }

    @PropertyName("work_out_days")
    public HashMap<String, List<String>> getWorkOutDays() {
        return workOutDays;
    }

    @PropertyName("work_out_days")
    public void setWorkOutDays(HashMap<String, List<String>> workOutDays) {
        this.workOutDays = workOutDays;
    }

    @SuppressLint("DefaultLocale")
    public double calculateBMI (){
        double height = this.getHeight();
        double weight = this.getWeight();

        double heightInMeters = height / 100;

        return Double.parseDouble(String.format("%.2f", weight / (Math.pow(heightInMeters, 2))));
    }

    @Exclude
    public Goals getGoals() {
        return goals;
    }

    @Exclude
    public void setGoals(Goals goals) {
        this.goals = goals;
    }

    public Nutriments getDailyNutriments() {
        return dailyNutriments;
    }

    public void setDailyNutriments(Nutriments dailyNutriments) {
        this.dailyNutriments = dailyNutriments;
    }

    public Nutriments getWeeklyNutriments() {
        return weeklyNutriments;
    }

    public void setWeeklyNutriments(Nutriments weeklyNutriments) {
        this.weeklyNutriments = weeklyNutriments;
    }
}