package com.example.oneus.subClasses.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.oneus.R;

import java.io.File;
import java.io.IOException;


public class DialogChooseScreen extends DialogFragment {
    private RadioButton btnWallpaper, btnLock;
    private String path;

    public DialogChooseScreen(String imagePath){
        this.path = imagePath;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_choose_screen, null);
        btnWallpaper = (RadioButton) view.findViewById(R.id.btnWallpaper);
        btnLock = (RadioButton) view.findViewById(R.id.btnLock);

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
            Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    WallpaperManager wpm = WallpaperManager.getInstance(getActivity().getApplicationContext());
                    try {
                        if (btnWallpaper.isChecked()){
                            wpm.setBitmap(bitmap);
                            Toast.makeText(getActivity(), "Set wallpaper successfully!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else if (btnLock.isChecked()){
                            wpm.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                            Toast.makeText(getActivity(), "Set lock screen successfully!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else {
                            Toast.makeText(getActivity(), "Please choose one screen type!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}