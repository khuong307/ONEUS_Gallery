package com.example.oneus.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.oneus.R;
import com.example.oneus.SubAdapter.AlbumAdapter;
import com.example.oneus.SubAdapter.FavoriteImageAdapter;
import com.example.oneus.subClasses.DialogNewAlbum;
import com.example.oneus.subClasses.FavImage;
import com.example.oneus.subClasses.ImageAlbum;

import java.io.File;
import java.util.ArrayList;
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

    public void setAlbumList(){
        mList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString() + "/ONEUS";
        File directory = new File (path);
        if (directory.exists()){
            File[] folder = directory.listFiles();
            for (int i = 0; i < folder.length; i++){
                File[] images = folder[i].listFiles();
                if (images.length != 0)
                    mList.add(new ImageAlbum(folder[i].getName(),images[0]));
            }
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAlbumList();
    }

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
                setAlbumList();
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