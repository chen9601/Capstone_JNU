package com.example.project01;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.project01.predictivemodels.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class followActivity extends AppCompatActivity {

    PhotoCardDatabase pdb =
            PhotoCardDatabase.getInstance(this);

    File file;

    CameraSurfaceView surfaceView;
    ImageView button;
    ImageView imageview;

    ImageView reset;
    ImageView save;
    TensorFlowClassifier classifier;
    private SquareImageView faceImageView;
    PhotoCard p;
    Bitmap b;
    String filename;
    int count=0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.follow);

        // 관리 권한 획득
        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
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

        this.faceImageView = (SquareImageView) this.findViewById(R.id.facialImageView);

        ImageView image = (ImageView)findViewById(R.id.follow);
        p = pdb.getRandomData(0,SettingValueGlobal.getInstance().getData());
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

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage(b,filename);
            }
        });

        save.setEnabled(false);

        reset=findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reset();
            }
        });

        loadModel();

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

                detectEmotion(bitmap);
            }
        });
    }



    private void detectEmotion(Bitmap bitmap){

        Bitmap image=bitmap;
        Bitmap grayImage = toGrayscale(image);
        Bitmap resizedImage=getResizedBitmap(grayImage,48,48);
        int pixelarray[];

        //Initialize the intArray with the same size as the number of pixels on the image
        pixelarray = new int[resizedImage.getWidth()*resizedImage.getHeight()];

        //copy pixel data from the Bitmap into the 'intArray' array
        resizedImage.getPixels(pixelarray, 0, resizedImage.getWidth(), 0, 0, resizedImage.getWidth(), resizedImage.getHeight());


        float normalized_pixels [] = new float[pixelarray.length];
        for (int i=0; i < pixelarray.length; i++) {
            // 0 for white and 255 for black
            int pix = pixelarray[i];
            int b = pix & 0xff;
            //  normalized_pixels[i] = (float)((0xff - b)/255.0);
            // normalized_pixels[i] = (float)(b/255.0);
            normalized_pixels[i] = (float)(b);

        }
        System.out.println(normalized_pixels);
        Log.d("pixel_values",String.valueOf(normalized_pixels));

        try{
            final Classification res = classifier.recognize(normalized_pixels);
            //if it can't classify, output a question mark

            filename=res.getLabel();

            if(check(res.getLabel(),p.emotion))
                Toast.makeText(this.getApplicationContext(),"정답입니다",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this.getApplicationContext(),"틀렸습니다",Toast.LENGTH_SHORT).show();


            }
        catch (Exception e){
            System.out.print("Exception:"+e.toString());

        }

        Matrix matrix=new Matrix();
        matrix.postRotate(270);
        Bitmap rotatedBitmap = Bitmap.createBitmap(grayImage, 0, 0, grayImage.getWidth(), grayImage.getHeight(), matrix, true);


        b=rotatedBitmap;
        save.setEnabled(true);


        this.faceImageView.setImageBitmap(rotatedBitmap);
        faceImageView.setVisibility(View.VISIBLE);
    }


    private void saveImage(Bitmap finalBitmap, String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name+ count+".jpg";
        File file = new File(myDir, fname);
        while (file.exists())
        {
            count++;
            fname = "Image-" + image_name+ count+".jpg";
            file = new File(myDir, fname);
        }
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Reset()
    {
        faceImageView.setVisibility(View.GONE);
    }

    private boolean check(String s1, String s2)
    {
        if(s1.equals("Angry")&&s2.equals("화나다."))
            return true;
        else if(s1.equals("Disgust")&&s2.equals("화나다."))
            return true;
        else if(s1.equals("Fear")&&s2.equals("놀라다."))
            return true;
        else if(s1.equals("Happy")&&s2.equals("기쁘다."))
            return true;
        else if(s1.equals("Sad")&&s2.equals("슬프다."))
            return true;
        else if(s1.equals("Surprise")&&s2.equals("놀라다."))
            return true;
        else if(s1.equals("Neutral"))
            return true;
        else return false;
    }

    private void loadModel() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier=TensorFlowClassifier.create(getAssets(), "CNN",
                            "opt_em_convnet_5000.pb", "labels.txt", 48,
                            "input", "output_50", true, 7);

                } catch (final Exception e) {
                    //if they aren't found, throw an error!
                    throw new RuntimeException("Error initializing classifiers!", e);
                }
            }
        }).start();
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
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
