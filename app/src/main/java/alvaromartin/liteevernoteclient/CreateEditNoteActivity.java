package alvaromartin.liteevernoteclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import alvaromartin.liteevernoteclient.utils.*;

/**
 * Created by Alvaro on 21/01/2016.
 */
public class CreateEditNoteActivity extends Activity {

    private final String tag_class = CreateEditNoteActivity.this.getClass().getSimpleName();

    // Edit Text to show title and description
    private EditText mTitle;
    private EditText mDescription;

    private String mCurrentEditedNoteId = "-1";
    private boolean isCreateAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag_class, "onCreate");
        setContentView(R.layout.activity_create_note);

        // Receive intent
        Intent intent = getIntent();
        if (intent != null) {
            isCreateAction = intent.getBooleanExtra(NotesListActivity.ACTION_CREATE, true);
        } else {
            // by default consider a create action
            isCreateAction = true;
        }

        // UI elements
        mTitle = (EditText) findViewById(R.id.create_note_title);
        mDescription = (EditText) findViewById(R.id.create_note_desc);
        Button saveEditButton = (Button) findViewById(R.id.create_note_edit_button);

        if (isCreateAction) {
            Log.d(tag_class, getString(R.string.new_note_to_create));
            // prepare the UI to create a new note
            mTitle.getEditableText().clear();
            mDescription.getEditableText().clear();
            // button text
            saveEditButton.setText(getString(R.string.save));
        } else {
            Log.d(tag_class, getString(R.string.note_to_edit));
            // prepare the UI to edit an existing note
            Intent noteInfo = getIntent();
            // save note id
            mCurrentEditedNoteId = noteInfo.getStringExtra(Constants.KEY_ID);
            // set note info
            mTitle.setText(noteInfo.getStringExtra(Constants.KEY_TITLE));
            mDescription.setText(Html.fromHtml(noteInfo.getStringExtra(Constants.KEY_DESCRIPTION)));
            // button text
            saveEditButton.setText(getString(R.string.edit));
        }

        saveEditButton.setOnClickListener(new View.OnClickListener() {
            // the same button is useful for the action save and edit
            @Override
            public void onClick(View v) {
                String title = mTitle.getText().toString();
                String description = mDescription.getText().toString();
                // validation (description is not mandatory)
                if (title.length() > 0) {
                    // create intent for sending result data
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(Constants.KEY_ID, mCurrentEditedNoteId);
                    resultIntent.putExtra(Constants.KEY_TITLE, title);
                    resultIntent.putExtra(Constants.KEY_DESCRIPTION, description);
                    setResult(RESULT_OK, resultIntent);
                    // destroy this activity to go back to the previous activity
                    finish();
                } else {
                    // inform the user with a dialog message
                    Utils.showDialogMessage(CreateEditNoteActivity.this, getString(R.string.error_title_needed));
                }
            }
        });
    }
}
