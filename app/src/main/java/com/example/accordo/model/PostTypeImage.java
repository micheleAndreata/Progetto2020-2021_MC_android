package com.example.accordo.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

public class PostTypeImage extends Post {

    private Bitmap image;

    public PostTypeImage(@NonNull String pid, String uid, String name, int pVersion, Bitmap image) {
        super(pid, uid, name, pVersion);
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    @NonNull
    public String toString(){
        return getPid() + "\n" + getUid() + "\n" + getName() + "\n" + getUserPicture() + "\n" + getPVersion() + "\n" + getImage();
    }
}
