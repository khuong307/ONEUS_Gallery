package com.example.oneus.fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.example.oneus.MainActivity;
import com.example.oneus.R;
import com.example.oneus.subClasses.Path;
import com.example.oneus.subClasses.TrashImage;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SlideshowDialogTrashFragment extends DialogFragment {
    private List<TrashImage> imageList;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    private int selectedPosition = 0;
    private ImageButton restoreBtn;


    static public SlideshowDialogTrashFragment newInstance() {
        SlideshowDialogTrashFragment f = new SlideshowDialogTrashFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trash_slider, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblTitle = (TextView) v.findViewById(R.id.title);
        lblDate = (TextView) v.findViewById(R.id.date);

        restoreBtn = (ImageButton) v.findViewById(R.id.btnRestore);

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

        TrashImage image = imageList.get(position);
        lblTitle.setText(image.getText());

        Date date = new Date(image.getImage().lastModified());
        lblDate.setText(date.toString());

        restoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String[]> history = Path.readHistoryFolder();
                for (int i = 0; i < history.size(); i++){
                    if(history.get(i)[1].compareTo(image.getText()) == 0){
                        String parent[] = history.get(i)[0].split("/");
                        Toast.makeText(getActivity(), "Restore Image To " +parent[parent.length-1] , Toast.LENGTH_SHORT).show();
                        try {
                            Path.copy(image.getImage(), new File(history.get(i)[0]+"/"+history.get(i)[1]));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Bundle bundle1 = new Bundle();
                        imageList.get(position).getImage().delete();
                        int size = new Integer(imageList.size());
                        imageList.remove(position);
                        myViewPagerAdapter.notifyDataSetChanged();
                        if(position == 0 && size == 1){
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            history.remove(i);
                            Path.writeHistoryFolder(history);
                            return;
                        }
                        else if(position == 0 &&  size == 2) {
                            bundle1.putInt("Position", 0);
                        }
                        else if(position == size - 1) {
                            bundle1.putInt("Position", position-1);
                        }
                        else {
                            bundle1.putInt("Position", position);
                        }
                        history.remove(i);
                        Path.writeHistoryFolder(history);

                        bundle1.putSerializable("imageList", (Serializable) imageList);
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        SlideshowDialogTrashFragment newFragment = SlideshowDialogTrashFragment.newInstance();
                        newFragment.setArguments(bundle1);
                        newFragment.show(ft, "slideshow");
                        return;
                    }
                }
            }
        });
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageList = (List<TrashImage>) getArguments().getSerializable("imageList");
        selectedPosition = getArguments().getInt("Position");
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

            TrashImage image = imageList.get(position);

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
