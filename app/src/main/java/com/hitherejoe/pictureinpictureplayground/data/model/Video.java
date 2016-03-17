package com.hitherejoe.pictureinpictureplayground.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {

    public int id;
    public String title;
    public String description;
    public int imageResource;
    public int videoResource;

    public Video() {

    }

    public Video(int id, String title, String description, int imageResource, int videoResource) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageResource = imageResource;
        this.videoResource = videoResource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeInt(this.imageResource);
        dest.writeInt(this.videoResource);
    }

    protected Video(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.imageResource = in.readInt();
        this.videoResource = in.readInt();
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}