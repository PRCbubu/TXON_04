package com.example.notetaker.Adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notetaker.R;
import com.example.notetaker.entities.Note;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>
{
    private List<Note> notes;

    public NotesAdapter(List<Note> notes)
    {
        this.notes = notes;
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
        AppCompatTextView textTtile, textSubtitle, textDateTime;
        LinearLayout LayoutNote;

        NoteViewHolder(@NonNull View itemView)
        {
            super(itemView);

            textTtile = itemView.findViewById(R.id.textTtile);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
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
                gradientDrawable.setColor(Color.parseColor(note.getColour()));
            }
            else
            {
                gradientDrawable.setColor(Color.parseColor("#0440400"));
            }
        }
    }
}
