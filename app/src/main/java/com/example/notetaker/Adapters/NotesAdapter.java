package com.example.notetaker.Adapters;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>
{

    private List<Note> notes;
    private NotesListener notesListener;

    public NotesAdapter(List<Note> notes, NotesListener notesListener)
    {
        this.notes = notes;
        this.notesListener = notesListener;
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
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position)
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
                textSubtitle.setText(note.getDate_time());

            textDateTime.setText(note.getNote_text());

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

            if(note.getImage_path() != null)
            {
                imageNote2.setImageBitmap(BitmapFactory.decodeFile(note.getImage_path()));
                imageNote2.setVisibility(View.VISIBLE);
            }
            else
            {
                imageNote2.setVisibility(View.GONE);
            }
        }
    }
}
