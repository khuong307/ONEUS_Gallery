package com.example.oneus.SubAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.oneus.subClasses.Dialog.DialogNewAlbum;
import com.example.oneus.subClasses.ImageAlbum;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        holder.name.setText("Album Name: "+ mList.get(position).getAlbum());
        holder.quantity.setText("Quantity: "+ mList.get(position).getQuantity());
        File tmp=new File(Environment.getExternalStorageDirectory().toString() + "/ONEUS/"+mList.get(position).getAlbum());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String d = sdf.format(tmp.lastModified()).toString();
        //Date d = new Date(tmp.lastModified());
        holder.lastModify.setText(d.toString());


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickViewListImage(mList.get(position).getAlbum());
            }
        });





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


}
