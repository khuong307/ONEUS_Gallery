package com.example.oneus.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.oneus.MainActivity;
import com.example.oneus.R;
import com.example.oneus.SubAdapter.FavoriteImageAdapter;
import com.example.oneus.SubAdapter.ImagesOfAlbumAdapter;
import com.example.oneus.SubAdapter.SpinnerAnimationAdapter;
import com.example.oneus.SubAdapter.SpinnerFactor;
import com.example.oneus.SubAdapter.SpinnerTime;
import com.example.oneus.subClasses.FavImage;
import com.example.oneus.subClasses.Image;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    List<FavImage> mList;
    FavoriteImageAdapter favoriteImageAdapter;

    //auto slideshow
    private Spinner spinner_time;
    private SpinnerTime spinnerTimeAdapter;
    private Spinner spinner_factor;
    private SpinnerFactor spinnerFactor;
    private Spinner spinner_animation;
    private SpinnerAnimationAdapter spinnerAnimationAdapter;
    private LinearLayout linearLayout;

    private ImageButton btnPlay;

    List<String> timeList = new ArrayList<>();
    List<String> factorList = new ArrayList<>();
    List<String> animList = new ArrayList<>();

    private String time_chosen;
    private String factor_chosen;
    private String anim_chosen;

    public void initItemList(){
        for (int i = 1; i <= 20; i++){
            timeList.add(i+"");
            factorList.add(i+"");
        }
    }
    public void initAnim(){
        animList.add("Fade In Page");
        animList.add("Zoom Out Page");
        animList.add("Depth Page");
        animList.add("Vertical Swipe");
        animList.add("None");
    }

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favorite, container, false);
        linearLayout = view.findViewById(R.id.playAnimation);
        if (mList.size() == 0){
            linearLayout.setVisibility(View.GONE);
        }
        recyclerView = view.findViewById(R.id.recycle_view);
        favoriteImageAdapter = new FavoriteImageAdapter(getContext(), mList);
        recyclerView.setAdapter(favoriteImageAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        recyclerView.addOnItemTouchListener(new FavoriteImageAdapter.RecyclerTouchListener(getContext(), recyclerView, new FavoriteImageAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("imageList", (Serializable) mList);
                bundle1.putInt("Position", position);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                SlideshowDialogFavoriteFragment newFragment = SlideshowDialogFavoriteFragment.newInstance();
                newFragment.setArguments(bundle1);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        initItemList();
        initAnim();
        spinner_time = (Spinner) view.findViewById(R.id.spinner_time);
        spinnerTimeAdapter = new SpinnerTime(getActivity(), timeList);
        spinner_time.setAdapter(spinnerTimeAdapter);
        spinner_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String time = (String) adapterView.getItemAtPosition(i);
                time_chosen = time;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//
        spinner_factor = (Spinner) view.findViewById(R.id.spinner_factor);
        spinnerFactor = new SpinnerFactor(getActivity(), factorList);
        spinner_factor.setAdapter(spinnerFactor);
        spinner_factor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String factor = (String) adapterView.getItemAtPosition(i);
                factor_chosen = factor;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinner_animation = (Spinner) view.findViewById(R.id.spinner_animation);
        spinnerAnimationAdapter = new SpinnerAnimationAdapter(getActivity(), animList);
        spinner_animation.setAdapter(spinnerAnimationAdapter);
        spinner_animation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String animate = (String) adapterView.getItemAtPosition(i);
                anim_chosen = animate;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnPlay = (ImageButton) view.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("ImageList", (Serializable) mList);
                bundle.putSerializable("Time", time_chosen);
                bundle.putDouble("Factor", Double.parseDouble(factor_chosen));
                bundle.putString("Animation", anim_chosen);
                bundle.putInt("Position", 0);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                AutoSlideshowFavoriteDialogFragment autoSlideshowDialogFragment = new AutoSlideshowFavoriteDialogFragment();
                autoSlideshowDialogFragment.setArguments(bundle);
                autoSlideshowDialogFragment.show(ft, "auto_slideshow");
            }
        });

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