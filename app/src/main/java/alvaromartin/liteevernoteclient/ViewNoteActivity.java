package alvaromartin.liteevernoteclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v7.app.ActionBar;

import alvaromartin.liteevernoteclient.utils.Constants;

/**
 * Created by Alvaro on 22/01/2016.
 */
public class ViewNoteActivity extends AppCompatActivity {

    private final String tag_class = ViewNoteActivity.this.getClass().getSimpleName();

    // Text view to show note description
    private TextView mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag_class, "onCreate");
        setContentView(R.layout.activity_view_note);

        // UI elements
        mDescription = (TextView) findViewById(R.id.view_note_desc);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        // get note info from intent
        Intent noteInfo = getIntent();
        // set note info
        setTitle(noteInfo.getStringExtra(Constants.KEY_TITLE));
        mDescription.setText(Html.fromHtml(noteInfo.getStringExtra(Constants.KEY_DESCRIPTION)));
    }

    //@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Log.d(tag_class, "onOptionsItemSelected");
        finish();
        return true;
    }
}
