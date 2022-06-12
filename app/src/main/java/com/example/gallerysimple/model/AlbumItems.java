package com.example.gallerysimple.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "albumItems")
public class AlbumItems {
    @PrimaryKey (autoGenerate = true)
    private int id;

    @ColumnInfo (name = "path")
    private String path;

    @ColumnInfo(name = "aid")
    private int aid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }
}
