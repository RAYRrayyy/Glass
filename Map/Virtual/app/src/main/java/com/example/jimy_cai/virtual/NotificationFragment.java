package com.example.jimy_cai.virtual;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Jimy on 7/06/2017.
 */

public class NotificationFragment extends DialogFragment {
    ImageView notifierImage;
    TextView notifierText;
    View notifierView;

    public NotificationFragment(Activity currentActivity) {
        LayoutInflater inflater = currentActivity.getLayoutInflater();
        notifierView = inflater.inflate(R.layout.toast_layout,
                null);
        // get references to the images
        notifierImage = (ImageView) notifierView.findViewById(R.id.location_image);
        notifierText = (TextView) notifierView.findViewById(R.id.arrival_text);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(notifierView)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });

        return builder.create();
    }


    // Set image in dialog
    public void setNotifierImage(int imageReference){
        notifierImage.setImageResource(imageReference);
    }


    // Set notifier text in dialog
    public void setNotifierText(String text){
        notifierText.setText(text);
    }
}