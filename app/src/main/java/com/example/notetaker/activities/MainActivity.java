package com.example.notetaker.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.notetaker.Adapters.NotesAdapter;
import com.example.notetaker.Database.NoteDatabase;
import com.example.notetaker.R;
import com.example.notetaker.entities.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static final int REQUEST_CODE_ADD_NOTE = 1;

    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;
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

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList);
        notesRecyclerView.setAdapter(notesAdapter);

        getNotes();
    }




    private void getNotes()
    {

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
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>>
        {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(List<Note> notes)
            {
                super.onPostExecute(notes);
                if(noteList.size() == 0)
                {
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                }
                else
                {
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                }
                notesRecyclerView.smoothScrollToPosition(0);
            }

            @Override
            protected List<Note> doInBackground(Void... voids)
            {
                return NoteDatabase.getDatabase(getApplicationContext()).noteDao().getAllNotes();
            }
        }

        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK)
            getNotes();
    }
}
