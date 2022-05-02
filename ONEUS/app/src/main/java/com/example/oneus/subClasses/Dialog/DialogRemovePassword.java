package com.example.oneus.subClasses.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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

import com.example.oneus.ListImageOfAlbum;
import com.example.oneus.R;

import java.io.File;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class DialogRemovePassword extends DialogFragment {
    private String albumName;
    private EditText txtPassword;
    SQLiteDatabase db;

    public DialogRemovePassword(String albumName) {
        this.albumName=albumName;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_remove_password, null);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);

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
                    if (password.isEmpty()==true){
                        Toast.makeText(getActivity(), "Please enter the password!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        try{
                            File storagePath = (getActivity()).getFilesDir();
                            String myDbPath = storagePath + "/" + "group01";
                            db = SQLiteDatabase.openDatabase(myDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                        } catch (SQLiteException e){}

                        String albumPath = Environment.getExternalStorageDirectory() +"/ONEUS/" + albumName;
                        Cursor c1 = db.rawQuery("select * from password where albumPath = '" + albumPath + "'", null);

                        c1.moveToPosition(0);
                        int idCol = c1.getColumnIndex("hashedPassword");
                        String hashedPassword = c1.getString(idCol);
                        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
                        if (result.verified){
                            db.beginTransaction();
                            try {
                                String raw_sql = "delete from password where albumPath = '" + albumPath + "';";
                                db.execSQL(raw_sql);
                                db.setTransactionSuccessful();
                            }
                            catch (SQLiteException e) {

                            }
                            finally { db.endTransaction(); }

                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Remove Password Successfully!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity(), "Incorrect Password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}