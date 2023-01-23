package com.geometry.spotsapp.control;

import com.geometry.spotsapp.model.Spot;

import org.osmdroid.views.overlay.Marker;

import java.util.List;

/**
 * Schnittstelle zu einer Kontrollklasse, die benutzt wird, um dem Model Aufträge zur
 * Persistierung von Spots oder zur Löschung dieser zu geben.
 */
public interface MapManipulator {
    /**
     * Erstelle einen neuen Marker auf der Map und teile dem Datastorage mit, es soll ihn
     * persistieren
     * @param title Name des Spots (verplfichtend)
     * @param category Kategorie des Spots (verpflichtend)
     * @param lat Breitengrad (verpflichtend)
     * @param lon Längengrad (verpflichtend)
     * @param description (optional)
     */
    void addANewMarker(String title, String category, double lat, double lon, String description);

    /**
     * Entferne einen speziellen Marker. Der Manipulator behält dabei den Überblick, welche Marker
     * welche spotId halten, um die Löschung zu ermöglichen.
     * @param spotId
     */
    void removeMarker(Marker spotId);
}
