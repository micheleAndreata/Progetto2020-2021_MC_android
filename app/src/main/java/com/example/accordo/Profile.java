package com.example.accordo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile_table")
public class Profile {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sid")
    private String sid;

    @ColumnInfo(name = "uid")
    private String uid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "picture")
    private String picture;

    @ColumnInfo(name = "pversion")
    private int pversion;

    public Profile(@NonNull String sid, String uid, String name, String picture, int pversion){
        this.sid = sid;
        this.uid = uid;
        this.name = name;
        this.picture = picture;
        this.pversion = pversion;
    }

    @NonNull
    public String getSid() {
        return sid;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public int getPversion() {
        return pversion;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setPversion(int pversion) {
        this.pversion = pversion;
    }
}
