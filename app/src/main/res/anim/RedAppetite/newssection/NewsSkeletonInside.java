package com.vadevelopment.RedAppetite.newssection;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vibrantappz on 9/14/2017.
 */

public class NewsSkeletonInside implements Parcelable {

    String nid;
    String headline;
    String date;

    public NewsSkeletonInside() {
    }

    protected NewsSkeletonInside(Parcel in) {
        nid = in.readString();
        headline = in.readString();
        date = in.readString();
        image = in.readString();
    }

    public static final Creator<com.vadevelopment.RedAppetite.newssection.NewsSkeletonInside> CREATOR = new Creator<com.vadevelopment.RedAppetite.newssection.NewsSkeletonInside>() {
        @Override
        public com.vadevelopment.RedAppetite.newssection.NewsSkeletonInside createFromParcel(Parcel in) {
            return new com.vadevelopment.RedAppetite.newssection.NewsSkeletonInside(in);
        }

        @Override
        public com.vadevelopment.RedAppetite.newssection.NewsSkeletonInside[] newArray(int size) {
            return new com.vadevelopment.RedAppetite.newssection.NewsSkeletonInside[size];
        }
    };

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String image;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nid);
        parcel.writeString(headline);
        parcel.writeString(date);
        parcel.writeString(image);
    }
}
