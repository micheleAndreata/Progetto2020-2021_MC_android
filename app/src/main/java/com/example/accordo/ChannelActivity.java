package com.example.accordo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accordo.database.PostImage;
import com.example.accordo.database.UserPicture;
import com.example.accordo.model.Model;
import com.example.accordo.model.Post;
import com.example.accordo.model.PostTypeImage;
import com.example.accordo.model.PostTypePosition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChannelActivity extends AppCompatActivity implements OnRecyclerViewClickListener {

    private static final String LOG_TAG = "ChannelActivity";
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 100;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 101;
    private static final int PICK_IMAGE = 200;

    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean requestingLocationUpdates;

    private NetworkManager networkManager;
    private Model model;
    private ChannelAdapter channelAdapter;

    private Looper secondaryThreadLooper;

    private String cTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        requestingLocationUpdates = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null)
                    Log.d(LOG_TAG, "error in finding location");
            }
        };

        HandlerThread handlerThread = new HandlerThread("ChannelHandlerThread");
        handlerThread.start();
        secondaryThreadLooper = handlerThread.getLooper();

        networkManager = NetworkManager.getInstance(this);
        model = Model.getInstance(getApplication());

        RecyclerView channelRecyclerView = findViewById(R.id.recyclerview);
        channelAdapter = new ChannelAdapter(this, model, this);
        channelRecyclerView.setAdapter(channelAdapter);
        channelRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        cTitle = intent.getStringExtra("cTitle");
        setTitle(cTitle);
        getChannel(cTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            Log.d(LOG_TAG, "starting location updates");
            startLocationUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
                Looper.getMainLooper());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "Permission granted");
                    findImage();
                } else {
                    Log.d(LOG_TAG, "Permission NOT granted");
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Attenzione");
                    builder.setMessage("Per usufruire di questa caratteristica è necessario il permesso alla memoria del telefono");
                    builder.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
                    builder.show();
                }
                break;
            case REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "Permission granted");
                    startLocationUpdates();
                    findAndSendPosition();
                } else {
                    Log.d(LOG_TAG, "Permission NOT granted");
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Attenzione");
                    builder.setMessage("Per usufruire di questa caratteristica è necessario il permesso alla posizione del telefono");
                    builder.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
                    builder.show();
                }
                break;
        }
    }

    public void onSendPositionClick(View v){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            findAndSendPosition();
        }
    }

    @SuppressLint("MissingPermission")
    public void findAndSendPosition(){
        fusedLocationClient.getLastLocation().addOnSuccessListener(
                this,
                location -> {
                    if (location != null) {
                        Log.d(LOG_TAG, "Last known location: LAT = " + location.getLatitude() + ", LON = " + location.getLongitude());
                        networkManager.addPost(
                                cTitle, "l", null, location.getLatitude(), location.getLongitude(),
                                response -> getChannel(cTitle),
                                error -> Log.d(LOG_TAG, "ERRORE chiamata server addPost (location)")
                        );
                    } else {
                        Log.d(LOG_TAG, "Last Known location not available");
                    }
                });
    }

    public void onSendImageClick(View v){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    REQUEST_READ_EXTERNAL_STORAGE);
        }
        else {
            findImage();
        }
    }

    public void findImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && data != null) {
            Uri imageUri = data.getData();
            (new Thread(() -> {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                } catch (IOException e) { e.printStackTrace(); }
                if (bitmap != null){
                    Bitmap lowQuality = reduceQuality(bitmap);
                    String base64 = bitmapToBase64(lowQuality);
                    networkManager.addPost(
                            cTitle, "i", base64, null, null,
                            result -> getChannel(cTitle),
                            error -> Log.d(LOG_TAG, "ERRORE chiamata server addPost (image)")
                    );
                }
            })).start();
        }
    }

    public void onSendTextClick(View v){
        EditText inputTextView = findViewById(R.id.inputText);
        String text = inputTextView.getText().toString();
        networkManager.addPost(cTitle, "t", text, null, null,
                response -> {
                    if (response) {
                        getChannel(cTitle);
                        inputTextView.setText("");
                    }
                },
                error -> Log.d(LOG_TAG, "ERRORE chiamata server addPost (text)"));
    }

    public void getChannel(String cTitle){
        networkManager.getChannel(
                cTitle,
                channel -> {
                    model.setChannel(channel);
                    channelAdapter.notifyDataSetChanged();
                    Handler handler = new Handler(secondaryThreadLooper);
                    handler.post(() -> {
                        updateUserPictures(channel);
                        updatePostImages(channel);
                    });
                },
                error -> Log.d(LOG_TAG, "ERRORE chiamata server getChannel"));
    }

    public void updateUserPictures(List<Post> postsFromServer){
        Handler mainHandler = new Handler(this.getMainLooper());

        List<UserPicture> usersSet = new ArrayList<>();
        for (Post post : postsFromServer){
            UserPicture user = new UserPicture(post.getUid(), post.getPVersion(), null);
            if (!usersSet.contains(user)){
                usersSet.add(user);
            }
        }

        List<UserPicture> dbUserPictureList = model.getUserPictureDao().getUserPictures();

        for (int i=0; i < usersSet.size(); i++){
            UserPicture serverUser = usersSet.get(i);
            if (dbUserPictureList.contains(serverUser)){
                UserPicture dbUser = dbUserPictureList.get(dbUserPictureList.indexOf(serverUser));
                if (dbUser.getPversion() < serverUser.getPversion()){
                    downloadAndUpdateUserPicture(serverUser.getUid());
                }
                else {
                    model.insertUserPicture(dbUser.getUid(), dbUser.getPversion(), dbUser.getPicture());
                    mainHandler.post(() -> channelAdapter.notifyDataSetChanged());
                }
            }
            else {
                downloadAndInsertUserPicture(serverUser.getUid());
            }
        }
    }

    public void downloadAndInsertUserPicture(String uid){
        networkManager.getUserPicture(
                uid,
                userPicture -> {
                    model.insertUserPicture(userPicture.getUid(), userPicture.getPversion(), userPicture.getPicture());
                    channelAdapter.notifyDataSetChanged();
                    //Aggiorno dati su DB
                    Handler handler = new Handler(secondaryThreadLooper);
                    handler.post(() -> model.getUserPictureDao().insert(userPicture));
                }, error -> Log.d(LOG_TAG, "ERRORE chiamata server getUserPicture"));
    }

    public void downloadAndUpdateUserPicture(String uid){
        networkManager.getUserPicture(
                uid,
                userPicture -> {
                    model.insertUserPicture(userPicture.getUid(), userPicture.getPversion(), userPicture.getPicture());
                    channelAdapter.notifyDataSetChanged();
                    //Aggiorno dati su DB
                    Handler handler = new Handler(secondaryThreadLooper);
                    handler.post(() -> model.getUserPictureDao().update(userPicture));
                }, error -> Log.d(LOG_TAG, "ERRORE chiamata server getUserPicture"));
    }

    public void updatePostImages(List<Post> postsFromServer){
        Handler mainHandler = new Handler(this.getMainLooper());

        List<PostImage> postImageList = new ArrayList<>();
        for (Post post : postsFromServer){
            if (post instanceof PostTypeImage){
                postImageList.add(new PostImage(post.getPid(), ""));
            }
        }

        List<PostImage> dbPostImagesList = model.getPostImageDao().getPostImages();
        for (PostImage postImage : postImageList){
            if (dbPostImagesList.contains(postImage)){
                //aggiungo immagine nella lista di post del model
                int index = dbPostImagesList.indexOf(postImage);
                PostImage dbPostImage = dbPostImagesList.get(index);
                model.insertPostImage(dbPostImage);
                //notifico adapter
                mainHandler.post(() -> channelAdapter.notifyDataSetChanged());
            }
            else {
                //scarico immagine da server
                networkManager.getPostImage(postImage.getPid(),
                        newPostImage -> {
                            //aggiorno immagine nella lista di post del model
                            model.insertPostImage(newPostImage);
                            //notifico adapter
                            channelAdapter.notifyDataSetChanged();
                            //salvo dati su DB
                            Handler handler = new Handler(secondaryThreadLooper);
                            handler.post(() -> model.getPostImageDao().insert(newPostImage));
                        }, error -> Log.d(LOG_TAG, "ERRORE chiamata server getPostImage"));
            }
        }
    }

    public String bitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public Bitmap reduceQuality(Bitmap bm) {
        int maxWidth = 300;
        int maxHeight = 300;
        int quality = 30;
        int width = bm.getWidth();
        int height = bm.getHeight();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        Bitmap compressed = BitmapFactory.decodeStream(new ByteArrayInputStream(outputStream.toByteArray()));

        if (width > height) {
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int)(height / ratio);
        } else if (height > width) {
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int)(width / ratio);
        } else {
            height = maxHeight;
            width = maxWidth;
        }
        return Bitmap.createScaledBitmap(compressed, width, height, true);
    }

    @Override
    public void onRecyclerViewClick(View v, int position) {
        //codice per visualizzare mappa
        findViewById(R.id.map_cardview).setVisibility(View.VISIBLE);
        PostTypePosition post = (PostTypePosition) model.getPost(position);
        double[] latLon = post.getLatLon();

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        MapboxMapOptions options = MapboxMapOptions.createFromAttributes(this, null);
        options.camera(new CameraPosition.Builder()
                .target(new LatLng(latLon[0], latLon[1]))
                .zoom(9)
                .build());
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        transaction.add(R.id.map_container, mapFragment, "com.mapbox.map");
        transaction.addToBackStack(null);
        transaction.commit();

        if (mapFragment != null) {
            mapFragment.getMapAsync(mapboxMap ->
                    mapboxMap.setStyle(Style.LIGHT, style -> {
                        MapView mapView = (MapView) mapFragment.getView();
                        SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);

                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setIconIgnorePlacement(true);

                        style.addImage("MARKER_ID", ContextCompat.getDrawable(this, R.drawable.mapbox_marker_icon_default));

                        symbolManager.create(new SymbolOptions()
                                .withLatLng(new LatLng(latLon[0], latLon[1]))
                                .withIconImage("MARKER_ID")
                                .withIconSize(2.0f));

                    }));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        findViewById(R.id.map_cardview).setVisibility(View.GONE);
    }
}