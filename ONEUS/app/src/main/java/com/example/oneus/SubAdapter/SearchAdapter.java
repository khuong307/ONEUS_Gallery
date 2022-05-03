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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.oneus.ListImageOfAlbum;
import com.example.oneus.R;
import com.example.oneus.subClasses.Dialog.DialogEnterPassword;
import com.example.oneus.subClasses.ImageAlbum;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    Context context;
    List<ImageAlbum> mList;

    public SearchAdapter(Context context, List<ImageAlbum> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView;
        itemView = layoutInflater.inflate(R.layout.custom_search_album, parent, false);
        return new SearchAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.thumbnail.setImageURI(Uri.fromFile(new File(String.valueOf(mList.get(position).getThumbnail()))));
        holder.name.setText("Album: "+ mList.get(position).getAlbum());
        holder.quantity.setText("Quantity: "+ mList.get(position).getQuantity());
        File tmp=new File(Environment.getExternalStorageDirectory().toString() + "/ONEUS/"+mList.get(position).getAlbum());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String d = sdf.format(tmp.lastModified());
        holder.lastModify.setText(d);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickViewListImage(mList.get(position).getAlbum());
            }
        });
    }

    private void onClickViewListImage(String albumName){
        boolean isExistedPassword = checkExistedPassword(albumName);
        if (isExistedPassword){
            openEnterPasswordDialog(albumName, 4);
        }else{
            Intent intent = new Intent(context, ListImageOfAlbum.class);
            Bundle bundle = new Bundle();
            bundle.putString("AlbumName", albumName);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() ;
    }


    public class MyViewHolder  extends RecyclerView.ViewHolder{
        ImageView thumbnail;
        TextView name;
        TextView quantity;
        TextView lastModify;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.album_thumbnail);
            name = itemView.findViewById(R.id.textName);
            quantity = itemView.findViewById(R.id.textQuantity);
            lastModify = itemView.findViewById(R.id.textLastModify);


            linearLayout = itemView.findViewById(R.id.listAlbum);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.custom_album_image;
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

    public void openEnterPasswordDialog(String albumName, int activityCode){
        DialogEnterPassword dialogEnterPasswordPassword = new DialogEnterPassword(albumName, activityCode);
        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
        dialogEnterPasswordPassword.show((manager), "Create Password Dialog");
    }
}