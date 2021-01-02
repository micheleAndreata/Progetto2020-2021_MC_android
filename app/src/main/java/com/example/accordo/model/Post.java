package com.example.accordo.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.Objects;

public abstract class Post {

    private String pid;
    private String uid;
    private String name;
    private int pVersion;
    private Bitmap userPicture = null;

    public Post(@NonNull String pid, String uid, String name, int pVersion){
        this.pid = pid;
        this.uid = uid;
        this.name = name;
        this.pVersion = pVersion;
    }

    public void setUserPicture(Bitmap userPicture) {
        this.userPicture = userPicture;
    }

    public Bitmap getUserPicture() {
        return userPicture;
    }

    public int getPVersion() {
        return pVersion;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPid() {
        return pid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setPVersion(int pVersion) {
        this.pVersion = pVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return pid.equals(post.pid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid);
    }
}
