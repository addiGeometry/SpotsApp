package com.geometry.spotsapp.control;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.Button;

import com.geometry.spotsapp.R;
import com.geometry.spotsapp.model.DatastorageAccessFactory;
import com.geometry.spotsapp.model.DatastorageListAccess;
import com.geometry.spotsapp.model.DatastorageMapAccess;
import com.geometry.spotsapp.model.SpotIconManager;
import com.geometry.spotsapp.ui.SpotInfoWindow;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapViewRepository;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.util.ArrayList;
import java.util.List;

public class ManipulatorTest {
    private MapView view;
    private DatastorageMapAccess DMAstorage;
    private DatastorageListAccess DMLstorage;
    private SpotIconManager spotIconManager;
    private MapManipulator mapManipulator;

    private CategoriesManipulator catManipulator;


    private final String title = "Title";
    private final String cat = "Kategorie";
    private final double lat = 59.99902310;
    private final double lon = 58.123403040;
    private final String desc = "Description";

    private int mockCatColor = 12;
    private int layoutid = 22;

    private void initMocks(){
        view = Mockito.mock(MapView.class);
        DMAstorage = Mockito.mock(DatastorageMapAccess.class);
        spotIconManager = Mockito.mock(SpotIconManager.class);
        MapViewRepository mapViewRepository = Mockito.mock(MapViewRepository.class);
        Context context = Mockito.mock(Context.class);

        Mockito.when(view.getRepository()).thenReturn(mapViewRepository);
        Mockito.when(view.getContext()).thenReturn(context);
        Mockito.when(context.getResources()).thenReturn(Mockito.mock(Resources.class));
        Mockito.when(mapViewRepository.getDefaultMarkerIcon()).thenReturn(Mockito.mock(Drawable.class));
        Mockito.when(mapViewRepository.getDefaultMarkerInfoWindow()).thenReturn(Mockito.mock(MarkerInfoWindow.class));
        Button button = Mockito.mock(Button.class);
        Mockito.when(view.findViewById(R.id.map_delete_spot)).thenReturn(button);
        Mockito.when(view.getOverlayManager()).thenReturn(Mockito.mock(OverlayManager.class));
        //Mockito.when(button.setBackgroundTintList(ColorStateList.valueOf(mockCatColor))).thenReturn()

        Mockito.when(DMAstorage.getCatColor(cat)).thenReturn(mockCatColor);
        SpotInfoWindowFactory spotInfoWindowFactory = Mockito.mock(SpotInfoWindowFactory.class);
        Mockito.when(spotInfoWindowFactory.newSpotInfoWindow(Mockito.anyInt(), Mockito.any(), Mockito.anyInt(), Mockito.any(), Mockito.any()))
                .thenReturn(Mockito.mock(SpotInfoWindow.class));

        mapManipulator = MapManipulatorFactory.newMapManipulatorMock(view,DMAstorage, spotIconManager, spotInfoWindowFactory);
    }

    private void initCatMock(){
        view = Mockito.mock(MapView.class);
        DatastorageAccessFactory dsfactory = Mockito.mock(DatastorageAccessFactory.class);
        DMLstorage = Mockito.mock(DatastorageListAccess.class);
        Mockito.when(dsfactory.newDatastorageListAcces(view)).thenReturn( Mockito.mock(DatastorageListAccess.class));

        //catManipulator = CategoriesManipulatorImpl.getInstance();

    }

    @Test
    public void addOneMarkerTest(){
        initMocks();
        mapManipulator.addANewMarker(title, cat, lat, lon, desc);
        Mockito.verify(DMAstorage).addSpot(Mockito.any(), Mockito.any());
        Mockito.verify(view).invalidate();
    }

    @Test
    public void addMultipleMarkerTest(){
        initMocks();
        mapManipulator.addANewMarker(title, cat, lat, lon, desc);
        mapManipulator.addANewMarker(title, cat, lat, lon, desc);
        mapManipulator.addANewMarker(title, cat, lat, lon, desc);

        Mockito.verify(DMAstorage, Mockito.times(3)).addSpot(Mockito.any(), Mockito.any());
        Mockito.verify(view, Mockito.times(3)).invalidate();
    }

    @Test
    public void addInvalidTitleException(){
        initMocks();

        Assert.assertThrows(IllegalArgumentException.class, () -> mapManipulator.addANewMarker("", cat, lat, lon, desc));
    }

    @Test
    public void addSpotNoCategory(){
        initMocks();

        Assert.assertThrows(IllegalAccessError.class, () -> mapManipulator.addANewMarker(title, null, lat, lon, desc));
    }

    @Test
    public void removeMarkerNumber12TestFromDB(){
        initMocks();
        Marker mockM = Mockito.mock(Marker.class);
        Mockito.when(mockM.getId()).thenReturn("12");
        mapManipulator.removeMarker(mockM);

        Mockito.verify(DMAstorage).removeSpot("12");
    }

    @Test
    public void removeMarkerNumber12TestFromMapView(){
        initMocks();
        Marker mockM = Mockito.mock(Marker.class);
        mapManipulator.removeMarker(mockM);

        List<String> allArrayNames = new ArrayList<>();
        allArrayNames.add("Debug");
        Mockito.when(DMAstorage.getAllCategoryNames()).thenReturn(allArrayNames);

        Mockito.verify(mockM).remove(view);
        Mockito.verify(view).invalidate();
    }

    @Test
    public void removeMultipleMarkerCompletely(){
        initMocks();
        Marker mockM1 = Mockito.mock(Marker.class);
        Marker mockM2 = Mockito.mock(Marker.class);
        Marker mockM3 = Mockito.mock(Marker.class);
        Mockito.when(mockM1.getId()).thenReturn("122");
        Mockito.when(mockM2.getId()).thenReturn("123");
        Mockito.when(mockM3.getId()).thenReturn("124");

        mapManipulator.removeMarker(mockM1);
        mapManipulator.removeMarker(mockM2);
        mapManipulator.removeMarker(mockM3);

        Mockito.verify(DMAstorage).removeSpot("123");
        Mockito.verify(DMAstorage).removeSpot("122");
        Mockito.verify(DMAstorage).removeSpot("124");
        Mockito.verify(mockM1).remove(view);
        Mockito.verify(mockM2).remove(view);
        Mockito.verify(mockM3).remove(view);
        Mockito.verify(view, Mockito.times(3)).invalidate();
    }

    @Test
    public void addOneCat() throws CategoryExiststAlreadyExcpetion {
        initCatMock();
        //catManipulator.addCategory(title, mockCatColor);
    }

}
