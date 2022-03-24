package com.example.oneus.SubAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.oneus.R;
import com.example.oneus.subClasses.ImageAlbum;

import java.util.List;

public class SpinnerFactor extends ArrayAdapter<String> {
    Context context;
    List<String> mList;

    public SpinnerFactor(Context context, List<String> mList) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.factor_spinner_row, parent, false);
        }

        TextView factor = convertView.findViewById(R.id.factor);
        factor.setText(mList.get(position));
        return convertView;
    }
}