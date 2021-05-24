package com.example.stt_tts;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Locale;

import android.speech.tts.TextToSpeech;

public class MainActivity extends Activity implements RecognitionListener, TextToSpeech.OnInitListener{


    private Intent recognizerIntent;
    private final int RESULT_SPEECH = 1000;
    final int PERMISSION = 1;
    private SpeechRecognizer speech;
    private TextView textView;
    private Button sttbtn,ttsbtn;
    private TextToSpeech tts;
    private EditText ttsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ( Build.VERSION.SDK_INT >= 23 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }


        //sst
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);

        textView = findViewById(R.id.sttResult);
        sttbtn = findViewById(R.id.sttStart);
        findViewById(R.id.sttStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ko-KR"); //언어지정입니다.
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);   //검색을 말한 결과를 보여주는 갯수
                startActivityForResult(recognizerIntent, RESULT_SPEECH);
            }
        });

        //tts
        tts = new TextToSpeech(this, this);
        //버튼, textview 연결
        ttsbtn = findViewById(R.id.ttsStart);
        ttsText = findViewById(R.id.ttsText);
        ttsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut();
            }
        });
    }
    @RequiresApi(api=Build.VERSION_CODES.LOLLIPOP)
    private void speakOut(){    //tts speakout 함수 : 입력된 텍스트를 음성으로 출력하는 함수
        CharSequence text = ttsText.getText();
        tts.setPitch((float)0.6);
        tts.setSpeechRate((float)1);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH,null,"id1");
    }

    @Override
    protected void onDestroy() {    //tts 후 destroy
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {     //sst에서 음성 인식 결과를 text로 화면에 출력
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH : {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    for(int i = 0; i < text.size() ; i++){
                        textView.setText(text.get(i));
                    }
                    for(int i = 0; i < text.size() ; i++){
                        Log.e("MainActivity", "onActivityResult text : " + text.get(i));
                    }
                }

                break;
            }
        }
    }

    @RequiresApi(api=Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) {        //tts 수행 성공시 초기화 정보
        if(status==TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.KOREA);
            if(result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "This Language is not supported");
            } else{
                ttsbtn.setEnabled(true);
                speakOut();
            }
        } else{
            Log.e("TTS","initalization Failed");
        }
    }



    @Override
    public void onError(int error) {
        String message;

        switch (error) {

            case SpeechRecognizer.ERROR_AUDIO:
                message = "오디오 에러";
                break;

            case SpeechRecognizer.ERROR_CLIENT:
                message = "클라이언트 에러";
                break;

            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "퍼미션없음";
                break;

            case SpeechRecognizer.ERROR_NETWORK:
                message = "네트워크 에러";
                break;

            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "네트웍 타임아웃";
                break;

            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "찾을수 없음";;
                break;

            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "바쁘대";
                break;

            case SpeechRecognizer.ERROR_SERVER:
                message = "서버이상";;
                break;

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "말하는 시간초과";
                break;

            default:
                message = "알수없는 에러";
                break;
        }

        Log.e("MainActivity", "SPEECH ERROR : " + message);
    }
    @Override
    public void onReadyForSpeech(Bundle params) {

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

    }
    @Override
    public void onResults(Bundle results) {

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }



}