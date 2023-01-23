package com.geometry.spotsapp.control;

import com.geometry.spotsapp.model.SpotCategory;

/**
 * Zuständig für die Sicht des SpotFragments auf das Model. Es werden hier
 * weniger Operationen benötigt, weil das SpotFragment im Gegensatz zum MapFragment nur Kategorien erstellen kann
 * und keine Spots.
 */
public interface CatManipulator {

    /**
     * Erstelle eine neue Kategorie und gebe an das Model den Auftrag zur Persistierung auf der Datenbank
     * @param category die neue Kategorie
     */
    void addCategory(SpotCategory category);
}
