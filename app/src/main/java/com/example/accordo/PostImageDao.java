package com.example.accordo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Dao
public interface PostImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PostImage postImage);

    @Delete
    void delete(PostImage postImage);

    @Update
    void update(PostImage postImage);

    @Query("SELECT * FROM postImage_table WHERE pid = :pid")
    PostImage getPostImage(String pid);

    @Query("SELECT * FROM postImage_table")
    List<PostImage> getPostImages();

    @Query("SELECT * FROM postImage_table")
    LiveData<List<PostImage>> getLivePostImages();
}
