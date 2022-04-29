package com.example.oneus.fragment;

import android.os.Bundle;
import android.os.Handler;
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