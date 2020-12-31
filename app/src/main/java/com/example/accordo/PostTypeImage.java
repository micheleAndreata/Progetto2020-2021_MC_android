package com.example.accordo;

import androidx.annotation.NonNull;

public class PostTypeImage extends Post {

    private String image;

    PostTypeImage(@NonNull String pid, String uid, String name, int pVersion, String image) {
        super(pid, uid, name, pVersion);
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String toString(){
        return getPid() + "\n" + getUid() + "\n" + getName() + "\n" + getUserPicture() + "\n" + getPVersion() + "\n" + getImage();
    }
}
