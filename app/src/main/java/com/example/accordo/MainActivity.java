package com.example.accordo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataRepository repo = new DataRepository(getApplication());
        repo.getChannel(this, "Gemitaiz", result -> {
            try {
                repo.updateDbUserPictures(this, result);
            } catch (JSONException e) { e.printStackTrace(); }
        });

        repo.getLiveDbUserPictures().observe(this, dbPostImages -> {
            int i = 0;
            for (UserPicture p : dbPostImages){
                Log.d("Main", i + ". " + p.getPicture());
                i++;
            }
        });
    }
}