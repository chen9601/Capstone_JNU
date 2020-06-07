package com.example.project01;

import android.graphics.Bitmap;

public class PhotoCard {
    String emotion;
    Bitmap img; // 이미지 리소스

    public PhotoCard(String emotion, Bitmap img) {
        this.emotion = emotion;
        this.img = img;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "PhotoCard{" +
                "emotion='" + emotion + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
