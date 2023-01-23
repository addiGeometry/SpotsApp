package com.geometry.spotsapp.control;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


/**
 * Part of CONTROL
 * The Permission Handler is used in the Main Activity to check, whether the user has granted the
 * necessary permissions to properly run spots. Since Android 11 this special grant by the user is
 * obligatory.
 *
 * This code is heavily inspired by the implementation of the osmdroid Team on Github:
 * Last
 * @author Alex O'Ree
 * O’Ree, Alex. 2022. “OSMDroid.”
 * GitHub. December 9, 2022.
 * https://github.com/osmdroid/osmdroid/blob/master/OpenStreetMapViewer/src/main/java/org/osmdroid/intro/PermissionsFragment.java
 */
public class PermissionHandler {
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 100;

    private static List<String> permissions = new ArrayList<>();
    /**
     * Checks, if the specified Permissions (ACCESS_FINE_LOCATION and WRITE_EXTERNAL_STORAGE) are granted already,
     * @param context Context of the calling Activity
     * @return true if Permissions are still needed, false if not
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

    public static void requestPermissions(Activity activity) {
        String[] params = permissions.toArray(new String[permissions.size()]);
        activity.requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
    }
}
