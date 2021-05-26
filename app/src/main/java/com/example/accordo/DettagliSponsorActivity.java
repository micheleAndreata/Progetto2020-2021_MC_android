package com.example.accordo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.accordo.model.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class DettagliSponsorActivity extends AppCompatActivity {

    private static final String LOG_TAG = "DettagliSponsorActivity";

//    private NetworkManager networkManager;
    private Model model;
    private int position_sponsor;
    private TextView nome_sponsor;
    private ImageView image_sponsor;
    private JSONObject sponsor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_sponsor);

        nome_sponsor = findViewById(R.id.nome_dettagli_sponsor);
        image_sponsor = findViewById(R.id.image_dettagli_sponsor);

        model = Model.getInstance(getApplication());

        Intent intent = getIntent();

        String temp = intent.getStringExtra("position_sponsor");
        Log.d(LOG_TAG, temp);
//        position_sponsor = Integer.parseInt(intent.getStringExtra("position_sponsor"));

//        sponsor = model.getSponsor().get(position_sponsor);

//        setActivity();


    }

    public void setActivity(){
        try{
            nome_sponsor.setText(sponsor.getString("text"));
        }catch (JSONException e) { e.printStackTrace(); }
    }

    //
    public void goToLink(View v){

        try{
            Log.d(LOG_TAG, sponsor.getString("url"));
        }catch (JSONException e) { e.printStackTrace(); }
    }
}