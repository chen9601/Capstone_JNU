package com.example.project01;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class settingDialog extends AppCompatActivity {
    public SharedPreferences settings;
    private static final String TAG ="ppp";
    int ff=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        WindowManager.LayoutParams  layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags  = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount  = 0.7f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.activity_setting_dialog);

        Switch sw = (Switch)findViewById(R.id.a_switch);
        final LinearLayout alarm_Layout = (LinearLayout)findViewById(R.id.alarm_Layout);

        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        final Boolean sw_check = settings.getBoolean("Checked",true);

        if(sw_check==true){
            sw.setChecked(true);
            alarm_Layout.setVisibility(View.VISIBLE);
        }else{
            sw.setChecked(false);
            alarm_Layout.setVisibility(View.INVISIBLE);
        }

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    alarm_Layout.setVisibility(View.VISIBLE);
                    ff=1;
                    settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("Checked", true);
                    editor.commit();
                     }else{
                        ff=0;
                        alarm_Layout.setVisibility(View.INVISIBLE);
                        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("Checked", false);
                        editor.commit();
                }
            }
        });

        final TimePicker picker = (TimePicker) findViewById(R.id.timePicker);
        picker.setIs24HourView(true);

        SharedPreferences sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
        long millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().getTimeInMillis());

        Calendar nextNotifyTime = new GregorianCalendar();
        nextNotifyTime.setTimeInMillis(millis);

        Date nextDate = nextNotifyTime.getTime();
        String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(nextDate);
        Toast.makeText(getApplicationContext(), "[처음 실행시] 다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

        Date currentTime = nextNotifyTime.getTime();
        SimpleDateFormat HourFormat = new SimpleDateFormat("kk", Locale.getDefault());
        SimpleDateFormat MinuteFormat = new SimpleDateFormat("mm", Locale.getDefault());

        int pre_hour = Integer.parseInt(HourFormat.format(currentTime));
        int pre_minute = Integer.parseInt(MinuteFormat.format(currentTime));

        if (Build.VERSION.SDK_INT >= 23) {
            picker.setHour(pre_hour);
            picker.setMinute(pre_minute);
        } else {
            picker.setCurrentHour(pre_hour);
            picker.setCurrentMinute(pre_minute);
        }

        Button button = (Button) findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int hour, hour_24, minute;
                String am_pm;
                if (Build.VERSION.SDK_INT >= 23) {
                    hour_24 = picker.getHour();
                    minute = picker.getMinute();
                } else {
                    hour_24 = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                }
                if (hour_24 > 12) {
                    am_pm = "PM";
                    hour = hour_24 - 12;
                } else {
                    hour = hour_24;
                    am_pm = "AM";
                }

                // 현재 지정된 시간으로 알람 시간 설정
                  Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, hour_24);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);

                // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
                    if (calendar.before(Calendar.getInstance())) {
                     calendar.add(Calendar.DATE, 1);
                    }

                    Date currentDateTime = calendar.getTime();
                    String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
                    Toast.makeText(getApplicationContext(), date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

                //  Preference에 설정한 값 저장
                    SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
                    editor.putLong("nextNotifyTime", (long) calendar.getTimeInMillis());
                    editor.apply();

                diaryNotification(calendar);
            }
        });
    }

    void diaryNotification(Calendar calendar)
    {
        Boolean dailyNotify = true;
        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        }
//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
    }

}
