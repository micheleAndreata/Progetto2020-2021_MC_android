package com.example.accordo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.accordo.database.PostImage;
import com.example.accordo.database.UserPicture;
import com.example.accordo.model.Post;
import com.example.accordo.model.PostTypeImage;
import com.example.accordo.model.PostTypePosition;
import com.example.accordo.model.PostTypeText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NetworkManager {

    private static final String LOG_TAG = "NetworkManager";

    private static NetworkManager instance = null;
    private static final String baseURL = "https://ewserver.di.unimi.it/mobicomp/accordo/";

    private final RequestQueue queue;
    private final SharedPreferences profile;
    private final String alternativeSid = null;

    private NetworkManager(Context context){
        queue = Volley.newRequestQueue(context.getApplicationContext());
        profile = context.getApplicationContext().getSharedPreferences("profile_data", Context.MODE_PRIVATE);
    }

    public static synchronized NetworkManager getInstance(Context context){
        if (instance == null)
            instance = new NetworkManager(context);
        return instance;
    }

    public void register(final ResponseListener<String> listener, Response.ErrorListener errorListener){
        String registerURL = baseURL + "register.php";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                registerURL,
                null,
                response -> {
                    String sid;
                    try {
                        sid = response.getString("sid");
                        Log.d(LOG_TAG, "register response OK");
                        listener.getResult(sid);
                    } catch (JSONException e) {
                        Log.d(LOG_TAG, "JSONException: probably key is not correct");
                        e.printStackTrace();
                    }
                },
                errorListener
        );
        queue.add(request);
    }

    public void getProfile(String sid, final ResponseListener<JSONObject> listener, Response.ErrorListener errorListener){
        String getProfileURL = baseURL + "getProfile.php";

        JSONObject jsonContent = new JSONObject();
        try {
            jsonContent.put("sid", sid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                getProfileURL,
                jsonContent,
                response -> {
                    Log.d(LOG_TAG, "getProfile response OK");
                    listener.getResult(response);
                },
                errorListener
        );
        queue.add(request);
    }

    public void setProfile(String newName, String newPicture, final ResponseListener<Boolean> listener, Response.ErrorListener errorListener){
        String setProfileURL = baseURL + "setProfile.php";

        JSONObject jsonContent = new JSONObject();
        try {
            jsonContent.put("sid", profile.getString("sid", alternativeSid));
        } catch (JSONException e) { e.printStackTrace(); }
        if (newName != null){
            try {
                jsonContent.put("name", newName);
            } catch (JSONException e) { e.printStackTrace(); }
        }
        if (newPicture != null){
            try {
                jsonContent.put("picture", newPicture);
            } catch (JSONException e) { e.printStackTrace(); }
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                setProfileURL,
                jsonContent,
                response -> {
                    Log.d(LOG_TAG, "setProfile response OK");
                    listener.getResult(true);
                },
                errorListener
        );
        queue.add(request);
    }

    public void addChannel(String ctitle, final ResponseListener<Boolean> listener, Response.ErrorListener errorListener){
        String addChannelURL = baseURL + "addChannel.php";

        JSONObject jsonContent = new JSONObject();
        try {
            jsonContent.put("sid", profile.getString("sid", alternativeSid));
        } catch (JSONException e) { e.printStackTrace(); }
        try {
            jsonContent.put("ctitle", ctitle);
        } catch (JSONException e) { e.printStackTrace(); }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                addChannelURL,
                jsonContent,
                response -> {
                    Log.d(LOG_TAG, "addChannel response OK");
                    listener.getResult(true);
                },
                errorListener
        );
        queue.add(request);
    }

    public void getWall(final ResponseListener<List<JSONObject>> listener, Response.ErrorListener errorListener){
        String getWallURL = baseURL + "getWall.php";
        JSONObject jsonContent = new JSONObject();
        try {
            jsonContent.put("sid", profile.getString("sid", alternativeSid));
        } catch (JSONException e) { e.printStackTrace(); }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                getWallURL,
                jsonContent,
                response -> {
                    Log.d(LOG_TAG, "getWall response OK");

                    try {
                        JSONArray jsonWall = response.getJSONArray("channels");
                        List<JSONObject> wall = new ArrayList<>();
                        for (int i=0; i < jsonWall.length(); i++){
                            wall.add(jsonWall.getJSONObject(i));
                        }
                        listener.getResult(wall);
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                errorListener
        );
        queue.add(request);
    }

    public void addPost(String ctitle, String type, String content, String lat, String lon, final ResponseListener<Boolean> listener,
                        Response.ErrorListener errorListener){
        String addPostURL = baseURL + "addPost.php";

        if (type.equals("l") && (lat == null || lon == null)){
            Log.d(LOG_TAG, "ERRORE: post di tipo posizione ha lat o lon nulli!");
        }
        if ((type.equals("i") || type.equals("t")) && content == null){
            Log.d(LOG_TAG, "ERRORE: post di tipo immagine o testo ha content nullo!");
        }

        JSONObject jsonContent = new JSONObject();
        try {
            jsonContent.put("sid", profile.getString("sid", alternativeSid));
            jsonContent.put("ctitle", ctitle);
            jsonContent.put("type", type);
        } catch (JSONException e) { e.printStackTrace(); }

        if (content != null){
            try {
                jsonContent.put("content", content);
            } catch (JSONException e) { e.printStackTrace(); }
        }

        if (lat != null && lon != null){
            try {
                jsonContent.put("lat", lat);
                jsonContent.put("lon", lon);
            } catch (JSONException e) { e.printStackTrace(); }
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                addPostURL,
                jsonContent,
                response -> {
                    Log.d(LOG_TAG, "addPost response OK");
                    listener.getResult(true);
                },
                errorListener
        );
        queue.add(request);
    }

    public void getChannel(String ctitle, final ResponseListener<List<Post>> listener, Response.ErrorListener errorListener){
        String getChannelURL = baseURL + "getChannel.php";

        JSONObject jsonContent = new JSONObject();
        try {
            jsonContent.put("sid", profile.getString("sid", alternativeSid));
            jsonContent.put("ctitle", ctitle);
        } catch (JSONException e) { e.printStackTrace(); }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                getChannelURL,
                jsonContent,
                response -> {
                    Log.d(LOG_TAG, "getChannel response OK");
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = response.getJSONArray("posts");
                    } catch (JSONException e) { e.printStackTrace(); }
                    List<Post> postList = jsonArrayToListOfPosts(jsonArray);
                    listener.getResult(postList);
                },
                errorListener
        );
        queue.add(request);
    }

    public void getPostImage(String pid, final ResponseListener<PostImage> listener, Response.ErrorListener errorListener){
        String getPostImageURL = baseURL + "getPostImage.php";

        JSONObject jsonContent = new JSONObject();
        try {
            jsonContent.put("sid", profile.getString("sid", alternativeSid));
            jsonContent.put("pid", pid);
        } catch (JSONException e) { e.printStackTrace(); }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                getPostImageURL,
                jsonContent,
                response -> {
                    Log.d(LOG_TAG, "getPostImage response OK");
                    try {
                        String picture = response.getString("content");
                        listener.getResult(new PostImage(pid, picture));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                errorListener
        );
        queue.add(request);
    }

    public void getUserPicture(String uid, final ResponseListener<UserPicture> listener, Response.ErrorListener errorListener){
        String getUserPictureURL = baseURL + "getUserPicture.php";

        JSONObject jsonContent = new JSONObject();
        try {
            jsonContent.put("sid", profile.getString("sid", alternativeSid));
            jsonContent.put("uid", uid);
        } catch (JSONException e) { e.printStackTrace(); }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                getUserPictureURL,
                jsonContent,
                response -> {
                    Log.d(LOG_TAG, "getUserPicture response OK");
                    try {
                        int pversion = response.getInt("pversion");
                        String picture = response.getString("picture");
                        listener.getResult(new UserPicture(uid, pversion, picture));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                errorListener
        );
        queue.add(request);
    }


    private static List<Post> jsonArrayToListOfPosts(JSONArray jsonArray){
        List<Post> postList = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonPost = jsonArray.getJSONObject(i);
                String type = jsonPost.getString("type");
                String pid = jsonPost.getString("pid");
                String uid = jsonPost.getString("uid");
                String name = jsonPost.getString("name");
                int pVersion = jsonPost.getInt("pversion");
                Post post;
                switch (type){
                    case "t":
                        String text = jsonPost.getString("content");
                        post = new PostTypeText(pid, uid, name, pVersion, text);
                        postList.add(post);
                        break;
                    case "i":
                        post = new PostTypeImage(pid, uid, name, pVersion, null);
                        postList.add(post);
                        break;
                    case "l":
                        double lat = jsonPost.getDouble("lat");
                        double lon = jsonPost.getDouble("lon");
                        post = new PostTypePosition(pid, uid, name, pVersion, lat, lon);
                        postList.add(post);
                        break;
                }
            } catch (JSONException e) {e.printStackTrace();}
        }
        return postList;
    }
}
