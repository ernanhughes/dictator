package com.banba.dictator;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RecordToTextListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements OnClickListener, TextToSpeech.OnInitListener {
        //voice recognition and general variables
//variable for checking Voice Recognition support on user device
        private static final int VR_REQUEST = 999;
        //ListView for displaying suggested words
        private ListView wordList;
        //Log tag for output information
        private final String LOG_TAG = "SpeechRepeatActivity";//***enter your own tag here***
        //TTS variables
//variable for checking TTS engine data on user device
        private int MY_DATA_CHECK_CODE = 0;
        //Text To Speech instance
        private TextToSpeech repeatTTS;

        public PlaceholderFragment() {
        }

        public void onClick(View v) {
            if (v.getId() == R.id.speech_btn) {
                //listen for results
                listenToSpeech();
            }
        }

        private void listenToSpeech() {
            //start the speech recognition intent passing required data
            Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            //indicate package
            listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, PlaceholderFragment.class.getPackage().getName());
            //message to display while listening
            listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a word!");
            //set speech model
            listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            //specify number of results to retrieve
            listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
            //start listening
            startActivityForResult(listenIntent, VR_REQUEST);
        }

        public void onInit(int initStatus) {
            //if successful, set locale
            if (initStatus == TextToSpeech.SUCCESS)
                repeatTTS.setLanguage(Locale.US);//***choose your own locale here***
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            //check speech recognition result
            if (requestCode == VR_REQUEST && resultCode == RESULT_OK) {
                //store the returned word list as an ArrayList
                ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                //set the retrieved list to display in the ListView using an ArrayAdapter
                wordList.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.word, suggestedWords));
            }

            //returned from TTS data check
            if (requestCode == MY_DATA_CHECK_CODE) {
                //we have the data - create a TTS instance
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
                    repeatTTS = new TextToSpeech(getActivity(), this);
                    //data not installed, prompt the user to install it
                else {
                    //intent will take user to TTS download page in Google Play
                    Intent installTTSIntent = new Intent();
                    installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installTTSIntent);
                }
            }

            //tss code here
            //call superclass method
            super.onActivityResult(requestCode, resultCode, data);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_record_to_text, container, false);
            //gain reference to speak button
            Button speechBtn = (Button) rootView.findViewById(R.id.speech_btn);
//gain reference to word list
            wordList = (ListView) rootView.findViewById(R.id.word_list);
//detect user clicks of suggested words
            wordList.setOnItemClickListener(new OnItemClickListener() {
                //click listener for items within list
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //cast the view
                    TextView wordView = (TextView) view;
                    //retrieve the chosen word
                    String wordChosen = (String) wordView.getText();
                    //output for debugging
                    Log.v(LOG_TAG, "chosen: " + wordChosen);
                    //output Toast message
                    Toast.makeText(getActivity(), "You said: " + wordChosen, Toast.LENGTH_SHORT).show();//**alter for your Activity name***

                    //speak the word using the TTS
                    repeatTTS.speak("You said: " + wordChosen, TextToSpeech.QUEUE_FLUSH, null);
                }
            });

//find out whether speech recognition is supported
            PackageManager packManager = getActivity().getPackageManager();
            List<ResolveInfo> intActivities = packManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
            if (intActivities.size() != 0) {
                //speech recognition is supported - detect user button clicks
                speechBtn.setOnClickListener(this);
                //prepare the TTS to repeat chosen words
                Intent checkTTSIntent = new Intent();
//check TTS data
                checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
//start the checking Intent - will retrieve result in onActivityResult
                startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
            } else {
                //speech recognition not supported, disable button and output message
                speechBtn.setEnabled(false);
                Toast.makeText(getActivity(), "Oops - Speech recognition not supported!", Toast.LENGTH_LONG).show();
            }


            return rootView;
        }
    }

}
