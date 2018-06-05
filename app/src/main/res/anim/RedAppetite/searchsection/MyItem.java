package com.vadevelopment.RedAppetite.searchsection;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by vibrantappz on 9/12/2017.
 */

public class MyItem implements ClusterItem {

    private final LatLng mPosition;
    String b_name;
    String b_address;
    String avg_rating;
    String distance;

    public String getVisiteddate() {
        return visiteddate;
    }

    public void setVisiteddate(String visiteddate) {
        this.visiteddate = visiteddate;
    }

    String visiteddate;

    public String getIsreviewd() {
        return isreviewd;
    }

    public void setIsreviewd(String isreviewd) {
        this.isreviewd = isreviewd;
    }

    String isreviewd;

    public String getIsfvrt() {
        return isfvrt;
    }

    public void setIsfvrt(String isfvrt) {
        this.isfvrt = isfvrt;
    }

    String isfvrt;
    String total;
    String type;

    public String getFreeimage() {
        return freeimage;
    }

    public void setFreeimage(String freeimage) {
        this.freeimage = freeimage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPremiumimage() {
        return premiumimage;
    }

    public void setPremiumimage(String premiumimage) {
        this.premiumimage = premiumimage;
    }

    String freeimage;
    String premiumimage;

    public String getLid() {
        return Lid;
    }

    public void setLid(String lid) {
        Lid = lid;
    }

    String Lid;

    public MyItem(String visiteddate, String isreviewd, String isfvrt, String Lid, double lat, double lng, String b_name,
                  String b_address, String avg_rating, String distance, String like, String dislike, String total,
                  String type, String freeimage, String premiumimage) {
        mPosition = new LatLng(lat, lng);
        this.visiteddate = visiteddate;
        this.isreviewd = isreviewd;
        this.isfvrt = isfvrt;
        this.b_name = b_name;
        this.b_address = b_address;
        this.avg_rating = avg_rating;
        this.distance = distance;
        this.like = like;
        this.dislike = dislike;
        this.Lid = Lid;
        this.total = total;
        this.type = type;
        this.freeimage = freeimage;
        this.premiumimage = premiumimage;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getB_name() {
        return b_name;
    }

    public void setB_name(String b_name) {
        this.b_name = b_name;
    }

    public String getB_address() {
        return b_address;
    }

    public void setB_address(String b_address) {
        this.b_address = b_address;
    }

    public String getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(String avg_rating) {
        this.avg_rating = avg_rating;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDislike() {
        return dislike;
    }

    public void setDislike(String dislike) {
        this.dislike = dislike;
    }

    String like;
    String dislike;

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
