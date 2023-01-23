package com.geometry.spotsapp.model;

import org.osmdroid.views.MapView;

public abstract class DatastorageAccessFactory extends Datastorage {
    private DatastorageAccessFactory(){
        super(null, null);
    }

    private static Datastorage datastorage;

    public static DatastorageMapAccess newDatastorageMapAccess(MapView map, SpotIconManager spotIcon){
       datastorage = new Datastorage(map,spotIcon);
       return datastorage;
    }

    public static DatastorageListAccess newDatastorageListAcces(MapView view){
        return new ListDataStorage();
    }
}
