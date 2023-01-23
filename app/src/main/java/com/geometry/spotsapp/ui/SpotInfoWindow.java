package com.geometry.spotsapp.ui;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.view.View;

import com.geometry.spotsapp.R;
import com.geometry.spotsapp.control.MapManipulator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

public class SpotInfoWindow extends MarkerInfoWindow {

    private final MapManipulator mapManipulator;
    private final Marker marker;

    /**
     * @param layoutResId layout that must contain these ids: bubble_title,bubble_description,
     *                    bubble_subdescription, bubble_image
     * @param mapView
     * @param mapManipulator
     * @param me
     */
    public SpotInfoWindow(int layoutResId, MapView mapView, int color, MapManipulator mapManipulator,Marker me) {
        super(layoutResId, mapView);
        this.mapManipulator = mapManipulator;
        this.marker = me;

        FloatingActionButton button = mView.findViewById(R.id.map_delete_spot);
        button.setBackgroundTintList(ColorStateList.valueOf(color));
        //button.setBackgroundColor(color);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSpotDialog();
                close();
            }
        });
    }

    private AlertDialog deleteSpotDialog = null;

    private void deleteSpotDialog() {
        if (deleteSpotDialog != null)
            deleteSpotDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());

        View view = View.inflate(mView.getContext(), R.layout.remove_spot_dialog, null);
        builder.setView(view);


        view.findViewById(R.id.rem_spot_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSpotDialog.dismiss();
            }
        });

        view.findViewById(R.id.rem_spot_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapManipulator.removeMarker(marker);
                deleteSpotDialog.dismiss();
            }
        });
        deleteSpotDialog = builder.show();
    }

}
