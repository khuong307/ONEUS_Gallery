package com.example.oneus.SubAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneus.R;
import com.example.oneus.subClasses.Image;

import java.io.File;
import java.util.List;

public class MultiImagesAdapter extends RecyclerView.Adapter<MultiImagesAdapter.MyViewHolder> {

    Context context;
    List<Image> mList;

    public MultiImagesAdapter(Context context, List<Image> mList) {
        this.context = context;
        this.mList = mList;
    }

    public List<Image> getImageList(){
        return this.mList;
    }

    @NonNull
    @Override
    public MultiImagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView;
        itemView = layoutInflater.inflate(R.layout.custom_multi_images, parent, false);
        return new MultiImagesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.imageView.setImageURI(Uri.fromFile(new File(String.valueOf(mList.get(position).getImage()))));
        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove(position);
            }
        });
    }

    private void remove(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder{

        ImageView imageView;
        ImageButton removeBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.images_multi);
            removeBtn = (ImageButton) itemView.findViewById(R.id.removeBtn);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return R.layout.custom_multi_images;
    }
}
