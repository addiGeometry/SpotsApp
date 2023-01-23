package com.geometry.spotsapp.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.Log;

import com.geometry.spotsapp.R;
import com.geometry.spotsapp.control.MapManipulator;
import com.geometry.spotsapp.ui.SpotInfoWindow;

import org.osmdroid.api.IMapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * created on 2/11/2018.
 *
 * @author Alex O'Ree
 */

class DatastoreImpl implements Datastore {
    /**
     * Spot-Table and Cat-Table Columns
     */
    public static final String SPOT_TABLE = "bookmarks";
    public static final String COLUMN_SPOT_ID = "markerid";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LON = "lon";
    public static final String COLUMN_SPOT_TITLE = "title";
    public static final String COLUMN_DESC = "description";

    public static final String COLUMN_CAT = "category";
    public static final String CAT_TABLE = "categories";
    public static final String COLUMN_CAT_ID = "catid";
    public static final String COLUMN_COLOR = "color";

    private HashMap<String, Integer> colormap;

    //public static final String DATABASE_FILENAME = "bookmarks.mDatabase";

    private final SpotIconManager spotIconManager;
    private MapManipulator mapManipulator;

    //for database creation
    protected File db_file;
    protected SQLiteDatabase sqLiteDatabase;
    public static final String DATABASE_FILENAME = "spots.mDatabase";


    public DatastoreImpl(SpotIconManager spotIcons) {
        createDatabase();
        colormap = new HashMap<>();
        spotIconManager = spotIcons;
    }

    private void createDatabase(){
        Configuration.getInstance().getOsmdroidTileCache().mkdirs();
        db_file = new File(Configuration.getInstance().getOsmdroidTileCache().getAbsolutePath() + File.separator + DATABASE_FILENAME);
        try{
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(db_file, null);
            this.createSpotTable();
            this.createCatTable();
        } catch (Throwable ex) {
            Log.e(IMapView.LOGTAG, "The Database Has Failed", ex);
        }
    }

    private void createCatTable() throws Exception{
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + CAT_TABLE + " (" +
                COLUMN_CAT + " TEXT, " +
                COLUMN_CAT_ID + " TEXT, " +
                COLUMN_COLOR + " INTEGER, PRIMARY KEY (" + COLUMN_CAT_ID + ") );");
    }

    private void createSpotTable() throws Exception{
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + SPOT_TABLE + " (" +
                COLUMN_LAT + " INTEGER , " +
                COLUMN_LON + " INTEGER, " +
                COLUMN_SPOT_TITLE + " TEXT, " +
                        COLUMN_SPOT_ID + " TEXT, " +
                        COLUMN_CAT + " TEXT, " +
                        COLUMN_DESC + " TEXT, PRIMARY KEY (" + COLUMN_SPOT_ID + ") );");
            }

            //TODO geopgrahpic bounding box?
            @Override
            @SuppressLint("Range")
            public List<Spot> getSpots(MapView view, MapManipulator mapManipulator) {
                List<Spot> spots = new ArrayList<>();
                try {
                    //TODO order by title
                    final Cursor cur = sqLiteDatabase.rawQuery("SELECT * FROM " + SPOT_TABLE, null);
                    while (cur.moveToNext()) {
                        Marker m = new Marker(view);
                        String id = cur.getString(cur.getColumnIndex(COLUMN_SPOT_ID));
                        m.setId(id);
                        m.setTitle(cur.getString(cur.getColumnIndex(COLUMN_SPOT_TITLE)));
                        m.setSubDescription(cur.getString(cur.getColumnIndex(COLUMN_DESC)));
                m.setPosition(new GeoPoint(cur.getDouble(cur.getColumnIndex(COLUMN_LAT)), cur.getDouble(cur.getColumnIndex(COLUMN_LON))));
                m.setSnippet(m.getPosition().toDoubleString());
                String category = cur.getString(cur.getColumnIndex(COLUMN_CAT));
                Integer color = colormap.get(category);
                if(color == null) {
                    color = this.getCatColor(category);
                    colormap.put(category, color);
                }
                m.setIcon(spotIconManager.getSpotIcon(color, category));
                m.setInfoWindow(new SpotInfoWindow(R.layout.spot_bubble, view, color, mapManipulator, m));
                Spot spot = new Spot(m, id, category);
                spots.add(spot);
            }
            cur.close();
        } catch (final Exception e) {
            Log.w(IMapView.LOGTAG, "Error getting tile sources: ", e);
        }
        return spots;
    }

    @Override
    @SuppressLint("Range")
    public List<String> getSpotNames() {
        List<String> names = new ArrayList<>();
        try {
            final Cursor cur = sqLiteDatabase.rawQuery("SELECT * FROM " + SPOT_TABLE, null);
            while (cur.moveToNext()) {
                names.add(cur.getString(cur.getColumnIndex(COLUMN_SPOT_TITLE)));
            }
        } catch (final Exception e) {
            Log.w(IMapView.LOGTAG, "Error getting database sources: ", e);
        }
        return names;
    }

    private void removeBookmark(String id) {
        sqLiteDatabase.delete(SPOT_TABLE, COLUMN_SPOT_ID, new String[]{COLUMN_SPOT_ID});
    }

    @Override
    public void addSpot(Marker bookmark, String category) {
        addSpot(bookmark.getId(), category, bookmark.getPosition().getLatitude(), bookmark.getPosition().getLongitude(), bookmark.getTitle(), bookmark.getSubDescription());
    }

    @Override
    public void removeSpot(String spotId) {
        sqLiteDatabase.delete(SPOT_TABLE, COLUMN_SPOT_ID + "=" + '"' + spotId + '"', null);
        //sqLiteDatabase.rawQuery("DELETE FROM " + SPOT_TABLE + " WHERE (" +  + "=" +  + spotId + '"' + ")", null);
    }

    @Override
    public void removeCategory(String category) {
        sqLiteDatabase.delete(CAT_TABLE, COLUMN_CAT + "=" + '"' + category + '"', null);
    }

    private void addSpot(String id, String category, double lat, double lon, String title, String description) {

        ContentValues cv = new ContentValues();
        if (id == null || id.length() == 0)
            cv.put(COLUMN_SPOT_ID, UUID.randomUUID().toString());
        else {
            sqLiteDatabase.delete(SPOT_TABLE, COLUMN_SPOT_ID + "=?", new String[]{id});
            cv.put(COLUMN_SPOT_ID, id);
        }
        cv.put(COLUMN_LAT, lat);
        cv.put(COLUMN_LON, lon);
        cv.put(COLUMN_DESC, description);
        cv.put(COLUMN_SPOT_TITLE, title);
        cv.put(COLUMN_CAT, category);

        sqLiteDatabase.insert(SPOT_TABLE, null, cv);
    }

    public void close() {
        sqLiteDatabase.close();
        sqLiteDatabase = null;
    }


    public void addCategory(String name, int color){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CAT_ID, UUID.randomUUID().toString());
        cv.put(COLUMN_CAT, name);
        cv.put(COLUMN_COLOR, color);

        sqLiteDatabase.insert(CAT_TABLE, null, cv);
    }

    @Override
    @SuppressLint("Range")
    public int getCatColor(String category) {
        final Cursor cur = sqLiteDatabase.rawQuery("SELECT * FROM " + CAT_TABLE + " WHERE (" + COLUMN_CAT +" = " + '"' + category + '"' + ") ", null);
        int color=0;
        while (cur.moveToNext()) {
            color = cur.getInt(cur.getColumnIndex(COLUMN_COLOR));
        }
        return color;
    }

    @SuppressLint("Range")
    public List<SpotCategory> getAllCategories() {
        List<SpotCategory> categories = new ArrayList<>();
        final Cursor cur = sqLiteDatabase.rawQuery("SELECT * FROM " + CAT_TABLE, null);
        while (cur.moveToNext()) {
            String id = cur.getString(cur.getColumnIndex(COLUMN_CAT_ID));
            String name = cur.getString(cur.getColumnIndex(COLUMN_CAT));
            int color = cur.getInt(cur.getColumnIndex(COLUMN_COLOR));
            SpotCategory spotCategory = new com.geometry.spotsapp.model.SpotCategory(id,name,color);

            categories.add(spotCategory);
        }
        return categories;
    }

    @SuppressLint("Range")
    public List<String> getAllCategoryNames() {
        List<String> categories = new ArrayList<>();
        final Cursor cur = sqLiteDatabase.rawQuery("SELECT * FROM " + CAT_TABLE, null);
        while (cur.moveToNext()) {
            String name = cur.getString(cur.getColumnIndex(COLUMN_CAT));
            categories.add(name);
        }
        return categories;
    }

    @Override
    @SuppressLint("Range")
    public List<String> getAllSpotNamesOfCategory(String category) {
        List<String> spots = new ArrayList<>();
        final Cursor cur = sqLiteDatabase.rawQuery("SELECT * FROM " + SPOT_TABLE + " WHERE (" + COLUMN_CAT +" = " + '"' + category + '"' + ") ", null);
        while (cur.moveToNext()) {
            String name = cur.getString(cur.getColumnIndex(COLUMN_SPOT_TITLE));
            spots.add(name);
        }
        return spots;
    }

    /**
     * Singleton
     */
    private static Datastore datastore;

    public static Datastore getInstance(SpotIconManager spotIconManager, SQLiteDatabase sql){
        if(DatastoreImpl.datastore == null) DatastoreImpl.datastore = new DatastoreImpl(spotIconManager);
        return datastore;
    }
}
