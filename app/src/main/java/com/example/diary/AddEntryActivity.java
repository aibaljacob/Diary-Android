package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEntryActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText editTitle, editContent;
    Button btnSave;

    long entryId = -1; // -1 means it's a new entry

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        myDb = new DatabaseHelper(this);
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        btnSave = findViewById(R.id.btnSave);

        // CHECK FOR EDIT MODE
        Intent intent = getIntent();
        if (intent.hasExtra("ID")) {
            // We are editing an existing entry!
            entryId = intent.getLongExtra("ID", -1);
            editTitle.setText(intent.getStringExtra("TITLE"));
            editContent.setText(intent.getStringExtra("CONTENT"));
            btnSave.setText("Update Entry"); // Change button text
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString().trim();
                String content = editContent.getText().toString().trim();

                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(AddEntryActivity.this, "Please enter title and content", Toast.LENGTH_SHORT).show();
                    return;
                }

                String currentDate = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(new Date());
                boolean isSuccess;

                if (entryId == -1) {
                    // It's a NEW entry
                    isSuccess = myDb.addEntry(title, content, currentDate);
                } else {
                    // It's an EXISTING entry being UPDATED
                    isSuccess = myDb.updateEntry(entryId, title, content, currentDate);
                }

                if (isSuccess) {
                    Toast.makeText(AddEntryActivity.this, "Diary saved!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to MainActivity
                } else {
                    Toast.makeText(AddEntryActivity.this, "Error saving diary", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}