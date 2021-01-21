package com.example.accordo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.profileBtn) {
            Log.d(LOG_TAG, "mio profilo");
            toProfileActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toProfileActivity(){
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }

    public void onNewChannelClick(View v){

        View viewInflated = LayoutInflater.from(this).inflate(
                R.layout.text_input_add_channel, findViewById(android.R.id.content),false);
        final EditText inputText = viewInflated.findViewById(R.id.inputText);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Nuovo Canale")
                .setView(viewInflated)
                .setPositiveButton("Aggiungi", null)
                .setNegativeButton("Cancella", null)
                .show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            networkManager.addChannel(
                    inputText.getText().toString(),
                    response -> {
                        getWall();
                        dialog.dismiss();
                    },
                    error -> {
                        Log.d(LOG_TAG, "Errore chiamata addChannel");
                        inputText.setError("Nome canale già presente. Sceglierne un altro.");
                    });
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