package com.example.hotel_reviewfrontend.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ReviewModel {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;
    String username;
    String title;
    String text;
    String hotel;
    String zipCode;

    public int getUpVote() {
        return upVote;
    }

    public void setUpVote(int upVote) {
        this.upVote = upVote;
    }

    public int getDownvote() {
        return downvote;
    }

    public void setDownvote(int downvote) {
        this.downvote = downvote;
    }

    int upVote;
    int downvote;
    float rating;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
            jsonObject.put("text", getText());
            jsonObject.put("hotel", getHotel());
            jsonObject.put("zipCode", getZipCode());
            jsonObject.put("rating", getRating());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }
}
