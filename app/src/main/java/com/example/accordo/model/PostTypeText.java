package com.example.accordo.model;

import androidx.annotation.NonNull;

public class PostTypeText extends Post {

    private String text;

    public PostTypeText(@NonNull String pid, String uid, String name, int pVersion, String text) {
        super(pid, uid, name, pVersion);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String toString(){
        return getPid() + "\n" + getUid() + "\n" + getName() + "\n" + getUserPicture() + "\n" + getPVersion() + "\n" + getText();
    }
}
