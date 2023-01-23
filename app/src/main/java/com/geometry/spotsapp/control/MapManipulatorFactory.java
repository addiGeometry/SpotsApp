package com.geometry.spotsapp.control;

import com.geometry.spotsapp.model.DatastorageMapAccess;

import org.osmdroid.views.MapView;

public abstract class MapManipulatorFactory extends MapManipulatorImp{
    public MapManipulatorFactory(MapView map, DatastorageMapAccess storage) {
        super(map, storage);
    }

    public static MapManipulator newMapManipulator(MapView view, DatastorageMapAccess storage){
        return new MapManipulatorImp(view, storage);
    }
}
