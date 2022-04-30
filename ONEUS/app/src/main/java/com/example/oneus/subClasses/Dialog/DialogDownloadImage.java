package com.example.oneus.subClasses.Dialog;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.oneus.MainActivity;
import com.example.oneus.R;
import com.example.oneus.subClasses.Path;
import com.squareup.picasso.Picasso;

import java.io.File;

public class DialogDownloadImage extends DialogFragment {
    private String albumPath;
    private EditText edtDownload;


    public DialogDownloadImage(String albumPath) {
        this.albumPath=albumPath;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_download_image, null);
        edtDownload = (EditText) view.findViewById(R.id.edtDownload);

        builder.setView(view);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog)getDialog();
        if(dialog != null) {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(153, 69, 0));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(153, 69, 0));
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        String url=edtDownload.getText().toString();
                        if(url.equals("")){
                            Toast.makeText(getContext(),"Please enter URL",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            openDisplayDownloadDialog(url,albumPath);
                            dialog.dismiss();
                        }
                    }
                }
            });
        }
    }


    public void openDisplayDownloadDialog(String url,String albumPath){
        DialogDisplayDownload dialogDisplayDownload = new DialogDisplayDownload(url,albumPath);
        FragmentManager manager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
        dialogDisplayDownload.show((manager), "Display Download Image Dialog");
    }
}