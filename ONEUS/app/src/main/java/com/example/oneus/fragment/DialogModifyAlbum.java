package com.example.oneus.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneus.R;
import com.example.oneus.SubAdapter.AlbumAdapter;
import com.example.oneus.subClasses.ImageAlbum;
import com.example.oneus.subClasses.Path;

import java.io.File;
import java.io.IOException;

public class DialogModifyAlbum extends DialogFragment {
    private EditText newAlbumName;
    private Button btnChoose;
    private ImageView imageChosen;

    // MInh
    private String oldAlbumName;

    public DialogModifyAlbum(String albumName) {
        this.oldAlbumName=albumName;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_modify_album_layout, null);
        newAlbumName = (EditText) view.findViewById(R.id.changeAlbumName);

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
        newAlbumName = (EditText) view.findViewById(R.id.changeAlbumName);
        newAlbumName.setText(oldAlbumName);
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
                    String albumName = newAlbumName.getText().toString();
                    if (albumName.isEmpty()==true){
                        Toast.makeText(getActivity(), "Please enter album's name!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            if (createSubsDirectory(albumName) == true){
                                RecyclerView recyclerView = getActivity().findViewById(R.id.recycle_view_album);
                                AlbumAdapter albumAdapter = new AlbumAdapter(getContext(), ImageAlbum.setAlbumList());
                                recyclerView.setAdapter(albumAdapter);
                                dialog.dismiss();
                            }
                        }
                    }
                }
            });
        }
    }


    boolean createSubsDirectory(String FolderName){
        File oldFolder=new File(Environment.getExternalStorageDirectory()+"/ONEUS/" +oldAlbumName);
        File folder = new File(Environment.getExternalStorageDirectory() +"/ONEUS/" + FolderName);
        if (oldFolder.exists()){
            return oldFolder.renameTo(folder);
        }else{
            Toast.makeText(getActivity(), "Can not rename album!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}