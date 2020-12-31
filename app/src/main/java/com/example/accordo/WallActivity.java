package com.example.accordo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WallActivity extends AppCompatActivity implements OnRecyclerViewClickListener{

    private static final String LOG_TAG = "WallActivity";

    private NetworkManager networkManager;
    private Model model;
    private MyWallAdapter myWallAdapter;
    private NotMyWallAdapter notMyWallAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkManager = NetworkManager.getInstance(this);

        model = Model.getInstance(getApplication());
        SharedPreferences profile = getSharedPreferences("profile_data", Context.MODE_PRIVATE);

        RecyclerView myWallRecyclerView = findViewById(R.id.recyclerview_myWall);
        myWallAdapter = new MyWallAdapter(this, model, this);
        myWallRecyclerView.setAdapter(myWallAdapter);
        myWallRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView notMyWallRecyclerView = findViewById(R.id.recyclerview_notMyWall);
        notMyWallAdapter = new NotMyWallAdapter(this, model, this);
        notMyWallRecyclerView.setAdapter(notMyWallAdapter);
        notMyWallRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //check Registrazione:
        if (profile.getString("sid", null) == null) {
            networkManager.register(
                    result -> {
                        SharedPreferences.Editor editor = profile.edit();
                        editor.putString("sid", result);
                        editor.apply();
                        Log.d(LOG_TAG, "nuova registrazione completata");

                        getWall();
                    },
                    error -> {
                        Log.d(LOG_TAG, "errore chiamata server register");
                    });
        }
        else {
            Log.d(LOG_TAG, "utente gia registrato");

            getWall();
        }
    }

    public void getWall(){
        networkManager.getWall(
                wall -> {
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
                    } catch (JSONException e) { e.printStackTrace(); }
                    model.setMyWall(mine);
                    myWallAdapter.notifyDataSetChanged();
                    model.setNotMyWall(notMine);
                    notMyWallAdapter.notifyDataSetChanged();
                }, error -> {
                    Log.d(LOG_TAG, "errore chiamata server getWall");
                });
    }

    @Override
    public void onRecyclerViewClick(View v, int position) {
        TextView t = v.findViewById(R.id.cTitle);
        String cTitle = t.getText().toString();
        Intent intent = new Intent(getApplicationContext(), ChannelActivity.class);
        intent.putExtra("cTitle", cTitle);
        startActivity(intent);
    }
}