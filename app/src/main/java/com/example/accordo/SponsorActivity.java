package com.example.accordo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.accordo.model.Model;

public class SponsorActivity extends AppCompatActivity implements OnRecyclerViewClickListener{

    private static final String LOG_TAG = "SponsorActivity";

    private NetworkManager networkManager;
    private Model model;
    private SponsorAdapter sponsorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsor);

        networkManager = NetworkManager.getInstance(this);
        model = Model.getInstance(getApplication());

        RecyclerView sponsorRecyclerView = findViewById(R.id.recyclerview_sponsor);
        sponsorAdapter = new SponsorAdapter(this, model, this);
        sponsorRecyclerView.setAdapter(sponsorAdapter);
        sponsorRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSponsor();
    }

    public void getSponsor(){
        networkManager.getSponsor(
                response -> {
                    model.setSponsor(response);
                    Log.d(LOG_TAG, "getSponsor ok");
                    sponsorAdapter.notifyDataSetChanged();
                }, error -> Log.d(LOG_TAG, "errore chiamata server getWall")
        );
    }

    @Override
    public void onRecyclerViewClick(View v, int position) {
        Intent intent = new Intent(getApplicationContext(), DettagliSponsorActivity.class);
        intent.putExtra("position_sponsor", position);
        startActivity(intent);
    }
}