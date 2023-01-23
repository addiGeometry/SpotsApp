package com.geometry.spotsapp.model;

import static com.geometry.spotsapp.ui.map.MapFragment.TAG;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.geometry.spotsapp.control.MapManipulator;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Datastorage implements DatastorageMapAccess {

    private boolean exportStatus = true;

    private final MapView map;
    private final SpotIconManager spotIconManager;

    //for database creation
    protected File db_file;
    protected SQLiteDatabase sqLiteDatabase;
    public static final String DATABASE_FILENAME = "spots.mDatabase";

    private Datastore datastore = null;


    protected Datastorage(MapView map, SpotIconManager spotIcon){
        this.map = map;
        this.spotIconManager = spotIcon;
        createDatastore();
    }

    /**
     * call me from a background thread
     */
    @Override
    public void exportToCsv(String[] fileList, String file, Activity activity) {
        File writeFile = new File(fileList[0] + File.separator + file);
        FileWriter fileWriter = null;
        exportStatus = true;
        try {
            fileWriter = new FileWriter(writeFile);
            CSVWriter writer = new CSVWriter(fileWriter);
            List<Marker> markers = null;
            String[] headers = new String[]{"Latitude", "Longitude", "Description", "Title"};
            writer.writeNext(headers);
            for (Marker m : markers) {
                String[] items = new String[4];
                items[0] = m.getPosition().getLatitude() + "";
                items[1] = m.getPosition().getLongitude() + "";
                items[2] = m.getSubDescription();
                items[3] = m.getTitle();
                writer.writeNext(items);
            }
        } catch (Exception ex) {
            exportStatus = false;
            ex.printStackTrace();
        } finally {
            if (fileWriter != null)
                try {
                    fileWriter.close();
                } catch (Exception ex) {
                }
        }

        final Activity act = activity;
        if (act != null) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (exportStatus) {
                        Toast.makeText(act, "Export Complete", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(act, "Export Failed", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void importFromCsv(String[] files, Activity activity) {
        final File file = new File(files[0]);
        final AtomicInteger imported = new AtomicInteger();
        final AtomicInteger failed = new AtomicInteger();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            CSVReader reader = new CSVReader(fileReader);
            String[] nextLine = reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                try {
                    String lat = nextLine[0];
                    String lon = nextLine[1];
                    String description = nextLine[2];
                    String title = nextLine[3];
                    //TODO REALLY IMPORTANT TO MAKE CSV WORK
                    SpotCategory spotCategory = null;
                    Marker m = new Marker(map);
                    m.setTitle(title);
                    m.setSubDescription(description);
                    m.setPosition(new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lon)));
                    //m.setIcon(spotIconManager.getSpotIcon(Spot.STANDARD));
                    //Via Import Get MARKER HEAD TODO
                    //spotDatastoreImpl.addSpot(m, spotIconManager.parseDBString(spotCategory));
                    map.getOverlayManager().add(m);
                    imported.getAndIncrement();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    failed.getAndIncrement();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fileReader != null)
                try {
                    fileReader.close();
                } catch (Exception ex) {
                }
        }

        final Activity act = activity;
        if (act != null) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, "Import Complete: " + imported.get() + "/" + failed.get() + "(imported/failed)", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void destroyDatastore(){
        if (sqLiteDatabase != null)
            this.close();
        sqLiteDatabase = null;
        datastore = null;
    }

    private void createDatastore(){
        if (datastore == null) datastore = new DatastoreImpl(spotIconManager);
    }

    public void close(){
            db_file = null;
            sqLiteDatabase.close();
            sqLiteDatabase = null;
            datastore = null;
    }

    @Nullable
    public Datastore getSpotDatastore(){
        return datastore;
    }

    public List<String> getAllCategoryNames(){
        return datastore.getAllCategoryNames();
    }

    @Override
    public int getCatColor(String category) {
        return datastore.getCatColor(category);
    }

    public List<SpotCategory> getAllCategories() {
        return datastore.getAllCategories();
    }


    public Drawable getSpotIcon(String category) {
        int color = datastore.getCatColor(category);
        Log.d(TAG, "MAYDAY " + color);
        return spotIconManager.getSpotIcon(color, category);
    }

    @Override
    public Drawable getSpotImage() {
        return spotIconManager.getSpotImage();
    }


    @Override
    public void addSpot(Marker bookmark, String category) {
        datastore.addSpot(bookmark, category);
    }

    @Override
    public void removeSpot(String spotId) {
        datastore.removeSpot(spotId);
    }


    @Override
    public List<Marker> getBookmarksAsMarkers(MapView view, MapManipulator mapManipulator) {
        List<Spot> spots = datastore.getSpots(view, mapManipulator);
        return spots.stream().map((spot) -> spot.getM()).collect(Collectors.toList());
    }
}
