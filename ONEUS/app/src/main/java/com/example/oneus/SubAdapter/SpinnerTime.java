package com.example.oneus.SubAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.oneus.R;
import com.example.oneus.subClasses.ImageAlbum;

import java.io.File;
import java.util.List;

public class SpinnerTime extends ArrayAdapter<String> {
    Context context;
    List<String> mList;

    public SpinnerTime(Context context, List<String> mList) {
        super(context, 0, mList);
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.time_spinner_row, parent, false);
        }

        TextView albumName = convertView.findViewById(R.id.timeDelay);
        albumName.setText(mList.get(position));
        return convertView;
    }
}