package com.example.oneus.SubAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneus.ListImageOfAlbum;
import com.example.oneus.R;
import com.example.oneus.fragment.DialogModifyAlbum;
import com.example.oneus.subClasses.Dialog.DialogAddImageBottom;
import com.example.oneus.subClasses.Dialog.DialogChangePassword;
import com.example.oneus.subClasses.Dialog.DialogCreatePassword;
import com.example.oneus.fragment.DialogDeleteAlbum;
import com.example.oneus.subClasses.Dialog.DialogEnterPassword;
import com.example.oneus.subClasses.Dialog.DialogNewAlbum;
import com.example.oneus.subClasses.Dialog.DialogRemovePassword;
import com.example.oneus.subClasses.ImageAlbum;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {
    Context context;
    List<ImageAlbum> mList;

    public AlbumAdapter(Context context, List<ImageAlbum> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public AlbumAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView;
        if(viewType == R.layout.custom_album_image){
            itemView = layoutInflater.inflate(R.layout.custom_album_image, parent, false);
        }
        else {
            itemView = layoutInflater.inflate(R.layout.add_button_home, parent, false);
        }
        return new AlbumAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(position == mList.size()) {
            holder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog();
                }
            });
        }
        else {
            holder.thumbnail.setImageURI(Uri.fromFile(new File(String.valueOf(mList.get(position).getThumbnail()))));
            holder.thumbnail1.setImageURI(Uri.fromFile(new File(String.valueOf(mList.get(position).getThumbnail()))));
            holder.thumbnail2.setImageURI(Uri.fromFile(new File(String.valueOf(mList.get(position).getThumbnail()))));
            holder.album_name.setText(mList.get(position).getAlbum());
            holder.quantity.setText(Integer.toString(mList.get(position).getQuantity()));

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickViewListImage(mList.get(position).getAlbum());
                }
            });

            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    openBottomSheetDialog(mList.get(position).getAlbum());
                    return true;
                }
            });

            //set transparent 2 thumbnails.
            holder.thumbnail.setImageAlpha(128);
            holder.thumbnail1.setImageAlpha(128);
        }
    }

    public void openBottomSheetDialog(String albumName) {
        View viewDialog= LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.bottom_sheet_album,null);

        BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(context);

        // Khang
        boolean isExistedPassword = checkExistedPassword(albumName);
        if (isExistedPassword){
            viewDialog.findViewById(R.id.layoutCreatePassword).setVisibility(View.GONE);
        }
        else{
            viewDialog.findViewById(R.id.layoutChangePassword).setVisibility(View.GONE);
            viewDialog.findViewById(R.id.layoutRemovePassword).setVisibility(View.GONE);
        }
        // Khang

        viewDialog.findViewById(R.id.layoutEditAlbum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExistedPassword){
                    openEnterPasswordDialog(albumName, 2);
                    bottomSheetDialog.dismiss();
                }
                else{
                    openModifyDialog(albumName);
                    bottomSheetDialog.dismiss();
                }
                // Khang
            }
        });

        viewDialog.findViewById(R.id.layoutMoveAlbum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAddImage(albumName);
                bottomSheetDialog.dismiss();
            }
        });

        viewDialog.findViewById(R.id.layoutDeleteAlbum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Khang
                if (isExistedPassword){
                    openEnterPasswordDialog(albumName, 3);
                    bottomSheetDialog.dismiss();
                }
                else{
                    openDeleteDialog(albumName);
                    bottomSheetDialog.dismiss();
                }
                // Khang
            }
        });

        viewDialog.findViewById(R.id.layoutCreatePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreatePasswordDialog(albumName);
                bottomSheetDialog.dismiss();
            }
        });

        viewDialog.findViewById(R.id.layoutChangePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangePasswordDialog(albumName);
                bottomSheetDialog.dismiss();
            }
        });

        viewDialog.findViewById(R.id.layoutRemovePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRemovePasswordDialog(albumName);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.show();
    }

    private void onClickViewListImage(String albumName){
        boolean isExistedPassword = checkExistedPassword(albumName);
        if (isExistedPassword){
            openEnterPasswordDialog(albumName, 4);
        }
        else {
            Intent intent = new Intent(context, ListImageOfAlbum.class);
            Bundle bundle = new Bundle();
            bundle.putString("AlbumName", albumName);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }


    public class MyViewHolder  extends RecyclerView.ViewHolder{

        ImageView thumbnail;
        ImageView thumbnail1;
        ImageView thumbnail2;
        TextView album_name;
        TextView quantity;
        ImageButton addBtn;

        LinearLayout linearLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.album_thumbnail);
            thumbnail1 = itemView.findViewById(R.id.album_thumbnail1);
            thumbnail2 = itemView.findViewById(R.id.album_thumbnail2);
            album_name = itemView.findViewById(R.id.albumName);
            quantity = itemView.findViewById(R.id.quantity_image);
            addBtn = (ImageButton) itemView.findViewById(R.id.btnAdd);

            linearLayout = itemView.findViewById(R.id.listAlbum);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mList.size()) ? R.layout.add_button_home : R.layout.custom_album_image;
    }

    public void openDialog(){
        DialogNewAlbum dialogNewAlbum = new DialogNewAlbum();
        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
        dialogNewAlbum.show((manager), "New Album Dialog");
    }

    public void openDialogAddImage(String albumName){
        DialogAddImageBottom dialogAddImageBottom = new DialogAddImageBottom();
        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
        Bundle data = new Bundle();
        data.putString("AlbumName", albumName);
        dialogAddImageBottom.setArguments(data);
        dialogAddImageBottom.show((manager), "Add Image Dialog");
    }

    // Minh
    public void openModifyDialog(String albumName){
        DialogModifyAlbum dialogModifyAlbum = new DialogModifyAlbum(albumName);
        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
        dialogModifyAlbum.show((manager), "Modify Album Dialog");
    }

    // Minh
    public void openDeleteDialog(String albumName){
        DialogDeleteAlbum dialogDeleteAlbum = new DialogDeleteAlbum(albumName);
        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
        dialogDeleteAlbum.show((manager), "Delete Album Dialog");
    }


    // Khang
    public void openCreatePasswordDialog(String albumName){
        DialogCreatePassword dialogCreatePassword = new DialogCreatePassword(albumName);
        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
        dialogCreatePassword.show((manager), "Create Password Dialog");
    }

    public void openChangePasswordDialog(String albumName){
        DialogChangePassword dialogChangePassword = new DialogChangePassword(albumName);
        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
        dialogChangePassword.show((manager), "Change Password Dialog");
    }

    public void openRemovePasswordDialog(String albumName){
        DialogRemovePassword dialogRemovePassword = new DialogRemovePassword(albumName);
        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
        dialogRemovePassword.show((manager), "Remove Password Dialog");
    }

    public void openEnterPasswordDialog(String albumName, int activityCode){
        DialogEnterPassword dialogEnterPasswordPassword = new DialogEnterPassword(albumName, activityCode);
        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
        dialogEnterPasswordPassword.show((manager), "Create Password Dialog");
    }

    public boolean checkExistedPassword(String albumName){
        SQLiteDatabase db;
        File storagePath = context.getFilesDir();
        String myDbPath = storagePath + "/" + "group01";
        db = SQLiteDatabase.openDatabase(myDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        String albumPath = Environment.getExternalStorageDirectory() +"/ONEUS/" + albumName;

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

        Cursor c1 = db.rawQuery("select * from password where albumPath = '" + albumPath + "'", null);
        int resultNum = c1.getCount();

        return resultNum != 0;
    }
}
