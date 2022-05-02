package com.example.oneus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        List<Image> mList = new ArrayList<>();
        String albumPath = Environment.getExternalStorageDirectory().toString() + "/ONEUS";
        String cameraPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera";
        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

        File albumDirectory = new File(albumPath);
        File cameraDirectory = new File(cameraPath);
        File downloadDirectory = new File(downloadPath);

        List<Bitmap> bmps = new ArrayList<>();

        if (cameraDirectory.exists()){
            File[] cameraSubFolder = cameraDirectory.listFiles();
            for (int i = 0; i < cameraSubFolder.length; i++){
                String path = cameraSubFolder[i].getPath();
                Bitmap bmp = BitmapFactory.decodeFile(path);
                if (checkDiffImage(bmps, bmp)){
                    bmps.add(bmp);
                    mList.add(new Image(cameraSubFolder[i], cameraSubFolder[i].getName()));
                }
            }
        }

        if (downloadDirectory.exists()){
            File[] downloadSubFolder = downloadDirectory.listFiles();
            for (int i = 0; i < downloadSubFolder.length; i++){
                String path = downloadSubFolder[i].getPath();
                Bitmap bmp = BitmapFactory.decodeFile(path);
                if (checkDiffImage(bmps, bmp)){
                    bmps.add(bmp);
                    mList.add(new Image(downloadSubFolder[i], downloadSubFolder[i].getName()));
                }
            }
        }

        if (albumDirectory.exists()){
            File[] albumSubFolder = albumDirectory.listFiles();
            for (int i = 0; i < albumSubFolder.length; i++){
                File[] tmp = albumSubFolder[i].listFiles();
                for (int j = 0; j < tmp.length; j++){
                    String path = tmp[j].getPath();
                    Bitmap bmp = BitmapFactory.decodeFile(path);
                    if (checkDiffImage(bmps, bmp)){
                        bmps.add(bmp);
                        mList.add(new Image(tmp[j], tmp[j].getName()));
                    }
                }
            }
        }

        return mList;
    }
    public boolean checkDiffImage(List<Bitmap> bmps, Bitmap inputBmp){
        for (int i = 0; i < bmps.size(); i++){
            if (bmps.get(i).sameAs(inputBmp))
                return false;
        }
        return true;
    }
}