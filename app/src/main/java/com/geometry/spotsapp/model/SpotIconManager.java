package com.geometry.spotsapp.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.geometry.spotsapp.R;

import java.util.HashMap;
import java.util.LinkedList;

public class SpotIconManager {

    private LinkedList<Drawable> iconList;
    private HashMap<String, Drawable> icons;
    private Context context;

    private SpotIconManager(Context context){
        this.context = context;
        icons = new HashMap<>();
        this.iconList = new LinkedList<>();
    }

    public Drawable getSpotIcon(int color, String category){
        Drawable spotIcon = icons.get(category);
        if(spotIcon == null) {
            spotIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_location_on_24);
            spotIcon.setTint(color);
        }
        return spotIcon;
    }

    public Drawable getSpotImage(){
        return ContextCompat.getDrawable(context, R.drawable.somelaun_foreground);
    }

    public static SpotIconManager spotIconManager;

    public static SpotIconManager getInstance(Context context){
        if(spotIconManager == null) spotIconManager = new SpotIconManager(context);
        return spotIconManager;
    }
}
