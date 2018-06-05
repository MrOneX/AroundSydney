package com.test.aroundsydney.models.repositories.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.test.aroundsydney.models.entitys.Location;

@Database(entities = {Location.class}, version = 1, exportSchema = false)
public abstract class LocationDatabase extends RoomDatabase {

    public abstract DaoAccess daoAccess();

}
