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

public class SpinnerAdapter extends ArrayAdapter<ImageAlbum> {
    Context context;
    List<ImageAlbum> mList;

    public SpinnerAdapter(Context context, List<ImageAlbum> mList) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.album_spinner_row, parent, false);
        }

        ImageView thumbnail = convertView.findViewById(R.id.album_thumbnail);
        TextView albumName = convertView.findViewById(R.id.album_name);
        thumbnail.setImageURI(Uri.fromFile(new File(String.valueOf(mList.get(position).getThumbnail()))));
        albumName.setText(mList.get(position).getAlbum());
        return convertView;
    }
}