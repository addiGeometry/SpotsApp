package com.geometry.spotsapp.model;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.geometry.spotsapp.control.MapManipulator;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

/**
 * Sicht für das MapFragment, um auf den Controller zu zu greifen.
 */
public interface DatastorageMapAccess{
    /**
     * Noch nicht implementiert
     * @param fileList
     * @param file
     * @param activity
     */
    void exportToCsv(String[] fileList, String file, Activity activity);

    /**
     * Noch nicht implementiert
     * @param files
     * @param activity
     */
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

