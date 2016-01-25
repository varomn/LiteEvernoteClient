package alvaromartin.liteevernoteclient.model;

import android.content.Context;

/**
 * Created by Alvaro on 21/01/2016.
 */
public class NoteFinal {
    private String mId;
    private String mTitle;
    private String mDescription;

    /**
     * Constructor with arguments
     *
     * @param context
     * @param id
     * @param title
     * @param description
     */
    public NoteFinal(Context context, String id, String title, String description) {
        this.mId = id;
        this.mTitle = title;
        this.mDescription = description;
    }

	/* Getters and Setters */

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getId() {
        return mId;
    }
}
