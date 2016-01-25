package alvaromartin.liteevernoteclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.evernote.client.android.EvernoteSession;

import alvaromartin.liteevernoteclient.utils.Utils;

/**
 * Created by Alvaro on 21/01/2016.
 */
public class LoginActivity extends AppCompatActivity {

    String tag_class = LoginActivity.this.getClass().getSimpleName();

    // real evernote request code for login
    private static final int REQUEST_CODE_LOGIN = 66394;

    /*
     * Your Evernote API key. See http://dev.evernote.com/documentation/cloud/
     * Please obfuscate your code to help keep these values secret.
     */
    private static final String CONSUMER_KEY = "alvarobq-7720";
    private static final String CONSUMER_SECRET = "6f40db93090c7eeb";

    /*
     * Initial development is done on Evernote's testing service, the sandbox.
     *
     * Change to PRODUCTION to use the Evernote production service
     * once your code is complete.
     */
    //private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.PRODUCTION;
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;

    /*
     * Set this to true if you want to allow linked notebooks for accounts that
     * can only access a single notebook.
     */
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;

    // login button that initiates the authentication process
    private Button mLoginButton;

    // represents a session with the Evernote web service API.
    private EvernoteSession mEvernoteSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag_class, "onCreate");
        setContentView(R.layout.activity_login);

        String consumerKey = CONSUMER_KEY;
        String consumerSecret = CONSUMER_SECRET;

        //Set up the Evernote singleton session, use EvernoteSession.getInstance() later
        mEvernoteSession = new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .setForceAuthenticationInThirdPartyApp(true)
                        //.setLocale(Locale.SIMPLIFIED_CHINESE)
                .build(consumerKey, consumerSecret)
                .asSingleton();

        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Check credentials in the server side
                mEvernoteSession.authenticate(LoginActivity.this);
                mLoginButton.setEnabled(false);
            }
        });

        // Check whether the session has valid authentication information
        if (EvernoteSession.getInstance().isLoggedIn()) {
            // handle success
            Log.d(tag_class, getResources().getString(R.string.credentials_valid));
            // try launch activity with Notes List
            launchNotesListActivity();
        }
    }

    /**
     * Launch LoginActivity from another context activity
     *
     * @param activity activity context
     */
    public static void launch(Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
    }

    /**
     * Launch NotesListActivity from current context activity
     */
    private void launchNotesListActivity() {
        // Start NotesListActivity
        Intent i = new Intent(LoginActivity.this, NotesListActivity.class);
        startActivity(i);
        // finish activity to avoid returning if back button is pressed in further activities
        LoginActivity.this.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(tag_class, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //case (EvernoteSession.REQUEST_CODE_LOGIN):
            case (REQUEST_CODE_LOGIN):
                Log.d(tag_class, getResources().getString(R.string.code_OK) + ": " + requestCode);
                if (resultCode == Activity.RESULT_OK) {
                    // handle success
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Check whether the session has valid authentication information
                            if (EvernoteSession.getInstance().isLoggedIn()) {
                                // handle success
                                Log.d(tag_class, getResources().getString(R.string.credentials_valid));
                                // try launch activity with Notes List
                                launchNotesListActivity();
                            }
                        }
                    }, 1000);
                } else {
                    // handle failure
                    Utils.showDialogMessage(LoginActivity.this, getResources().getString(R.string.credentials_invalid));
                    Log.d(tag_class, getResources().getString(R.string.credentials_invalid));
                    mLoginButton.setEnabled(true);
                }
                break;
            default:
                Log.d(tag_class, getResources().getString(R.string.error_sign_in) + ". Request Code Login: " + requestCode);
                break;
        }
    }
}