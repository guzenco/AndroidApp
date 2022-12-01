package com.lab.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Meteor.class}, version = 1)
public abstract class MeteorsDatabase extends RoomDatabase {
    public abstract MeteorsDao getImagesDao();
}

