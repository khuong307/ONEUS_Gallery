package com.example.oneus.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.oneus.R;
import com.example.oneus.subClasses.Animation.DepthPageTransformer;
import com.example.oneus.subClasses.Image;
import com.example.oneus.subClasses.Animation.ScrollerCustomDuration;
import com.example.oneus.subClasses.Animation.VerticalPageTransformer;
import com.example.oneus.subClasses.Animation.ZoomOutPageTransformer;
import com.github.chrisbanes.photoview.PhotoView;

import java.lang.reflect.Field;
import java.util.List;

public class AutoSlideshowDialogFragment extends DialogFragment {
    private List<Image> imageList;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int selectedPosition = 0;
    private final Handler slideHandler = new Handler();
    private ScrollerCustomDuration mScroller = null;
    private double factor;
    private String animation;
    private ImageButton replayBtn;


    static public AutoSlideshowDialogFragment newInstance() {
        AutoSlideshowDialogFragment auto = new AutoSlideshowDialogFragment();
        return auto;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        replayBtn = v.findViewById(R.id.btnReplay);
        replayBtn.setVisibility(View.GONE);
        replayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentItem(0);
                replayBtn.setVisibility(View.GONE);
            }
        });

        animation = getArguments().getString("Animation");

        if (animation.compareToIgnoreCase("Fade In Page") == 0){
            viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
                @Override
                public void transformPage(@NonNull View view, float position) {
                    if (position <= -1.0F || position >= 1.0F) {        // [-Infinity,-1) OR (1,+Infinity]
                        view.setAlpha(0.0F);
                        view.setVisibility(View.GONE);
                    } else if( position == 0.0F ) {     // [0]
                        view.setAlpha(1.0F);
                        view.setVisibility(View.VISIBLE);
                    } else {

                        // Position is between [-1,1]
                        view.setAlpha(1.0F - Math.abs(position));
                        view.setTranslationX(-position * (view.getWidth() / 2));
                        view.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        else if(animation.compareToIgnoreCase("Zoom Out Page") == 0){
            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        }
        else if(animation.compareToIgnoreCase("Depth Page") == 0){
            viewPager.setPageTransformer(true, new DepthPageTransformer());
        }
        else if(animation.compareToIgnoreCase("Vertical Swipe") == 0){
            viewPager.setPageTransformer(true, new VerticalPageTransformer());
        }

        imageList = (List<Image>) getArguments().getSerializable("ImageList");
        selectedPosition = getArguments().getInt("Position");
        factor = getArguments().getDouble("Factor");



        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        Field scroller = null;
        try {
            scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new ScrollerCustomDuration(getContext(), (Interpolator) interpolator.get(null));
            mScroller.setScrollDurationFactor(factor);
            scroller.set(viewPager, mScroller);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        setCurrentItem(selectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    private Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
        }
    };

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
        slideHandler.removeCallbacks(slideRunnable);
        float time = Float.parseFloat(getArguments().get("Time").toString());
        slideHandler.postDelayed(slideRunnable, (long) time*1000);
        if (position == imageList.size()-1){
            replayBtn.setVisibility(View.VISIBLE);
        }else{
            replayBtn.setVisibility(View.GONE);
        }
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
            Glide.with(getActivity()).load(image.getImage()).transition(DrawableTransitionOptions.withCrossFade(1000)).into(imageViewPreview);
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
