package com.example.accordo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserPictureDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(UserPicture userPicture);

    @Delete
    void delete(UserPicture userPicture);

    @Update
    void update(UserPicture userPicture);

    @Query("SELECT * FROM userPicture_table WHERE uid = :uid")
    UserPicture getUserPicture(String uid);

    @Query("SELECT * FROM userPicture_table")
    List<UserPicture> getUserPictures();

    @Query("SELECT * FROM userPicture_table")
    LiveData<List<UserPicture>> getLiveUserPictures();
}
