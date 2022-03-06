package com.example.oneus.subClasses;

import java.io.File;

public class ImageAlbum {
    String albumName;
    File thumbnail;

    public ImageAlbum(String album, File thumbnail) {
        this.albumName = album;
        this.thumbnail = thumbnail;
    }


    public String getAlbum() {
        return albumName;
    }

    public void setAlbum(String album) {
        this.albumName = album;
    }

    public File getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(File thumbnail) {
        this.thumbnail = thumbnail;
    }
}
