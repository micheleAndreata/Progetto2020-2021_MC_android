package com.example.accordo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accordo.database.PostImage;
import com.example.accordo.database.UserPicture;
import com.example.accordo.model.Model;
import com.example.accordo.model.Post;
import com.example.accordo.model.PostTypeImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChannelActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ChannelActivity";

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 100;

    private static final int PICK_IMAGE = 200;

    private NetworkManager networkManager;
    private Model model;
    private ChannelAdapter channelAdapter;

    private Looper secondaryThreadLooper;

    private String cTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        HandlerThread handlerThread = new HandlerThread("ChannelHandlerThread");
        handlerThread.start();
        secondaryThreadLooper = handlerThread.getLooper();

        networkManager = NetworkManager.getInstance(this);

        model = Model.getInstance(getApplication());

        RecyclerView channelRecyclerView = findViewById(R.id.recyclerview);
        channelAdapter = new ChannelAdapter(this, model);
        channelRecyclerView.setAdapter(channelAdapter);
        channelRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        cTitle = intent.getStringExtra("cTitle");
        setTitle(cTitle);
        getChannel(cTitle);
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
        }
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
                            error -> Log.d(LOG_TAG, "ERRORE chiamata addPost")
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
                error -> Log.d(LOG_TAG, "ERRORE chiamata server addPost"));
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

    private static Bitmap reduceQuality(Bitmap bm) {
        int maxWidth = 300;
        int maxHeight = 300;
        int quality = 30;
        int width = bm.getWidth();
        int height = bm.getHeight();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        Bitmap compressed = BitmapFactory.decodeStream(new ByteArrayInputStream(outputStream.toByteArray()));

        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int)(height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int)(width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        return Bitmap.createScaledBitmap(compressed, width, height, true);
    }
}