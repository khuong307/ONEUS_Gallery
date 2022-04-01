package com.example.oneus.SubAdapter;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oneus.ListImageOfAlbum;
import com.example.oneus.R;
import com.example.oneus.subClasses.DialogAddImage;
import com.example.oneus.subClasses.DialogNewAlbum;
import com.example.oneus.subClasses.FavImage;
import com.example.oneus.subClasses.Image;

import java.io.File;
import java.util.List;

public class ImagesOfAlbumAdapter extends RecyclerView.Adapter<ImagesOfAlbumAdapter.MyViewHolder> {

    ListImageOfAlbum context;
    List<Image> mList;

    public ImagesOfAlbumAdapter(ListImageOfAlbum context, List<Image> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ImagesOfAlbumAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView;
        if(viewType == R.layout.custom_images_in_album){
            itemView = layoutInflater.inflate(R.layout.custom_images_in_album, parent, false);
        }
        else {
            itemView = layoutInflater.inflate(R.layout.add_image_button, parent, false);
        }
        return new ImagesOfAlbumAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(position == mList.size()) {
            holder.addImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAddImgDialog(mList.get(0).getImage().getParent());
                }
            });
        }
        else{
            holder.checkImgChosen.setVisibility(View.GONE);
            holder.imageView.setImageURI(Uri.fromFile(new File(String.valueOf(mList.get(position).getImage()))));
            holder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
                }
            });

            holder.checkImgChosen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.check(view, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder{

        ImageView imageView;
        ImageButton editBtn;
        CheckBox checkImgChosen;

        ImageButton addImgBtn;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_in_album);
            editBtn = (ImageButton) itemView.findViewById(R.id.editBtn);
            addImgBtn = (ImageButton) itemView.findViewById(R.id.addImgBtn);
            checkImgChosen = (CheckBox) itemView.findViewById(R.id.itemIMGChoose);

            linearLayout = itemView.findViewById(R.id.listImages);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return (position == mList.size()) ? R.layout.add_image_button : R.layout.custom_images_in_album;
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ImagesOfAlbumAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ImagesOfAlbumAdapter.ClickListener clickListener) {
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

    public void openAddImgDialog(String parentFolder){
        DialogAddImage dialogAddImage = new DialogAddImage();
        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("ParentFolder", parentFolder);
        dialogAddImage.setArguments(bundle);
        dialogAddImage.show((manager), "Add New Image");
    }
}
