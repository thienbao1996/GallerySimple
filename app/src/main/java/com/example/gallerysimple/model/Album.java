package com.example.gallerysimple.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "albums")
public class Album {
    @PrimaryKey (autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
