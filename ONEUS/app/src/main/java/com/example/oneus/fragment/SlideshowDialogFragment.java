package com.example.oneus.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.oneus.R;
import com.example.oneus.subClasses.Image;
import com.example.oneus.subClasses.Path;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class SlideshowDialogFragment extends DialogFragment {
    private List<Image> imageList;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    private ImageButton favBtn;
    private ImageButton replayBtn;
    private int selectedPosition = 0;

    static public SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = v.findViewById(R.id.viewpager);
        lblCount = v.findViewById(R.id.lbl_count);
        lblTitle = v.findViewById(R.id.title);
        lblDate = v.findViewById(R.id.date);
        favBtn = v.findViewById(R.id.favBtn);
        replayBtn = v.findViewById(R.id.btnReplay);
        replayBtn.setVisibility(View.GONE);

        imageList = (List<Image>) getArguments().getSerializable("imageList");
        selectedPosition = getArguments().getInt("Position");


        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        setCurrentItem(selectedPosition);
        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " of " + imageList.size());

        Image image = imageList.get(position);
        lblTitle.setText(image.getText());

        Date date = new java.util.Date(image.getImage().lastModified());
        lblDate.setText(date.toString());

        boolean isInFav = image.isInFavorite();
        if (isInFav == true){
            favBtn.setColorFilter(Color.rgb(173, 17, 17)); // deep red.
        }else{
            favBtn.setColorFilter(Color.rgb(0, 0, 0)); //black
        }

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast;
                List<Image> favList = Image.setImageList("Favorite");
                int index = image.indexInFavorite();
                boolean stillInFav = image.isInFavorite();
                if (stillInFav){
                    favBtn.setColorFilter(Color.rgb(0, 0, 0)); //black
                    toast = Toast.makeText(getActivity(), "Remove from Favorite", Toast.LENGTH_SHORT);
                    favList.get(index).getImage().delete();
                }else{
                    favBtn.setColorFilter(Color.rgb(173, 17, 17)); // deep red.
                    toast = Toast.makeText(getActivity(), "Add to Favorite", Toast.LENGTH_SHORT);
                    String destination = Environment.getExternalStorageDirectory().toString() + "/ONEUS/Favorite/"+image.getImage().getName();
                    try {
                        Path.copy(image.getImage(), new File(destination));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    //	adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }



        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            PhotoView imageViewPreview = view.findViewById(R.id.image_preview);
            Image image = imageList.get(position);
            Glide.with(getActivity()).load(image.getImage()).into(imageViewPreview);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
