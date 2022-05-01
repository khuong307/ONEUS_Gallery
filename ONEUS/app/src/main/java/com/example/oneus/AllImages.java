package com.example.oneus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.oneus.SubAdapter.AllIMGAdapter;
import com.example.oneus.fragment.SlideshowAllImageFragment;
import com.example.oneus.fragment.SlideshowDialogFragment;
import com.example.oneus.subClasses.FileUtils;
import com.example.oneus.subClasses.Image;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AllImages extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Image> imageList;
    AllIMGAdapter imageAdapter;
    ImageButton btnBackAlbumList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_images);
        getSupportActionBar().hide();
        btnBackAlbumList = findViewById(R.id.btnBackAlbumList);
        btnBackAlbumList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllImages.this, MainActivity.class));
            }
        });

        imageList = FindFiles();
        recyclerView = findViewById(R.id.recycle_view_list_image_of_album);
        imageAdapter = new AllIMGAdapter(this, imageList);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        recyclerView.addOnItemTouchListener(new AllIMGAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new AllIMGAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("imageList", (Serializable) imageList);
                bundle1.putInt("Position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowAllImageFragment newFragment = SlideshowAllImageFragment.newInstance();
                newFragment.setArguments(bundle1);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
    private List<Image> FindFiles() {
        List<Image> imageList = new ArrayList<>();
        String SD_CARD_ROOT;
        File mFile = Environment.getExternalStorageDirectory();
        SD_CARD_ROOT=mFile.toString();

        final List<String> tFileList = new ArrayList<String>();
        Resources resources = getResources();
        // array of valid image file extensions
        String[] imageTypes = {"jpeg", "png", "jpg"};
        FilenameFilter[] filter = new FilenameFilter[imageTypes.length];

        int i = 0;
        for (final String type : imageTypes) {
            filter[i] = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith("." + type);
                }
            };
            i++;
        }

        FileUtils fileUtils = new FileUtils();
        File[] allMatchingFiles = fileUtils.listFilesAsArray(
                new File(SD_CARD_ROOT), filter, -1);
        for (File f : allMatchingFiles) {
            tFileList.add(f.getAbsolutePath());
            File tmp = new File(f.getAbsolutePath());
            imageList.add(new Image(tmp, tmp.getName()));
        }
        return imageList;
    }
}