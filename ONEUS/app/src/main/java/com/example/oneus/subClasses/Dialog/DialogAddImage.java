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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneus.ListImageOfAlbum;
import com.example.oneus.R;
import com.example.oneus.SubAdapter.AlbumAdapter;
import com.example.oneus.SubAdapter.ImagesOfAlbumAdapter;
import com.example.oneus.SubAdapter.MultiImagesAdapter;
import com.example.oneus.subClasses.Image;
import com.example.oneus.subClasses.Path;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DialogAddImage extends DialogFragment {
    private ImageButton btnChoose;
    private RecyclerView recyclerView;
    private MultiImagesAdapter multiImagesAdapter;
    private List<Image> multiImages = new ArrayList<>();
    int PICK_IMAGE_MULTIPLE = 1;

    private ImageButton btnCapture;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_image_dialog_layout, null);
        btnChoose = view.findViewById(R.id.btnChoose);
        recyclerView = view.findViewById(R.id.recycler_images_chosen);


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

        btnCapture = view.findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,100);
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
            if (multiImages.size() > 0){
                btnCapture.setVisibility(View.GONE);
            }else{
                btnCapture.setVisibility(View.VISIBLE);
            }
            Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(153, 69, 0));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(153, 69, 0));
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        if(multiImages.size() == 0){
                            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_SHORT).show();
                        }else{
                            for(int i = 0; i < multiImages.size(); i++){
                                File inputPath = multiImages.get(i).getImage();
                                String parentFolder = getArguments().getString("ParentFolder");
                                File output = new File(parentFolder);
                                String newPath = Environment.getExternalStorageDirectory() + "/ONEUS/" + output.getName() +"/" + inputPath.getName();
                                try {
                                    if (new File(newPath).exists() == true){
                                        Path.copy(inputPath, new File(newPath+"new"));
                                    }else{
                                        Path.copy(inputPath, new File(newPath));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            ListImageOfAlbum listImageOfAlbum = (ListImageOfAlbum) getActivity();
                            listImageOfAlbum.clearActionMode();
                            listImageOfAlbum.updateImageList();
                            listImageOfAlbum.updateImageAdapter();
                            listImageOfAlbum.updateRecyclerView();
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Import Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            try {
                Intent data = result.getData();
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
                    LinearLayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    multiImagesAdapter = new MultiImagesAdapter((ListImageOfAlbum) getContext(), multiImages);
                    recyclerView.setAdapter(multiImagesAdapter);
                    recyclerView.setLayoutManager(layout);
                } else {
                    Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    });

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==100){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            String parentFolder = getArguments().getString("ParentFolder");
            File bitmapFile= Path.SaveBitmap(parentFolder,bitmap);
            if (bitmapFile.exists()){
                Uri bitmapUri= convertBitmapToUri(bitmapFile);
                final AlertDialog dialog = (AlertDialog)getDialog();
                dialog.dismiss();
            }
            else
                Toast.makeText(getActivity(),"Capture image is unsuccesfully",Toast.LENGTH_SHORT).show();
        }
    }


    public static Uri convertBitmapToUri(File file){
        Uri bitmapUri = Uri.fromFile(file);
        return bitmapUri;
    }
}
