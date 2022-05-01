package com.example.oneus.SubAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.oneus.AllImages;
import com.example.oneus.R;
import com.example.oneus.subClasses.Image;

import java.io.File;
import java.util.List;

public class AllIMGAdapter extends RecyclerView.Adapter<AllIMGAdapter.MyViewHolder> {

    AllImages context;
    List<Image> mList;
    LayoutInflater layoutInflater;

    public AllIMGAdapter(AllImages context, List<Image> mList) {
        setHasStableIds(true);
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public AllIMGAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = layoutInflater.inflate(R.layout.custom_all_images, parent, false);

        return new AllIMGAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.imageView.setImageURI(Uri.fromFile(new File(String.valueOf(mList.get(position).getImage()))));
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class MyViewHolder  extends RecyclerView.ViewHolder{

        ImageView imageView;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_in_album);
            linearLayout = itemView.findViewById(R.id.listImages);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private AllIMGAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final AllIMGAdapter.ClickListener clickListener) {
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
}
