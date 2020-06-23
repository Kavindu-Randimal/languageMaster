package com.example.foreignlanguagepractice;

import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.example.foreignlanguagepractice.MainActivity.phraseDatabase;

public class Translate extends AppCompatActivity {

    private ListView phrase;
    private TextView tvTranslate;
    private Button btnPronouce;
    private String translatedTxt;
    private Spinner langSpinner;
    public DatabaseManager phrasesDatabase;
    List<String> phrases = new ArrayList<>();
    List<String> subscribedLangs = new ArrayList<>();
    List<String> subscribedLangCodes = new ArrayList<>();
    String selectedLangCode;
    String selectedPhrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phrasesDatabase = new DatabaseManager(this);
        setContentView(R.layout.activity_translate);
        phrase = findViewById(R.id.phrase_list);
        phrase.setChoiceMode(phrase.CHOICE_MODE_SINGLE);
        langSpinner = findViewById(R.id.lang_spinner);
        tvTranslate = findViewById(R.id.tvTranslate);
        btnPronouce = findViewById(R.id.btnPronounce);
        viewAll();
        setSpinner();
    }

    public void setSpinner() {
        Cursor res = phraseDatabase.getAllLangs();

        if (res.getCount() == 0) {
            Toast.makeText(Translate.this, "Nothing have subscribed", Toast.LENGTH_LONG).show();
        } else {
            while (res.moveToNext()) {
                String lang = res.getString(1);
                String langCode = res.getString(2);
                int isSubscribed = res.getInt(3);

                if (isSubscribed == 1) {
                    subscribedLangs.add(lang);
                    subscribedLangCodes.add(langCode);
                }
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subscribedLangs) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);

                text.setTextColor(Color.WHITE);

                return view;
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter
        langSpinner.setAdapter(dataAdapter);
    }

    public void viewAll() {
        Cursor res = phrasesDatabase.getAllData();

        if (res.getCount() == 0) {
            Toast.makeText(Translate.this, "Nothing to show", Toast.LENGTH_LONG).show();
        } else {
            while (res.moveToNext()) {
                String name = res.getString(1);
                phrases.add(name);
                ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, phrases) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);

                        text.setTextColor(Color.WHITE);

                        return view;
                    }
                };
                phrase.setAdapter(aa);
            }
            phrase.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                    for (int j = 0; j < parent.getChildCount(); j++) {
                        parent.getChildAt(j).setSelected(false);
                        parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                    }
                    selectedPhrase = phrases.get(position);
                    view.setBackgroundColor(Color.LTGRAY);
                }
            });
        }
    }

    public void translate(View view) {
        if (selectedPhrase == null) {
            Toast.makeText(Translate.this, "Please select a phrase to translate", Toast.LENGTH_LONG).show();
            return;
        }
        selectedLangCode = subscribedLangCodes.get(langSpinner.getSelectedItemPosition());
        TranslateTask task = new TranslateTask();
        task.execute();
    }

    public void play(View view) {
        SpeechTask task = new SpeechTask();
        task.execute();
    }

    private class TranslateTask extends AsyncTask<String, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(String... urls) {
            String urlString = "https://api.eu-gb.language-translator.watson.cloud.ibm.com/instances/a6a40fb5-95af-4213-b720-5b383e6b9396";

            String userCredentials = "apikey:zNKmZ9HB10-K2bIvPCsCFoMSmzitCQVh4U0RFB0GVHLy";
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

            String jsonInputString = "{\"text\": \"" + selectedPhrase + "\", \"model_id\":\"en-" + selectedLangCode + "\"}";

            HttpURLConnection urlConnection = null;
            URL url = null;
            JSONObject object = null;
            InputStream inStream = null;
            OutputStream outStream = null;
            try {
                url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", basicAuth);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                outStream = urlConnection.getOutputStream();
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                outStream.write(input, 0, input.length);

                inStream = urlConnection.getInputStream();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
                String temp, response = "";
                while ((temp = bReader.readLine()) != null) {
                    response += temp;
                }
                object = (JSONObject) new JSONTokener(response).nextValue();
                return (((JSONObject) ((JSONArray) object.get("translations")).get(0)).get("translation")).toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Failed to translate";
        }


        @Override
        protected void onPostExecute(String result) {
            tvTranslate.setText(result);
            btnPronouce.setEnabled(true);
            translatedTxt = result;
        }
    }

    private class SpeechTask extends AsyncTask<String, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(String... urls) {
            String urlString = "https://api.eu-gb.text-to-speech.watson.cloud.ibm.com/instances/f81f5f49-f4c8-42b9-bf22-5a60fdbadba1";

            String userCredentials = "apikey:e_GU2ne0qB1tAy6hIcIL4iy_menxnzxuS-ws9UI8UT3q";
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

            String jsonInputString = "{\"text\":\"" + translatedTxt + "\"}";

            HttpURLConnection urlConnection = null;
            URL url = null;
            JSONObject object = null;
            InputStream inStream = null;
            OutputStream outStream = null;
            try {
                url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", basicAuth);
                urlConnection.setRequestProperty("Content-Type", "application/json");
//                urlConnection.setRequestProperty("Accept", "audio/wav");

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                outStream = urlConnection.getOutputStream();
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                outStream.write(input, 0, input.length);

                inStream = urlConnection.getInputStream();

                // save file
                File path = new File(getExternalCacheDir(), "ltAudio");
                if (!path.exists()) {
                    path.mkdirs();
                }
                File file = new File(path, "test.wav");
                FileOutputStream fileOutput = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int bufferLength = 0;

                while ((bufferLength = inStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                }
                fileOutput.close();

                MediaPlayer mp = new MediaPlayer();
                mp.setDataSource(getApplicationContext(), Uri.parse("file://" + file.getPath()));
                mp.prepare();
                mp.start();
                return "Ok";

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Failed to translate";
        }
    }
}
