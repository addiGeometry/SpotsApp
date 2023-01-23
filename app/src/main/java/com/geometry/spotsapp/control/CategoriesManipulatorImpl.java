package com.geometry.spotsapp.control;

import com.geometry.spotsapp.model.DatastorageAccessFactory;
import com.geometry.spotsapp.model.DatastorageListAccess;
import com.geometry.spotsapp.model.MapInstance;
import com.geometry.spotsapp.model.SpotCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoriesManipulatorImpl implements CategoriesManipulator{
    private DatastorageListAccess datastorage;

    private CategoriesManipulatorImpl() {
        datastorage = DatastorageAccessFactory.newDatastorageListAcces(MapInstance.getInstance());
    }

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

    public static CategoriesManipulator getInstance(){
        if(CategoriesManipulatorImpl.categoriesManipulator == null) CategoriesManipulatorImpl.categoriesManipulator = new CategoriesManipulatorImpl();
        return CategoriesManipulatorImpl.categoriesManipulator;
    }
}
