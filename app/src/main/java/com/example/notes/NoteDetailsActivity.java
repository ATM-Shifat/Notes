package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {
    EditText titleEditText,contentEditText;
    ImageButton saveNoteButton, deleteNoteButton;
    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText = findViewById(R.id.notes_title);
        contentEditText = findViewById(R.id.notes_content);
        saveNoteButton = findViewById(R.id.save_note_button);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteButton  = findViewById(R.id.delete_note_button);

        title  = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);

        if(isEditMode){
            pageTitleTextView.setText("Edit your note");
            deleteNoteButton.setVisibility(View.VISIBLE);
        }

        saveNoteButton.setOnClickListener( (v)-> saveNote());

        deleteNoteButton.setOnClickListener((v)-> deleteNoteFromFirebase() );
    }

    void saveNote(){
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        if(title==null || title.isEmpty()){
            titleEditText.setError("Empty Title");
            return;
        }
        else if(content == null || content.isEmpty() ){
            contentEditText.setText("Empty Content");
            return;
        }
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;
        if (isEditMode) {
            //update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        } else {
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }


        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //note is added
                    Utility.showToast(NoteDetailsActivity.this, "Note successfully added");
                    finish();
                } else {
                    Utility.showToast(NoteDetailsActivity.this, "Note adding failed");
                }
            }
        });
    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is deleted
                    Utility.showToast(NoteDetailsActivity.this,"Note deleted successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this,"Failed while deleting note");
                }
            }
        });
    }
}