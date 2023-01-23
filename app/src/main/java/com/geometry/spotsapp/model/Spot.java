package com.geometry.spotsapp.model;

import org.osmdroid.views.overlay.Marker;

public class Spot{

    private final String spotId, category;
    private final Marker m;

    public Spot(Marker m, String spotId, String category) {
        this.spotId = spotId;
        this.category = category;
        this.m = m;
    }

    public Marker getM() {
        return m;
    }

    public String getSpotId() {
        return spotId;
    }

    public String getCategory() {
        return category;
    }
}
