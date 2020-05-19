package com.example.project01;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class PhotoCardItemView extends LinearLayout {
    ImageView image;
    TextView emotion;
    LinearLayout layout;

    Context context;
    int index;

    public PhotoCardItemView(Context context, int i) { // 액티비티의 컨텍스트와 어뎁터의 인덱스를 받아옴
        super(context);
        this.index = i;
        init(context);
    }

    public PhotoCardItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context){
        this.context = context;

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.photocard,this,true);

        image = findViewById(R.id.follow);
        emotion = findViewById(R.id.textView);
        layout = findViewById(R.id.clickableLayout);

        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("dd",index+" layout clicked");
                menuDialog();
            }
        });

    }
    public void setEmotion(String s){
        emotion.setText(s);
    }
    public void setImg(Bitmap img){
        image.setImageBitmap(img);
    }

    // 다이얼로그
    public void menuDialog() {
        final String[] option = new String[] { "카메라", "갤러리", "삭제" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.select_dialog_item, option);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Select Option");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Log.d("dialog clicked",Integer.toString(which));
                // TODO Auto-generated method stub
                switch (which){
                    case 0:
                        ((PhotoCardActivity)context).Capture();
                        ((PhotoCardActivity)context).selectedIdx = index;
                        break;
                    case 1:
                        ((PhotoCardActivity)context).BringImgFromGallery();
                        ((PhotoCardActivity)context).selectedIdx = index;
                        break;
                    case 2:
                        // 기존데이터 삭제하고 빈카드 추가하기
                        Drawable drawable = getResources().getDrawable(R.drawable.empty);
                        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                        byte[] img = DbBitmapUtility.getBytes(bitmap);

                        String emotion = ((PhotoCardActivity)context).defaultEmotion[index];
                        ((PhotoCardActivity)context).pdb.updateData(img,emotion,1);
                        ((PhotoCardActivity)context).adapter.Rerendering();
                        break;
                    default:
                        break;
                }
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}
