package com.example.oneus.subClasses;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
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

    public static List<ImageAlbum> sortFolder(int type){
        List<ImageAlbum> mList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString() + "/ONEUS";
        File directory = new File (path);
        File[] listFoler = directory.listFiles();

        if (type == 1){
            Arrays.sort(listFoler, new Comparator<File>() { // sort by name
                public int compare(File f1, File f2) {
                    return f1.getName().compareToIgnoreCase(f2.getName());
                }
            });
        }else if (type == 2){
            Arrays.sort(listFoler, new Comparator<File>() { //sort by size
                public int compare(File f1, File f2) {
                    return Long.compare(Path.getFileSize(f2), Path.getFileSize(f1));
                }
            });
        }else if (type == 3){
            Arrays.sort(listFoler, new Comparator<File>() { // sort by quantity
                public int compare(File f1, File f2) {
                    return Long.compare(f2.listFiles().length, f1.listFiles().length);
                }
            });
        }else if (type == 4){ // sort by last modified
            Arrays.sort(listFoler, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.compare(f2.lastModified(), f1.lastModified());
                }
            });
        }
        for (int i = 0; i < listFoler.length; i++){
            if(listFoler[i].isDirectory() == true && listFoler[i].getName().compareTo("Trash") != 0 && listFoler[i].getName().compareTo("Favorite") != 0){
                File[] images = listFoler[i].listFiles();
                if (images.length != 0)
                    mList.add(new ImageAlbum(listFoler[i].getName(),images[0]));
            }
        }
        return mList;
    }

    public static List<ImageAlbum> setSearchAlbumList(String content){
        List<ImageAlbum> mList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString() + "/ONEUS";
        File directory = new File (path);
        if (directory.exists()){
            File[] folder = directory.listFiles();
            for (int i = 0; i < folder.length; i++){
                if(folder[i].isDirectory() == true && folder[i].getName().compareTo("Trash") != 0 && folder[i].getName().compareTo("Favorite") != 0 ) {
                    String tmp = Integer.toString(folder[i].listFiles().length);
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    String date = sdf.format(folder[i].lastModified());
                    if(folder[i].getName().toLowerCase().contains(content.toLowerCase())
                            ||tmp.compareTo(content)== 0
                            ||date.compareTo(content) == 0){
                        File[] images = folder[i].listFiles();
                        if (images.length != 0)
                            mList.add(new ImageAlbum(folder[i].getName(),images[0]));
                    }
                }
            }
        }
        return mList;
    }

}
