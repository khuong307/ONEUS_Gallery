package com.example.oneus.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneus.R;
import com.example.oneus.SubAdapter.SearchAdapter;
import com.example.oneus.subClasses.ImageAlbum;

import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {
    ListView listView;
    ArrayAdapter adapter;
    View view;
    RecyclerView recyclerView;
    List<ImageAlbum> mList;
    SearchAdapter searchAdapter;
    Button btnSearch;
    EditText inputAlbum;

    public SearchFragment() {
        // Required empty public constructor

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mList = ImageAlbum.setAlbumList();
        view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycle_search_album);
        searchAdapter = new SearchAdapter(getContext(), mList);
        btnSearch=view.findViewById(R.id.btnSearch);
        inputAlbum = view.findViewById(R.id.inputAlbum);




        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                String nameAlbum = inputAlbum.getText().toString();
                mList = ImageAlbum.setSearchAlbumList(nameAlbum);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                searchAdapter = new SearchAdapter(getContext(), mList);
                recyclerView.setAdapter(searchAdapter);
                recyclerView.setLayoutManager(linearLayoutManager);
            }
        });

        return view;
    }
}