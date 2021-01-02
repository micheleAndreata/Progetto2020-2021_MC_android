package com.example.accordo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accordo.model.Model;

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
    private SharedPreferences profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        networkManager = NetworkManager.getInstance(this);

        model = Model.getInstance(getApplication());
        profile = getSharedPreferences("profile_data", Context.MODE_PRIVATE);

        RecyclerView myWallRecyclerView = findViewById(R.id.recyclerview_myWall);
        myWallAdapter = new MyWallAdapter(this, model, this);
        myWallRecyclerView.setAdapter(myWallAdapter);
        myWallRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView notMyWallRecyclerView = findViewById(R.id.recyclerview_notMyWall);
        notMyWallAdapter = new NotMyWallAdapter(this, model, this);
        notMyWallRecyclerView.setAdapter(notMyWallAdapter);
        notMyWallRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkRegistration();
    }

    public void checkRegistration(){
        if (profile.getString("sid", null) == null) {
            networkManager.register(
                    result -> {
                        SharedPreferences.Editor editor = profile.edit();
                        editor.putString("sid", result);
                        editor.apply();
                        Log.d(LOG_TAG, "nuova registrazione completata");
                        getWall();
                    },
                    error -> Log.d(LOG_TAG, "errore chiamata server register"));
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
                }, error -> Log.d(LOG_TAG, "errore chiamata server getWall"));
    }

    public void onNewChannelClick(View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nuovo Canale");

        View viewInflated = LayoutInflater.from(this).inflate(
                R.layout.text_input_add_channel, findViewById(android.R.id.content),false);
        final EditText inputText = viewInflated.findViewById(R.id.inputText);
        builder.setView(viewInflated);

        builder.setPositiveButton("Aggiungi", (dialog, which) -> {
            Log.d(LOG_TAG, inputText.getText().toString());
            networkManager.addChannel(
                    inputText.getText().toString(),
                    response -> {
                        if (response)
                            getWall();
                    },
                    error -> Log.d(LOG_TAG, "Errore chiamata addChannel"));
        });
        builder.setNegativeButton("Cancella", (dialog, which) -> dialog.cancel());

        builder.show();
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