package alvaromartin.liteevernoteclient.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.evernote.client.android.EvernoteSession;

import alvaromartin.liteevernoteclient.LoginActivity;
import alvaromartin.liteevernoteclient.R;

/**
 * Created by Alvaro on 21/01/2016.
 */
public class Utils {

    /**
     * Log out session for evernote account and launch initial activity to start process
     *
     * @param activity
     */
    public static void logout(Activity activity) {
        EvernoteSession.getInstance().logOut();
        LoginActivity.launch(activity);
        activity.finish();
    }

    /**
     * Indicate order to show notes list from sshare preferences
     *
     * @param context Context of activity call
     *
     * Return 'true' for order by date and 'false' for order by title
     */
    public static boolean isOrderByDate(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.SHARED_PREFERENCES, 0);
        return settings.getBoolean(Constants.DATE_ORDER, true);
    }

    /**
     * Show a dialog message to the user
     *
     * @param context of the application
     * @param message to be shown
     */
    public static void showDialogMessage(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(R.string.button_ok, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Save a given Boolean value in Shared Preferences
     *
     * @param context
     * @param sharedPreferencesName
     * @param key
     * @param value
     */
    public static void saveInSharedPreferences(Context context, String sharedPreferencesName, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(sharedPreferencesName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * Check for a Internet connection available
     *
     * @param context Context of activity call
     */
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
