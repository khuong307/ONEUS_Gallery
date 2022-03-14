package com.example.oneus;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oneus.SubAdapter.ImagesOfAlbumAdapter;
import com.example.oneus.fragment.SlideshowDialogFragment;
import com.example.oneus.subClasses.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListImageOfAlbum extends AppCompatActivity {
    View view;
    RecyclerView recyclerView;
    List<Image> imageList;
    ImagesOfAlbumAdapter imageAdapter;

    Toolbar toolbar;
    TextView textViewToolbar;

    public boolean isActionMode = false;
    List<Image> selectionList = new ArrayList<>();
    int counter = 0;
    public int Position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_image_of_album);

        //center ONEUS in Action Bar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        textViewToolbar = findViewById(R.id.text_toolbar);
        textViewToolbar.setVisibility(View.GONE);


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
                RadioButton radioButton = (RadioButton) view.findViewById(R.id.itemIMGChoose);
                radioButton.setVisibility(View.VISIBLE);
                startSelection(position);
                if (Position == position){
                    radioButton.setChecked(true);
                    Position = -1;
                }
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        check(view, position);
                    }
                });
            }
        }));
    }

    public void startSelection(int position){
        if (!isActionMode){
            isActionMode = true;
            selectionList.add(imageList.get(position));
            counter++;
            Toast.makeText(ListImageOfAlbum.this, counter+"", Toast.LENGTH_SHORT).show();
            updateToolbarText(counter);

            toolbar.inflateMenu(R.menu.mennu_delete_action);
            toolbar.setVisibility(View.VISIBLE);
            textViewToolbar.setVisibility(View.VISIBLE);
            Position = position;
            imageAdapter.notifyDataSetChanged();
        }
    }

    public void check(View view, int position){
        if(((RadioButton)view).isChecked() == true){
            selectionList.add(imageList.get(position));
            counter++;
        }else{
            selectionList.remove(imageList.get(position));
            counter--;
        }
        Toast.makeText(this, counter+"", Toast.LENGTH_SHORT).show();
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
}