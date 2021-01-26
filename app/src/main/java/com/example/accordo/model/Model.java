package com.example.accordo.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.accordo.NetworkManager;
import com.example.accordo.database.AppDatabase;
import com.example.accordo.database.PostImage;
import com.example.accordo.database.PostImageDao;
import com.example.accordo.database.UserPictureDao;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private static Model instance;

    private List<Post> channel = new ArrayList<>();
    private List<String> myWall = new ArrayList<>();
    private List<String> notMyWall = new ArrayList<>();

    private final UserPictureDao userPictureDao;
    private final PostImageDao postImageDao;

    private Model(@NonNull Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.userPictureDao = db.userPictureDao();
        this.postImageDao = db.postImageDao();
    }

    public static Model getInstance(Application application){
        if (instance == null){
            instance = new Model(application);
        }
        return instance;
    }

    public PostImageDao getPostImageDao() {
        return postImageDao;
    }

    public UserPictureDao getUserPictureDao() {
        return userPictureDao;
    }

    public List<String> getMyWall() {
        return myWall;
    }

    public List<String> getNotMyWall() {
        return notMyWall;
    }

    public void setMyWall(List<String> myWall) {
        this.myWall = myWall;
    }

    public void setNotMyWall(List<String> notMyWall) {
        this.notMyWall = notMyWall;
    }

    public synchronized int getChannelSize(){
        return channel.size();
    }

    public synchronized Post getPost(int index) {
        return channel.get(index);
    }

    public synchronized void setChannel(List<Post> channel) {
        this.channel = channel;
    }

    public synchronized void insertUserPicture(String uid, int pVersion, Bitmap bitmapPicture) {
        for (int i=0; i < channel.size(); i++){
            Post post = channel.get(i);
            if (post.getUid().equals(uid)){
                post.setUserPicture(bitmapPicture);
                post.setPVersion(pVersion);
                channel.set(i, post);
            }
        }
    }

    public synchronized void insertPostImage(String pid, Bitmap bitmapImage){
        for (int i=0; i < channel.size(); i++){
            Post post = channel.get(i);
            if (post.getPid().equals(pid)){
                ((PostTypeImage) post).setImage(bitmapImage);
                channel.set(i, post);
            }
        }
    }
}
