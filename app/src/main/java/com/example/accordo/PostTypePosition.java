package com.example.accordo;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class PostTypePosition extends Post {

    private double lat;
    private double lon;


    PostTypePosition(@NonNull String pid, String uid, String name, int pVersion, double lat, double lon) {
        super(pid, uid, name, pVersion);
        this.lat = lat;
        this.lon = lon;
    }

    public double[] getLatLon(){
        return new double[]{lat, lon};
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setLatLon(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }

    public String toString(){
        return getPid() + "\n" + getUid() + "\n" + getName() + "\n" + getUserPicture() + "\n" + getPVersion() + "\n" + Arrays.toString(getLatLon());
    }
}
