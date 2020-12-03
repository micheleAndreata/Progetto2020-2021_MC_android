package com.example.accordo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


public class DataRepository {

    private UserPictureDao userPictureDao;
    private PostImageDao postImageDao;

    private LiveData<List<PostImage>> dbPostImages;
    private LiveData<List<UserPicture>> dbUserPictures;

    private SharedPreferences profile;

    DataRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        this.userPictureDao = db.userPictureDao();
        this.postImageDao = db.postImageDao();

        this.dbPostImages = postImageDao.getLivePostImages();
        this.dbUserPictures = userPictureDao.getLiveUserPictures();

        //TODO che succede se ho più istanze diverse di DataRepository?
        profile = application.getSharedPreferences("profile_data", Context.MODE_PRIVATE);
    }

    public LiveData<List<PostImage>> getLiveDbPostImages() {
        return dbPostImages;
    }

    public LiveData<List<UserPicture>> getLiveDbUserPictures() {
        return dbUserPictures;
    }

    public void updateDbPostImages(Context ctx, JSONObject channelPostsFromServer) throws JSONException {
        JSONArray posts = channelPostsFromServer.getJSONArray("posts");
        List<String> serverPostImages = new ArrayList<>();
        for (int i=0; i < posts.length(); i++){
            JSONObject p = posts.getJSONObject(i);
            if (p.getString("type").equals("i")){
                serverPostImages.add(p.getString("pid"));
            }
        }
        if (serverPostImages.size() != 0){
            AppDatabase.databaseWriteExecutor.execute(() -> {
                List<PostImage> dbPostImages = postImageDao.getPostImages();
                Map<String,PostImage> dbImagesHash = new HashMap<>();
                for (PostImage i : dbPostImages)
                    dbImagesHash.put(i.getPid(),i);

                for (String pid : serverPostImages){
                    if (!dbImagesHash.containsKey(pid)){
                        JSONObject jsonGetPostImage = new JSONObject();
                        try {
                            jsonGetPostImage.put("sid", profile.getString("sid", null));
                            jsonGetPostImage.put("pid", pid);
                        } catch (JSONException e) { e.printStackTrace(); }

                        NetworkManager.getInstance(ctx).getPostImage(jsonGetPostImage, result -> {
                            if (result != null){
                                try {
                                    String picture = result.getString("content");
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        postImageDao.insert(new PostImage(pid, picture));
                                    });
                                } catch (JSONException e) { e.printStackTrace(); }
                            }
                            else {
                                Log.d("DataRepository", "error chiamata server getPostImage");
                            }
                        });
                    }
                }
            });
        }
    }

    void updateDbUserPictures(Context ctx, JSONObject channelPostsFromServer) throws JSONException {
        JSONArray serverPosts = channelPostsFromServer.getJSONArray("posts");
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<UserPicture> dbUserPictures = userPictureDao.getUserPictures();
            Map<String, UserPicture> dbUserPicsHash = new HashMap<>();
            for (UserPicture p : dbUserPictures)
                dbUserPicsHash.put(p.getUid(), p);
            try {
                for (int i = 0; i < serverPosts.length(); i++) {
                    JSONObject p = serverPosts.getJSONObject(i);
                    String uid = p.getString("uid");

                    JSONObject jsonGetUserPicture = new JSONObject();
                    jsonGetUserPicture.put("sid", profile.getString("sid", null));
                    jsonGetUserPicture.put("uid", uid);

                    if (dbUserPicsHash.containsKey(uid)){
                        int serverPversion = Integer.parseInt(p.getString("pversion"));
                        int dbPversion = Integer.parseInt(dbUserPicsHash.get(uid).getPversion());
                        if (dbPversion < serverPversion) {
                            NetworkManager.getInstance(ctx).getUserPicture(jsonGetUserPicture, result -> {
                                try {
                                    String pversion = result.getString("pversion");
                                    String picture = result.getString("picture");
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        userPictureDao.update(new UserPicture(uid, pversion, picture));
                                    });
                                } catch (JSONException e) { e.printStackTrace(); }
                            });
                        }
                    }
                    else {
                        NetworkManager.getInstance(ctx).getUserPicture(jsonGetUserPicture, result -> {
                            try {
                                String pversion = result.getString("pversion");
                                String picture = result.getString("picture");
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    userPictureDao.insert(new UserPicture(uid, pversion, picture));
                                });
                            } catch (JSONException e) { e.printStackTrace(); }
                        });
                    }
                }
            } catch (JSONException e) { e.printStackTrace(); }
        });
    }

    void register(Context ctx){
        if (profile.getString("sid", null) == null) {
            NetworkManager.getInstance(ctx).register(result -> {
                SharedPreferences.Editor editor = profile.edit();
                editor.putString("sid", result);
                editor.apply();
                //TODO nuova registrazione
                Log.d("DataRepository", "nuova registrazione completata");
            });
        }
        else {
            Log.d("DataRepository", "utente gia registrato");
        }
    }

    void getChannel(Context ctx, String ctitle, final ResponseListener<JSONObject> listener){
        JSONObject jsonGetChannel = new JSONObject();
        try {
            jsonGetChannel.put("sid", profile.getString("sid", null));
            jsonGetChannel.put("ctitle", ctitle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkManager.getInstance(ctx).getChannel(jsonGetChannel, listener);
    }

    void getWall(Context context, final ResponseListener<JSONObject> listener){
        JSONObject jsonContent = new JSONObject();
        try {jsonContent.put("sid", profile.getString("sid", null));}
        catch (JSONException e) {e.printStackTrace();}
        NetworkManager.getInstance(context).getWall(jsonContent, listener);
    }

    /*
    void setProfile(String sid, String uid, String name, String picture, String pversion){
        SharedPreferences.Editor editor = profile.edit();
        editor.putString("sid", sid);
        editor.putString("uid", uid);
        editor.putString("name", name);
        editor.putString("picture", picture);
        editor.putString("pversion", pversion);
        editor.apply();
    }
     */

}
