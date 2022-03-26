package com.example.oneus.subClasses;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DialogAddImage extends DialogFragment {
    private Button btnChoose;
    private RecyclerView recyclerView;
    private MultiImagesAdapter multiImagesAdapter;
    private List<Image> multiImages = new ArrayList<>();
    int PICK_IMAGE_MULTIPLE = 1;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_image_dialog_layout, null);
        btnChoose = (Button) view.findViewById(R.id.btnChoose);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_images_chosen);


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
                                        copy(inputPath, new File(newPath+"new"));
                                    }else{
                                        copy(inputPath, new File(newPath));
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
                                String imageEncoded = getPath(getActivity(), uri);
                                File inputPath = new File(imageEncoded);
                                multiImages.add(new Image(inputPath, inputPath.getName()));
                            } else {
                                if (data.getClipData() != null) {
                                    ClipData mClipData = data.getClipData();
                                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                                        ClipData.Item item = mClipData.getItemAt(i);
                                        Uri uri = item.getUri();
                                        String imageEncoded = getPath(getActivity(), uri);
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

    public static String getPath(final Context context, final Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
