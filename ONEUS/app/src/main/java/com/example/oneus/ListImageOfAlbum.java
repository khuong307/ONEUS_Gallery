package com.example.oneus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oneus.SubAdapter.ImagesOfAlbumAdapter;
import com.example.oneus.fragment.SlideshowDialogFragment;
import com.example.oneus.subClasses.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListImageOfAlbum extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Image> imageList;
    ImagesOfAlbumAdapter imageAdapter;

    Toolbar toolbar;
    TextView textViewToolbar;
    ImageButton btnBack;
    ImageButton btnDelete;
    ImageButton btnMove;


    public boolean isActionMode = false;
    List<Image> selectionList = new ArrayList<>();
    int counter = 0;

    public void updateImageList(){
        Bundle bundle = getIntent().getExtras();
        String albumName = bundle.get("AlbumName").toString();
        imageList.clear();
        imageList = Image.setImageList(albumName);
    }

    public void updateImageAdapter(){
        imageAdapter = new ImagesOfAlbumAdapter(this, imageList);
    }

    public void updateRecyclerView(){
        recyclerView.setAdapter(imageAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_image_of_album);

        //center ONEUS in Action Bar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        textViewToolbar = findViewById(R.id.text_toolbar);
        textViewToolbar.setVisibility(View.GONE);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setVisibility(View.GONE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearActionMode();
            }
        });

        btnDelete = findViewById(R.id.item_delete);
        btnDelete.setVisibility(View.GONE);

        btnMove = findViewById(R.id.item_move);
        btnMove.setVisibility(View.GONE);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        String albumName = bundle.get("AlbumName").toString();
//
        imageList = Image.setImageList(albumName);
        recyclerView = findViewById(R.id.recycle_view_list_image_of_album);
        imageAdapter = new ImagesOfAlbumAdapter(this, imageList);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        recyclerView.addOnItemTouchListener(new ImagesOfAlbumAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new ImagesOfAlbumAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String folderNames[] = imageList.get(0).getImage().getParent().split("/");
                imageList = Image.setImageList(folderNames[folderNames.length-1]);
                if (position!= imageList.size()){
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("imageList", (Serializable) imageList);
                    bundle1.putInt("Position", position);

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                    newFragment.setArguments(bundle1);
                    newFragment.show(ft, "slideshow");
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if (position != imageList.size()){
                    startSelection(position);
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.itemIMGChoose);
                    checkBox.setVisibility(View.VISIBLE);
                    if (checkBox.isChecked() == false){
                        checkBox.setChecked(true);
                        selectionList.add(imageList.get(position));
                        counter++;
                        updateToolbarText(counter);
                    }
                }
            }
        }));

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < selectionList.size(); i++){
                    //Toast.makeText(ListImageOfAlbum.this, i+"", Toast.LENGTH_SHORT).show();
                    int index = findIndexInList(selectionList.get(i));
                    remove(index);
                }

                updateToolbarText(0);
                clearActionMode();
            }
        });

    }

    public void clearActionMode() {
        isActionMode = false;
        toolbar.setVisibility(View.GONE);
        textViewToolbar.setText("0 item selected");
        counter = 0;
        selectionList.clear();

        for (int i = 0; i < imageList.size(); i++) {
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
            holder.itemView.findViewById(R.id.itemIMGChoose).setVisibility(View.GONE);
            CheckBox checkBox = (CheckBox) holder.itemView.findViewById(R.id.itemIMGChoose);
            checkBox.setChecked(false);
            holder.itemView.findViewById(R.id.editBtn).setVisibility(View.VISIBLE);
        }

//
        RecyclerView.ViewHolder holderAddBtn = recyclerView.findViewHolderForAdapterPosition(imageList.size());
        holderAddBtn.itemView.findViewById(R.id.addImgBtn).setVisibility(View.VISIBLE);


        updateImageList();
        imageAdapter = new ImagesOfAlbumAdapter(this, imageList);
        recyclerView.setAdapter(imageAdapter);
    }

    public void startSelection(int position){
        Toast.makeText(this, position+"", Toast.LENGTH_SHORT).show();
        if (isActionMode == false){
            isActionMode = true;
            toolbar.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnMove.setVisibility(View.VISIBLE);
            textViewToolbar.setVisibility(View.VISIBLE);

            for (int i = 0; i < imageList.size(); i++) {
                RecyclerView.ViewHolder holder =  recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                holder.itemView.findViewById(R.id.itemIMGChoose).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.editBtn).setVisibility(View.GONE);
            }
            RecyclerView.ViewHolder holderAddBtn =  recyclerView.findViewHolderForAdapterPosition(imageList.size());
            holderAddBtn.itemView.findViewById(R.id.addImgBtn).setVisibility(View.GONE);
        }
    }

    public void check(View view, int position){
        if(((CheckBox)view).isChecked() == true){
            selectionList.add(imageList.get(position));
            counter++;
        }else{
            selectionList.remove(imageList.get(position));
            counter--;
        }
        updateToolbarText(counter);
    }

    private void updateToolbarText(int counter){
        if (counter == 0){
            textViewToolbar.setText("0 item selected");
        }
        else if (counter == 1){
            textViewToolbar.setText("1 item selected");
        }
        else{
            textViewToolbar.setText(counter + " items selected");
        }
    }


    //btn trash - delete item.

    private void remove(int index) {
        Toast.makeText(this, index+"", Toast.LENGTH_SHORT).show();
        imageList.get(index).getImage().delete();
        imageList.remove(index);
        Log.d("Index", index+"");
        imageAdapter.notifyItemRemoved(index);
    }

    private int findIndexInList(Image obj){
        int index = imageList.indexOf(obj);
        return index;
    }
}