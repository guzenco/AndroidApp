package com.lab.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public abstract class MeteorsDao {

    @Query("SELECT * FROM meteors")
    public abstract Meteor[] getMeteors();

    @Insert
    public abstract long[] insert(Meteor[] newMeteors);

    @Query("DELETE FROM meteors")
    public abstract void deleteAll();
    @Transaction
    public void updateMeteors(Meteor[] newMeteors) {
        deleteAll();
        insert(newMeteors);
    }
}
