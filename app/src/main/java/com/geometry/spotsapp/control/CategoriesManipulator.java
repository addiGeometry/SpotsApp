package com.geometry.spotsapp.control;

/**
 * Zuständig für die Sicht des SpotFragments auf das Model. Es werden hier
 * weniger Operationen benötigt, weil das SpotFragment im Gegensatz zum MapFragment nur Kategorien erstellen kann
 * und keine Spots.
 */

public interface CategoriesManipulator {
    /**
     * Erstelle eine neue Kategorie und gebe an das Model den Auftrag zur Persistierung auf der Datenbank
     * @param name die neue Kategorie
     * @color die Farbe der Kategorie
     */
    public void addCategory(String name, int color) throws CategoryExiststAlreadyExcpetion;
}

