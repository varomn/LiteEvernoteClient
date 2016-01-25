package alvaromartin.liteevernoteclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import alvaromartin.liteevernoteclient.R;
import alvaromartin.liteevernoteclient.model.NoteFinal;

/**
 * Created by Alvaro on 21/01/2016.
 */
public class CustomArrayAdapter extends ArrayAdapter<NoteFinal> {

    private Context mContext;
    private int mResource;
    private ArrayList<NoteFinal> mListOfObjects;

    public CustomArrayAdapter(Context context, int resource, ArrayList<NoteFinal> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.mListOfObjects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(mResource, null);

        // get the specific note we are working with
        final NoteFinal noteItem = mListOfObjects.get(position);

        // Personalize the item view representation
        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.item_note_layout);
        TextView title = (TextView) convertView.findViewById(R.id.item_note_title);
        TextView description = (TextView) convertView.findViewById(R.id.item_note_description);

        // set note info
        title.setText(noteItem.getTitle());
        description.setText(Html.fromHtml(noteItem.getDescription()));

        // Configure the button to send an email with note info
        ImageButton sendNote = (ImageButton) convertView.findViewById(R.id.item_note_email);
        sendNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                // set title email
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, String.format(mContext.getResources().getString(R.string.email_subject), noteItem.getTitle()));
                // set content email
                emailIntent.putExtra(Intent.EXTRA_TEXT, String.format(mContext.getResources().getString(R.string.email_body), noteItem.getDescription()));
                try {
                    mContext.startActivity(Intent.createChooser(emailIntent, mContext.getString(R.string.email_send_title)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(mContext, mContext.getString(R.string.email_send_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }
}
