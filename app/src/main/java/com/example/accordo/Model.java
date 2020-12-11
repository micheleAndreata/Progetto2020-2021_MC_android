package com.example.accordo;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

import java.util.List;

public class Model extends AndroidViewModel {

    private DataRepository repo;
    private MutableLiveData<List<String>> wall;
    private MutableLiveData<List<JSONObject>> channel;

    private LiveData<List<PostImage>> dbPostImage;
    private LiveData<List<UserPicture>> dbUserPicture;

    public Model(@NonNull Application application) {
        super(application);
        repo = new DataRepository(application);
        this.dbPostImage = repo.getLiveDbPostImages();
        this.dbUserPicture = repo.getLiveDbUserPictures();
    }

    public LiveData<List<String>> getWall() {
        return wall;
    }

    public void updateWall(Context ctx){
        repo.getWall(ctx, result -> wall.setValue(result));
    }
}