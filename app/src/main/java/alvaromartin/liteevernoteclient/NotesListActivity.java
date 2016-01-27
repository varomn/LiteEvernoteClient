package alvaromartin.liteevernoteclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.NoteSortOrder;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import alvaromartin.liteevernoteclient.adapters.CustomArrayAdapter;
import alvaromartin.liteevernoteclient.model.NoteFinal;
import alvaromartin.liteevernoteclient.utils.*;

/**
 * Created by Alvaro on 21/01/2016.
 */
public class NotesListActivity extends AppCompatActivity {

    private final String tag_class = NotesListActivity.this.getClass().getSimpleName();

    // Actions
    public static final String ACTION_CREATE = "actionCreate";

    // Requests for result
    public static final int REQUEST_CREATE = 1;
    public static final int REQUEST_EDIT = 2;

    // Notes array list (no persistence)
    private ArrayList<NoteFinal> mNotes = new ArrayList<NoteFinal>();

    // ListView
    private ListView mListView;

    // Text view to indicate order to show notes list
    private TextView mIndOrder;

    // Adapter
    private CustomArrayAdapter mListAdapter;

    // Note clicked
    private NoteFinal mClickedNote;

    // Task to update notes list
    private UpdateListTask updateListTask;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag_class, "onCreate");
        setContentView(R.layout.activity_notes_list);

        // UI elements
        mListView = (ListView) findViewById(R.id.notes_list_view);
        mIndOrder = (TextView) findViewById(R.id.notes_list_order);

        // Custom adapter representation
        mListAdapter = new CustomArrayAdapter(NotesListActivity.this, R.layout.list_item_note, mNotes);

        // Set the adapter to the ListView
        mListView.setAdapter(mListAdapter);

        // Listener to handle clicks on list items
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get the object representing this item
                mClickedNote = mListAdapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(NotesListActivity.this);
                builder.setTitle(R.string.note_item_options_title)
                        .setCancelable(true)
                        .setPositiveButton(R.string.note_item_option_edit, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // EDIT
                                // Send its information in the intent for an edit action
                                Intent editIntent = new Intent(NotesListActivity.this, CreateEditNoteActivity.class);
                                editIntent.putExtra(ACTION_CREATE, false);
                                editIntent.putExtra(Constants.KEY_ID, mClickedNote.getId());
                                editIntent.putExtra(Constants.KEY_TITLE, mClickedNote.getTitle());
                                editIntent.putExtra(Constants.KEY_DESCRIPTION, mClickedNote.getDescription());
                                // start activity for result with a edit request
                                startActivityForResult(editIntent, REQUEST_EDIT);
                                // finally, close dialog
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.note_item_option_view, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // VIEW
                                // Send its information in the intent for a view action
                                Intent viewIntent = new Intent(NotesListActivity.this, ViewNoteActivity.class);
                                viewIntent.putExtra(Constants.KEY_TITLE, mClickedNote.getTitle());
                                viewIntent.putExtra(Constants.KEY_DESCRIPTION, mClickedNote.getDescription());
                                // start activity
                                startActivity(viewIntent);
                                // finally, close dialog
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        // reset notes list
        mNotes.clear();

        // check order to show notes list and update text view
        if (Utils.isOrderByDate(NotesListActivity.this))
            mIndOrder.setText(getString(R.string.order_by_date));
        else mIndOrder.setText(getString(R.string.order_by_title));

        if (mNotes.isEmpty()) { // notes list empty
            // refresh list
            updateListTask = new UpdateListTask();
            updateListTask.execute();
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(tag_class, "onActivityResult");
        // check if the request went well and what type of request it is
        if (resultCode == Activity.RESULT_OK) {
            // retrieve note data
            String id = data.getStringExtra(Constants.KEY_ID);
            String title = data.getStringExtra(Constants.KEY_TITLE);
            String description = data.getStringExtra(Constants.KEY_DESCRIPTION);
            switch (requestCode) {
                case REQUEST_CREATE: // Add the new note to the list
                    // create note from evernote cloud
                    createNoteFromEvernoteCloud(title, description);
                    break;
                case REQUEST_EDIT: // update the note edited
                    // edit note from evernote cloud
                    editNoteFromEvernoteCloud(id, title, description);
                    break;
            }
            // refresh list
            updateListTask = new UpdateListTask();
            updateListTask.execute();
        }
    }

    @Override
    protected void onResume() {
        Log.d(tag_class, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(tag_class, "onPause");
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(tag_class, "onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                // create note
                Intent i = new Intent(NotesListActivity.this, CreateEditNoteActivity.class);
                i.putExtra(ACTION_CREATE, true);
                // Execution with result
                startActivityForResult(i, REQUEST_CREATE);
                return true;

            case R.id.action_order_by_date:
                // Order by creation/modification date
                Utils.saveInSharedPreferences(NotesListActivity.this, Constants.SHARED_PREFERENCES, Constants.DATE_ORDER, true);
                // refresh list
                updateListTask = new UpdateListTask();
                updateListTask.execute();
                mIndOrder.setText(getString(R.string.order_by_date));
                return true;

            case R.id.action_order_by_title:
                // Order by title
                Utils.saveInSharedPreferences(NotesListActivity.this, Constants.SHARED_PREFERENCES, Constants.DATE_ORDER, false);
                // refresh list
                updateListTask = new UpdateListTask();
                updateListTask.execute();
                mIndOrder.setText(getString(R.string.order_by_title));
                return true;

            case R.id.action_about:
                // Launch web browser, web for evernote developers
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dev.evernote.com/doc/"));
                startActivity(browserIntent);
                return true;

            case R.id.action_logout:
                Log.d(tag_class, getString(R.string.logout_info));
                // start login activity and finish the current one
                Utils.logout(this);
                return true;

            case R.id.action_exit:
                Log.d(tag_class, getString(R.string.action_exit));
                // finish process app
                android.os.Process.killProcess(android.os.Process.myPid());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Look and restore personal notes list from evernote cloud in a given ArrayList
     * Return 'true' if you get the list correctly
     */
    private boolean restoreNotesListFromEvernoteCloud(ArrayList<NoteFinal> notesList, int offset, int maxNotes) {
        Log.d(tag_class, "restoreNotesListFromEvernoteCloud");
        boolean result = true;
        // reset current notes list
        mNotes.clear();
        // check for internet and evernote session
        if (!Utils.isInternetAvailable(NotesListActivity.this) || !EvernoteSession.getInstance().isLoggedIn()) {
            return false;
        }
        // get client to access primary methods for personal note data
        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        // search filter, only used for indicate order to show notes
        NoteFilter noteFilter = new NoteFilter();
        // get order (title/date) from share preferences
        if (Utils.isOrderByDate(NotesListActivity.this)) { // by date
            noteFilter.setOrder(NoteSortOrder.UPDATED.getValue());
            Log.d(tag_class, "Se intenta actualizar lista por " + getString(R.string.order_by_date));
        } else { // by title
            noteFilter.setOrder(NoteSortOrder.TITLE.getValue());
            Log.d(tag_class, "Se intenta actualizar lista por " + getString(R.string.order_by_title));
        }
        // query required to get note list from cloud
        final Future<NoteList> notesAsync = noteStoreClient.findNotesAsync(noteFilter, offset, maxNotes, new EvernoteCallback<NoteList>() {
            @Override
            public void onSuccess(NoteList result) {
            }

            @Override
            public void onException(Exception exception) {
                Log.e(tag_class, getString(R.string.error_get_notes), exception);
            }
        });
        List<Note> lista;
        try {
            // final notes list
            lista = notesAsync.get().getNotes();
            // at least there should be a note
            if (lista != null && lista.size() > 0) {
                for (Note note : lista) { // per each note
                    // get note info such as ID or title
                    String id = note.getGuid();
                    String title = note.getTitle();
                    String content;
                    // to get particular content note is necessary make another query
                    Future<String> noteContentAsync = noteStoreClient.getNoteContentAsync(id, new EvernoteCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                        }

                        @Override
                        public void onException(Exception exception) {
                            Log.e(tag_class, getString(R.string.error_get_content_note), exception);
                        }
                    });
                    // final content note
                    content = noteContentAsync.get();
                    if (Utils.isOrderByDate(NotesListActivity.this)) { // by date
                        // add particular note to given ArrayList (at the end)
                        notesList.add(new NoteFinal(NotesListActivity.this, id, title, content));
                    } else { // by title
                        // add particular note to given ArrayList (at first position)
                        notesList.add(0, new NoteFinal(NotesListActivity.this, id, title, content));
                    }
                    //Log.d(tag_class, "NOTA: " + title +"\n" + content);
                }
            } else {
                result = false;
            }
        } catch (Exception exception) {
            Log.e(tag_class, getString(R.string.error_get_notes), exception);
            result = false;
        }
        return result;
    }

    /**
     * Create a new note from evernote cloud with some note info: title and description
     */
    private void createNoteFromEvernoteCloud(String title, String description) {
        Log.d(tag_class, "createNoteFromEvernoteCloud");
        // check for internet and evernote session
        if (!Utils.isInternetAvailable(NotesListActivity.this) || !EvernoteSession.getInstance().isLoggedIn()) {
            return;
        }
        // get client to access primary methods for personal note data
        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        // new note
        Note note = new Note();
        // set note info
        note.setTitle(title);
        note.setContent(EvernoteUtil.NOTE_PREFIX + description + EvernoteUtil.NOTE_SUFFIX);
        // instruction required to create note from cloud
        noteStoreClient.createNoteAsync(note, new EvernoteCallback<Note>() {
            @Override
            public void onSuccess(Note result) {
                Log.d(tag_class, getString(R.string.new_note_created));
            }

            @Override
            public void onException(Exception exception) {
                Log.e(tag_class, getString(R.string.error_note_created), exception);
            }
        });
    }

    /**
     * Edit an existing note from evernote cloud with some new note info: title and description
     */
    private void editNoteFromEvernoteCloud(String id, String title, String description) {
        Log.d(tag_class, "editNoteFromEvernoteCloud");
        // check for internet and evernote session
        if (!Utils.isInternetAvailable(NotesListActivity.this) || !EvernoteSession.getInstance().isLoggedIn()) {
            return;
        }
        // get client to access primary methods for personal note data
        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        Note note;
        // query required to get a note from its ID
        final Future<Note> noteAsync = noteStoreClient.getNoteAsync(id, true, false, false, false, new EvernoteCallback<Note>() {
            @Override
            public void onSuccess(Note result) {
            }

            @Override
            public void onException(Exception exception) {
                Log.e(tag_class, getString(R.string.error_note_find), exception);
            }
        });
        try {
            // final note found
            note = noteAsync.get();
            // set new note info
            note.setTitle(title);
            note.setContent(EvernoteUtil.NOTE_PREFIX + description + EvernoteUtil.NOTE_SUFFIX);
            // instruction required to update note from cloud
            noteStoreClient.updateNoteAsync(note, new EvernoteCallback<Note>() {
                @Override
                public void onSuccess(Note result) {
                    Log.d(tag_class, getString(R.string.note_edited));
                }

                @Override
                public void onException(Exception exception) {
                    Log.e(tag_class, getString(R.string.error_note_edited), exception);
                }
            });
        } catch (Exception exception) {
            Log.e(tag_class, getString(R.string.error_note_edited), exception);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(tag_class, "onStart");

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NotesList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://alvaromartin.liteevernoteclient/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(tag_class, "onStop");

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NotesList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://alvaromartin.liteevernoteclient/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * Task to update notes list from evernote cloud
     */
    private class UpdateListTask extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog progressDialog = new ProgressDialog(NotesListActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(tag_class, "onPreExecute");
            // open a progress dialog to indicate task process
            if (!progressDialog.isShowing()) {
                progressDialog.setMessage(getString(R.string.loading_list));
                progressDialog.show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(tag_class, "doInBackground");
            // restore notes from evernote cloud (max 30 notes)
            return restoreNotesListFromEvernoteCloud(mNotes, 0, 30);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Log.d(tag_class, "onPostExecute");
            // finish progress dialog
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (result) { // case positive
                // refresh the list to see the notes retrieved
                mListAdapter.notifyDataSetInvalidated();
                Log.d(tag_class, getResources().getString(R.string.list_update_OK));
            } else { // case negative
                Utils.showDialogMessage(NotesListActivity.this, getResources().getString(R.string.list_update_KO));
                Log.d(tag_class, getResources().getString(R.string.list_update_KO));
            }
        }
    }
}
