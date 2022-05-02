package com.example.oneus.SubAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.oneus.fragment.FavoriteFragment;
import com.example.oneus.fragment.HomeFragment;
import com.example.oneus.fragment.SearchFragment;
import com.example.oneus.fragment.TrashFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new FavoriteFragment();
            case 2:
                return new SearchFragment();
            case 3:
                return new TrashFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
