package com.example.notetaker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.notetaker.R;

public class MainActivity extends AppCompatActivity
{
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatImageView imageAddNotesButton = findViewById(R.id.imageAddNotesButton);
        imageAddNotesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Toast.makeText(getApplicationContext(), "Pressed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
                startActivity(intent);
                int requestCodeAddNote = REQUEST_CODE_ADD_NOTE;
            }
        });
    }
}