package com.example.oneus.subClasses.Dialog;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.oneus.ListImageOfAlbum;
import com.example.oneus.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DialogDisplayDownload extends DialogFragment {
    private String url;
    private String albumPath;
    private ImageView imvDownload;


    public DialogDisplayDownload(String url,String albumPath) {
        this.albumPath=albumPath;
        this.url=url;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_display_download_image, null);
        imvDownload = (ImageView) view.findViewById(R.id.imvDownload);

        Picasso.get().load(url).into(imvDownload);

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
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(153, 69, 0));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(153, 69, 0));
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        if(imvDownload.getDrawable()==null){
                            Toast.makeText(getContext(),"Can not download image",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else
                        {
                            Bitmap bitmap = ((BitmapDrawable)imvDownload.getDrawable()).getBitmap();
                            String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                                    .format(System.currentTimeMillis());
                            String imageName = time +".JPEG";
                            File file = new File(albumPath,imageName);

                            OutputStream out;

                            try {
                                out = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                                out.flush();
                                out.close();

                                ListImageOfAlbum listImageOfAlbum = (ListImageOfAlbum) getActivity();
                                listImageOfAlbum.clearActionMode();
                                listImageOfAlbum.updateImageList();
                                listImageOfAlbum.updateImageAdapter();
                                listImageOfAlbum.updateRecyclerView();
                                Toast.makeText(getContext(),"Download Image Successfully",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }catch (Exception e){
                                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
    }
}
