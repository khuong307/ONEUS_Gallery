package com.example.oneus;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.oneus.SubAdapter.ImagesOfAlbumAdapter;
import com.example.oneus.fragment.SlideshowDialogFragment;
import com.example.oneus.subClasses.Image;

import java.io.Serializable;
import java.util.List;

public class ListImageOfAlbum extends AppCompatActivity {
    View view;
    RecyclerView recyclerView;
    List<Image> imageList;
    ImagesOfAlbumAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_image_of_album);

        //center ONEUS in Action Bar
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

            }
        }));
    }
}