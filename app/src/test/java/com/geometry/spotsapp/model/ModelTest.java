package com.geometry.spotsapp.model;

import org.junit.Test;
import org.mockito.Mockito;

public class ModelTest {


    @Test
    public void datastoreTest(){
        SpotIconManager manager = Mockito.mock(SpotIconManager.class);
        Datastore datastore = new DatastoreImpl(manager);
    }

    @Test
    public void makeSpotPersistentTest(){
        SpotIconManager manager = Mockito.mock(SpotIconManager.class);
        Datastore datastore = new DatastoreImpl(manager);
    }

    @Test
    public void deleteSpot(){
    }

    @Test
    public void getAllSpotsTest(){
    }

    @Test
    public void addMultipleSpotsAndGet(){
    }

    @Test
    public void getCategoryColorTest(){

    }

    @Test
    public void getCatColorTest(){

    }

    @Test
    public void addCategoryTest(){

    }
}
