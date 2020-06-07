
package com.example.project01;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class graphActivity extends AppCompatActivity {
    private static final String TAG = "ppp";
    String Receive;
    int sw=0;

    ArrayList<String> array2 = new ArrayList<>();
    ArrayList<Integer> countArray = new ArrayList<>();

    ArrayList urls = new ArrayList();

    private static final String SETTINGS_PLAYER_JSON = "settings_item_json";

    SharedPreferences sp = null;
    SharedPreferences.Editor editor = null;

    private LineChart lineChart;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();

        Receive = intent.getStringExtra("Count");
        Log.d(TAG, Receive + "값받나");

        setContentView(R.layout.graph);
        array2 = getStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON);
        array2.add(Receive);

        Log.d(TAG,"여기오나?");
        Log.d(TAG,Receive+"receive값");

        if(Receive==null){
            for(int i=0; i<countArray.size();i++){
                Log.d(TAG,countArray.get(i)+"출력");
            }
            Chart(countArray);
        }else{
            setStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON, array2);
            Chart(countArray);

            for(int i=0; i<countArray.size();i++){
                Log.d(TAG,countArray.get(i)+"출력");
            }

        }

    }

    private void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(graphActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();

<<<<<<< Updated upstream
        for (int i = 0; i < values.size(); i++) {
           a.put(values.get(i));

        }
        if (!values.isEmpty()) {
            Log.d(TAG,values+"test");
            //Log.d(TAG, a.toString() + "여기가 저장할 때?");
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    private ArrayList getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);

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
        for(int i=0; i<urls.size(); i++){
            String aa = urls.get(i).toString();
            if(aa.length()<=2)   {
                countArray.add(Integer.parseInt(aa));
            }
        }
        return urls;
    }

    protected void onPause() {
        super.onPause();
        setStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON, array2);
        Log.d(TAG, "Put json");
=======
        barEntries = new ArrayList<>();
        for(int i=0; i<countArray.size(); i++){
            barEntries.add(new BarEntry(i,countArray.get(i)));
         }
>>>>>>> Stashed changes

    }
    public void Chart(ArrayList<Integer> data){
        lineChart = (LineChart)findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<>();

        for(int i=0; i<data.size();i++ ){
            entries.add(new Entry(i,data.get(i)));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "속성명1");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setCircleColorHole(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();

    }


}