package com.example.oneus.fragment;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.oneus.MainActivity;
import com.example.oneus.R;
import com.example.oneus.subClasses.Image;
import com.example.oneus.subClasses.Path;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SlideshowDialogTrashFragment extends DialogFragment {
    private List<Image> showList;
    private ViewPager viewPager;
    private TrashPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    private int selectedPosition = 0;
    private ImageButton restoreBtn;


    public void updateView(int pos){
        viewPager.setAdapter(null);
        myViewPagerAdapter =new TrashPagerAdapter(showList);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.setCurrentItem(pos);
        displayMetaInfo(pos);
    }

    static public SlideshowDialogTrashFragment newInstance() {
        SlideshowDialogTrashFragment f = new SlideshowDialogTrashFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trash_slider, container, false);
        viewPager = v.findViewById(R.id.viewpager);
        lblCount = v.findViewById(R.id.lbl_count);
        lblTitle = v.findViewById(R.id.title);
        lblDate = v.findViewById(R.id.date);

        restoreBtn = v.findViewById(R.id.btnRestore);
        showList = (List<Image>) getArguments().getSerializable("trashList");
        selectedPosition = getArguments().getInt("Position");

        myViewPagerAdapter = new TrashPagerAdapter(showList);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        setCurrentItem(selectedPosition);

        getDialog().setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    TrashFragment trashFragment = (TrashFragment) getParentFragment();
                    trashFragment.updateImageList();
                    trashFragment.updateAdapter();
                    dismiss();
                }
                return true;
            }
        });

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
        lblCount.setText((position + 1) + " of " + showList.size());
        Image currentImage = showList.get(position);
        lblTitle.setText(currentImage.getText());

        Date date = new Date(currentImage.getImage().lastModified());
        lblDate.setText(date.toString());

        restoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] parent = currentImage.getImage().getName().split("_");
                Toast.makeText(getActivity(), "Restore Image To " +parent[0] , Toast.LENGTH_SHORT).show();
                try {
                    Date date = Calendar.getInstance().getTime();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = dateFormat.format(date);
                    String sortName = strDate.replace(':', '_').replace('-','_');
                    String extension = Path.getExtension(currentImage.getImage());
                    Path.copy(currentImage.getImage(), new File(Environment.getExternalStorageDirectory().toString() + "/ONEUS/"+parent[0]+"/"+sortName+"."+extension));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                showList.get(position).getImage().delete();
                int size = showList.size();
                showList.remove(position);
                myViewPagerAdapter.notifyDataSetChanged();

                int nextIndex = 0;
                if(position == 0 && size == 1){
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    return;
                }
                else if(position == 0 &&  size == 2) {
                    nextIndex = 0;
                }
                else if(position == size - 1) {
                    nextIndex = position - 1;
                }
                else {
                    nextIndex = position;
                }
                updateView(nextIndex);
            }
        });
    }


    //	adapter
    public class TrashPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;
        private List<Image> imageList = new ArrayList<>();

        public TrashPagerAdapter(List<Image> imageList) {
            this.imageList = imageList;
        }



        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            PhotoView imageViewPreview = view.findViewById(R.id.image_preview);
            Image image = imageList.get(position);
            Glide.with(getActivity())
                    .load(image.getImage())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(imageViewPreview);
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
