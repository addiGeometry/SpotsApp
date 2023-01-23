package com.geometry.spotsapp.model;

import org.junit.Test;
import org.mockito.Mockito;

public class ModelTest {



    @Test
    public void DatastoreTest(){
        SpotIconManager manager = Mockito.mock(SpotIconManager.class);
        Datastore datastore = new DatastoreImpl(manager);
    }
}
