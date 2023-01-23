package com.geometry.spotsapp.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.osmdroid.api.IMapView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * SQLite Datenbank zur Speicherung der verschiedenen Kategorien
 */
public class CategoryDatastore {
    /**
     * Category-Table
     */
    public static final String TABLE = "categories";
    public static final String COLUMN_ID = "catid";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COLOR = "color";

    protected SQLiteDatabase sqLiteDatabase;

    public CategoryDatastore(SQLiteDatabase database) {
        //spotIconManager = spotIcons;
        try {
            this.sqLiteDatabase = database;
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_ID + " TEXT, " +
                    COLUMN_COLOR + " INTEGER, PRIMARY KEY (" + COLUMN_ID + ") );");
        } catch (Throwable ex) {
            Log.e(IMapView.LOGTAG, "Unable to start the bookmark database. Check external storage availability.", ex);
        }
    }

    public void addCategory(String name, int color){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, UUID.randomUUID().toString());
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_COLOR, color);

        sqLiteDatabase.insert(TABLE, null, cv);
    }

    @SuppressLint("Range")
    public List<SpotCategory> getAllCategories() {
        List<SpotCategory> categories = new ArrayList<>();
        final Cursor cur = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE, null);
        while (cur.moveToNext()) {
            String id = cur.getString(cur.getColumnIndex(COLUMN_ID));
            String name = cur.getString(cur.getColumnIndex(COLUMN_NAME));
            int color = cur.getInt(cur.getColumnIndex(COLUMN_COLOR));
            SpotCategory spotCategory = new SpotCategory(id,name,color);

            categories.add(spotCategory);
        }
        return categories;
    }
}
