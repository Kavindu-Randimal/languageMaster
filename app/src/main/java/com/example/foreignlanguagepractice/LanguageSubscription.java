package com.example.foreignlanguagepractice;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.foreignlanguagepractice.MainActivity.phraseDatabase;

public class LanguageSubscription extends AppCompatActivity {

    ListView languageList;
    List<String> languages = new ArrayList<>();
    List<Integer> languageSubscriptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_subscription);
        languageList = findViewById(R.id.lang_list);
        languageList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        loadData();
    }

    public void loadData() {
        Cursor res = phraseDatabase.getAllLangs();

        if (res.getCount() == 0) {
            Toast.makeText(LanguageSubscription.this, "Nothing to show", Toast.LENGTH_LONG).show();
        } else {
            while (res.moveToNext()) {
                String phrase = res.getString(1);
                languages.add(phrase);

                int isSubscribed = res.getInt(3);
                languageSubscriptions.add(isSubscribed);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, languages) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    View view = super.getView(position, convertView, parent);
                    TextView text = (TextView) view.findViewById(android.R.id.text1);

                    text.setTextColor(Color.WHITE);
                    return view;
                }
            };
            languageList.setAdapter(arrayAdapter);

            for (int i = 0; i < languageSubscriptions.size(); i++) {
                if (languageSubscriptions.get(i) == 1) {
                    languageList.setItemChecked(i, true);
                }
            }

            languageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // selected item
//                    selectedPosition = position;
                }
            });
        }
    }

    public void update(View view) {
        SparseBooleanArray arr = languageList.getCheckedItemPositions();
        for (int i =0; i<languageList.getCount(); i++) {
            int subscribed = arr.get(i) ? 1 : 0;
            languageSubscriptions.set(i, subscribed);
            phraseDatabase.updateSubscription(String.valueOf(i + 1), subscribed);
        }
        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_LONG).show();
    }
}