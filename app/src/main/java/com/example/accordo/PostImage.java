package com.example.accordo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "postImage_table")
public class PostImage {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "pid")
    private String pid;

    @NonNull
    @ColumnInfo(name = "picture")
    private String picture;

    public PostImage(@NonNull String pid, @NonNull String picture){
        this.pid = pid;
        this.picture = picture;
    }

    @NonNull
    public String getPicture() {
        return picture;
    }

    @NonNull
    public String getPid() {
        return pid;
    }
}
