package com.geometry.spotsapp.model;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.geometry.spotsapp.control.MapManipulator;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public interface DatastorageMapAccess{

    void exportToCsv(String[] fileList, String file, Activity activity);

    void importFromCsv(String[] files, Activity activity);

    void destroyDatastore();

    Drawable getSpotIcon(String category);

    Drawable getSpotImage();

    void addSpot(Marker bookmark, String category);

    void removeSpot(String spotId);

    List<Marker> getBookmarksAsMarkers(MapView view, MapManipulator mapManipulator);

    void close();

    List<String> getAllCategoryNames();

    int getCatColor(String category);
}

