package com.geometry.spotsapp.model;

public class SpotCategory {
    private String name;

    private final String id;
    private int color;

    public SpotCategory(String id, String name, int color){
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public String getName(){
        return this.name;
    }
}
