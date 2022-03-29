package com.example.oneus.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.oneus.R;
import com.example.oneus.subClasses.FavImage;
import com.example.oneus.subClasses.Image;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.Date;
import java.util.List;

public class SlideshowDialogFavoriteFragment extends DialogFragment {
    private String TAG = SlideshowDialogFavoriteFragment.class.getSimpleName();
    private List<FavImage> imageList;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    private int selectedPosition = 0;
    private ImageButton replayBtn;
    private ImageButton favBtn;


    static public SlideshowDialogFavoriteFragment newInstance() {
        SlideshowDialogFavoriteFragment f = new SlideshowDialogFavoriteFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblTitle = (TextView) v.findViewById(R.id.title);
        lblDate = (TextView) v.findViewById(R.id.date);
        replayBtn = (ImageButton) v.findViewById(R.id.btnReplay);
        favBtn = (ImageButton) v.findViewById(R.id.favBtn);
        favBtn.setVisibility(View.GONE);
        replayBtn.setVisibility(View.GONE);

        final ImageView imageViewPreview = (ImageView) v.findViewById(R.id.image_preview);

        imageList = (List<FavImage>) getArguments().getSerializable("imageList");
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

        FavImage image = imageList.get(position);
        lblTitle.setText(image.getText());

        Date date = new Date(image.getImage().lastModified());
        lblDate.setText(date.toString());
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

            FavImage image = imageList.get(position);

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
