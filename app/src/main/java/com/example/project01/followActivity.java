package com.example.project01;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class followActivity extends AppCompatActivity {

    PhotoCardDatabase pdb =
            PhotoCardDatabase.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow);

        try {
            pdb.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView image = (ImageView)findViewById(R.id.follow);
        PhotoCard p = pdb.getRandomData(0,SettingValueGlobal.getInstance().getData());
        image.setImageBitmap(p.img);

    }
}
