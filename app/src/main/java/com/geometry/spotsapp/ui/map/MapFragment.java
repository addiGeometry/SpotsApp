package com.geometry.spotsapp.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.geometry.spotsapp.BuildConfig;
import com.geometry.spotsapp.R;
import com.geometry.spotsapp.control.SpotInfoWindowFactory;
import com.geometry.spotsapp.model.SpotIconManager;
import com.geometry.spotsapp.control.MapManipulator;
import com.geometry.spotsapp.control.MapManipulatorFactory;
import com.geometry.spotsapp.model.DatastorageMapAccess;
import com.geometry.spotsapp.model.DatastorageAccessFactory;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.github.angads25.filepicker.controller.DialogSelectionListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.modules.ArchiveFileFactory;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * VIEW
 * Das Map Fragment ist für die Darstellung der Karte basierend auf Open Streat Map zuständig
 */
public class MapFragment extends Fragment implements LocationListener {
    private static int MENU_LAST_ID = Menu.FIRST; // Always set to last unused id
    public static final String TAG = "spotsMapFrag";

    //Osmdroid:
    private MapView map = null;
    private IMapController mapController;

    //Control
    private MapManipulator mapManipulator;

    //Model
    private DatastorageMapAccess datastorage;
    private DatastorageAccessFactory dsfactory;


    /**
     * For the bookmark logic
     */
    private LocationManager lm;
    private MyLocationNewOverlay mMyLocationOverlay = null;
    private Location currentLocation = null;

    private SpotIconManager spotIconManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dsfactory = new DatastorageAccessFactory();
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Log.d(TAG, "onFragmentCreated");

        //Navigation
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        map = view.findViewById(R.id.map_view);
        initializeMap();

        return view;
    }

    public void onResume() {
        super.onResume();

        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
            //this fails on AVD 19s, even with the appcompat check, says no provided named gps is available
            if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this.getContext(), "Error: Permissions missing", Toast.LENGTH_SHORT);
                return;
            }
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0f, this);
        } catch (Exception ex) {
        }

        try {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0l, 0f, this);
        } catch (Exception ex) {
        }
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        setCenter();
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();//needed for compass, my location overlays, v6.0.0 and up
        try {
            lm.removeUpdates(this);
        } catch (Exception ex){
            //don`t handle
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (datastorage != null)
            datastorage.close();
        datastorage = null;
        if (addBookmark != null)
            addBookmark.dismiss();
        addBookmark = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void setCenter(){
        GeoPoint startPoint = new GeoPoint(52.4570,13.5264);
        mapController.setCenter(startPoint);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation = location;
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }


    /**
     * Hier wird die OSM-Map initialisiert
     */
    public void initializeMap(){
        if (map != null) {
            addOverlays();

            map.setTilesScaledToDpi(true);
            map.setMultiTouchControls(true);
            map.setUseDataConnection(true);
            map.setTileSource(TileSourceFactory.MAPNIK);


            mapController = map.getController();
            mapController.zoomTo(14, 10L);


        } else {
            Log.d(TAG, "FATAL ERROR MAP NOT LOADED");
        }
    }

    /**
     * Methods
     */

    AlertDialog addBookmark = null;
    /**
     * Hier werden alle Overlaydarstellungen initialisiert
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public void addOverlays() {
        //Icon init
        spotIconManager = SpotIconManager.getInstance(getContext());

        Log.d(TAG, "onMFragModelInitialised");
        //init Model
        if (datastorage == null) {
            datastorage = dsfactory.newDatastorageMapAccess(map, spotIconManager);
        }

        //init Control
        mapManipulator = MapManipulatorFactory.newMapManipulator(map, datastorage, new SpotInfoWindowFactory());
        Log.d(TAG, "onMFragControlInitialised");

        //add all our bookmarks to the view
        addllMarkers();

        this.mMyLocationOverlay = new MyLocationNewOverlay(map);
        mMyLocationOverlay.setEnabled(true);


        GpsMyLocationProvider mGpsMyLocationProvider = new GpsMyLocationProvider(getActivity());
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(mGpsMyLocationProvider, map);


        this.map.getOverlays().add(mMyLocationOverlay);
        //support long press to add a bookmark

        //TODO menu item to
        MapEventsOverlay events = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {


                addSpotDialog(p);
                return true;
            }
        });

        map.getOverlayManager().add(events);
    }

    private void addllMarkers() {
        map.getOverlayManager().addAll(datastorage.getBookmarksAsMarkers(map, mapManipulator));
    }

    private String spinnerSelect;

    private void addSpinner(Spinner spinner) {
        List<String> categories = datastorage.getAllCategoryNames();
        //List<String> items = categories.stream().map((spotCategory) -> spotCategory.getName()).collect(Collectors.toList());

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), R.layout.cat_spinner, categories);
        // Apply the adapter to the spinner
        if(spinner != null){
            spinner.setAdapter(adapter);
        } else if(spinner == null) {
            Toast.makeText(getContext(),"Fatal - Spinner not working",Toast.LENGTH_SHORT);
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerSelect = categories.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerSelect = "Standard";
            }
        });
    }

    private void addSpotDialog(GeoPoint p) {
        if (addBookmark != null)
            addBookmark.dismiss();

        //TODO prompt for user input
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = View.inflate(getContext(), R.layout.add_spot_dialog, null);
        builder.setView(view);


        final EditText lat = view.findViewById(R.id.bookmark_lat);
        lat.setText(p.getLatitude() + "");
        final EditText lon = view.findViewById(R.id.bookmark_lon);
        lon.setText(p.getLongitude() + "");
        final EditText title = view.findViewById(R.id.bookmark_title);
        final EditText description = view.findViewById(R.id.bookmark_description);
        final Spinner spinner = view.findViewById(R.id.categorySpinner);
        addSpinner(spinner);

        view.findViewById(R.id.bookmark_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookmark.dismiss();
            }
        });
        view.findViewById(R.id.bookmark_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean valid = true;
                double latD = 0;
                double lonD = 0;
                //basic validate input
                try {
                    latD = Double.parseDouble(lat.getText().toString());
                } catch (Exception ex) {
                    valid = false;
                }
                try {
                    lonD = Double.parseDouble(lon.getText().toString());
                } catch (Exception ex) {
                    valid = false;
                }

                if (!map.getTileSystem().isValidLatitude(latD))
                    valid = false;
                if (!map.getTileSystem().isValidLongitude(lonD))
                    valid = false;

                if (valid) {
                    //call control for model change
                    //SpotCategory category = SpotIconManager.parseCategory(spinnerSelect);
                    try {
                        mapManipulator.addANewMarker(title.getText().toString(), spinnerSelect, latD, lonD, description.getText().toString());
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(getContext(), "Spots müssen einen Namen haben", Toast.LENGTH_SHORT).show();
                    } catch (IllegalAccessError e){
                        Toast.makeText(getContext(), "Erstelle zuerst eine Kategorie", Toast.LENGTH_SHORT).show();
                    }
                }
                addBookmark.dismiss();
            }
        });
        addBookmark = builder.show();
    }

    private static final int MENU_BOOKMARK_MY_LOCATION = Menu.FIRST;
    private static final int MENU_BOOKMARK_IMPORT = MENU_BOOKMARK_MY_LOCATION + 1;
    private static final int MENU_BOOKMARK_EXPORT = MENU_BOOKMARK_IMPORT + 1;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.add(0, MENU_BOOKMARK_MY_LOCATION, Menu.NONE, "Bookmark Current Location").setCheckable(false);
        MENU_LAST_ID++;
        menu.add(0, MENU_BOOKMARK_IMPORT, Menu.NONE, "Import from CSV").setCheckable(false);
        MENU_LAST_ID++;
        menu.add(0, MENU_BOOKMARK_EXPORT, Menu.NONE, "Export to CSV").setCheckable(false);
        MENU_LAST_ID++;
        try {
            map.getOverlayManager().onCreateOptionsMenu(menu, MENU_BOOKMARK_MY_LOCATION + 1, map);
        } catch (NullPointerException npe) {
            //can happen during CI tests and very rapid fragment switching
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        try {
            map.getOverlayManager().onPrepareOptionsMenu(menu, MENU_LAST_ID, map);
        } catch (NullPointerException npe) {
            //can happen during CI tests and very rapid fragment switching
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_BOOKMARK_MY_LOCATION) {
            //TODO
            if (currentLocation != null) {
                GeoPoint pt = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                addSpotDialog(pt);
                return true;
            }

        } else if (item.getItemId() == MENU_BOOKMARK_IMPORT) {
            //TODO
            showFilePicker();
            return true;

        } else if (item.getItemId() == MENU_BOOKMARK_EXPORT) {
            //TODO
            showFileExportPicker();
            return true;

        } else if (map.getOverlayManager().onOptionsItemSelected(item, MENU_LAST_ID, map)) {
            return true;
        }
        return false;
    }

    private void showFileExportPicker() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.DIR_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);

        FilePickerDialog dialog = new FilePickerDialog(getContext(), properties);
        dialog.setTitle("Save CSV File");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(final String[] files) {
                //files is the array of the paths of files selected by the Application User.
                if (files.length == 1) {

                    //now prompt for a file name
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapFragment.this.getContext());
                    builder.setTitle("Enter file name (.csv)");

                    // Set up the input
                    final EditText input = new EditText(MapFragment.this.getContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    input.setLines(1);
                    input.setText("export.csv");

                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //save the file here.
                            if (input.getText() == null)
                                return;
                            ExecutorService executorService = Executors.newSingleThreadExecutor();
                            executorService.submit(
                                    () -> {
                                        String file = input.getText().toString();
                                        if (!file.toLowerCase().endsWith(".csv")) {
                                            file = file + ".csv";
                                        }
                                        datastorage.exportToCsv(files, file, getActivity());
                                    }
                            );
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            }

        });
        dialog.show();
    }

    private void showFilePicker() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);

        Set<String> registeredExtensions = ArchiveFileFactory.getRegisteredExtensions();

        registeredExtensions.add("csv");

        String[] ret = new String[registeredExtensions.size()];
        ret = registeredExtensions.toArray(ret);
        properties.extensions = ret;

        FilePickerDialog dialog = new FilePickerDialog(getContext(), properties);
        dialog.setTitle("Select a CSV File");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(final String[] files) {
                //files is the array of the paths of files selected by the Application User.
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.submit(
                        () -> {
                            datastorage.importFromCsv(files, getActivity());}
                );
            }

        });
        dialog.show();
    }
}