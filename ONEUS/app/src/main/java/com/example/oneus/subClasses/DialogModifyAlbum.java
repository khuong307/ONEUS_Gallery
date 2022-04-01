package com.example.oneus.subClasses;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DialogModifyAlbum extends DialogFragment {
    private EditText newAlbumName;
    private Button btnChoose;
    private ImageView imageChosen;


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
        newAlbumName = (EditText) view.findViewById(R.id.newAlbumName);
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
                    }else{
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            if (createSubsDirectory(albumName) == true){
                                String[] sourcePath = imageChosen.getTag().toString().split("raw:");
                                File inputPath = new File(sourcePath[1]);
                                String newPathAlbum = Environment.getExternalStorageDirectory() + "/ONEUS/" + albumName + "/" + inputPath.getName();
                                try {
                                    copy(inputPath, new File(newPathAlbum));
                                    RecyclerView recyclerView = getActivity().findViewById(R.id.recycle_view_album);
                                    AlbumAdapter albumAdapter = new AlbumAdapter(getContext(), ImageAlbum.setAlbumList());
                                    recyclerView.setAdapter(albumAdapter);
                                    dialog.dismiss();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });
        }
    }


    boolean createSubsDirectory(String FolderName){
        File folder = new File(Environment.getExternalStorageDirectory() +"/ONEUS/" + FolderName);
        if (!folder.exists()){
            folder.mkdir();
            return true;
        }else{
            Toast.makeText(getActivity(), "Folder already existed!", Toast.LENGTH_SHORT).show();
            return false;
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
