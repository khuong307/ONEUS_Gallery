package com.example.oneus.subClasses;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneus.R;
import com.example.oneus.SubAdapter.AlbumAdapter;
import com.example.oneus.SubAdapter.SpinnerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DialogMoveAlbum extends DialogFragment {
    private String albumName;
    private TextView txvMoveAlbum;
    Spinner spinner;
    SpinnerAdapter spinnerAdapter;
    List<ImageAlbum> mList;
    String destination;

    public DialogMoveAlbum(String albumName) {
        this.albumName=albumName;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_move_album_layout, null);
        txvMoveAlbum = (TextView) view.findViewById(R.id.title_moveAlbum);

        spinner = view.findViewById(R.id.spnAlbumList);

        String currentFolder = this.albumName;
        mList = ImageAlbum.setAlbumListExcept(currentFolder);
        spinnerAdapter = new SpinnerAdapter(getActivity(), mList);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ImageAlbum item = (ImageAlbum) adapterView.getItemAtPosition(i);
                destination = item.getAlbum();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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
                    try {
                        String status = MoveAlbum();
                        Toast.makeText(getActivity(),status,Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    RecyclerView recyclerView = getActivity().findViewById(R.id.recycle_view_album);
                    AlbumAdapter albumAdapter = new AlbumAdapter(getContext(), ImageAlbum.setAlbumList());
                    recyclerView.setAdapter(albumAdapter);
                    dialog.dismiss();
                }
            });
        }
    }



    boolean createSubsDirectory(String ParentFolderName,String FolderName){
        File folder = new File(Environment.getExternalStorageDirectory() +"/ONEUS/" + ParentFolderName+"/"+FolderName);
        if (!folder.exists()){
            folder.mkdir();
            return true;
        }else{
            Toast.makeText(getActivity(), "Folder already existed!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    ArrayList<File> ListImages(){
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

    String RemoveImages(String path){
        File file = new File(path);
        if (!file.exists())
            return "The file does not exist";
        if (file.delete())
            return "";
        else
            return "Delete failed";
    }

    String RemoveAlbum(){
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

    String MoveAlbum() throws IOException {
        String root = Environment.getExternalStorageDirectory()+"/ONEUS/"+albumName;
        File currentFolder = new File(root);
        if (!currentFolder.exists())
            return "The album does not exist";

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (createSubsDirectory(destination,albumName) == true){
                ArrayList<File> files =ListImages();
                for (int i=0;i< files.size();i++){
                    copy(new File(root+"/"+files.get(i).getName()),new File(Environment.getExternalStorageDirectory().toString()+"/ONEUS/"+destination+files.get(i).getName()));
                }
                RemoveAlbum();
            }
        }
        return "Moving successfully";
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
