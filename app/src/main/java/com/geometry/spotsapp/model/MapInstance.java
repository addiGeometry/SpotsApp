package com.geometry.spotsapp.model;

import org.osmdroid.views.MapView;

public abstract class MapInstance {

    private static MapView map;

    public static void mapSaver(MapView map){
        MapInstance.map = map;
    }

    public static MapView getInstance(){
        return MapInstance.map;
    }

}
