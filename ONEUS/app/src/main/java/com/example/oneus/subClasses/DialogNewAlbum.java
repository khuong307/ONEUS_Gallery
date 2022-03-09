package com.example.oneus.subClasses;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.oneus.MainActivity;
import com.example.oneus.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class DialogNewAlbum extends DialogFragment {
    private EditText newAlbumName;
    private Button btnChoose;
    private ImageView imageChosen;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);
        newAlbumName = (EditText) view.findViewById(R.id.newAlbumName);
        btnChoose = (Button) view.findViewById(R.id.btnChoose);
        imageChosen = (ImageView) view.findViewById(R.id.imageChosen);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });



        builder.setView(view).setTitle("New Album").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //create new folder.
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    String albumName = newAlbumName.getText().toString();
                    createSubsDirectory(albumName);
                    String sourcePath = imageChosen.getTag().toString();
                    File file = new File(sourcePath);
                    String newPathAlbum = Environment.getExternalStorageDirectory() +"/ONEUS/" + albumName +"/"+file.getName();
                    File inputPath = new File (Environment.getExternalStorageDirectory()+"/Pictures", file.getName());
                    long size = inputPath.length();
                    Toast.makeText(getActivity(), size+"", Toast.LENGTH_SHORT).show();
                    try {
                        copy(inputPath, new File(newPathAlbum));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        newAlbumName = (EditText) view.findViewById(R.id.newAlbumName);
        return builder.create();
    }


    void createSubsDirectory(String FolderName){
        File folder = new File(Environment.getExternalStorageDirectory() +"/ONEUS/" + FolderName);
        if (!folder.exists()){
            folder.mkdir();
        }else{
            Toast.makeText(getActivity(), "Folder already existed!", Toast.LENGTH_SHORT).show();
        }
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
        new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                imageChosen.setImageURI(uri);
                imageChosen.setTag(uri.toString().replace("%3A", ":").replace("%2F", "/"));
            }
    });

    public void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

}