package com.geometry.spotsapp.model;

import org.osmdroid.views.MapView;

/**
 * Factory zur erzeugung von DatastorageAccess-Objekten
 */
public class DatastorageAccessFactory{
    private static Datastorage datastorage;

    public DatastorageMapAccess newDatastorageMapAccess(MapView map, SpotIconManager spotIcon){
       datastorage = new Datastorage(map,spotIcon);
       return datastorage;
    }

    public DatastorageListAccess newDatastorageListAcces(MapView view){
        return new ListDataStorage();
    }
}
