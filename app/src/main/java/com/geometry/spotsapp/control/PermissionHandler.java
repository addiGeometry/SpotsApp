package com.geometry.spotsapp.control;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


/**
 * Teil des controls
 */
public class PermissionHandler {
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 100;

    private static List<String> permissions = new ArrayList<>();

    /**
     * Überprüfe, ob noch Berechtigungen für die Nutzung der App benötigt werden. Seit Anroid 11
     * ist es Notwending für Sensiblen-Berechtigungen beim Nutzer nachzufragen.
     * @param context
     * @return
     */
    public static boolean needsPermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        }
        if (permissions.isEmpty()) {
            //Permissions granted!
            return false;
        }
        return true;
    }

    /**
     * Falls Berechtigungen fehlen, frage diese beim Nutzer an
     * @param activity
     */
    public static void requestPermissions(Activity activity) {
        String[] params = permissions.toArray(new String[permissions.size()]);
        activity.requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
    }
}
