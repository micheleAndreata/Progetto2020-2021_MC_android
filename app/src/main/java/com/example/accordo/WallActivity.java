package com.example.accordo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        // getSharedPreferences serve per vedere se l'utente è già registrato (si controlla  dopo)
        profile = getSharedPreferences("profile_data", Context.MODE_PRIVATE);

        // primo blocco setta la recuclerview dei "mie canali"
        RecyclerView myWallRecyclerView = findViewById(R.id.recyclerview_sponsor);
        myWallAdapter = new MyWallAdapter(this, model, this);
        myWallRecyclerView.setAdapter(myWallAdapter);
        myWallRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // secondo blocco setta la recuclerview per "gli altri canali"
        RecyclerView notMyWallRecyclerView = findViewById(R.id.recyclerview_notMyWall);
        notMyWallAdapter = new NotMyWallAdapter(this, model, this); // perte di controller della reciclerview
        notMyWallRecyclerView.setAdapter(notMyWallAdapter);
        notMyWallRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkRegistration();
    }

    public void checkRegistration(){ // funzione che controlla la registrazione (registrazione implicita)
        if (profile.getString("sid", null) == null) {
            networkManager.register(
                    result -> {
                        //salvo il sid nella SharedPreferences
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
                    // creo due liste di stringhe che contengono i titoli dei canali (così elimino il json)
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
                    model.setMyWall(mine); //insierisco nel model i dati presi
                    myWallAdapter.notifyDataSetChanged(); // per ogno modifica si deve notificare all'adapter che ci è stato una modifica per AGGIORNARE L'INTERFACCIA

                    model.setNotMyWall(notMine);
                    notMyWallAdapter.notifyDataSetChanged();
                }, error -> Log.d(LOG_TAG, "errore chiamata server getWall"));
    }

    // ----- queste chiamate servono per l'interfaccia


    // funzioni per gestire il menù

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // per gestire ogni singolo btn (in questo caso solo)
        if (item.getItemId() == R.id.profileBtn) {
            Log.d(LOG_TAG, "mio profilo");
            toProfileActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // funzione per gestire lo spostarsi verso la finestra del profilo
    public void toProfileActivity(){
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }

    // gestisce la creazione di un nuovo canale
    public void onNewChannelClick(View v){

        // gestisce la creazione della finestra di dialogo prendendo il file text_input_add_channel.xml
        View viewInflated = LayoutInflater.from(this).inflate(
                R.layout.text_input_add_channel, findViewById(android.R.id.content),false);
        final EditText inputText = viewInflated.findViewById(R.id.inputText);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Nuovo Canale")
                .setView(viewInflated)
                .setPositiveButton("Aggiungi", null)
                .setNegativeButton("Cancella", null)
                .show();

        /**
        si definisce esplicitamente il setOnClickListener del bottone 
        per gestire gli eventuali errori della callback
         */ 
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            networkManager.addChannel(
                    inputText.getText().toString(),
                    response -> {
                        getWall();
                        dialog.dismiss(); // per chiudere la finestra
                    },
                    error -> {
                        Log.d(LOG_TAG, "Errore chiamata addChannel");
                        inputText.setError("Nome canale già presente. Sceglierne un altro.");
                    });
        });

    }

    //
    public void goToSponsor(View v){
        Intent intent = new Intent(getApplicationContext(), SponsorActivity.class);
        startActivity(intent);
    }

    // gestisce lo spostamento tra il wallactivity e la channerlactivity
    @Override
    public void onRecyclerViewClick(View v, int position) {
        TextView t = v.findViewById(R.id.cTitle);
        // trasformo il textView in una stringa
        String cTitle = t.getText().toString();
        Intent intent = new Intent(getApplicationContext(), ChannelActivity.class);
        intent.putExtra("cTitle", cTitle);
        startActivity(intent);
    }
}