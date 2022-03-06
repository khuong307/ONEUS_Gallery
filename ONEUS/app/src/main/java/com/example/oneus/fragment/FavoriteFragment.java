package com.example.oneus.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.oneus.R;
import com.example.oneus.SubAdapter.FavoriteImageAdapter;
import com.example.oneus.subClasses.FavImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    List<FavImage> mList;
    FavoriteImageAdapter favoriteImageAdapter;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerView = view.findViewById(R.id.recycle_view);
        favoriteImageAdapter = new FavoriteImageAdapter(getContext(), mList);
        recyclerView.setAdapter(favoriteImageAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString() + "/ONEUS/Favorite";
        File directory = new File (path);
        if (directory.exists()){
            File[] images = directory.listFiles();

            for (int i = 0; i < images.length; i++){
                mList.add(new FavImage(images[i], images[i].getName()));
            }
        }
    }
}