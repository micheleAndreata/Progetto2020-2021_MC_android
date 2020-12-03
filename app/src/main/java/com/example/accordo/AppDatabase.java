package com.example.accordo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserPicture.class, PostImage.class}, version = 2, exportSchema = false)
public abstract class AppDatabase  extends RoomDatabase {

    public abstract UserPictureDao userPictureDao();
    public abstract PostImageDao postImageDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static AppDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (AppDatabase.class) {
                if (INSTANCE == null)
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                            .fallbackToDestructiveMigration() //TODO capire se tenere o no
                            .build();
            }
        }
        return INSTANCE;
    }
}
