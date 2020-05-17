package com.example.project01;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class fitActivity extends AppCompatActivity {
    int[] img= {R.drawable.happy,R.drawable.sad,R.drawable.angry,R.drawable.bb,R.drawable.cc,R.drawable.dd,R.drawable.ee,R.drawable.ff};
    int[] ff = new int[8];
    ImageView image=null;
    TextToSpeech tts;
    int num=0,count=0;
    private static final String TAG ="ppp";
    Button bt1,bt2,bt3,bt4;
    String sfName = "myFile";
    int sw2=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 5);
            toast( "권한을 넘기지 않으면, 음성 인식 기능을 사용할 수 없습니다");
        }

        setContentView(R.layout.fit);

        bt1 = (Button)findViewById(R.id.b1);
        bt2 = (Button)findViewById(R.id.b2);
        bt3 = (Button)findViewById(R.id.b3);
        bt4 = (Button)findViewById(R.id.b4);

        Button input = (Button)findViewById(R.id.say);
        final TextView txt = new TextView(this);
        txt.setText("\n");
        txt.setTextSize(18);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.KOREAN);
            }
        });

        image = (ImageView)findViewById(R.id.imageview);
        Randomimage();

        input.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                inputVoice(txt);
            }
        });
    }

    private void Randomimage() {
            Random ram = new Random();
            num = ram.nextInt(img.length);
            if(ff[num]==1) Randomimage();
            if(count==8) finish();
            //randomText(num);
            image.setImageResource(img[num]);
    }
    public void randomText(int num2){
       // Log.d(TAG,num2+"번 카드");
        switch(num2) {
            case 0:
                bt1.setText("기쁘다");
                bt2.setText("슬프다");
                bt3.setText("화난다");
                bt4.setText("");
                break;
            case 1:
                bt1.setText("슬프다");
                bt2.setText("화난다");
                bt3.setText("기쁘다");
                bt4.setText("");
                break;
            case 2:
                bt1.setText("화나다");
                bt2.setText("기쁘다");
                bt3.setText("슬프다");
                bt4.setText("");
                break;
            case 3:
                bt1.setText("즐거워보여");
                bt2.setText("화난다");
                bt3.setText("기쁘다");
                bt4.setText("");
                break;
            case 4:
                bt1.setText("무섭다");
                bt2.setText("화난다");
                bt3.setText("기쁘다");
                bt4.setText("");
                break;
            case 5:
                bt1.setText("짜증나다");
                bt2.setText("화난다");
                bt3.setText("기쁘다");
                bt4.setText("");
                break;
            case 6:
                bt1.setText("자신있다");
                bt2.setText("화난다");
                bt3.setText("기쁘다");
                bt4.setText("");
                break;
            case 7:
                bt1.setText("신난다");
                bt2.setText("화난다");
                bt3.setText("기쁘다");
                bt4.setText("안돼");
                break;
        }
        return;
    }

    public void inputVoice(final TextView txt) {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
            final SpeechRecognizer stt = SpeechRecognizer.createSpeechRecognizer(this);
            stt.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    toast("음성 입력 시작...");
                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                }

                @Override
                public void onEndOfSpeech() {
                    toast("음성 입력 종료");
                }

                @Override
                public void onError(int error) {
                    toast("오류 발생 : " + error);
                    stt.destroy();
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> result = (ArrayList<String>) results.get(SpeechRecognizer.RESULTS_RECOGNITION);
                    replyAnswer(result.get(0), txt);
                    stt.destroy();
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
            stt.startListening(intent);
        } catch (Exception e) {
            toast(e.toString());
        }
    }
    private void replyAnswer(String input, TextView txt){
        try{
            switch(num) {
                case 0:
                    ff[0]=1;
                    if(input.equals("기뻐 보여") || input.equals("기쁘다") || input.equals("행복해보여") || input.equals("행복하다")  ) {
                        count++;
                        tts.speak("맞았어요", TextToSpeech.QUEUE_FLUSH, null);
                        Randomimage();
                    } else {
                        tts.speak("다시한번 생각해 볼래", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    break;
                case 1:
                    ff[1]=1;
                    if(input.equals("슬퍼 보여") || input.equals("슬프다")) {
                        count++;
                        tts.speak("맞았습니다", TextToSpeech.QUEUE_FLUSH, null);
                        Randomimage();
                    }
                    else {
                        tts.speak("다시한번 생각해볼래", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    break;
                case 2:
                    ff[2]=1;
                    if(input.equals("화나 보여") || input.equals("화나다") ) {
                        tts.speak("맞았어요", TextToSpeech.QUEUE_FLUSH, null);
                        count++;
                        Randomimage();
                    }
                    else {
                        tts.speak("다시한번 생각해볼래요", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    break;
                case 3:
                    ff[3]=1;
                    if(input.equals("즐거워 보여") || input.equals("즐겁다") || input.equals("재밌다")|| input.equals("신난다") ) {
                        tts.speak("맞았어요", TextToSpeech.QUEUE_FLUSH, null);
                        count++;
                        Randomimage();
                    }
                    else {
                        tts.speak("다시한번 생각해볼래요", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    break;
                case 4:
                    ff[4]=1;
                    if(input.equals("무서워") || input.equals("무섭다") || input.equals("무서워 보여") ) {
                        tts.speak("맞았어요", TextToSpeech.QUEUE_FLUSH, null);
                        count++;
                        Randomimage();
                    }
                    else {
                        tts.speak("다시한번 생각해볼래요", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    break;
                case 5:
                    ff[5]=1;
                    toast("5");
                    if(input.equals("짜증 나") || input.equals("짜증나 보여") || input.equals("짜증나") ) {
                        tts.speak("맞았어요", TextToSpeech.QUEUE_FLUSH, null);
                        count++;
                        Randomimage();
                    }
                    else {
                        tts.speak("다시한번 생각해볼래요", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    break;
                case 6:
                    ff[6]=1;
                    if(input.equals("자신 있다") || input.equals("뿌듯 하다") || input.equals("자신 있어 보여") ) {
                        tts.speak("맞았어요", TextToSpeech.QUEUE_FLUSH, null);
                        count++;
                        Randomimage();
                    }
                    else {
                        tts.speak("다시한번 생각해볼래요", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    break;
                case 7:

                    ff[7]=1;
                    if(input.equals("신난다") || input.equals("신나 보여") || input.equals("재밌다") ) {
                        tts.speak("맞았어요", TextToSpeech.QUEUE_FLUSH, null);
                        count++;
                        Randomimage();
                    }
                    else {
                        tts.speak("다시한번 생각해볼래요", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    break;
            }

        } catch (Exception e) {
            toast(e.toString());
        }
    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void onBackPressed() {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage(count+"문제 맞췄습니다!\n"+"정말 종료 하시겠습니까?");
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG,count+"개,나감");

                Intent intent = new Intent();
                intent.putExtra("Count",String.valueOf(count));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alBuilder.setTitle("프로그램 종료");
        alBuilder.show();

    }
    public void onPause(){
        super.onPause();
     }

}

