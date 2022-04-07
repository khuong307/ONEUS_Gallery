package com.example.oneus.SubAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.oneus.R;
import com.example.oneus.fragment.TrashFragment;
import com.example.oneus.subClasses.Image;
import java.io.File;
import java.util.List;

public class TrashImageAdapter extends RecyclerView.Adapter<TrashImageAdapter.MyViewHolder> {

    Context context;
    List<Image> mList;
    TrashFragment trashFragment;

    public TrashImageAdapter(Context context, List<Image> mList,TrashFragment trashFragment) {
        this.context = context;
        this.mList = mList;
        this.trashFragment = trashFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.custom_trash_image, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (trashFragment.isActionMode){
            holder.checkImgChosen.setVisibility(View.VISIBLE);
            holder.delBtn.setVisibility(View.GONE);
            if(trashFragment.selectionList.contains(mList.get(position)) == true){
                holder.checkImgChosen.setChecked(true);
            }else{
                holder.checkImgChosen.setChecked(false);
            }
        }else{
            holder.delBtn.setVisibility(View.VISIBLE);
            holder.checkImgChosen.setChecked(false);
            holder.checkImgChosen.setVisibility(View.GONE);
        }
        holder.imageView.setImageURI(Uri.fromFile(new File(String.valueOf(mList.get(position).getImage()))));
        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageName = mList.get(position).getText();
                mList.get(position).getImage().delete();
                remove(position);
                Toast.makeText(context.getApplicationContext(), "Remove " + imageName, Toast.LENGTH_SHORT).show();
            }
        });

        holder.checkImgChosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trashFragment.check(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder{

        ImageView imageView;
        ImageButton delBtn;
        CheckBox checkImgChosen;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_trash);
            delBtn = itemView.findViewById(R.id.delBtn);
            checkImgChosen = itemView.findViewById(R.id.itemIMGChoose);
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private TrashImageAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final TrashImageAdapter.ClickListener clickListener) {
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
