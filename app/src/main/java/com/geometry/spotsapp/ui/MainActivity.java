package com.geometry.spotsapp.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.geometry.spotsapp.R;
import com.geometry.spotsapp.control.PermissionHandler;
import com.geometry.spotsapp.model.MapInstance;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.geometry.spotsapp.databinding.ActivityMainBinding;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private MapView map = null;

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigationBar();


        //FACTORY NEEDED
        PermissionHandler permissionHandler = new PermissionHandler();

        if (permissionHandler.needsPermissions(this)) {
            permissionHandler.requestPermissions(this);
        }

        map = (MapView) findViewById(R.id.map_view);
        MapInstance.mapSaver(map);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void setupNavigationBar() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_spots,  R.id.navigation_share)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    /**
     * The activity handles a request for further Permissions
     * @param requestCode Code for the transaction specified by the dev
     * @param permissions array filled with the permissions that are to be requested
     * @param grantResults array will be filled with values containing if a grant was successful. (PackageManager.PERMISSION_GRANTED or PackageManager.PERMISSION_DENIED)
     *
     * Method copied from:
     * @author Alex O'Ree
     * O’Ree, Alex. 2022. “OSMDroid.”
     * GitHub. December 9, 2022.
     * https://github.com/osmdroid/osmdroid/blob/master/OpenStreetMapViewer/src/main/java/org/osmdroid/intro/PermissionsFragment.java
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionHandler.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, -1);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, -1);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION and WRITE_EXTERNAL_STORAGE
                Boolean location = perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                Boolean storage = perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                if (location && storage) {
                    // All Permissions Granted
                    Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
                } else if (storage) {
                    Toast.makeText(this, "Storage permission is required to store map tiles to reduce data usage and for offline usage.", Toast.LENGTH_LONG).show();
                } else if (location) {
                    Toast.makeText(this, "Location permission is required to show the user's location on map.", Toast.LENGTH_LONG).show();
                } else {
                    // All Permissions Denied
                    Toast.makeText(this, "Storage permission is required to store map tiles to reduce data usage and for offline usage." +
                            "\nLocation permission is required to show the user's location on map.", Toast.LENGTH_SHORT).show();
                }
                Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
