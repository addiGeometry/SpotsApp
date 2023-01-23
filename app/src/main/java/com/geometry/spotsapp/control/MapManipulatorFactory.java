package com.geometry.spotsapp.control;

import com.geometry.spotsapp.model.DatastorageMapAccess;
import com.geometry.spotsapp.model.SpotIconManager;

import org.osmdroid.views.MapView;

/**
 * Factory-Klasse zur Erzeugung neuer MapManipulators und Mock-Objekte dieser
 */
public abstract class MapManipulatorFactory extends MapManipulatorImp{
    public MapManipulatorFactory(MapView map, DatastorageMapAccess storage, SpotInfoWindowFactory factory) {
        super(map, storage, factory);
    }

    public static MapManipulator newMapManipulator(MapView view, DatastorageMapAccess storage, SpotInfoWindowFactory factory){
        return new MapManipulatorImp(view, storage, factory);
    }

    public static MapManipulator newMapManipulatorMock(MapView view, DatastorageMapAccess storage, SpotIconManager spotIconManager, SpotInfoWindowFactory factory){
        return new MapManipulatorImp(view,storage, factory, spotIconManager);
    }


}
