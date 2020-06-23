package com.example.foreignlanguagepractice;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static com.example.foreignlanguagepractice.MainActivity.phraseDatabase;

public class Offline extends AppCompatActivity {

    ListView table;
    Spinner langs;
    List<String> languages = new ArrayList<>();
    List<String> languageCodes = new ArrayList<>();
    List<String> phrases = new ArrayList<>();
    String selectedLangCode;
    public DatabaseManager phrasesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);
        table = findViewById(R.id.offlineData);
        langs = findViewById(R.id.langs);
        phrasesDatabase = new DatabaseManager(this);
        table.addHeaderView(getLayoutInflater().inflate(R.layout.table_layout, null, false));

        setSpinner();
    }

    private void loadTable() {
        Cursor res = phrasesDatabase.getOfflineData(selectedLangCode);
        final ArrayList<String> phrases = new ArrayList<>();
        final ArrayList<String> translations = new ArrayList<>();
        phrases.clear();
        translations.clear();

        if (res.getCount() == 0) {
            Toast.makeText(Offline.this, "Nothing to show", Toast.LENGTH_LONG).show();
        } else {
            while (res.moveToNext()) {
                String phrase = res.getString(1);
                String translation = res.getString(2);
                phrases.add(phrase);
                translations.add(translation);
            }
        }
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.table_layout, R.id.tablePhrase, phrases) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView phrase = (TextView) view.findViewById(R.id.tablePhrase);
                TextView translation = (TextView) view.findViewById(R.id.tableTranslation);
                translation.setText(translations.get(position));

                phrase.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                translation.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                return view;
            }
        };
        table.setAdapter(aa);
        table.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int i, long p4) {
            }
        });
    }


    public void setSpinner() {
        Cursor langs1 = phraseDatabase.getAllLangs();
        Cursor phrases1 = phraseDatabase.getAllData();

        if (langs1.getCount() == 0) {
            Toast.makeText(Offline.this, "No languages found", Toast.LENGTH_LONG).show();
        } else {
            while (langs1.moveToNext()) {
                String lang = langs1.getString(1);
                String langCode = langs1.getString(2);
                languages.add(lang);
                languageCodes.add(langCode);
            }

            if (phrases1.getCount() == 0) {
                Toast.makeText(Offline.this, "No languages found", Toast.LENGTH_LONG).show();
            } else {
                while (phrases1.moveToNext()) {
                    String phrase = phrases1.getString(1);
                    phrases.add(phrase);
                }
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages) {
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
        langs.setAdapter(dataAdapter);
        selectedLangCode = languageCodes.get(0);
        langs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedLangCode = languageCodes.get(position);
                loadTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    public void saveOffline(View view) {
        TranslateTask translateTask = new TranslateTask();
        translateTask.execute();
    }

    private class TranslateTask extends AsyncTask<String, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(String... urls) {
            String urlString = "https://api.us-south.language-translator.watson.cloud.ibm.com/instances/2b084659-4952-4486-a70b-bbeeee671351/v3/translate?version=2018-05-01";

            String userCredentials = "apikey:mfPUEcD2mBa4oU1m1j5d_H_ECi73M2pN06IW3Vvei7tk";
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

            JSONArray array = new JSONArray(phrases);
            String jsonInputString = "{\"text\": " + array.toString() + ", \"model_id\":\"en-" + selectedLangCode + "\"}";

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
                JSONArray translations = ((JSONArray) object.get("translations"));
                for (int i = 0; i < translations.length(); i++) {
                    String translation = ((JSONObject) translations.get(i)).get("translation").toString();
                    phrasesDatabase.insertOfflineData(phrases.get(i), translation, selectedLangCode);
                }
                return "Done";

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Failed to translate";
        }


        @Override
        protected void onPostExecute(String result) {

        }
    }
}
