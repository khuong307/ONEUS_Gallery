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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.oneus.subClasses.Image;
import com.example.oneus.subClasses.ImageAlbum;
import com.example.oneus.subClasses.Path;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class DialogMoveImage extends DialogFragment {
    TextView title;
    Spinner spinner;
    ImageButton addBtn;
    ImageButton backBtn;
    EditText newAlb;
    SpinnerAdapter spinnerAdapter;
    List<ImageAlbum> mList;
    String destination;
    Integer Mode = 1;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.move_images_dialog_layout, null);
        title = view.findViewById(R.id.title_moveImage);
        spinner = view.findViewById(R.id.spinner_albumList);
        addBtn = view.findViewById(R.id.newAlbumBtn);
        backBtn = view.findViewById(R.id.btnBack);
        newAlb = view.findViewById(R.id.newAlb);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBtn.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                newAlb.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);
                Mode = 2;
            }
        });


        backBtn.setVisibility(View.GONE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backBtn.setVisibility(View.GONE);
                newAlb.setVisibility(View.GONE);
                newAlb.setText("");
                spinner.setVisibility(View.VISIBLE);
                addBtn.setVisibility(View.VISIBLE);
                Mode = 1;
            }
        });

        newAlb.setVisibility(View.GONE);

        String currentFolder = getArguments().getString("ParentFolder");
        mList = ImageAlbum.setAlbumListExcept(currentFolder);
        if (mList.size() == 0){
            Mode = 2;
            spinner.setVisibility(View.GONE);
            backBtn.setVisibility(View.GONE);
        }

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
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(153, 69, 0));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(153, 69, 0));
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        List<Image> selectedItem = (List<Image>) getArguments().getSerializable("SelectedItems");
                        ListImageOfAlbum listImageOfAlbum = (ListImageOfAlbum) getActivity();
                        String newName = newAlb.getText().toString();
                        Toast.makeText(getActivity(), Mode+"", Toast.LENGTH_SHORT).show();
                        if (Mode == 1){
                            cloneFile(selectedItem, listImageOfAlbum, destination);
                        }else if (Mode == 2){
                            if (newName.isEmpty() == true){
                                Toast.makeText(getActivity(), "Please enter album's name!", Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                if (Path.createSubsDirectory(newName) == true){
                                    cloneFile(selectedItem, listImageOfAlbum, newName);

                                }else{
                                    Toast.makeText(getActivity(), "Album already existed!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                        listImageOfAlbum.updateToolbarText(0);
                        listImageOfAlbum.clearActionMode();
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    public void cloneFile(List<Image> selectedItem, ListImageOfAlbum listImageOfAlbum, String albumName){
        List<Image> imageList = (List<Image>) getArguments().getSerializable("ImageList");
        boolean isAll = false;
        if (imageList.size() == selectedItem.size())
            isAll= true;
        for(int i = 0; i < selectedItem.size(); i++){
            try {
                int index = listImageOfAlbum.findIndexInList(selectedItem.get(i));
                Path.copy(selectedItem.get(i).getImage(), new File(Environment.getExternalStorageDirectory().toString() + "/ONEUS/"+albumName+"/"+selectedItem.get(i).getText()));
                listImageOfAlbum.remove(index);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(isAll){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }
}
