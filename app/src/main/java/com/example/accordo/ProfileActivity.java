package com.example.accordo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ProfileActivity";

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 100;
    private static final int PICK_IMAGE_CODE = 101;

    private NetworkManager networkManager;
    private SharedPreferences profile;
    private EditText nomeView;
    private ImageView pictureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        networkManager = NetworkManager.getInstance(this);
        profile = getSharedPreferences("profile_data", Context.MODE_PRIVATE);

        nomeView = findViewById(R.id.nomeView);
        String name = profile.getString("name", null);
        nomeView.setText(name);

        pictureView = findViewById(R.id.pictureView);
        String stringPicture = profile.getString("picture", null);
        Bitmap bitmapPicture = ImageUtils.base64ToBitmap(stringPicture);
        if (bitmapPicture != null && bitmapPicture.getWidth() != 0 && bitmapPicture.getHeight() != 0) {
            bitmapPicture = Bitmap.createScaledBitmap(bitmapPicture, 200, 200, false);
            pictureView.setImageBitmap(bitmapPicture);
        }
        else {
            pictureView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.userpicture, null));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_TAG, "Permission granted");
                changePicture();
            }  else {
                Log.d(LOG_TAG, "Permission NOT granted");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Attenzione");
                builder.setMessage("Per usufruire di questa caratteristica è necessario il permesso alla memoria del telefono");
                builder.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
                builder.show();
            }
        }
    }

    public void onPictureChangeClick(View v){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    REQUEST_READ_EXTERNAL_STORAGE);
        }
        else {
            changePicture();
        }
    }

    public void changePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CODE && data != null) {
            Log.d(LOG_TAG, "image retrieved");
            Uri imageUri = data.getData();
            pictureView.setImageURI(imageUri);
            (new Thread(() -> {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmap != null){
                    Bitmap lowQuality = ImageUtils.reduceImageQuality(bitmap);
                    String base64 = ImageUtils.bitmapToBase64(lowQuality);
                    networkManager.setProfile(
                            null,
                            base64,
                            (response) -> {
                                Log.d(LOG_TAG, "immagine profilo salvata sul server");
                                SharedPreferences.Editor editor = profile.edit();
                                editor.putString("picture", base64);
                                editor.apply();
                            }, error -> {
                                Log.d(LOG_TAG, "ERRORE chiamata setPicture");
                            }
                    );
                }
            })).start();
        }
        else
            Log.d(LOG_TAG, "ERRORE richiesta immagine");
    }

    public void onNameChangeClick(View v){
        String newName = nomeView.getText().toString();
        Log.d(LOG_TAG, newName);
        networkManager.setProfile(
                newName,
                null,
                response -> {
                    Log.d(LOG_TAG, "nome cambiato correttamente");
                    SharedPreferences.Editor editor = profile.edit();
                    editor.putString("name", newName);
                    editor.apply();
                    correctDialog();
                },
                error -> {
                    Log.d(LOG_TAG, "ERRORE chiamata setProfile");
                    errorDialog();
                });
    }
    public void correctDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Operazione completata");
        builder.setMessage("nome cambiato correttamente");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    public void errorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Errore");
        builder.setMessage("Il nome inserito è già stato preso. Sceglierne un altro.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}