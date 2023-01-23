package com.geometry.spotsapp.model;

import java.util.List;

public interface DatastorageListAccess {
    List<SpotCategory> getAllCategories();

    List<String> getAllCategoryNames();

    List<String> getAllSpotNamesOfCategory(String category);

    List<String> getSpotNames();

    void addCategory(String category, int color);

    int getCatColor(String category);

    void removeCategory(String category);
}
