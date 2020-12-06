package com.example.accordo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "userPicture_table")
public class UserPicture {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @NonNull
    @ColumnInfo(name = "pversion")
    private String pversion;

    @NonNull
    @ColumnInfo(name = "picture")
    private String picture;

    public UserPicture(@NonNull String uid, @NonNull String pversion, @NonNull String picture){
        this.uid = uid;
        this.pversion = pversion;
        this.picture = picture;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    @NonNull
    public String getPversion() {
        return pversion;
    }

    @NonNull
    public String getPicture() {
        return picture;
    }
}
