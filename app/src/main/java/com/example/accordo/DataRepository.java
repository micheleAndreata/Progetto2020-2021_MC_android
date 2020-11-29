package com.example.accordo;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class DataRepository {

    private ProfileDao profileDao;
    private UserPictureDao userPictureDao;
    private PostImageDao postImageDao;

    private LiveData<Profile> myProfile;
    private LiveData<List<PostImage>> dbPostImages;
    private LiveData<List<UserPicture>> dbUserPictures;

    DataRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        this.profileDao = db.profileDao();
        this.userPictureDao = db.userPictureDao();
        this.postImageDao = db.postImageDao();

        this.myProfile = profileDao.getProfile();
        this.dbPostImages = postImageDao.getPostImages();
        this.dbUserPictures = userPictureDao.getUserPictures();
    }

    void getWall(Context context, final ResponseListener<JSONObject> listener){
        JSONObject jsonContent = new JSONObject();
        try {jsonContent.put("sid", myProfile.getValue().getSid());}
        catch (JSONException e) {e.printStackTrace();}
        NetworkManager.getInstance(context).getWall(jsonContent, result -> listener.getResult(result));
    }

    void getChannel(String ctitle, Context context, final ResponseListener<JSONArray> listener){

        JSONObject jsonGetChannel = new JSONObject();
        try {
            jsonGetChannel.put("sid", myProfile.getValue().getSid());
            jsonGetChannel.put("ctitle", ctitle);
        }
        catch (JSONException e) {e.printStackTrace();}

        NetworkManager.getInstance(context).getChannel(jsonGetChannel, getChannelResult -> {

            //espongo il PID per fare una ricerca piu efficiente
            HashMap<String,PostImage> dbImagesMap = new HashMap<>();
            for (PostImage i : dbPostImages.getValue()) dbImagesMap.put(i.getPid(),i);

            HashMap<String,UserPicture> dbUserPicsMap = new HashMap<>();
            for (UserPicture i : dbUserPictures.getValue()) dbUserPicsMap.put(i.getUid(),i);

            try {
                JSONArray posts = getChannelResult.getJSONArray("posts");
                for (int i = 0; i < posts.length(); i++) {

                    AtomicInteger syncCount = new AtomicInteger(); //dovrebbe servire a gestire le 2 chiamate volley ma non so come

                    final JSONObject p = posts.getJSONObject(i);
                    String pid = p.getString("pid");
                    if (p.getString("type").equals("i")) {
                        if (dbImagesMap.containsKey(pid)) {
                            p.put("content", dbImagesMap.get(pid));
                            //TODO syncCount
                        }
                        else {
                            //TODO scarica immagine, salvala su db e in posts
                            JSONObject jsonGetPostImage = new JSONObject();
                            jsonGetPostImage.put("sid", myProfile.getValue().getSid());
                            jsonGetPostImage.put("pid", pid);
                            NetworkManager.getInstance(context).getPostImage(jsonGetPostImage, getPostImageResult -> {
                                synchronized (this){
                                    try {
                                        p.put("content", getPostImageResult.getString("content"));
                                        //TODO syncCount
                                    } catch (JSONException e) { e.printStackTrace(); }
                                }
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    try {
                                        postImageDao.insert(new PostImage(pid, getPostImageResult.getString("content")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                            });
                        }
                    }
                    else {
                        //TODO syncCount
                    }
                    String uid = p.getString("uid");
                    int serverPversion = Integer.parseInt(p.getString("pversion"));
                    //TODO aggiungi immagine di profilo utente a posts. se non su DB, scaricala
                    if (dbUserPicsMap.containsKey(uid)) {
                        int dbPversion = Integer.parseInt(dbUserPicsMap.get(uid).getPversion());
                        if (dbPversion < serverPversion) {
                            JSONObject jsonGetUserPicture = new JSONObject();
                            jsonGetUserPicture.put("sid", myProfile.getValue().getSid());
                            jsonGetUserPicture.put("uid", uid);
                            NetworkManager.getInstance(context).getUserPicture(jsonGetUserPicture, getUserPictureResult -> {
                                try {
                                    synchronized (this) {
                                        p.put("userPicture", getUserPictureResult.getString("picture"));
                                        //TODO syncCount
                                    }
                                } catch (JSONException e) { e.printStackTrace(); }
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    try {
                                        String pversion = getUserPictureResult.getString("pversion");
                                        String picture = getUserPictureResult.getString("picture");
                                        UserPicture user = new UserPicture(uid, pversion, picture);
                                        userPictureDao.update(user);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                            });
                        }
                        else {
                            String picture = dbUserPicsMap.get(uid).getPicture();
                            p.put("userPicture", picture);
                            //TODO syncCount
                        }
                    }
                    else {

                    }


                    //posts.put(i, p);
                }
                listener.getResult(posts);
            } catch (JSONException e) { e.printStackTrace(); }
        });
    }

}
