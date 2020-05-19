package com.example.project01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import android.graphics.Matrix;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PhotoCardActivity extends AppCompatActivity {

    PhotoCardDatabase pdb =
            PhotoCardDatabase.getInstance(this);
    PhotoAdapter adapter;
    GridView gridView;

    File file;
    int selectedIdx;
    String [] defaultEmotion = {
            "화나다","놀라다","기쁘다","슬프다","짜증나다","자신있다","신나다","즐겁다"
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_card);

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


        gridView = findViewById(R.id.gridView);
        adapter = new PhotoAdapter(this);

        try {
            pdb.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        // db 전처리용
//        Drawable d = getResources().getDrawable(R.drawable.emotion_sad);
//        Bitmap b = ((BitmapDrawable)d).getBitmap();
//        pdb.insertData(DbBitmapUtility.getBytes(b),"happy");

        // db에 있는 정보 가져오기
        ArrayList<PhotoCard> arr = pdb.selectAllData();
//        adapter.addItem(pdb.selectAllData());

        for(int i=8;i<16;i++){ // 사용자 이미지 어뎁터에 추가
            adapter.addItem(arr.get(i));
        }
        gridView.setAdapter(adapter);
    }

//    // 액션바
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.action_bar,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.bringPhoto:
//                BringImgFromGallery();// 갤러리에서 사진 가져오기
//                return true;
//            case R.id.takePhoto: // 카메라 열기
//                Capture();
//                return true;
//            case R.id.action_delete:
//                Toast.makeText(this,"delete photo",Toast.LENGTH_LONG).show();
//
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


    // 갤러리에서 사진 가져오기
    public void BringImgFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 102);
    }
    // 카메라
    public void Capture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d("dd","start capturing");
        if(Build.VERSION.SDK_INT>=24){
            try{
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                builder.detectFileUriExposure();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)); // 파일 관련 부가데이터 추가
        startActivityForResult(intent,101);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode== Activity.RESULT_OK){ // 카메라
            try{
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;//파일 사이즈 조절
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);

                ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                int exifOrientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = exifOrientationToDegrees(exifOrientation);
                bitmap = rotate(bitmap, exifDegree);

                byte[] img = DbBitmapUtility.getBytes(bitmap);

                pdb.updateData(img,defaultEmotion[selectedIdx],0);

                adapter.Rerendering();
            }
            catch(Exception e){
                Toast.makeText(this,"exception founded",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else if(requestCode==102){ // 갤러리
            if(resultCode == RESULT_OK)
            {
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;//파일 사이즈 조절
                    Bitmap bitmap = BitmapFactory.decodeStream(in,null,options);
                    in.close();

                    byte[] img = DbBitmapUtility.getBytes(bitmap);
                    pdb.updateData(img,defaultEmotion[selectedIdx],0);

                    adapter.Rerendering();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
            else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    // exif 정보 수정하여 회전
    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    // 이미지를 회전
    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }


    // 그리드 뷰
    class PhotoAdapter extends BaseAdapter {
        ArrayList<PhotoCard> items = new ArrayList<PhotoCard>();
        Context c;

        public PhotoAdapter(Context c) {
            this.c = c;
        }

        public void addItem(PhotoCard item){
            items.add(item);
        }
        public void addItem(ArrayList<PhotoCard> items){
            this.items.addAll(items);
        }
        public void removeAllItem(){
            items.clear();
        }
        public void Rerendering(){
            Log.d("gridview","updated");
            adapter.removeAllItem();
            ArrayList<PhotoCard> arr = pdb.selectAllData();
            for(int i=8;i<16;i++){ // 사용자 이미지 어뎁터에 추가
                adapter.addItem(arr.get(i));
            }
            adapter.notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            PhotoCardItemView v = new PhotoCardItemView(c,i);
            PhotoCard item = items.get(i);
            v.setEmotion(item.emotion);
            v.setImg(item.img);

            return v;
        }
    }


}
