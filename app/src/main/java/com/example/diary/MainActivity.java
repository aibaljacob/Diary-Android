package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    Button btnAddEntry;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);
        btnAddEntry = findViewById(R.id.btnAddEntry);
        listView = findViewById(R.id.listView);

        // Open the Add Entry screen
        btnAddEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEntryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDiaryEntries(); // Reload list every time we come back to this screen
    }

    private void loadDiaryEntries() {
        Cursor cursor = myDb.getAllEntries();

        String[] from = new String[]{DatabaseHelper.COL_TITLE, DatabaseHelper.COL_DATE};
        int[] to = new int[]{R.id.textTitle, R.id.textDate};

        // Standard simple adapter (no more custom button handling needed)
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this, R.layout.list_item, cursor, from, to, 0);
        listView.setAdapter(adapter);

        // CLICK TO VIEW ENTRY
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);

                final String title = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TITLE));
                final String content = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_CONTENT));
                String date = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_DATE));

                // Create the popup with Close, Edit, and Delete buttons
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(title + " (" + date + ")")
                        .setMessage(content)
                        .setPositiveButton("Close", null) // Button 1
                        .setNeutralButton("Edit", new DialogInterface.OnClickListener() { // Button 2
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, AddEntryActivity.class);
                                intent.putExtra("ID", id);
                                intent.putExtra("TITLE", title);
                                intent.putExtra("CONTENT", content);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() { // Button 3
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Show a confirmation dialog before actually deleting
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Confirm Delete")
                                        .setMessage("Are you sure you want to delete this entry?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface innerDialog, int innerWhich) {
                                                myDb.deleteEntry(id); // Delete from DB
                                                loadDiaryEntries();   // Refresh the list
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                            }
                        })
                        .show();
            }
        });
    }
}