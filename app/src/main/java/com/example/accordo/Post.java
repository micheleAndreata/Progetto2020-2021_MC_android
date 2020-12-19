package com.example.accordo;

import org.json.JSONException;
import org.json.JSONObject;

public class Post {

    private String pid;
    private String type;
    private String content;
    private Double lat;
    private Double lon;
    private String uid;
    private String name;

    public Post(JSONObject jsonPost) throws JSONException {
        this.type = jsonPost.getString("type");
        this.pid = jsonPost.getString("pid");
        this.uid = jsonPost.getString("uid");
        this.name = jsonPost.getString("name");
        if (this.type.equals("l")){
            this.lat = jsonPost.getDouble("lat");
            this.lon = jsonPost.getDouble("lon");
        }
        else if (this.type.equals("t")){
            this.content = jsonPost.getString("content");
        }
        else {
            this.content = null;
        }
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getName() {
        return name;
    }

    public String getPid() {
        return pid;
    }

    public String getType() {
        return type;
    }

    public String getUid() {
        return uid;
    }
}
