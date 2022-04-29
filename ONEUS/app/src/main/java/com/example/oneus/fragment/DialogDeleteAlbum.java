package com.example.oneus.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneus.R;
import com.example.oneus.SubAdapter.AlbumAdapter;
import com.example.oneus.subClasses.Image;
import com.example.oneus.subClasses.ImageAlbum;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

public class DialogDeleteAlbum extends DialogFragment {
    private String albumName;
    private TextView txvDeleteAlbum;
    private ImageView thumbnail;
    private TextView delAlbumName;

    public DialogDeleteAlbum(String albumName) {
        this.albumName=albumName;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete_album_layout, null);
        txvDeleteAlbum = (TextView) view.findViewById(R.id.txvDeleteAlbum);
        thumbnail = (ImageView) view.findViewById(R.id.delThumbnail);
        delAlbumName = (TextView) view.findViewById(R.id.delAlbumName);


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
        File thumbnailsIMG = Image.setImageList(this.albumName).get(0).getImage();
        thumbnail.setImageURI(Uri.fromFile(thumbnailsIMG));
        delAlbumName.setText(albumName);
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
                    RemoveAlbum();
                    SQLiteDatabase db = null;
                    try{
                        File storagePath = (getActivity()).getFilesDir();
                        String myDbPath = storagePath + "/" + "group01";
                        db = SQLiteDatabase.openDatabase(myDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                    } catch (SQLiteException e){}

                    String albumPath = Environment.getExternalStorageDirectory() +"/ONEUS/" + albumName;

                    db.beginTransaction();
                    try {
                        String raw_sql = "delete from password where albumPath = '" + albumPath + "';";
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
                }
            });
        }
    }


    boolean createSubsDirectory(String FolderName){
        File oldFolder=new File(Environment.getExternalStorageDirectory()+"/ONEUS/" +albumName);
        File folder = new File(Environment.getExternalStorageDirectory() +"/ONEUS/" + FolderName);
        if (oldFolder.exists()){
            folder.mkdir();
            return oldFolder.renameTo(folder);
        }else{
            Toast.makeText(getActivity(), "Can not rename album!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public ArrayList<File> ListImages(){
        String root = Environment.getExternalStorageDirectory()+"/ONEUS/"+albumName;
        File dir = new File(root);
        File[] items = dir.listFiles();
        ArrayList<File> files = new ArrayList<File>();
        if (items==null)
            return files;
        for (int i = 0; i < items.length; ++i)
            if (!items[i].isDirectory())
                files.add(items[i]);
        return files;
    }

    public String RemoveImages(String path){
        File file = new File(path);
        if (!file.exists())
            return "The file does not exist";
        if (file.delete())
            return "";
        else
            return "Delete failed";
    }

    public String RemoveAlbum(){
        String root = Environment.getExternalStorageDirectory()+"/ONEUS/"+albumName;
        File currentFolder = new File(root);
        if (!currentFolder.exists())
            return "The album does not exist";

        ArrayList<File> files =ListImages();
        for (int i=0;i< files.size();i++)
            RemoveImages(root+"/"+files.get(i).getName());
        if (currentFolder.delete())
            return "";
        else
            return "Delete failed";
    }
}