package com.example.oneus.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.oneus.R;
import com.example.oneus.SubAdapter.AlbumAdapter;
import com.example.oneus.subClasses.ImageAlbum;
import com.example.oneus.subClasses.Path;

import java.io.File;
import java.util.List;

public class HomeFragment extends Fragment{
    View view;
    RecyclerView recyclerView;
    List<ImageAlbum> mList;
    AlbumAdapter albumAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageButton addBtn;


    public HomeFragment() {
        // Required empty public constructor
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = ImageAlbum.setAlbumList();
    }

    // Khang
    @Override
    public void onResume(){
        super.onResume();
        String albumPath = Environment.getExternalStorageDirectory().toString() + "/ONEUS";
        String allAlbumPath = albumPath + "/All";
        String cameraPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera";
        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

        File albumDirectory = new File(albumPath);
        File cameraDirectory = new File(cameraPath);
        File downloadDirectory = new File(downloadPath);

        if (cameraDirectory.exists()){
            File[] cameraSubFolder = cameraDirectory.listFiles();
            for (int i = 0; i < cameraSubFolder.length; i++){
                try{
                    Path.copy(cameraSubFolder[i], new File(allAlbumPath + "/" + cameraSubFolder[i].getName()));
                }
                catch (Exception e){};
            }
        }

        if (downloadDirectory.exists()){
            File[] downloadSubFolder = downloadDirectory.listFiles();
            for (int i = 0; i < downloadSubFolder.length; i++){
                try{
                    Path.copy(downloadSubFolder[i], new File(allAlbumPath + "/" + downloadSubFolder[i].getName()));
                }
                catch (Exception e){};
            }
        }

        if (albumDirectory.exists()){
            File[] albumSubFolder = albumDirectory.listFiles();
            for (int i = 0; i < albumSubFolder.length; i++){
                if (!albumSubFolder[i].getName().equals("Trash") && !albumSubFolder[i].getName().equals("Favorite") && !albumSubFolder[i].getName().equals("All")){
                    File[] tmp = albumSubFolder[i].listFiles();
                    for (int j = 0; j < tmp.length; j++){
                        try{
                            Path.copy(tmp[j], new File(allAlbumPath + "/" + tmp[j].getName()));
                        }
                        catch (Exception e){};
                    }
                }
            }
        }

        mList = ImageAlbum.setAlbumList();
    }
    // Khang

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycle_view_album);
        albumAdapter = new AlbumAdapter(getContext(), mList);
        recyclerView.setAdapter(albumAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        addBtn = (ImageButton) view.findViewById(R.id.btnAdd);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList = ImageAlbum.setAlbumList();
                albumAdapter = new AlbumAdapter(getContext(), mList);
                recyclerView.setAdapter(albumAdapter);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        return view;
    }
}