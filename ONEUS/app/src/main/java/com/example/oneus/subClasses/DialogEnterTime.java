package com.example.oneus.subClasses;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.oneus.ListImageOfAlbum;
import com.example.oneus.R;
import com.example.oneus.SubAdapter.SpinnerAdapter;
import com.example.oneus.SubAdapter.SpinnerAnimationAdapter;
import com.example.oneus.SubAdapter.SpinnerFactor;
import com.example.oneus.SubAdapter.SpinnerTime;
import com.example.oneus.fragment.AutoSlideshowDialogFragment;
import com.example.oneus.fragment.SlideshowDialogFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DialogEnterTime extends DialogFragment {
    private Spinner spinner_time;
    private SpinnerTime spinnerTimeAdapter;
    private Spinner spinner_factor;
    private SpinnerFactor spinnerFactor;
    private Spinner spinner_animation;
    private SpinnerAnimationAdapter spinnerAnimationAdapter;

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


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_enter_time, null);

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



        builder.setView(view);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog)getDialog();
        if(dialog != null) {
            Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(153, 69, 0));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(153, 69, 0));
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Image> imageList = (List<Image>) getArguments().getSerializable("ImageList");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ImageList", (Serializable) imageList);
                    bundle.putSerializable("Time", time_chosen);
                    bundle.putDouble("Factor", Double.parseDouble(factor_chosen));
                    bundle.putString("Animation", anim_chosen);
                    bundle.putInt("Position", 0);
                    Toast.makeText(getActivity(), factor_chosen, Toast.LENGTH_SHORT).show();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    AutoSlideshowDialogFragment autoSlideshowDialogFragment = new AutoSlideshowDialogFragment();
                    autoSlideshowDialogFragment.setArguments(bundle);
                    autoSlideshowDialogFragment.show(ft, "auto_slideshow");
                    dismiss();
                }
            });
        }
    }
}
