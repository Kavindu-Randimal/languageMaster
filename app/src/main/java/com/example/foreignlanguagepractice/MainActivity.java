package com.example.foreignlanguagepractice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static DatabaseManager phraseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phraseDatabase = new DatabaseManager(this);

        Button buttonAddPhrases = findViewById(R.id.buttonAddPhrases);
        Button buttonDisplayPhrases = findViewById(R.id.buttonDisplayPhrases);
        Button buttonEditPhrases = findViewById(R.id.buttonEditPhrases);
        Button buttonLanguageSubscription = findViewById(R.id.buttonLanguageSubscription);
        Button buttonTranslate = findViewById(R.id.buttonTranslate);
        Button buttonOffline = findViewById(R.id.buttonOffline);


        buttonAddPhrases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), AddPhrases.class);
                startActivity(startIntent);
            }
        });

        buttonDisplayPhrases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), DisplayPhrases.class);
                startActivity(startIntent);
            }
        });

        buttonEditPhrases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), EditPhrases.class);
                startActivity(startIntent);
            }
        });

        buttonLanguageSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), LanguageSubscription.class);
                startActivity(startIntent);
            }
        });

        buttonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), Translate.class);
                startActivity(startIntent);
            }
        });

        buttonOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), Offline.class);
                startActivity(startIntent);
            }
        });
    }
}
