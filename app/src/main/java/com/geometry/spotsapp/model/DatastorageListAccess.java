package com.geometry.spotsapp.model;

import java.util.List;

/**
 * Schnittstelle für das Spot-Fragment
 */
public interface DatastorageListAccess {

    /**
     * @return alle Kategorienamen als Liste
     */
    List<String> getAllCategoryNames();

    /**
     * @param category gebe nur die Namen der Spots einer bestimmten Kategorie zurück
     * @return
     */
    List<String> getAllSpotNamesOfCategory(String category);

    /**
     * Gebe den Auftrag zum persistieren einer neuen Kategorie an das Datastore
     * @param category Name der Kategorie
     * @param color Farbe der Kategorie
     */
    void addCategory(String category, int color);

    /**
     * Gebe die Farbe einer Kategorie zurück
     * @param category
     * @return
     */
    int getCatColor(String category);

    /**
     * Entferne die Kategorie mit dem gegebenen Namen
     * @param category Name der Kategorie
     */
    void removeCategory(String category);
}
