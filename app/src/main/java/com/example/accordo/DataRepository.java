package com.example.accordo;

import android.app.Application;
import android.content.Context;

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

    private Profile myProfile;

    DataRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        this.profileDao = db.profileDao();
        this.userPictureDao = db.userPictureDao();
        this.postImageDao = db.postImageDao();
        this.myProfile = profileDao.getProfile();
    }

    void getWall(Context context, final ResponseListener<JSONObject> listener){
        JSONObject jsonContent = new JSONObject();
        try {jsonContent.put("sid", myProfile.getSid());}
        catch (JSONException e) {e.printStackTrace();}
        NetworkManager.getInstance(context).getWall(jsonContent, result -> listener.getResult(result));
    }

    void getChannel(String ctitle, Context context, final ResponseListener<JSONArray> listener){

        JSONObject jsonGetChannel = new JSONObject();
        try {
            jsonGetChannel.put("sid", myProfile.getSid());
            jsonGetChannel.put("ctitle", ctitle);
        }
        catch (JSONException e) {e.printStackTrace();}

        NetworkManager.getInstance(context).getChannel(jsonGetChannel, getChannelResult -> {

            List<PostImage> dbPostImages = postImageDao.getPostImages();
            //espongo il PID per fare una ricerca piu efficiente
            HashMap<String,PostImage> dbImagesMap = new HashMap<>();
            for (PostImage i : dbPostImages) dbImagesMap.put(i.getPid(),i);

            try {
                JSONArray posts = getChannelResult.getJSONArray("posts");
                for (int i = 0; i < posts.length(); i++) {

                    AtomicInteger syncCount = new AtomicInteger(); //dovrebbe servire a gestire le 2 chiamate volley ma non so come

                    final JSONObject p = posts.getJSONObject(i);
                    String pid = p.getString("pid");
                    if (p.getString("type").equals("i")) {
                        if (dbImagesMap.containsKey(pid)) {
                            p.put("content", dbImagesMap.get(pid));
                            //posts = posts.put(i, p);
                        }
                        else {
                            //TODO scarica immagine, salvala su db e in posts
                            JSONObject jsonGetPostImage = new JSONObject();
                            jsonGetPostImage.put("sid", myProfile.getSid());
                            jsonGetPostImage.put("pid", pid);
                            NetworkManager.getInstance(context).getPostImage(jsonGetPostImage, getPostImageResult -> {
                                synchronized (this){
                                    try {
                                        p.put("content", getPostImageResult.getString("content"));
                                        syncCount.getAndIncrement(); //non so cosa sto facendo
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
                    //TODO aggiungi immagine di profilo utente a posts. se non su DB, scaricala
                }
                listener.getResult(posts);
            } catch (JSONException e) { e.printStackTrace(); }
        });
    }

}
