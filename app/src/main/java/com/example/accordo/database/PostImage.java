package com.example.accordo.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "postImage_table")
public class PostImage {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "pid")
    private String pid;

    @NonNull
    @ColumnInfo(name = "image")
    private String image;

    public PostImage(@NonNull String pid, @NonNull String image){
        this.pid = pid;
        this.image = image;
    }

    @NonNull
    public String getImage() {
        return image;
    }

    @NonNull
    public String getPid() {
        return pid;
    }

    public void setImage(@NonNull String image){
        this.image = image;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostImage postImage = (PostImage) o;
        return pid.equals(postImage.pid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid);
    }
}
