package com.vadevelopment.RedAppetite.searchsection;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vibrantappz on 8/10/2017.
 */

public class Skeleton_SearchFirst implements Parcelable{
    String buildingname;
    String address;
    String distance;
    String isreviewed;
    String latitude;

    public Skeleton_SearchFirst(){

    }

    protected Skeleton_SearchFirst(Parcel in) {
        buildingname = in.readString();
        address = in.readString();
        distance = in.readString();
        isreviewed = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        heading = in.readString();
        ind_rating = in.readString();
        recommendation = in.readString();
        rvid = in.readString();
        isfav = in.readString();
        lid = in.readString();
        avgrating = in.readString();
        sad = in.readString();
        happy = in.readString();
        total = in.readString();
        type = in.readString();
        typeone = in.readString();
        freeimage = in.readString();
        premiumimage = in.readString();
        visited_date = in.readString();
    }

    public static final Creator<com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst> CREATOR = new Creator<com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst>() {
        @Override
        public com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst createFromParcel(Parcel in) {
            return new com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst(in);
        }

        @Override
        public com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst[] newArray(int size) {
            return new com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst[size];
        }
    };

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    String longitude;

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    String heading;

    public String getInd_rating() {
        return ind_rating;
    }

    public void setInd_rating(String ind_rating) {
        this.ind_rating = ind_rating;
    }

    String ind_rating;

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    String recommendation;

    public String getRvid() {
        return rvid;
    }

    public void setRvid(String rvid) {
        this.rvid = rvid;
    }

    String rvid;
    String isfav;

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    String lid;

    public String getAvgrating() {
        return avgrating;
    }

    public void setAvgrating(String avgrating) {
        this.avgrating = avgrating;
    }

    public String getBuildingname() {
        return buildingname;
    }

    public void setBuildingname(String buildingname) {
        this.buildingname = buildingname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getIsreviewed() {
        return isreviewed;
    }

    public void setIsreviewed(String isreviewed) {
        this.isreviewed = isreviewed;
    }

    public String getIsfav() {
        return isfav;
    }

    public void setIsfav(String isfav) {
        this.isfav = isfav;
    }

    public String getSad() {
        return sad;
    }

    public void setSad(String sad) {
        this.sad = sad;
    }

    public String getHappy() {
        return happy;
    }

    public void setHappy(String happy) {
        this.happy = happy;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFreeimage() {
        return freeimage;
    }

    public void setFreeimage(String freeimage) {
        this.freeimage = freeimage;
    }

    public String getPremiumimage() {
        return premiumimage;
    }

    public void setPremiumimage(String premiumimage) {
        this.premiumimage = premiumimage;
    }

    String avgrating;
    String sad;
    String happy;
    String total;
    String type;

    public String getTypeone() {
        return typeone;
    }

    public void setTypeone(String typeone) {
        this.typeone = typeone;
    }

    String typeone;
    String freeimage;
    String premiumimage;

    public String getVisited_date() {
        return visited_date;
    }

    public void setVisited_date(String visited_date) {
        this.visited_date = visited_date;
    }

    String visited_date;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(buildingname);
        parcel.writeString(address);
        parcel.writeString(distance);
        parcel.writeString(isreviewed);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(heading);
        parcel.writeString(ind_rating);
        parcel.writeString(recommendation);
        parcel.writeString(rvid);
        parcel.writeString(isfav);
        parcel.writeString(lid);
        parcel.writeString(avgrating);
        parcel.writeString(sad);
        parcel.writeString(happy);
        parcel.writeString(total);
        parcel.writeString(type);
        parcel.writeString(typeone);
        parcel.writeString(freeimage);
        parcel.writeString(premiumimage);
        parcel.writeString(visited_date);
    }
}
