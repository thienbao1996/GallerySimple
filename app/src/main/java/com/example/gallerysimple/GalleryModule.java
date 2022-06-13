package com.example.gallerysimple;

import android.content.Context;

import androidx.room.Room;

import com.example.gallerysimple.util.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class GalleryModule {

    @Singleton
    @Provides
    public static AppDatabase provideRoomDatabase(@ApplicationContext Context appContext) {
        return Room.databaseBuilder(appContext,
                AppDatabase.class, AppDatabase.class.getSimpleName()).build();
    }
}
