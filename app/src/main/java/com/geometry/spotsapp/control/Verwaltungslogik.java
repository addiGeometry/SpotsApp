package com.geometry.spotsapp.control;

public interface Verwaltungslogik{
    /**
     * Füge eine Kategorie hinzu
     * @param name
     * @param color
     * @return die ID der erzeugten Kategorie
     */
    int addCategory(String name, int color);
    /**
     * Ändere die Kategorie mit der ID:
     * @param catID
     * @param name
     * @param farbe
     * @return true, wenn die Änderung erfolgreich war
     */
    boolean changeCategory(int catID, String name, String farbe);

    /**
     * Füge einen Spot an den Koordinaten hinzu. D.h. persistiere die Daten und erstelle einen Marker auf der MapView
     * @param Latitude
     * @param Longitude
     * @param name
     * @param beschreibung
     * @param preisklasse
     * @param catID
     * @param imageBlob
     * @return die ID des erzeguten Spots
     */
    int addSpot(Double Latitude, Double Longitude, String name, String beschreibung, int preisklasse, int catID, String imageBlob);

    /**
     * Ändere den Spot mir der ID:
     * @param spotID
     * @param Longitude
     * @param name
     * @param beschreibung
     * @param preisklasse
     * @param catID
     * @param imageBlobt
     * @return true, wenn erfolgreich
     */
    boolean changeSpot(int spotID, Double Longitude, String name, String beschreibung, int preisklasse, int catID, String imageBlobt);
}
