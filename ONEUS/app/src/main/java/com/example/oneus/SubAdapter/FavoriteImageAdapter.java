package com.example.oneus.SubAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneus.R;
import com.example.oneus.subClasses.FavImage;

import java.io.File;
import java.util.List;

public class FavoriteImageAdapter extends RecyclerView.Adapter<FavoriteImageAdapter.MyViewHolder> {

    Context context;
    List<FavImage> mList;

    public FavoriteImageAdapter(Context context, List<FavImage> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.custom_favorite_image, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.imageView.setImageURI(Uri.fromFile(new File(String.valueOf(mList.get(position).getImage()))));
        holder.textView.setText(mList.get(position).getText());
        holder.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mList.get(position).getImage().delete();
                remove(position);
                Toast.makeText(context.getApplicationContext(), "Undo Favorite Image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;
        ImageButton favBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_favorite);
            textView = itemView.findViewById(R.id.txt);
            favBtn = itemView.findViewById(R.id.favBtn);
        }
    }

    private void remove(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }
}
