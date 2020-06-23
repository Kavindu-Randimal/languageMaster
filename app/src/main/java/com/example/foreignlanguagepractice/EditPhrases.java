package com.example.foreignlanguagepractice;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static com.example.foreignlanguagepractice.MainActivity.phraseDatabase;

public class EditPhrases extends AppCompatActivity {

    ListView checkList;
    ArrayList<String> selectedItems;
    EditText etEditPhrase;
    Button btnEditPhraseEdit;
    int selectedPosition = -1;
    final ArrayList<String> items = new ArrayList<>();
    final ArrayList<Integer> itemIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phrases);
        checkList = findViewById(R.id.checkable_list);
        checkList.setChoiceMode(checkList.CHOICE_MODE_SINGLE);
        selectedItems = new ArrayList<String>();
        etEditPhrase = findViewById(R.id.etEditPhrase);
        btnEditPhraseEdit = findViewById(R.id.btnEditPhraseEdit);
        viewAll();

    }

    public void viewAll() {
        Cursor res = phraseDatabase.getAllData();

        if (res.getCount() == 0) {
            Toast.makeText(EditPhrases.this, "Nothing to show", Toast.LENGTH_LONG).show();
        } else {
            while (res.moveToNext()) {
                String phrase = res.getString(1);
                items.add(phrase);

                int itemId = res.getInt(0);
                itemIds.add(itemId);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, items) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    View view = super.getView(position, convertView, parent);
                    TextView text = (TextView) view.findViewById(android.R.id.text1);

                    text.setTextColor(Color.WHITE);

                    return view;
                }
            };
            checkList.setAdapter(arrayAdapter);

            checkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // selected item
                    selectedPosition = position;
                }
            });
        }
    }

    public void edit(View view) {
        if (selectedPosition != -1) {
            etEditPhrase.setText(items.get(selectedPosition));
        } else {
            Toast.makeText(EditPhrases.this, "Please Select a Phrase to edit", Toast.LENGTH_LONG).show();
        }
    }

    public void update(View v){
        boolean isUpdate = phraseDatabase.updateData(etEditPhrase.getText().toString(), itemIds.get(selectedPosition).toString());
        if (isUpdate) {
            Toast.makeText(EditPhrases.this, "Updated", Toast.LENGTH_LONG).show();

            // refresh
            Intent intent = getIntent();
            finish();
            startActivity(intent);

        } else {
            Toast.makeText(EditPhrases.this, "Not Updated", Toast.LENGTH_LONG).show();

        }
    }

}
