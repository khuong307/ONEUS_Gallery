package com.example.oneus.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneus.R;
import com.example.oneus.SubAdapter.AlbumAdapter;
import com.example.oneus.subClasses.ImageAlbum;

import java.io.File;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class DialogCreatePassword extends DialogFragment {
    private EditText txtPassword, txtPasswordConfirm;
    private String albumName;
    SQLiteDatabase db;

    public DialogCreatePassword(String albumName) {
        this.albumName=albumName;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_password, null);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);
        txtPasswordConfirm = (EditText) view.findViewById(R.id.txtPasswordConfirm);

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
                    String password = txtPassword.getText().toString();
                    String passwordConfirm = txtPasswordConfirm.getText().toString();
                    if (password.isEmpty()==true){
                        Toast.makeText(getActivity(), "Please enter the password!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (password.equals(passwordConfirm)){
                            try{
                                File storagePath = (getActivity()).getFilesDir();
                                String myDbPath = storagePath + "/" + "group01";
                                db = SQLiteDatabase.openDatabase(myDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                            } catch (SQLiteException e){}

                            db.beginTransaction();
                            try {
                                db.execSQL("create table if not exists password ("
                                        + " albumPath text PRIMARY KEY, "
                                        + " hashedPassword text); " );
                                db.setTransactionSuccessful();
                            }
                            catch (SQLiteException e) {

                            }
                            finally { db.endTransaction(); }

                            db.beginTransaction();
                            try {
                                String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                                String path = Environment.getExternalStorageDirectory() +"/ONEUS/" + albumName;
                                String raw_sql = "insert into password(albumPath, hashedPassword) values ('" + path + "', '" + hashedPassword + "');";
                                db.execSQL(raw_sql);
                                db.setTransactionSuccessful();
                            }
                            catch (SQLiteException e) {

                            }
                            finally { db.endTransaction(); }

                            RecyclerView recyclerView = getActivity().findViewById(R.id.recycle_view_album);
                            AlbumAdapter albumAdapter = new AlbumAdapter(getContext(), ImageAlbum.setAlbumList());
                            recyclerView.setAdapter(albumAdapter);
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Create Password Successfully!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity(), "Password confirm does not match", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}
