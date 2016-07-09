package programmer.ie.dictator.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;

import programmer.ie.dictator.R;
import programmer.ie.dictator.util.L;

public class TextToSpeechActivity extends Activity implements
        TextToSpeech.OnInitListener {
    private static final int MY_DATA_CHECK_CODE = 2534;
    private static final HashMap<String, String> ttsParams = new HashMap<String, String>();

    static {
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(AudioManager.STREAM_NOTIFICATION));
    }

    private TextToSpeech tts;
    private Button btnSpeak;
    private TextView txtText;
    private String lastTextToSpeak;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);
        checkIsTTSInstalled();

        tts = new TextToSpeech(this, this);
        btnSpeak = (Button) findViewById(R.id.btnSpeak);
        txtText = (TextView) findViewById(R.id.txtText);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                speakOut();
            }
        });
    }


    public void queueSpeak(String textToSpeak) {
        if (tts != null) {
            tts.speak(textToSpeak, TextToSpeech.QUEUE_ADD, ttsParams);
            while (tts.isSpeaking()) {
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    L.e(e.getMessage());
                }
            }
        } else {
            this.lastTextToSpeak = textToSpeak;
            tts = new TextToSpeech(this, this);
        }
    }


    public void checkIsTTSInstalled() {
        final int MY_DATA_CHECK_CODE = 2534;
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
            } else {
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                L.e("This Language is not supported");
            } else {
                btnSpeak.setEnabled(true);
                speakOut();
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut() {
        String text = txtText.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}