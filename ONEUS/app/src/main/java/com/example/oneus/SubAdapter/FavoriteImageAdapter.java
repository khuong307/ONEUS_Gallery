package com.example.oneus.SubAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
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
        holder.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageName = mList.get(position).getText();
                mList.get(position).getImage().delete();
                remove(position);
                Toast.makeText(context.getApplicationContext(), "Remove " + imageName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder{

        ImageView imageView;
        ImageButton favBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_favorite);
            favBtn = itemView.findViewById(R.id.favBtn);
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private FavoriteImageAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FavoriteImageAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onClick(child, recyclerView.getChildPosition(child));
                    }
                    return super.onDoubleTap(e);
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                //clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private void remove(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }
}
