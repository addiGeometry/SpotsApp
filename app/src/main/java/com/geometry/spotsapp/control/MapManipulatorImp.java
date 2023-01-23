package com.geometry.spotsapp.control;

import com.geometry.spotsapp.R;
import com.geometry.spotsapp.model.DatastorageMapAccess;
import com.geometry.spotsapp.model.Spot;
import com.geometry.spotsapp.model.SpotIconManager;
import com.geometry.spotsapp.ui.SpotInfoWindow;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.UUID;

/**
 * Implementierung des MapManipulators, der die Persistierung
 */
public class MapManipulatorImp implements MapManipulator{

    private final MapView map;
    //private final DatastoreImpl storage;
    private final DatastorageMapAccess datastorage;
    private final SpotIconManager spotIconManager;
    private HashMap<String, Spot> refSet = new HashMap<>();
    private SpotInfoWindowFactory factory;


    protected MapManipulatorImp(MapView map, DatastorageMapAccess datastorage, SpotInfoWindowFactory factory){

        this.map = map;
        this.datastorage = datastorage;
        this.spotIconManager = SpotIconManager.getInstance(map.getContext());
        this.factory = factory;
    }

    protected MapManipulatorImp(MapView map, DatastorageMapAccess datastorage, SpotInfoWindowFactory factory, SpotIconManager spotIconManager){
        this.map = map;
        this.datastorage = datastorage;
        this.spotIconManager = spotIconManager;
        this.factory = factory;
    }


    @Override
    public void addANewMarker(String title, String category, double lat, double lon, String description) throws IllegalArgumentException, IllegalAccessError{
        if(title.length() > 16 || title.length() == 0) throw new IllegalArgumentException();
        if(category == null) throw new IllegalAccessError();
        //List<String> catNames = datastorage.getAllCategoryNames();
        Marker marker = new Marker(map);
        String id = UUID.randomUUID().toString();
        marker.setId(id);
        marker.setTitle(title);
        marker.setSubDescription(description);
        marker.setPosition(new GeoPoint(lat, lon));
        marker.setSnippet(marker.getPosition().toDoubleString());
        int color = datastorage.getCatColor(category);
        marker.setIcon(spotIconManager.getSpotIcon(color, category));

        marker.setInfoWindow(factory.newSpotInfoWindow(R.layout.spot_bubble, map, color, this, marker));
        //Speichern auf Model
        datastorage.addSpot(marker, category);

        map.getOverlayManager().add(marker);
        map.invalidate();
    }

    @Override
    public void removeMarker(Marker me) {
        me.remove(map);
        datastorage.removeSpot(me.getId());
        map.invalidate();
    }
}
