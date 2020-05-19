package com.example.project01;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

public class PhotoCardDatabase {
    private static final String DB_NAME = "Database.db";
    private static final String TABLE_NAME = "PhotoCards";

    SQLiteDatabase db;
    Context context;

    private static volatile PhotoCardDatabase pdb; // 싱글톤으로 인스턴스 반환

    public static PhotoCardDatabase getInstance(Context context) {
        if (pdb == null) {
            synchronized (PhotoCardDatabase.class) {
                if (pdb == null) {
                    pdb = new PhotoCardDatabase(context);
                }
            }
        }
        return pdb;
    }

    private PhotoCardDatabase(Context context) {
        this.context = context;
    }

    public void openDataBase() throws IOException {
        Log.d("dd","open data base");
        DbHelper dbHelper = new DbHelper(context,DB_NAME,null,1);
        dbHelper.createDataBase();
        db = dbHelper.getWritableDatabase();
    }

    public ArrayList<PhotoCard> selectAllData(){
        if(db!=null) {
            Log.d("dd","select all data start");
            String sql = "select PHOTO, EMOTION from " + TABLE_NAME;
            Cursor cursor = db.rawQuery(sql, null);
            Log.d("dd","row count : "+cursor.getCount());
            ArrayList<PhotoCard> ret = new ArrayList<>();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                byte[] img = cursor.getBlob(0);
                Bitmap bitmap = DbBitmapUtility.getImage(img);
                String emotion = cursor.getString(1);

                ret.add(new PhotoCard(emotion, bitmap));
            }
            return ret;
        }else {
            Log.d("dd","db is closed");
            return null;
        }
    }
    public  PhotoCard selectTargetData(int id){
        if(db!=null){
            String sql = "select PHOTO, EMOTION, isEmpty from "+TABLE_NAME+" where _id="+id+";";
            Cursor cursor = db.rawQuery(sql,null);
            cursor.moveToNext();
            Log.d("cursor",cursor.getCount()+"");

            if(cursor.getInt(2)==0) {
                byte[] img = cursor.getBlob(0);
                Bitmap bitmap = DbBitmapUtility.getImage(img);
                String emotion = cursor.getString(1);

                PhotoCard p = new PhotoCard(emotion, bitmap);
                return p;
            }else{ // 사진이 빈 카드일 경우
                Log.d("selectTargetDate","photo is null");
               return null;
            }
        }else{
            Log.d("dd","db is closed");
            return null;
        }
    }

    public void insertData(byte[] img,String emotion){
        ContentValues cv = new  ContentValues();
        cv.put("PHOTO",img);
        cv.put("EMOTION",emotion);
        db.insert( TABLE_NAME, null, cv );

    }

    public void updateData(byte[] img,String emotion,int isEmpty){
        ContentValues cv = new ContentValues();
        cv.put("PHOTO",img);
        cv.put("EMOTION",emotion);
        cv.put("isEmpty",isEmpty);
        String where = "EMOTION=?";
        String[] whereArgs = new String[] {emotion};

        db.update("PhotoCards",cv,where,whereArgs);
    }

    public void deleteData(int id){
        db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE _id="+id+";");
    }

    public PhotoCard getRandomData(int moduleCode,int categorySetCode) {
        // code => 0 : 얼굴인식 모듈을 위한 코드
        //         1 : 음성인식 모듈을 위한 코드

        // categorySetCode => 0 : 기본 이미지
        //                     1 : 사진 이미지
        //                     2 : 기본+사진 이미지

        int total = selectAllData().size();
        Log.d("db","total num :"+total);
        Log.d("setting",categorySetCode+"");
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int randomNum = random.nextInt(total)+1;

        switch(categorySetCode){
            case 0:
                if(moduleCode==0){
                    randomNum = random.nextInt(4)+1;
                }else if(moduleCode==1){
                    randomNum = random.nextInt(8)+1;
                }
                break;
            case 1:

                for(int i=9;i<17;i++) { // 다 비었는지 확인
                    if (selectTargetData(i) != null) break;
                    if (i == 16) {
                        // 다 비었으면 없다고 메시지, 기본 카드로 변경
                        Toast.makeText(context,"사용자 지정 카드가 없습니다",Toast.LENGTH_LONG).show();
                        Log.d("photocard","사용자 지정 카드가 없습니다.");
                        return getRandomData(moduleCode,0);
                    }
                }
                if(moduleCode==0){
                    randomNum = random.nextInt(4)+9;
                }else if(moduleCode==1){
                    randomNum = random.nextInt(8)+9;
                }
                break;
            case 2:
                if(moduleCode==0){
                    int [] num = {1,2,3,4,9,10,11,12};
                    randomNum = num[random.nextInt(8)];
                }else if(moduleCode==1){
                    randomNum = random.nextInt(16)+1;
                }
                break;
        }

        if(selectTargetData(randomNum)==null){
            return getRandomData(moduleCode,categorySetCode);
        }else{
            return selectTargetData(randomNum);
        }
    }
    class DbHelper extends SQLiteOpenHelper {

        private  String DB_PATH = "";
        private  String DB_NAME ="Database.db";

        public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            if(android.os.Build.VERSION.SDK_INT >= 17){
                DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
            }
            else
            {
                DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
            }
//            this.mContext = context;
        }

        public void createDataBase() throws IOException
        {
            //데이터베이스가 없으면 asset폴더에서 복사해온다.
            boolean mDataBaseExist = checkDataBase();
            if(!mDataBaseExist)
            {
                this.getReadableDatabase();
                this.close();
                try
                {
                    //Copy the database from assests
                    copyDataBase();
                    Log.e("dd", "createDatabase database created");
                }
                catch (IOException mIOException)
                {
                    throw new Error("ErrorCopyingDataBase");
                }
            }
        }

        ///data/data/your package/databases/Da Name <-이 경로에서 데이터베이스가 존재하는지 확인한다
        private boolean checkDataBase()
        {
            File dbFile = new File(DB_PATH + DB_NAME);
            //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
            return dbFile.exists();
        }

        //assets폴더에서 데이터베이스를 복사
        private void copyDataBase() throws IOException
        {
            InputStream mInput = context.getAssets().open(DB_NAME); //
            String outFileName = DB_PATH + DB_NAME;
            OutputStream mOutput = new FileOutputStream(outFileName);
            byte[] mBuffer = new byte[1024];
            int mLength;
            while ((mLength = mInput.read(mBuffer))>0)
            {
                mOutput.write(mBuffer, 0, mLength);
            }
            mOutput.flush();
            mOutput.close();
            mInput.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            if(db!=null){
                Log.d("dd","table created");
                String sql = "create table if not exists "+TABLE_NAME
                        +"(_id integer PRIMARY KEY autoincrement, PHOTO BLOB , EMOTION text, isEmpty integer)";
                db.execSQL(sql);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if(newVersion>oldVersion){
                db.execSQL("drop table if exists "+TABLE_NAME);

                onCreate(db);
            }
        }

    }
}
