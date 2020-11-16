package com.example.accordo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Profile profile);

    @Delete
    void delete(Profile profile);

    @Update
    void update(Profile profile);

    @Query("UPDATE profile_table SET name = :newName WHERE sid =  :sid")
    void updateName(String sid, String newName);

    @Query("UPDATE profile_table SET picture = :newPicture, pversion = :newPVersion WHERE sid =  :sid")
    void updatePicture(String sid, String newPicture, String newPVersion);

    @Query("SELECT * FROM profile_table WHERE sid = :sid")
    Profile getProfile(String sid);
}
