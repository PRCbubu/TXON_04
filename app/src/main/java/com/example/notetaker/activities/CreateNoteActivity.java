package com.example.notetaker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.notetaker.Database.NoteDatabase;
import com.example.notetaker.R;
import com.example.notetaker.entities.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateNoteActivity extends AppCompatActivity
{
    private AppCompatEditText inputNoteTitle, inputNoteSubtitle, inputNote;
    private AppCompatTextView textDateTime;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        AppCompatImageView imageBack = findViewById(R.id.imageBack);

        imageBack.setOnClickListener(view -> onBackPressed());

        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNote = findViewById(R.id.inputNote);

        textDateTime = findViewById(R.id.textDateTime);

        textDateTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));

        AppCompatImageView imageSave = findViewById(R.id.imageSave);

        imageSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                saveNote();
            }
        });
    }

    private void saveNote()
    {
        if(inputNoteTitle.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "Note title cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else if (inputNoteSubtitle.getText().toString().trim().isEmpty() && inputNote.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "Note cannot be empty", Toast.LENGTH_SHORT).show();
        }

        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNote_text(inputNote.getText().toString());
        note.setDate_time(textDateTime.getText().toString());


//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        Handler handler = new Handler(Looper.getMainLooper());
//
//        executor.execute(() ->
//        {
//
//            //Background work here
//            NoteDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
//
//            handler.post(() ->
//            {
//                //UI Thread work here
//                Intent intent = new Intent();
//                setResult(RESULT_OK, intent);
//                finish();
//            });
//        });


        class saveNoteTask extends AsyncTask<Void, Void, Void>
        {

            @Override
            protected Void doInBackground(Void... voids)
            {
                NoteDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        new saveNoteTask().execute();
    }
}