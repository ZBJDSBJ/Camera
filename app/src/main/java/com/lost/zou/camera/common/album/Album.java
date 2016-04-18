package com.lost.zou.camera.common.album;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 *  相册数据bean
 */
public class Album implements Parcelable{
    private String albumUri;
    private String title;
    private ArrayList<PhotoItem> photos;

    public Album(String title, String uri, ArrayList<PhotoItem> photos) {
        this.title = title;
        this.albumUri = uri;
        this.photos = photos;
    }

    protected Album(Parcel in) {
        albumUri = in.readString();
        title = in.readString();
        photos = in.createTypedArrayList(PhotoItem.CREATOR);
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public String getAlbumUri() {
        return albumUri;
    }

    public void setAlbumUri(String albumUri) {
        this.albumUri = albumUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<PhotoItem> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<PhotoItem> photos) {
        this.photos = photos;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumUri);
        dest.writeString(title);
        dest.writeTypedList(photos);
    }
}
