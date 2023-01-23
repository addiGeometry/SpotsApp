package com.geometry.spotsapp.control;

import com.geometry.spotsapp.model.Spot;

import org.osmdroid.views.overlay.Marker;

import java.util.List;

public interface MapManipulator {

    void addANewMarker(String title, String category, double lat, double lon, String description);

    void removeMarker(Marker spotId);
}
