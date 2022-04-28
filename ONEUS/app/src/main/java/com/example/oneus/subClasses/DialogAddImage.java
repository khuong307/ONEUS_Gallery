package com.example.oneus.subClasses;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneus.ListImageOfAlbum;
import com.example.oneus.R;
import com.example.oneus.SubAdapter.AlbumAdapter;
import com.example.oneus.SubAdapter.ImagesOfAlbumAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class DialogAddImage extends DialogFragment {
    private Button btnChoose;
    private ImageView imageChosen;
    private String parentFolder;

    // Minh
    private Button btnCapture;

    public String getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(String parentFolder) {
        this.parentFolder = parentFolder;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_image_dialog_layout, null);
        btnChoose = (Button) view.findViewById(R.id.btnChoose);
        imageChosen = (ImageView) view.findViewById(R.id.imageChosen);

        //Minh
        btnCapture = (Button) view.findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,100);
            }
        });

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
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog)getDialog();
        if(dialog != null) {
            Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(153, 69, 0));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(153, 69, 0));
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        String[] sourcePath = imageChosen.getTag().toString().split("raw:");
                        File inputPath = new File(sourcePath[1]);
                        String parentFolder = getArguments().getString("ParentFolder");

                        File output = new File(parentFolder);
                        String newPath = Environment.getExternalStorageDirectory() + "/ONEUS/" + output.getName() +"/" + inputPath.getName();
                        try {
                            if (new File(newPath).exists() == true){
                                copy(inputPath, new File(newPath+"new"));
                            }else{
                                copy(inputPath, new File(newPath));
                            }
                            ListImageOfAlbum listImageOfAlbum = (ListImageOfAlbum) getActivity();
                            listImageOfAlbum.clearActionMode();
                            listImageOfAlbum.updateImageList();
                            listImageOfAlbum.updateImageAdapter();
                            listImageOfAlbum.updateRecyclerView();
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Import Success", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
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

    // Minh


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==100){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            String parentFolder = getArguments().getString("ParentFolder");
            File bitmapFile=SaveBitmap(parentFolder,bitmap);
            if (bitmapFile.exists()){
                Uri bitmapUri= convertBitmapToUri(bitmapFile);
                imageChosen.setImageURI(bitmapUri);
            }
            else
                Toast.makeText(getActivity(),"Capture image is unsuccesfully",Toast.LENGTH_SHORT).show();
        }
    }


    public static Uri convertBitmapToUri(File file){
        Uri bitmapUri = Uri.fromFile(file);
        return bitmapUri;
    }

    public static File SaveBitmap(String folderName,Bitmap bitmap){
        String path = folderName;
        File dir = new File(path);
        if (!dir.exists())
            return null;
        File file=new File(dir,Long.toString(new Date().getTime())+".jpg");
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        }
        catch (Exception ex) {
            return null;
        }
        return file;
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
