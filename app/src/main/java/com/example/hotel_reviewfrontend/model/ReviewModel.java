package com.example.hotel_reviewfrontend.model;

import android.widget.RatingBar;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ReviewModel {
    String title;
    String description;
    String hotel;
    String zipCode;
    float rating;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public JSONObject toJson() throws IOException {
        JSONObject jsonObject = new JSONObject();


        try {
            jsonObject.put("title", getTitle());
            jsonObject.put("description", getDescription());
            jsonObject.put("hotel", getHotel());
            jsonObject.put("zipCode", getZipCode());
            jsonObject.put("rating", getRating());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }
}
