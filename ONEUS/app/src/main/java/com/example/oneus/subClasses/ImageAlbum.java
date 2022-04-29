package com.example.oneus.subClasses;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public int getQuantity(){
        File parentFolder = new File(this.thumbnail.getParent());
        return parentFolder.listFiles().length;
    }

    public static List<ImageAlbum> setAlbumList(){
        List<ImageAlbum> mList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString() + "/ONEUS";
        File directory = new File (path);
        if (directory.exists()){
            File[] folder = directory.listFiles();
            for (int i = 0; i < folder.length; i++){
                if(folder[i].isDirectory() == true && folder[i].getName().compareTo("Trash") != 0 && folder[i].getName().compareTo("Favorite") != 0) {
                    File[] images = folder[i].listFiles();
                    if (images.length != 0)
                        mList.add(new ImageAlbum(folder[i].getName(),images[0]));
                }
            }
        }
        return mList;
    }

    public static List<ImageAlbum> setAlbumListExcept(String folderName){
        List<ImageAlbum> mList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString() + "/ONEUS";
        File directory = new File (path);
        if (directory.exists()){
            File[] folder = directory.listFiles();
            for (int i = 0; i < folder.length; i++){
                if(folder[i].isDirectory() == true && folder[i].getName().compareTo(folderName) != 0 && folder[i].getName().compareTo("Trash") != 0 && folder[i].getName().compareTo("Favorite") != 0){
                    File[] images = folder[i].listFiles();
                    if (images.length != 0)
                        mList.add(new ImageAlbum(folder[i].getName(),images[0]));
                }
            }
        }
        return mList;
    }
}
