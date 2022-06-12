package com.example.gallerysimple.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AlbumItemsDao {
    @Insert
    Completable insertItem(AlbumItems... albumItems);

    @Query("SELECT * FROM albumItems WHERE aid = :aid")
    Single<List<AlbumItems>> getItemsByAlbumId(int aid);

    @Query("SELECT * FROM albumItems WHERE aid IN (:ids) AND path LIKE :path")
    Single<List<AlbumItems>> getItemByPathAndAlbumID(String path, int[] ids);

    @Query("DELETE FROM albumItems WHERE path LIKE :path AND aid = :id")
    Completable deleteItem(String path, int id);

    @Query("DELETE FROM albumItems WHERE aid = :id")
    Completable deleteItemByAlbumId(int id);

    @Query("SELECT * FROM albumItems")
    Single<List<AlbumItems>> getItems();

    @Query("SELECT COUNT(aid) FROM albumItems WHERE aid = :id AND path LIKE :path")
    Single<Integer> getItem(String path, int id);

    @Query("INSERT INTO albumItems(path, aid) VALUES(:path, :aid)")
    Completable insertItem(String path, int aid);
}
