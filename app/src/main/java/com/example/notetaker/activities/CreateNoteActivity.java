package com.example.notetaker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.notetaker.Database.NoteDatabase;
import com.example.notetaker.R;
import com.example.notetaker.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateNoteActivity extends AppCompatActivity
{
    private AppCompatEditText inputNoteTitle, inputNoteSubtitle, inputNote;
    private AppCompatTextView textDateTime;

    private View viewSubtitleIndicator;

    private String selectedColour;
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
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);

        textDateTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));

        AppCompatImageView imageSave = findViewById(R.id.imageSave);

        imageSave.setOnClickListener(view -> saveNote());

        //selectedColour = "#044040";

        initMiscellaneous();
        setSubtileIndicatorColour();
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
        note.setColour(selectedColour);


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

    private void initMiscellaneous()
    {
        final LinearLayout LayoutMiscellaneous = findViewById(R.id.LayoutMiscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(LayoutMiscellaneous);

        LayoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final AppCompatImageView imageColour1 =  findViewById(R.id.imageColour1);
        final AppCompatImageView imageColour2 =  findViewById(R.id.imageColour2);
        final AppCompatImageView imageColour3 =  findViewById(R.id.imageColour3);
        final AppCompatImageView imageColour4 =  findViewById(R.id.imageColour4);
        final AppCompatImageView imageColour5 =  findViewById(R.id.imageColour5);

        LayoutMiscellaneous.findViewById(R.id.viewColour1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectedColour = "#044040";
                imageColour1.setImageResource(R.drawable.baseline_done_24);
                imageColour2.setImageResource(0);
                imageColour3.setImageResource(0);
                imageColour4.setImageResource(0);
                imageColour5.setImageResource(0);
                setSubtileIndicatorColour();
            }
        });

        LayoutMiscellaneous.findViewById(R.id.viewColour2).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectedColour = "#D61355";
                imageColour1.setImageResource(0);
                imageColour2.setImageResource(R.drawable.baseline_done_24);
                imageColour3.setImageResource(0);
                imageColour4.setImageResource(0);
                imageColour5.setImageResource(0);
                setSubtileIndicatorColour();
            }
        });

        LayoutMiscellaneous.findViewById(R.id.viewColoue3).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectedColour = "#4D13D6";
                imageColour1.setImageResource(0);
                imageColour2.setImageResource(0);
                imageColour3.setImageResource(R.drawable.baseline_done_24);
                imageColour4.setImageResource(0);
                imageColour5.setImageResource(0);
                setSubtileIndicatorColour();
            }
        });

        LayoutMiscellaneous.findViewById(R.id.viewColoue4).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectedColour = "#1DAC51";
                imageColour1.setImageResource(0);
                imageColour2.setImageResource(0);
                imageColour3.setImageResource(0);
                imageColour4.setImageResource(R.drawable.baseline_done_24);
                imageColour5.setImageResource(0);
                setSubtileIndicatorColour();
            }
        });

        LayoutMiscellaneous.findViewById(R.id.viewColoue5).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectedColour = "#D61355";
                imageColour1.setImageResource(0);
                imageColour2.setImageResource(0);
                imageColour3.setImageResource(0);
                imageColour4.setImageResource(0);
                imageColour5.setImageResource(R.drawable.baseline_done_24);
                setSubtileIndicatorColour();
            }
        });

    }

    private void setSubtileIndicatorColour()
    {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedColour));
    }
}