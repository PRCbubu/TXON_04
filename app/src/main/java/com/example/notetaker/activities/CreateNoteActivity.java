package com.example.notetaker.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.notetaker.Database.NoteDatabase;
import com.example.notetaker.R;
import com.example.notetaker.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
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

    private AppCompatImageView imageNote;
    private AppCompatTextView textWebURL;
    private LinearLayout layoutWebURL;

    private AppCompatImageView imageNote2;

    private View viewSubtitleIndicator;

    private String selectedNoteColour;
    private String selectedImagePath;
    private Note alreadyAvailableNote;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    private AlertDialog dialogAddURL;
    private AlertDialog dialogDeleteNote;

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
        imageNote = findViewById(R.id.imageNote);

        textWebURL = findViewById(R.id.textWebURL);
        layoutWebURL = findViewById(R.id.LayoutWebURL);

        imageNote2 = findViewById(R.id.imageNote2);

        textDateTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));

        AppCompatImageView imageSave = findViewById(R.id.imageSave);

        imageSave.setOnClickListener(view -> saveNote());

        selectedNoteColour = "#044040";
        selectedImagePath = "";

        if(getIntent().getBooleanExtra("isViewOnUpdate", false))
        {
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        findViewById(R.id.imageRemoveImage).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(View.GONE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
                selectedImagePath = "";
            }
        });

        findViewById(R.id.imageRemoveWebURL).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                textWebURL.setText(null);
                layoutWebURL.setVisibility(View.GONE);
            }
        });

        initMiscellaneous();
        setSubtitleIndicatorColour();
    }

    private void setViewOrUpdateNote()
    {
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        inputNote.setText(alreadyAvailableNote.getNote_text());
        textDateTime.setText(alreadyAvailableNote.getDate_time());

        if(alreadyAvailableNote.getWeb_link() != null && !alreadyAvailableNote.getWeb_link().trim().isEmpty())
        {
            textWebURL.setText(alreadyAvailableNote.getWeb_link());
            layoutWebURL.setVisibility(View.VISIBLE);
        }

        if(alreadyAvailableNote.getImage_path() != null && !alreadyAvailableNote.getImage_path().trim().isEmpty())
        {
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImage_path()));
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImage_path();
        }
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
        note.setColour(selectedNoteColour);
        note.setImage_path(selectedImagePath);

        if(layoutWebURL.getVisibility() == View.VISIBLE)
            note.setWeb_link(textWebURL.getText().toString());

        if(alreadyAvailableNote != null)
            note.setId(alreadyAvailableNote.getId());

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
                selectedNoteColour = "#044040";
                imageColour1.setImageResource(R.drawable.baseline_done_24);
                imageColour2.setImageResource(0);
                imageColour3.setImageResource(0);
                imageColour4.setImageResource(0);
                imageColour5.setImageResource(0);
                setSubtitleIndicatorColour();
            }
        });

        LayoutMiscellaneous.findViewById(R.id.viewColour2).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectedNoteColour = "#D61355";
                imageColour1.setImageResource(0);
                imageColour2.setImageResource(R.drawable.baseline_done_24);
                imageColour3.setImageResource(0);
                imageColour4.setImageResource(0);
                imageColour5.setImageResource(0);
                setSubtitleIndicatorColour();
            }
        });

        LayoutMiscellaneous.findViewById(R.id.viewColour3).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectedNoteColour = "#4D13D6";
                imageColour1.setImageResource(0);
                imageColour2.setImageResource(0);
                imageColour3.setImageResource(R.drawable.baseline_done_24);
                imageColour4.setImageResource(0);
                imageColour5.setImageResource(0);
                setSubtitleIndicatorColour();
            }
        });

        LayoutMiscellaneous.findViewById(R.id.viewColour4).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectedNoteColour = "#1DAC51";
                imageColour1.setImageResource(0);
                imageColour2.setImageResource(0);
                imageColour3.setImageResource(0);
                imageColour4.setImageResource(R.drawable.baseline_done_24);
                imageColour5.setImageResource(0);
                setSubtitleIndicatorColour();
            }
        });

        LayoutMiscellaneous.findViewById(R.id.viewColour5).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectedNoteColour = "#D64113";
                imageColour1.setImageResource(0);
                imageColour2.setImageResource(0);
                imageColour3.setImageResource(0);
                imageColour4.setImageResource(0);
                imageColour5.setImageResource(R.drawable.baseline_done_24);
                setSubtitleIndicatorColour();
            }
        });

        if(alreadyAvailableNote != null && alreadyAvailableNote.getColour() != null && !alreadyAvailableNote.getColour().trim().isEmpty())
        {
            switch (alreadyAvailableNote.getColour())
            {
                case "#D61355" : LayoutMiscellaneous.findViewById(R.id.viewColour2).performClick(); break;
                case "#4D13D6" : LayoutMiscellaneous.findViewById(R.id.viewColour3).performClick(); break;
                case "#1DAC51" : LayoutMiscellaneous.findViewById(R.id.viewColour4).performClick(); break;
                case "#D64113" : LayoutMiscellaneous.findViewById(R.id.viewColour5).performClick(); break;

            }
        }

        LayoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(CreateNoteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                }
                else
                {
                    selectImage();
                }
            }
        });

        LayoutMiscellaneous.findViewById(R.id.LayoutAddUrl).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddURLDialogue();
            }
        });

        if(alreadyAvailableNote != null)
        {
            LayoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            LayoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNoteDialogue();
                }
            });
        }
    }

    private void showDeleteNoteDialogue()
    {
        if(dialogDeleteNote == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );
            builder.setView(view);

            dialogDeleteNote = builder.create();

            if(dialogDeleteNote.getWindow() != null)
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void>
                    {

                        @Override
                        protected Void doInBackground(Void... voids)
                        {
                            NoteDatabase.getDatabase(getApplicationContext()).noteDao().deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused)
                        {
                            super.onPostExecute(unused);

                            Intent intent = new Intent();
                            intent.putExtra("isNoteDeleted", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    new DeleteNoteTask().execute();
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    dialogDeleteNote.dismiss();
                }
            });
        }

        dialogDeleteNote.show();
    }

    private void setSubtitleIndicatorColour()
    {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColour));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length >0)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                selectImage();
        }
        else
        {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK)
            if(data != null)
            {
                Uri selectedImageUri = data.getData();
                if(selectedImageUri != null)
                {
                    try
                    {

                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);

//                        Resources res = getResources();
//                        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
//                        dr.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight())/0.02f);
//
//                        imageNote.setImageDrawable(dr);

                        selectedImagePath = getPathFromUri(selectedImageUri);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
    }

    private String getPathFromUri(Uri contentUri)
    {
        String filePath;
        Cursor cursor = getContentResolver().query(contentUri,null, null, null, null);

        if(cursor == null)
            filePath = contentUri.getPath();
        else
        {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private void showAddURLDialogue()
    {
        if(dialogAddURL == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,
                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);

            dialogAddURL = builder.create();

            if(dialogAddURL.getWindow() != null)
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            final AppCompatEditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(inputURL.getText().toString().trim().isEmpty())
                        Toast.makeText(CreateNoteActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    else if(!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches())
                        Toast.makeText(CreateNoteActivity.this, "Enter Valid URL", Toast.LENGTH_SHORT).show();
                    else
                    {
                        textWebURL.setText(inputURL.getText().toString());
                        layoutWebURL.setVisibility(View.VISIBLE);
                        dialogAddURL.dismiss();
                    }
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    dialogAddURL.dismiss();
                }
            });
        }
        dialogAddURL.show();
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void selectImage()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if(intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);


    }
}