package com.example.gallerysimple.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "directories")
public class Directory {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "path") 
    public String path = "";

    @ColumnInfo(name = "filename") 
    public String name = "";

    @ColumnInfo(name = "last_modified") 
    public long modified = 0;

    @ColumnInfo(name = "date_taken") 
    public long taken = 0;

    @ColumnInfo(name = "size") 
    public long size = 0;

    @ColumnInfo(name = "media_types") 
    public String types = "";

    @ColumnInfo(name = "favorite")
    public boolean favorite = false;

    @ColumnInfo(name = "duration")
    public long duration = 0;

    @ColumnInfo(name = "isDelete")
    private int isDelete = 0;

    public Directory(int id, String path, String name, long modified, long taken, long size, String types) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.modified = modified;
        this.taken = taken;
        this.size = size;
        this.types = types;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Directory() {}

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }
}
