package com.example.project01;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.nio.InvalidMarkException;
import java.util.ArrayList;

public class todayActivity extends AppCompatActivity {

    ImageView happycard;
    ImageView sadcard;
    SoundPool sound;
    int soundId;
    String emotion;
    Intent intent;
    private static final String TAG ="ppp";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.today);

    }
    public void open(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(todayActivity.this);

        switch (view.getId()) {
            case R.id.happycard:
                final View v = factory.inflate(R.layout.h_pop, null);
                alertDialogBuilder.setView(v);
                alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        emotion="기쁜하루";
                    }
                });
                alertDialogBuilder.setNegativeButton("다시선택", null);
                break;
            case R.id.sadcard:
                final View v2 = factory.inflate(R.layout.activity_pop2, null);
                alertDialogBuilder.setView(v2);
                alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        emotion="슬픈하루";
                    }
                });
                alertDialogBuilder.setNegativeButton("다시선택", null);
                break;
            case R.id.angrycard:
                final View v3 = factory.inflate(R.layout.pop3, null);
                alertDialogBuilder.setView(v3);
                alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        emotion="화난하루";
                    }
                });
                alertDialogBuilder.setNegativeButton("다시선택", null);
                break;

            case R.id.excited:
                final View v4 = factory.inflate(R.layout.pop4, null);
                alertDialogBuilder.setView(v4);
                alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        emotion="신난하루";
                    }
                });
                alertDialogBuilder.setNegativeButton("다시선택", null);
                break;

            case R.id.scary:
                final View v5 = factory.inflate(R.layout.pop5, null);
                alertDialogBuilder.setView(v5);
                alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        emotion="무서운 하루";
                    }
                });
                alertDialogBuilder.setNegativeButton("다시선택", null);
                break;

            case R.id.irritation:
                final View v6 = factory.inflate(R.layout.pop6, null);
                alertDialogBuilder.setView(v6);
                alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        emotion="짜증난 하루";
                    }
                });
                alertDialogBuilder.setNegativeButton("다시선택", null);
                break;

            case R.id.confidence:
                final View v7 = factory.inflate(R.layout.pop7, null);
                alertDialogBuilder.setView(v7);
                alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        emotion="자신있는 하루";

                    }
                });
                alertDialogBuilder.setNegativeButton("다시선택", null);
                break;
            case R.id.fun:
                final View v8 = factory.inflate(R.layout.pop8, null);
                alertDialogBuilder.setView(v8);
                alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        emotion="신난 하루";

                    }
                });
                alertDialogBuilder.setNegativeButton("다시선택", null);
                break;
        }

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void bt_diary(View target) {
        intent =  new Intent(this,diaryActivity.class);
        intent.putExtra("String", emotion);
        startActivity(intent);
        emotion=null;
    }

}
