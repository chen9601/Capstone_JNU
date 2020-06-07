package com.example.project01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    protected String mRecordingFile;
    int sw=0;
    static final int GET_STRING=1;
    private static final String TAG = "ppp";
    String COUNT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Camera Permission Granted",Toast.LENGTH_LONG).show();
        }else{
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA},1);
        }
    }

    public void myListener(View target) {
        Intent intent = new Intent(getApplicationContext(), followActivity.class);
        startActivity(intent);
    }

    public void myListener1(View target) {
        Intent intent = new Intent(getApplicationContext(), todayActivity.class);
        startActivity(intent);
    }
    public void myListener2(View target) {
        Intent in = new Intent(this, fitActivity.class);
        startActivityForResult(in,GET_STRING);
    }


    public void myListener3(View target) {
        Intent intent = new Intent(getApplicationContext(),PhotoCardActivity.class);
        startActivity(intent);
    }

    public void setting(View target){
        Intent intent = new Intent(this, settingDialog.class);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==GET_STRING){
            if(resultCode==RESULT_OK){
                Log.d(TAG,data.getStringExtra("Count")+"받음");
                COUNT = data.getStringExtra("Count");
            }
        }
    }
    public void bt_graph(View v){
        Intent intent= new Intent(this, graphActivity.class);

        intent.putExtra("Count", COUNT);
        intent.putExtra("SW",1);
        startActivity(intent);
;       //sw 추가해서 한번만 값 들어가게 하기
        Log.d(TAG,COUNT+"값 출력");
        COUNT=null;
    }

}
