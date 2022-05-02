package com.example.oneus.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneus.MainActivity;
import com.example.oneus.R;
import com.example.oneus.SubAdapter.SearchAdapter;
import com.example.oneus.subClasses.ImageAlbum;

import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    List<ImageAlbum> mList;
    SearchAdapter searchAdapter;
    ImageButton btnSearch;
    EditText inputAlbum;
    TextView noResult;

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
        btnSearch = view.findViewById(R.id.btnSearch);
        inputAlbum = view.findViewById(R.id.inputAlbum);
        noResult = view.findViewById(R.id.noResult);
        noResult.setVisibility(View.GONE);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameAlbum = inputAlbum.getText().toString();
                if (nameAlbum.isEmpty()){
                    Toast toast = Toast.makeText(getActivity(), "Please input information", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else{
                    mList.clear();
                    mList = ImageAlbum.setSearchAlbumList(nameAlbum);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    searchAdapter = new SearchAdapter(getContext(), mList);
                    recyclerView.setAdapter(searchAdapter);
                    recyclerView.setLayoutManager(linearLayoutManager);

                    if (mList.size() == 0){
                        noResult.setVisibility(View.VISIBLE);
                    }else{
                        noResult.setVisibility(View.GONE);
                    }
                }
            }
        });
        return view;
    }
}