package com.geometry.spotsapp.model;

import static com.geometry.spotsapp.model.SpotIconManager.spotIconManager;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.osmdroid.api.IMapView;
import org.osmdroid.config.Configuration;

import java.io.File;
import java.util.List;

public class ListDataStorage implements DatastorageListAccess{


    protected File db_file;
    protected SQLiteDatabase sqLiteDatabase;
    public static final String DATABASE_FILENAME = "spots.mDatabase";
    private Datastore datastore = null;

    public ListDataStorage(){
        createDatastore();
    }

    private void createDatastore(){
        createDatabase();
        if (datastore == null) datastore = new DatastoreImpl(spotIconManager);
    }

    private void createDatabase(){
        Configuration.getInstance().getOsmdroidTileCache().mkdirs();
        db_file = new File(Configuration.getInstance().getOsmdroidTileCache().getAbsolutePath() + File.separator + DATABASE_FILENAME);
        try{
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(db_file, null);
        } catch (Throwable ex) {
            Log.e(IMapView.LOGTAG, "The Database Has Failed", ex);
        }
    }


    @Override
    public List<SpotCategory> getAllCategories() {
        return datastore.getAllCategories();
    }

    public List<String> getAllCategoryNames(){
        return datastore.getAllCategoryNames();
    }

    @Override
    public List<String> getAllSpotNamesOfCategory(String category) {
        return datastore.getAllSpotNamesOfCategory(category);
    }

    @Override
    public List<String> getSpotNames(){
        return datastore.getSpotNames();
    }

    @Override
    public void addCategory(String category, int color) {
        datastore.addCategory(category,color);
    }

    @Override
    public int getCatColor(String category) {
        return datastore.getCatColor(category);
    }

    @Override
    public void removeCategory(String category) {
        datastore.removeCategory(category);
    }
}
