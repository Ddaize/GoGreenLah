package com.example.gogreenlah;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ImageDescDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_image_dialog, null);

        builder.setView(view)
                .setTitle("Item Description")
                .setNegativeButton("later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getContext(), featureTwoActivity.class));
                    }
                })
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getContext(), ImageDetails.class);
                        Bundle argument = getArguments();
                        String imageID = argument.getString("id");
                        String imageUri = argument.getString("uri");


                        Bundle bundle = new Bundle();
                        bundle.putString("id", imageID);
                        bundle.putString("uri", imageUri);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
        return builder.create();
    }
}
