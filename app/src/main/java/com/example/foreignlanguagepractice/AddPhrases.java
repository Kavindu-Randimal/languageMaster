package com.example.foreignlanguagepractice;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import static com.example.foreignlanguagepractice.MainActivity.phraseDatabase;

public class AddPhrases extends AppCompatActivity {

    EditText etAddPhrase;
    Button btnAddPhrase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phrases);

        etAddPhrase = findViewById(R.id.et_Add_phrase);
        btnAddPhrase = findViewById(R.id.btnAddPhrase);
        addData();
    }

    private void addData() {
        btnAddPhrase.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!(etAddPhrase.getText().toString().equals(""))) {
                            Cursor res = phraseDatabase.getAllData();
                            int i = 0;
                            while (res.moveToNext()) {
                                if (res.getString(0).equals(etAddPhrase.toString())) {
                                    i++;
                                }
                            }
                            boolean isInserted;
                            if (i != 1) {
                                isInserted = phraseDatabase.insertData(etAddPhrase.getText().toString());
                                if (isInserted) {
                                    Toast.makeText(AddPhrases.this, "Data Inserted", Toast.LENGTH_LONG).show();
                                    etAddPhrase.setText("");
                                }else
                                    Toast.makeText(AddPhrases.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddPhrases.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AddPhrases.this, "Empty fields found", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }
}
