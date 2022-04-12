package com.example.oneus.subClasses;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.TextView;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DialogDeleteAlbum extends DialogFragment {
    private String albumName;
    private TextView txvDeleteAlbum;

    public DialogDeleteAlbum(String albumName) {
        this.albumName=albumName;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete_album_layout, null);
        txvDeleteAlbum = (TextView) view.findViewById(R.id.txvDeleteAlbum);


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
        txvDeleteAlbum.setText(albumName+" will be delete");

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
                    RemoveAlbum();
                    RecyclerView recyclerView = getActivity().findViewById(R.id.recycle_view_album);
                    AlbumAdapter albumAdapter = new AlbumAdapter(getContext(), ImageAlbum.setAlbumList());
                    recyclerView.setAdapter(albumAdapter);
                    dialog.dismiss();
                }
            });
        }
    }


    boolean createSubsDirectory(String FolderName){
        File oldFolder=new File(Environment.getExternalStorageDirectory()+"/ONEUS/" +albumName);
        File folder = new File(Environment.getExternalStorageDirectory() +"/ONEUS/" + FolderName);
        if (oldFolder.exists()){
            folder.mkdir();
            return oldFolder.renameTo(folder);
        }else{
            Toast.makeText(getActivity(), "Can not rename album!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public ArrayList<File> ListImages(){
        String root = Environment.getExternalStorageDirectory()+"/ONEUS/"+albumName;
        File dir = new File(root);
        File[] items = dir.listFiles();
        ArrayList<File> files = new ArrayList<File>();
        if (items==null)
            return files;
        for (int i = 0; i < items.length; ++i)
            if (!items[i].isDirectory())
                files.add(items[i]);
        return files;
    }

    public String RemoveImages(String path){
        File file = new File(path);
        if (!file.exists())
            return "The file does not exist";
        if (file.delete())
            return "";
        else
            return "Delete failed";
    }

    public String RemoveAlbum(){
        String root = Environment.getExternalStorageDirectory()+"/ONEUS/"+albumName;
        File currentFolder = new File(root);
        if (!currentFolder.exists())
            return "The album does not exist";

        ArrayList<File> files =ListImages();
        for (int i=0;i< files.size();i++)
           RemoveImages(root+"/"+files.get(i).getName());
        if (currentFolder.delete())
            return "";
        else
            return "Delete failed";
    }


    //copy a binary files.
    public void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
}
