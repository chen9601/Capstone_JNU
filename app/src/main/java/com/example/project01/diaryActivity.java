package com.example.project01;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class diaryActivity extends AppCompatActivity {
    private static final String TAG ="ppp";
    ArrayAdapter<String> adapter;
    ArrayList<String> array = new ArrayList<>();
    private static final String SETTINGS_PLAYER_JSON = "settings_item_json2";
    ListView listView;

    String Receive2;
    String Receive;
    static SharedPreferences sp = null;
    static SharedPreferences.Editor editor = null;
    TextView tvList;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams  layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags  = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount  = 0.7f;
        getWindow().setAttributes(layoutParams);

        Intent intent = getIntent();
        Receive = intent.getStringExtra("String");

//        Toast.makeText(getApplicationContext(),Receive, Toast.LENGTH_LONG).show();

        setContentView(R.layout.activity_diary);

        listView = (ListView) findViewById(R.id.listView);
        array = getStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
        listView.setAdapter(adapter);

        if(Receive==null)  {
            getStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON);
            //값 저장 안하고 불러오는 코드
            return;
        }

        Date currentTime = Calendar.getInstance().getTime();
        String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일", Locale.getDefault()).format(currentTime);

        if (Receive.length() > 0) {
            Receive2 = date_text+"  "+ Receive;
            array.add(Receive2 + "");
            adapter.notifyDataSetChanged();
            Log.d(TAG, Receive.toString());
        }
    }
    //값 저장하는 코드
    private void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();

        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    private ArrayList getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList urls = new ArrayList();

        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return urls;
    }

    protected void onPause() {
        super.onPause();
        setStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON, array);
        Log.d(TAG, "Put json");

    }

}
