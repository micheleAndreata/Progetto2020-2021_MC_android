package com.example.accordo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Model model = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(Model.class);

        RecyclerView myChannelsRecyclerView = findViewById(R.id.recyclerview_mine);
        final WallAdapter myChannelsAdapter = new WallAdapter(new WallAdapter.WordDiff());
        myChannelsRecyclerView.setAdapter(myChannelsAdapter);
        myChannelsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView otherChannelsRecyclerView = findViewById(R.id.recyclerview_notMine);
        final WallAdapter otherChannelsAdapter = new WallAdapter(new WallAdapter.WordDiff());
        otherChannelsRecyclerView.setAdapter(otherChannelsAdapter);
        otherChannelsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences profile = getSharedPreferences("profile_data", Context.MODE_PRIVATE);
        Log.d(LOG_TAG, Objects.requireNonNull(profile.getString("sid", "a")));

        //check Registrazione:
        if (profile.getString("sid", null) == null) {
            NetworkManager.getInstance(this).register(result -> {
                SharedPreferences.Editor editor = profile.edit();
                editor.putString("sid", result);
                editor.apply();

                model.updateWall(this);
                model.getMyChannels().observe(this, myChannelsAdapter::submitList);
                model.getOtherChannels().observe(this, otherChannelsAdapter::submitList);

                Log.d(LOG_TAG, "nuova registrazione completata");
            });
        }
        else {
            model.updateWall(this);
            model.getMyChannels().observe(this, myChannelsAdapter::submitList);
            model.getOtherChannels().observe(this, otherChannelsAdapter::submitList);
            Log.d(LOG_TAG, "utente gia registrato");
        }

        model.getChannel(this, "Gemitaiz");
        (new DataRepository(getApplication())).getLiveDbPostImages().observe(this, postImages -> {
            for (PostImage postImage : postImages)
                Log.d(LOG_TAG, postImage.getPid() + " " + postImage.getPicture());
        });
    }
}