package com.example.notetaker.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.notetaker.Adapters.NotesAdapter;
import com.example.notetaker.Database.NoteDatabase;
import com.example.notetaker.Listeners.NotesListener;
import com.example.notetaker.R;
import com.example.notetaker.entities.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener
{
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTES = 3;


    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;

    private int noteClickedPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatImageView imageAddNotesButton = findViewById(R.id.imageAddNotesButton);
        imageAddNotesButton.setOnClickListener(view ->
        {
            //Toast.makeText(getApplicationContext(), "Pressed", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
//            startActivity(intent);
            startActivityForResult(
                    new Intent(getApplicationContext(), CreateNoteActivity.class), REQUEST_CODE_ADD_NOTE
            );
        });

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        notesRecyclerView.setAdapter(notesAdapter);

        getNotes(REQUEST_CODE_SHOW_NOTES, false);

        AppCompatEditText inputSearch = findViewById(R.id.SearchText);
        inputSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(noteList.size() != 0)
                    notesAdapter.searchedNotes(editable.toString());
            }
        });
    }


    @Override
    public void onNoteClicked(Note note, int position)
    {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOnUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted)
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
//                if(noteList.size() == 0)
//                {
//                    noteList.addAll(notes);
//                    notesAdapter.notifyDataSetChanged();
//                }
//                else
//                {
//                    noteList.add(0, notes.get(0));
//                    notesAdapter.notifyItemInserted(0);
//                }
//                notesRecyclerView.smoothScrollToPosition(0);

                if(requestCode == REQUEST_CODE_SHOW_NOTES)
                {
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                }
                else if (requestCode == REQUEST_CODE_ADD_NOTE)
                {
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE)
                {
                    noteList.remove(noteClickedPosition);

                    if(isNoteDeleted)
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    else
                    {
                        noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }

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
            getNotes(REQUEST_CODE_ADD_NOTE, false);
        else if(requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK)
            if(data != null)
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));

    }
}
