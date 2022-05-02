package com.example.oneus.SubAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.oneus.subClasses.Dialog.DialogNewAlbum;
import com.example.oneus.subClasses.ImageAlbum;

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

            //set transparent 2 thumbnails.
            holder.thumbnail.setImageAlpha(128);
            holder.thumbnail1.setImageAlpha(128);
        }
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
}
