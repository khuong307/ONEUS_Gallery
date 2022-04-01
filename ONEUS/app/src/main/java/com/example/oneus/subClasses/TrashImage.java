package com.example.oneus.subClasses;

import java.io.File;

public class TrashImage {
    File image;
    String text;

    public TrashImage(File image, String text) {
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
}
