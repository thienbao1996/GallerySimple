package com.example.gallerysimple;

import android.app.Application;

import androidx.room.Room;

import com.example.gallerysimple.util.AppDatabase;

public class GalleryApplication extends Application {
    private static AppDatabase database;


    @Override
    public void onCreate() {
        super.onCreate();

        database = Room.databaseBuilder(this,
                AppDatabase.class, AppDatabase.class.getSimpleName()).build();
    }

    public static AppDatabase getDatabase() {
        return database;
    }
}
