package com.example.oneus.subClasses.Dialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneus.ListImageOfAlbum;
import com.example.oneus.MainActivity;
import com.example.oneus.R;
import com.example.oneus.SubAdapter.AlbumAdapter;
import com.example.oneus.SubAdapter.MultiImagesAdapter;
import com.example.oneus.SubAdapter.MultiImagesNewAlbumAdapter;
import com.example.oneus.subClasses.Image;
import com.example.oneus.subClasses.ImageAlbum;
import com.example.oneus.subClasses.Path;

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
import java.util.ArrayList;
import java.util.List;

public class DialogNewAlbum extends DialogFragment {
    private EditText newAlbumName;
    private Button btnChoose;
    public static ImageView imageChosen;

    private RecyclerView recyclerViewMulti;
    private MultiImagesNewAlbumAdapter multiImagesAdapter;
    private List<Image> multiImages = new ArrayList<>();


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);
        newAlbumName = (EditText) view.findViewById(R.id.newAlbumName);
        btnChoose = (Button) view.findViewById(R.id.btnChoose);
        imageChosen = (ImageView) view.findViewById(R.id.imageChosen);
        recyclerViewMulti = (RecyclerView) view.findViewById(R.id.recycler_images_chosen);


        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                mGetContent.launch(intent);
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
                        if (multiImages.size() == 0){
                            Toast.makeText(getActivity(), "You haven't picked Image!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            if (createSubsDirectory(albumName) == true){
                                for(int i = 0; i < multiImages.size(); i++){
                                    File inputPath = multiImages.get(i).getImage();
                                    String newPathAlbum = Environment.getExternalStorageDirectory() + "/ONEUS/" + albumName +"/" + inputPath.getName();
                                    try {
                                        Path.copy(inputPath, new File(newPathAlbum));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
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
        File folder = new File(Environment.getExternalStorageDirectory() +"/ONEUS/" + FolderName);
        if (!folder.exists()){
            folder.mkdir();
            return true;
        }else{
            Toast.makeText(getActivity(), "Folder already existed!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            try {
                Intent data = result.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if(data.getData()!=null){
                        Uri uri=data.getData();
                        String imageEncoded = Path.getPath(getActivity(), uri);
                        File inputPath = new File(imageEncoded);
                        multiImages.add(new Image(inputPath, inputPath.getName()));
                    } else {
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {
                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                String imageEncoded = Path.getPath(getActivity(), uri);
                                File inputPath = new File(imageEncoded);
                                multiImages.add(new Image(inputPath, inputPath.getName()));
                            }
                        }
                    }
                    setThumbnail(multiImages.get(0).getImage());
                    if(multiImages.size() > 1){
                        LinearLayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        multiImagesAdapter = new MultiImagesNewAlbumAdapter(getContext(), multiImages);
                        recyclerViewMulti.setAdapter(multiImagesAdapter);
                        recyclerViewMulti.setLayoutManager(layout);
                    }
                } else {
                    Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    });

    public static void setThumbnail(File image){
        imageChosen.setImageURI(Uri.fromFile(image));
    }

}
