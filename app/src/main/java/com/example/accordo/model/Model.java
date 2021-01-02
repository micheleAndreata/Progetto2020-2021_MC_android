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

    private NetworkManager networkManager;

    private SharedPreferences profile;

    private List<Post> channel = new ArrayList<>();
    private List<String> myWall = new ArrayList<>();
    private List<String> notMyWall = new ArrayList<>();

    private UserPictureDao userPictureDao;
    private PostImageDao postImageDao;

    private Model(@NonNull Application application) {
        this.networkManager = NetworkManager.getInstance(application);

        AppDatabase db = AppDatabase.getDatabase(application);
        this.userPictureDao = db.userPictureDao();
        this.postImageDao = db.postImageDao();

        profile = application.getSharedPreferences("profile_data", Context.MODE_PRIVATE);
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

    public synchronized void setPost(int index, Post post){
        channel.set(index, post);
    }

    public synchronized void insertUserPicture(String uid, int pVersion, String base64Picture) {
        Bitmap bitmapPicture;
        try {
            if (base64Picture != null){
                byte[] decodedPicture = Base64.decode(base64Picture, Base64.DEFAULT);
                bitmapPicture = BitmapFactory.decodeByteArray(decodedPicture, 0, decodedPicture.length);
            }
            else
                bitmapPicture = null;
        }
        catch (IllegalArgumentException e) {
            bitmapPicture = null;
        }

        for (int i=0; i < channel.size(); i++){
            Post post = channel.get(i);
            if (post.getUid().equals(uid)){
                post.setUserPicture(bitmapPicture);
                post.setPVersion(pVersion);
                channel.set(i, post);
            }
        }
    }

    public synchronized void insertPostImage(PostImage postImage){
        Bitmap bitmapImage;
        try {
            byte[] decodedImage = Base64.decode(postImage.getImage(), Base64.DEFAULT);
            bitmapImage = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
        }
        catch (IllegalArgumentException e) {
            bitmapImage = null;
        }

        for (int i=0; i < channel.size(); i++){
            Post post = channel.get(i);
            if (post.getPid().equals(postImage.getPid())){
                ((PostTypeImage) post).setImage(bitmapImage);
                channel.set(i, post);
            }
        }
    }
}
