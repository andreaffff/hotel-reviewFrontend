package com.example.hotel_reviewfrontend.utils;

import com.example.hotel_reviewfrontend.model.ReviewModel;

public interface OnClickAction {
    public void onDelete(int id);
    public void onUpdate(int id, String hotel, String zipCode);
}
