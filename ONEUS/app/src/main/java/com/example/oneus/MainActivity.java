package com.example.oneus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.oneus.SubAdapter.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;

public class MainActivity extends AppCompatActivity{

    private static final int PERMISSION_REQUEST_CODE = 7;
    public static final String App_Name = "ONEUS";

    public ImageButton btnSort;


    private ViewPager viewPager;
    private BottomNavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //center ONEUS in Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        btnSort = getSupportActionBar().getCustomView().findViewById(R.id.btnSort);

        // yêu cầu người dùng cho phép truy cập bộ nhớ.
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            createDirectory(this.App_Name);
            createSubsDirectory("Favorite");
            createSubsDirectory("Trash");
            createSubsDirectory("Original");
        }else{
            askPermission();
        }




        navigationView = findViewById(R.id.bottom_nav);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        viewPager.setCurrentItem(0);
                        btnSort.setVisibility(View.VISIBLE);
                        Toast toast = Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        break;

                    case R.id.action_favorite:
                        viewPager.setCurrentItem(1);
                        btnSort.setVisibility(View.GONE);
                        Toast toast_1 = Toast.makeText(MainActivity.this, "Favorite", Toast.LENGTH_SHORT);
                        toast_1.setGravity(Gravity.CENTER, 0, 0);
                        toast_1.show();
                        break;

                    case R.id.action_search:
                        btnSort.setVisibility(View.GONE);
                        viewPager.setCurrentItem(2);
                        Toast toast_2 = Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT);
                        toast_2.setGravity(Gravity.CENTER, 0, 0);
                        toast_2.show();
                        break;

                    case R.id.action_trash:
                        btnSort.setVisibility(View.GONE);
                        viewPager.setCurrentItem(3);
                        Toast toast_3 = Toast.makeText(MainActivity.this, "Trash", Toast.LENGTH_SHORT);
                        toast_3.setGravity(Gravity.CENTER, 0, 0);
                        toast_3.show();
                        break;
                }
                return true;
            }
        });

        //view pager
        viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);

        //
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        navigationView.getMenu().findItem(R.id.action_home).setChecked(true);
                        break;
                    case 1:
                        navigationView.getMenu().findItem(R.id.action_favorite).setChecked(true);
                        break;
                    case 2:
                        navigationView.getMenu().findItem(R.id.action_search).setChecked(true);
                        break;
                    case 3:
                        navigationView.getMenu().findItem(R.id.action_trash).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    void askPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                createDirectory(this.App_Name);
            }else{
                Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void createDirectory(String FolderName){
        File folder = new File(Environment.getExternalStorageDirectory(), FolderName);
        if (!folder.exists()){
            folder.mkdir();
            Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
        }
    }

    void createSubsDirectory(String FolderName){
        File folder = new File(Environment.getExternalStorageDirectory() +"/ONEUS/" + FolderName);
        if (!folder.exists()){
            folder.mkdir();
        }
    }

    @Override
    public void onBackPressed() {
        if (navigationView.getSelectedItemId() == R.id.action_home) {
            finish();
        }
        else {
            super.onBackPressed();
        }
    }




}