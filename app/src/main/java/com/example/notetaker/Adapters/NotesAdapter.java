package com.example.notetaker.Adapters;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetaker.Listeners.NotesListener;
import com.example.notetaker.R;
import com.example.notetaker.entities.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>
{

    private List<Note> notes;
    private NotesListener notesListener;

    private Timer timer;
    private List<Note> notesSource;

    public NotesAdapter(List<Note> notes, NotesListener notesListener)
    {
        this.notes = notes;
        this.notesListener = notesListener;
        notesSource = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, final int position)
    {
        holder.setNote(notes.get(position));
        holder.LayoutNote.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                notesListener.onNoteClicked(notes.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder
    {
        private void ToastMaker(String msg)
        {
            Log.d("msg", msg);
        }
        AppCompatTextView textTtile, textSubtitle, textDateTime;
        AppCompatImageView imageNote2;
        LinearLayout LayoutNote;

        NoteViewHolder(@NonNull View itemView)
        {
            super(itemView);

            textTtile = itemView.findViewById(R.id.textTtile);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            LayoutNote = itemView.findViewById(R.id.LayoutNote);
            imageNote2 = itemView.findViewById(R.id.imageNote2);
        }

        void setNote(Note note)
        {
            textTtile.setText(note.getTitle());

            if(note.getSubtitle().trim().isEmpty())
                textSubtitle.setVisibility(View.GONE);
            else
                textSubtitle.setText(note.getSubtitle());

            textDateTime.setText(note.getDate_time());

            GradientDrawable gradientDrawable = (GradientDrawable) LayoutNote.getBackground();
            if(note.getColour() != null)
            {
                //ToastMaker("Pressed");
                gradientDrawable.setColor(Color.parseColor(note.getColour()));
            }
            else
            {
                //ToastMaker("NotPressed");
                gradientDrawable.setColor(Color.parseColor("#044040"));
            }

            if(!note.getImage_path().isEmpty())
            {
                Log.i("sys", note.getImage_path());
                Log.i("SYSTEM", "");
                imageNote2.setImageBitmap(BitmapFactory.decodeFile(note.getImage_path()));
                imageNote2.setVisibility(View.VISIBLE);
            }
            else
            {
                imageNote2.setVisibility(View.GONE);
            }
        }
    }

    public void searchedNotes(final String searchKeyword)
    {
        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if(searchKeyword.trim().isEmpty())
                    notes = notesSource;
                else
                {
                    ArrayList<Note> temp = new ArrayList<>();
                    for(Note note : notesSource)
                    {
                        if(note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                        || note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                        || note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase()))
                            temp.add(note);
                    }
                    notes = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer()
    {
        if(timer != null)
        {
            timer.cancel();
        }
    }
}
