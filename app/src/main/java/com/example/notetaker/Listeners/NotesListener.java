package com.example.notetaker.Listeners;

import com.example.notetaker.entities.Note;

public interface NotesListener
{
    void onNoteClicked(Note note, int position);
}
