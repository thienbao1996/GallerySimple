package com.example.gallerysimple.util;

import androidx.room.RoomDatabase;

import com.example.gallerysimple.model.Album;
import com.example.gallerysimple.model.AlbumDao;
import com.example.gallerysimple.model.AlbumItems;
import com.example.gallerysimple.model.AlbumItemsDao;
import com.example.gallerysimple.model.Directory;
import com.example.gallerysimple.model.DirectoryDao;

@androidx.room.Database(entities = {Directory.class, Album.class, AlbumItems.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DirectoryDao directoryDao();
    public abstract AlbumDao albumDao();
    public abstract AlbumItemsDao albumItemsDao();
}
