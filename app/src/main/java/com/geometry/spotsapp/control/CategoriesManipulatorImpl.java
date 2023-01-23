package com.geometry.spotsapp.control;

import com.geometry.spotsapp.model.DatastorageAccessFactory;
import com.geometry.spotsapp.model.DatastorageListAccess;
import com.geometry.spotsapp.model.MapInstance;
import com.geometry.spotsapp.model.SpotCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Control-Klasse für die Manipulation der perrsistenten Daten von Kategorien
 */
public class CategoriesManipulatorImpl implements CategoriesManipulator{
    private DatastorageListAccess datastorage;
    private DatastorageAccessFactory dsfactory;


    /*
    Mit Singleton implementiert
     */
    private CategoriesManipulatorImpl() {
        dsfactory = new DatastorageAccessFactory();
        datastorage = initDatastorage();
    }

    /**
     * Erzeuge eine neue Instance mit der Factory. Es gibt nur einen Globalen Controller.
     * @return
     */
    public DatastorageListAccess initDatastorage(){
        return dsfactory.newDatastorageListAcces(MapInstance.getInstance());
    }

    /**
     * @param name der neuen Kategorie
     * @param color
     * @throws CategoryExiststAlreadyExcpetion
     * @throws IllegalArgumentException
     */
    @Override
    public void addCategory(String name, int color) throws CategoryExiststAlreadyExcpetion, IllegalArgumentException{
        if(name.length() > 16 || name.length() == 0) throw new IllegalArgumentException();
        List<String> catNames = datastorage.getAllCategoryNames();
        for(String cat : catNames){
            if(name.equals(cat)) throw new CategoryExiststAlreadyExcpetion("Duplicate Category Name");
        }
        datastorage.addCategory(name, color);
    }

    private static CategoriesManipulator categoriesManipulator;

    /**
     * Singleton-Pattern Methode zur Erzeugung neuer MapManipulator
     * @return
     */
    public static CategoriesManipulator getInstance(){
        if(CategoriesManipulatorImpl.categoriesManipulator == null) CategoriesManipulatorImpl.categoriesManipulator = new CategoriesManipulatorImpl();
        return CategoriesManipulatorImpl.categoriesManipulator;
    }
}
