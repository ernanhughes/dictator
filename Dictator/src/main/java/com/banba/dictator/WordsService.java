package com.banba.dictator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.banba.dictator.ui.L;
import com.banba.dictator.ui.util.BundleUtil;

import java.lang.ref.WeakReference;

/**
 * Created by Ernan on 26/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class WordsService extends Service {

    protected static AudioManager mAudioManager;
    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;
    protected final Messenger mServerMessenger = new Messenger(new IncomingHandler(this));

    protected boolean mIsListening;
    protected volatile boolean mIsCountDownOn;
    private static boolean mIsStreamSolo = false;

    public static final String MSG_MESSAGE = "MESSAGE_MSG";
    static final int MSG_RECOGNIZER_START_LISTENING = 1;
    static final int MSG_RECOGNIZER_CANCEL = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        boolean available = SpeechRecognizer.isRecognitionAvailable(this);
        if (available) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
            mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    this.getPackageName());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message m = (Message) intent.getExtras().get(MSG_MESSAGE);
        try {
            mServerMessenger.send(m);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    protected static class IncomingHandler extends Handler {
        private WeakReference<WordsService> mtarget;

        IncomingHandler(WordsService target) {
            mtarget = new WeakReference<WordsService>(target);
        }


        @Override
        public void handleMessage(Message msg) {
            final WordsService target = mtarget.get();

            switch (msg.what) {
                case MSG_RECOGNIZER_START_LISTENING:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if (!mIsStreamSolo) {
                            mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);
                        }
                    }
                    if (!target.mIsListening) {
                        target.mSpeechRecognizer.startListening(target.mSpeechRecognizerIntent);
                        target.mIsListening = true;
                        L.d("message start listening");
                    }
                    break;

                case MSG_RECOGNIZER_CANCEL:
                    if (mIsStreamSolo) {
                        mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, false);
                        mIsStreamSolo = false;
                    }
                    target.mSpeechRecognizer.cancel();
                    target.mIsListening = false;
                    L.d("message canceled recognizer");
                    break;
            }
        }
    }


    // Count down timer for Jelly Bean work around
    protected CountDownTimer mNoSpeechCountDown = new CountDownTimer(5000, 5000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            mIsCountDownOn = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
            try {
                mServerMessenger.send(message);
                message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                mServerMessenger.send(message);
            } catch (RemoteException e) {
                L.e(e.getMessage());
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIsCountDownOn) {
            mNoSpeechCountDown.cancel();
        }
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected class SpeechRecognitionListener implements RecognitionListener {
        @Override
        public void onBeginningOfSpeech() {
            if (mIsCountDownOn) {
                mIsCountDownOn = false;
                mNoSpeechCountDown.cancel();
            }
            L.d("onBeginningOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            L.d("onBufferReceived"); //$NON-NLS-1$
        }

        @Override
        public void onEndOfSpeech() {
            L.d("onEndOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onError(int error) {
            String errorMessage = "Error: " + error;
            switch (error) {
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    errorMessage = "Network operation timed out.";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    errorMessage = "Other network related errors.";
                    break;
                case SpeechRecognizer.ERROR_AUDIO:
                    errorMessage = "Audio recording error.";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    errorMessage = "Server sends error status.";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    errorMessage = "Other client side errors.";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    errorMessage = "No speech input.";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    errorMessage = "No recognition result matched.";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    errorMessage = "RecognitionService busy.";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    errorMessage = "Insufficient permissions.";
                    break;
            }
            DictatorApp.reportException(WordsService.this, new Exception(errorMessage));
            L.e(errorMessage);

            if (mIsCountDownOn) {
                mIsCountDownOn = false;
                mNoSpeechCountDown.cancel();
            }
            mIsListening = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
            try {
                mServerMessenger.send(message);
            } catch (RemoteException e) {
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            L.d("onPartialResults " + BundleUtil.toString(params)); //$NON-NLS-1$
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            L.d("onPartialResults " + BundleUtil.toString(partialResults)); //$NON-NLS-1$
            Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
            try {
                mServerMessenger.send(message);
            } catch (RemoteException e) {
            }
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mIsCountDownOn = true;
                mNoSpeechCountDown.start();
            }
            L.d("onReadyForSpeech " + BundleUtil.toString(params)); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results) {
            L.d("onResults " + BundleUtil.toString(results)); //$NON-NLS-1$
            Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
            try {
                mServerMessenger.send(message);
            } catch (RemoteException e) {
            }
        }

        @Override
        public void onRmsChanged(float rms) {
            L.d("onRmsChanged"); //$NON-NLS-1$
        }
    }
}
