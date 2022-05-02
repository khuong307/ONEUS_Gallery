package com.example.oneus.subClasses;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Image {
    File image;
    String text;

    public Image(File image, String text) {
        this.image = image;
        this.text = text;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static List<Image> setImageList(String folder){
        List<Image> imageList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString() + "/ONEUS/"+folder;
        File directory = new File (path);
        if (directory.exists()){
            File[] images = directory.listFiles();
            for (int i = 0; i < images.length; i++){
                imageList.add(new Image(images[i], images[i].getName()));
            }
        }
        return imageList;
    }

    public boolean isInFavorite(){
        List<Image> fav = setImageList("Favorite");
        for (int i = 0; i < fav.size(); i++){
            if (fav.get(i).getImage().getName().compareTo(this.getImage().getName()) == 0)
                return true;
        }

        return false;
    }

    public int indexInFavorite(){
        List<Image> fav = setImageList("Favorite");
        for (int i = 0; i < fav.size(); i++){
            if (fav.get(i).getImage().getName().compareTo(this.getImage().getName()) == 0)
                return i;
        }

        return -1;
    }
}
