package com.example.oneus;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.oneus.SubAdapter.ImagesOfAlbumAdapter;
import com.example.oneus.fragment.SlideshowDialogFragment;
import com.example.oneus.subClasses.Dialog.DialogDeleteAlbum;
import com.example.oneus.subClasses.Dialog.DialogEnterTime;
import com.example.oneus.subClasses.Dialog.DialogMoveImage;
import com.example.oneus.subClasses.Image;
import com.example.oneus.subClasses.Path;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListImageOfAlbum extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Image> imageList;
    ImagesOfAlbumAdapter imageAdapter;
    FloatingActionButton fab;

    Toolbar toolbar;
    TextView textViewToolbar;
    ImageButton btnBack;
    ImageButton btnDelete;
    ImageButton btnMove;
    Button chooseAllBtn;
    String albumName;


    public void setAlbumName(){
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        String albumName = bundle.get("AlbumName").toString();
        this.albumName = albumName;
    }
    public String getAlbumName(){
        return this.albumName;
    }


    public static boolean isActionMode = false;
    public static List<Image> selectionList = new ArrayList<>();
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
        isActionMode = false;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        textViewToolbar = findViewById(R.id.text_toolbar);
        textViewToolbar.setVisibility(View.GONE);
        chooseAllBtn = findViewById(R.id.chooseAllBtn);
        chooseAllBtn.setVisibility(View.GONE);
        chooseAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionList.clear();
                selectionList = new ArrayList<>(imageList);
                updateToolbarText(imageList.size());
                chooseAllItem();
            }
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setVisibility(View.GONE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearActionMode();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.btnInfo);
        fab.setImageTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ListImageOfAlbum.this, R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_bottom_sheet_info, (LinearLayout)findViewById(R.id.bottomSheetInfo));
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });


        btnDelete = findViewById(R.id.item_delete);
        btnDelete.setVisibility(View.GONE);

        btnMove = findViewById(R.id.item_move);
        btnMove.setVisibility(View.GONE);
        btnMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_view_images);



        setAlbumName();
        TextView title = (TextView) findViewById(R.id.albumTitle);
        title.setText("ALBUM "+albumName);

        ImageButton returnBtn = (ImageButton) findViewById(R.id.btnBackAlbumList);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isActionMode = false;
                Intent intent = new Intent(ListImageOfAlbum.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ImageButton btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogEnterTime dialogEnterTime = new DialogEnterTime();
                FragmentManager manager = getSupportFragmentManager();

                Bundle bundle = new Bundle();
                bundle.putSerializable("ImageList", (Serializable) imageList);
                dialogEnterTime.setArguments(bundle);
                dialogEnterTime.show((manager), "Enter Time");
            }
        });

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
                if (selectionList.size() == imageList.size()){
                    openDialogDeleteAlbum();
                }else{
                    for (int i = 0; i < selectionList.size(); i++){
                        try {
                            Path.copy(selectionList.get(i).getImage(), new File(Environment.getExternalStorageDirectory().toString() + "/ONEUS/Trash/"+selectionList.get(i).getText()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int index = findIndexInList(selectionList.get(i));
                        remove(index);
                    }
                    updateToolbarText(0);
                    clearActionMode();
                }
            }
        });

    }

    public void clearActionMode() {
        isActionMode = false;
        toolbar.setVisibility(View.GONE);
        chooseAllBtn.setVisibility(View.GONE);
        textViewToolbar.setText("Selected: 0");
        counter = 0;
        selectionList.clear();

        updateImageList();
        imageAdapter = new ImagesOfAlbumAdapter(this, imageList);
        recyclerView.setAdapter(imageAdapter);
    }

    public void startSelection(int position){
        if (isActionMode == false){
            isActionMode = true;
            toolbar.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnMove.setVisibility(View.VISIBLE);
            textViewToolbar.setVisibility(View.VISIBLE);
            chooseAllBtn.setVisibility(View.VISIBLE);

            updateImageAdapter();
            updateRecyclerView();
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

    public void updateToolbarText(int counter){
        textViewToolbar.setText("Selected: "+counter);
    }


    //btn trash - delete item.

    public void remove(int index) {
        imageList.get(index).getImage().delete();
        imageList.remove(index);
        Log.d("Index", index+"");
        imageAdapter.notifyItemRemoved(index);
    }

    public int findIndexInList(Image obj){
        int index = imageList.indexOf(obj);
        return index;
    }

    public void chooseAllItem(){
        counter = imageList.size();
        updateImageAdapter();
        updateRecyclerView();
    }


    public void openDialog(){
        DialogMoveImage dialogMoveImage = new DialogMoveImage();
        FragmentManager manager = (this).getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("ParentFolder", albumName);
        bundle.putSerializable("SelectedItems", (Serializable) selectionList);
        dialogMoveImage.setArguments(bundle);
        dialogMoveImage.show((manager), "Move Images");
    }

    public void openDialogDeleteAlbum(){
        DialogDeleteAlbum dialogDeleteAlbum = new DialogDeleteAlbum();
        FragmentManager manager = (this).getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("ParentFolder", imageList.get(0).getImage().getParent());
        dialogDeleteAlbum.setArguments(bundle);
        dialogDeleteAlbum.show((manager), "Delete Album");
    }
}