package com.example.accordo;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Model extends AndroidViewModel {

    private DataRepository repo;
    private MutableLiveData<List<String>> myChannels = new MutableLiveData<>();
    private MutableLiveData<List<String>> otherChannels = new MutableLiveData<>();
    private MutableLiveData<List<Post>> channel = new MutableLiveData<>();

    public Model(@NonNull Application application) {
        super(application);
        repo = new DataRepository(application);
    }

    public MutableLiveData<List<String>> getMyChannels() {
        return myChannels;
    }

    public MutableLiveData<List<String>> getOtherChannels() {
        return otherChannels;
    }

    public void updateWall(Context ctx){
        repo.getWall(ctx, wall -> {
            List<String> mine = new ArrayList<>();
            List<String> notMine = new ArrayList<>();
            try {
                for (JSONObject channel : wall){
                    if (channel.getString("mine").equals("t")){
                        mine.add(channel.getString("ctitle"));
                    }
                    else{
                        notMine.add(channel.getString("ctitle"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            myChannels.setValue(mine);
            otherChannels.setValue(notMine);
        });
    }

    public void getChannel(Context ctx, String ctitle){
        List<Post> channel = new ArrayList<>();
        repo.getChannel(ctx, ctitle, jsonObjectChannel -> {
            try {
                JSONArray jsonArrayChannel = jsonObjectChannel.getJSONArray("posts");
                for (int i=0; i < jsonArrayChannel.length(); i++){
                    JSONObject p = jsonArrayChannel.getJSONObject(i);
                    channel.add(new Post(p));
                }
                this.channel.setValue(channel);
                repo.updateDbPostImages(ctx, jsonObjectChannel);
                repo.updateDbUserPictures(ctx, jsonObjectChannel);
            }
            catch (JSONException e) {e.printStackTrace();}
        });
    }
}