package com.example.oneus.subClasses.Dialog;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import com.example.oneus.ListImageOfAlbum;
import com.example.oneus.MainActivity;
import com.example.oneus.R;
import com.example.oneus.SubAdapter.SpinnerAdapter;
import com.example.oneus.fragment.TrashFragment;
import com.example.oneus.subClasses.ImageAlbum;
import com.example.oneus.subClasses.TrashImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class DialogReturnImage extends DialogFragment {
    TextView title;
    Spinner spinner;
    SpinnerAdapter spinnerAdapter;
    List<ImageAlbum> mList;
    String destination;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.return_images_dialog_layout, null);
        title = view.findViewById(R.id.title_moveImage);
        spinner = view.findViewById(R.id.spinner_albumList);

        mList = ImageAlbum.setAlbumListExcept("Trash");
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
            Toast.makeText(getContext(), spinner.getChildCount()+"", Toast.LENGTH_SHORT).show();
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(0, 0, 0));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(0, 0, 0));
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getContext(), destination, Toast.LENGTH_SHORT).show();
                        List<TrashImage> selectedItem = (List<TrashImage>) getArguments().getSerializable("SelectedItems");

                        for(int i = 0; i < selectedItem.size(); i++){
                            try {
                                copy(selectedItem.get(i).getImage(), new File(Environment.getExternalStorageDirectory().toString() + "/ONEUS/"+destination+"/"+selectedItem.get(i).getText()));
                                selectedItem.get(i).getImage().delete();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        getContext().startActivity(intent);
                        dialog.dismiss();
                    }
                }
            });
        }
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
