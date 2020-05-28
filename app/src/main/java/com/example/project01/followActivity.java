package com.example.project01;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class followActivity extends AppCompatActivity {

    PhotoCardDatabase pdb =
            PhotoCardDatabase.getInstance(this);

    File file;

    CameraSurfaceView surfaceView;
    ImageView button;
    ImageView imageview;

    Button reset;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow);

        // 관리 권한 획득
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Read Storage Permission Granted",Toast.LENGTH_LONG).show();
        }else{
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        File sdcard = Environment.getExternalStorageDirectory();//sd카드에 저장.
        file = new File(sdcard,"capture.png");


        try {
            pdb.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView image = (ImageView)findViewById(R.id.follow);
        PhotoCard p = pdb.getRandomData(0,SettingValueGlobal.getInstance().getData());
        image.setImageBitmap(p.img);

        surfaceView = findViewById(R.id.surfaceview);
        surfaceView.init(this);

        button = findViewById(R.id.camerabutton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Capture();
            }
        });

    }
    // 카메라
    public void Capture(){
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        Log.d("dd","start capturing");
        if(Build.VERSION.SDK_INT>=24){
            try{
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                builder.detectFileUriExposure();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)); // 파일 관련 부가데이터 추가
//        startActivityForResult(intent,101);
        surfaceView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                Log.d("followActivity","capturing");

                camera.startPreview();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode== Activity.RESULT_OK){ // 카메라
            try{
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;//파일 사이즈 조절
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);

                byte[] img = DbBitmapUtility.getBytes(bitmap);

            }
            catch(Exception e){
                Toast.makeText(this,"exception founded",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
