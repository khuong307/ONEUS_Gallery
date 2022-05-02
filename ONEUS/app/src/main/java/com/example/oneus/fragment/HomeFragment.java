package com.example.oneus.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.oneus.MainActivity;
import com.example.oneus.R;
import com.example.oneus.SubAdapter.AlbumAdapter;
import com.example.oneus.subClasses.ImageAlbum;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment{
    View view;
    RecyclerView recyclerView;
    List<ImageAlbum> mList;
    AlbumAdapter albumAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageButton addBtn;
    ImageButton btnSort;


    public HomeFragment() {
        // Required empty public constructor
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = ImageAlbum.setAlbumList();

        btnSort = ((AppCompatActivity)getActivity()).getSupportActionBar().getCustomView().findViewById(R.id.btnSort);
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSortDialog();
            }
        });
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

    public void openBottomSortDialog() {
        View viewDialog= LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.bottom_sheet_sort,null);
        BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(getContext());
        File oneus = new File(Environment.getExternalStorageDirectory() +"/ONEUS");
        File[] listOneus = oneus.listFiles();

        viewDialog.findViewById(R.id.sortName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mList.clear();
                mList = ImageAlbum.sortFolder(1);
                albumAdapter = new AlbumAdapter(getContext(), mList);
                recyclerView.setAdapter(albumAdapter);
                bottomSheetDialog.dismiss();
            }
        });

        viewDialog.findViewById(R.id.sortSize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mList.clear();
                mList = ImageAlbum.sortFolder(2);
                albumAdapter = new AlbumAdapter(getContext(), mList);
                recyclerView.setAdapter(albumAdapter);
                bottomSheetDialog.dismiss();
            }
        });

        viewDialog.findViewById(R.id.sortQuantity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mList.clear();
                mList = ImageAlbum.sortFolder(3);
                albumAdapter = new AlbumAdapter(getContext(), mList);
                recyclerView.setAdapter(albumAdapter);
                bottomSheetDialog.dismiss();
            }
        });

        viewDialog.findViewById(R.id.sortLastModified).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mList.clear();
                mList = ImageAlbum.sortFolder(4);
                albumAdapter = new AlbumAdapter(getContext(), mList);
                recyclerView.setAdapter(albumAdapter);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.show();
    }
}