package com.geometry.spotsapp.control;

import com.geometry.spotsapp.ui.SpotInfoWindow;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;


public class SpotInfoWindowFactory{

    public SpotInfoWindow newSpotInfoWindow(int layoutResId, MapView mapView, int color, MapManipulator mapManipulator, Marker me){
        return new SpotInfoWindow(layoutResId, mapView, color, mapManipulator, me);
    }
}
