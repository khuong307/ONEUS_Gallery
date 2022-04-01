package com.example.oneus.SubAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneus.ListImageOfAlbum;
import com.example.oneus.MainActivity;
import com.example.oneus.R;
import com.example.oneus.subClasses.DialogModifyAlbum;
import com.example.oneus.subClasses.DialogNewAlbum;
import com.example.oneus.subClasses.ImageAlbum;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder>  {
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

            // Minh
            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    openBottomSheetDialog();
                    return true;
                }
            });

            //set transparent 2 thumbnails.
            holder.thumbnail.setImageAlpha(128);
            holder.thumbnail1.setImageAlpha(128);
        }
    }


    // Minh
    public void openBottomSheetDialog() {
        View viewDialog= LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.bottom_sheet_album,null);

        BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(context);

        viewDialog.findViewById(R.id.layoutEditAlbum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openModifyDialog();
                bottomSheetDialog.dismiss();
            }
        });

        viewDialog.findViewById(R.id.layoutMoveAlbum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Move album",Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        viewDialog.findViewById(R.id.layoutDeleteAlbum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Delete album",Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.show();
    }

    private void onClickViewListImage(String albumName){
        Intent intent = new Intent(context, ListImageOfAlbum.class);
        Bundle bundle = new Bundle();
        bundle.putString("AlbumName", albumName);
        intent.putExtras(bundle);
        context.startActivity(intent);
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

    // Minh
    public void openModifyDialog(){
        DialogModifyAlbum dialogModifyAlbum = new DialogModifyAlbum();
        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
        dialogModifyAlbum.show((manager), "Modify Album Dialog");
    }
}
