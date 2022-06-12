package com.example.gallerysimple.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface AlbumDao {
    @Insert
    Completable insertAlbum(Album... album);

    @Update
    Completable updateAlbum(Album... albums);

    @Query("DELETE FROM albums WHERE id = :id")
    Completable deleteAlbumById(int id);

    @Query("SELECT * FROM albums")
    Single<List<Album>> getAllAlbums();

    @Query("SELECT * FROM albums WHERE name LIKE :name")
    Single<Album> getAlbumByName(String name);

    @Query("SELECT * FROM albums WHERE id = :id")
    Single<Album> getAlbumById(int id);
}
