package com.example.gallerysimple.model;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface DirectoryDao {
    @Query("SELECT * FROM directories")
    Single<List<Directory>> getAll();

    @Query("SELECT * FROM directories WHERE id IN (:id)")
    Single<Directory> getDirectorieById(int id);

    @Insert(onConflict = REPLACE)
    Completable insertAll(List<Directory> directories);

    @Delete
    Completable delete(Directory directory);

    @Update
    Completable updateDirectories(Directory ... directories);
}
