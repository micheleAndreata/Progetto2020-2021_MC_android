package com.example.accordo.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "userPicture_table")
public class UserPicture {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @NonNull
    @ColumnInfo(name = "pversion")
    private Integer pversion;

    @ColumnInfo(name = "picture")
    private String picture;

    public UserPicture(@NonNull String uid, @NonNull Integer pversion, String picture){
        this.uid = uid;
        this.pversion = pversion;
        this.picture = picture;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    @NonNull
    public Integer getPversion() {
        return pversion;
    }

    public String getPicture() {
        return picture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPicture that = (UserPicture) o;
        return uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }
}
