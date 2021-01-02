package com.example.accordo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accordo.database.PostImage;
import com.example.accordo.database.UserPicture;
import com.example.accordo.model.Model;
import com.example.accordo.model.Post;
import com.example.accordo.model.PostTypeImage;

import java.util.ArrayList;
import java.util.List;

public class ChannelActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ChannelActivity";

    private NetworkManager networkManager;
    private Model model;
    private ChannelAdapter channelAdapter;

    private Looper secondaryThreadLooper;

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
        String cTitle = intent.getStringExtra("cTitle");
        getChannel(cTitle);
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
                PostImage dbPostImage = dbPostImagesList.get(dbPostImagesList.indexOf(postImage));
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
}