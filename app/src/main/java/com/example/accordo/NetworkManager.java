package com.example.accordo;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class NetworkManager {

    private static final String LOG_TAG = "NetworkManager";

    private static NetworkManager instance = null;
    private static final String baseURL = "https://ewserver.di.unimi.it/mobicomp/accordo/";

    private RequestQueue queue;

    private NetworkManager(Context context){
        queue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized NetworkManager getInstance(Context context){
        if (instance == null)
            instance = new NetworkManager(context);
        return instance;
    }

    public void register(final NetworkResponseListener<String> listener){
        String registerURL = baseURL + "register.php";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                registerURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String sid;
                        try {
                            sid = response.getString("sid");
                            Log.d(LOG_TAG, "register response OK");
                            listener.getResult(sid);
                        } catch (JSONException e) {
                            Log.d(LOG_TAG, "JSONException: probably key is not correct");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "register error Response code: " + error.networkResponse.statusCode);
                        listener.getResult(null);
                    }
                }
        );
        queue.add(request);
    }

    public void getProfile(String sid, final NetworkResponseListener<JSONObject> listener){
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
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, "getProfile response OK");
                        listener.getResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "getProfile error Response code: " + error.networkResponse.statusCode);
                        listener.getResult(null);
                    }
                }
        );
        queue.add(request);
    }

    public void setProfile(JSONObject jsonContent, final NetworkResponseListener<Boolean> listener){
        String setProfileURL = baseURL + "setProfile.php";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                setProfileURL,
                jsonContent,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, "setProfile response OK");
                        listener.getResult(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "setProfile error Response code: " + error.networkResponse.statusCode);
                        listener.getResult(false);
                    }
                }
        );
        queue.add(request);
    }

    public void addChannel(JSONObject jsonContent, final NetworkResponseListener<Boolean> listener){
        String addChannelURL = baseURL + "addChannel.php";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                addChannelURL,
                jsonContent,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, "addChannel response OK");
                        listener.getResult(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "addChannel error Response code: " + error.networkResponse.statusCode);
                        listener.getResult(false);
                    }
                }
        );
        queue.add(request);
    }

    public void getWall(JSONObject jsonContent, final NetworkResponseListener<JSONObject> listener){
        String getWallURL = baseURL + "getWall.php";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                getWallURL,
                jsonContent,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, "getWall response OK");
                        listener.getResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "getWall error Response code: " + error.networkResponse.statusCode);
                        listener.getResult(null);
                    }
                }
        );
        queue.add(request);
    }

    public void addPost(JSONObject jsonContent, final NetworkResponseListener<Boolean> listener){
        String addPostURL = baseURL + "addPost.php";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                addPostURL,
                jsonContent,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, "addPost response OK");
                        listener.getResult(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "addPost error Response code: " + error.networkResponse.statusCode);
                        listener.getResult(false);
                    }
                }
        );
        queue.add(request);
    }

    public void getChannel(JSONObject jsonContent, final NetworkResponseListener<JSONObject> listener){
        String getChannelURL = baseURL + "getChannel.php";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                getChannelURL,
                jsonContent,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, "getChannel response OK");
                        listener.getResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "getChannel error Response code: " + error.networkResponse.statusCode);
                        listener.getResult(null);
                    }
                }
        );
        queue.add(request);
    }

    public void getPostImage(JSONObject jsonContent, final NetworkResponseListener<JSONObject> listener){
        String getPostImageURL = baseURL + "getPostImage.php";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                getPostImageURL,
                jsonContent,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, "getPostImage response OK");
                        listener.getResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "getPostImage error Response code: " + error.networkResponse.statusCode);
                        listener.getResult(null);
                    }
                }
        );
        queue.add(request);
    }

    public void getUserPicture(JSONObject jsonContent, final NetworkResponseListener<JSONObject> listener){
        String getUserPictureURL = baseURL + "getUserPicture.php";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                getUserPictureURL,
                jsonContent,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, "getUserPicture response OK");
                        listener.getResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "getUserPicture error Response code: " + error.networkResponse.statusCode);
                        listener.getResult(null);
                    }
                }
        );
        queue.add(request);
    }
}
