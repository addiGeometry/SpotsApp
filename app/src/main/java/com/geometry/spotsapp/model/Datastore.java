package com.geometry.spotsapp.model;

import android.graphics.drawable.Drawable;

import com.geometry.spotsapp.control.MapManipulator;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

interface Datastore {

    void addSpot(Marker bookmark, String category);

    void removeSpot(String spotId);

    void removeCategory(String category);

    List<Spot> getSpots(MapView view, MapManipulator mapManipulator);

    List<String> getSpotNames();

    List<String> getAllCategoryNames();

    List<SpotCategory> getAllCategories();

    List<String> getAllSpotNamesOfCategory(String category);

    void addCategory(String category, int color);

    int getCatColor(String category);
}
