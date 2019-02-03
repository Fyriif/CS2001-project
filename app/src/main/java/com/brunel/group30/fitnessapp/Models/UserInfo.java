package com.brunel.group30.fitnessapp.Models;

import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.List;

public class UserInfo {

    public static final String COLLECTION_NAME = "user-info";

    @PropertyName("forename")
    private String forename;

    @PropertyName("surname")
    private String surname;

    @PropertyName("dob")
    private String dob;

    @PropertyName("is_male")
    private boolean isMale;

    @PropertyName("height")
    private int height;

    @PropertyName("weight")
    private int weight;

    @PropertyName("is_disabled")
    private boolean isDisabled;

    @PropertyName("location")
    private HashMap<String, Boolean> locations;

    @PropertyName("work_out_days")
    private HashMap<String, List<String>> workOutDays;

    public UserInfo() {

    }

    public UserInfo(String forename, String surname, String dob) {
        this.forename = forename;
        this.surname = surname;
        this.dob = dob;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setIsMale(boolean isMale) {
        isMale = isMale;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public HashMap<String, Boolean> getLocations() {
        return locations;
    }

    public void setLocations(HashMap<String, Boolean> locations) {
        this.locations = locations;
    }

    public HashMap<String, List<String>> getWorkOutDays() {
        return workOutDays;
    }

    public void setWorkOutDays(HashMap<String, List<String>> workOutDays) {
        this.workOutDays = workOutDays;
    }
}