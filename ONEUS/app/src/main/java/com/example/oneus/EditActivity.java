package com.example.oneus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private BottomNavigationView navigationView;
    private PhotoView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        String URI = extras.getString("URI");
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        navigationView = (BottomNavigationView) findViewById(R.id.bottom_edit_nav);
        img = (PhotoView) findViewById(R.id.full_image);

        img.setImageURI(Uri.parse(URI));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_crop:
                        Toast toast = Toast.makeText(EditActivity.this, "Crop", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        break;

                    case R.id.action_sun:
                        Toast toast_1 = Toast.makeText(EditActivity.this, "Bright", Toast.LENGTH_SHORT);
                        toast_1.setGravity(Gravity.CENTER, 0, 0);
                        toast_1.show();
                        break;

                    case R.id.action_contrast:
                        Toast toast_2 = Toast.makeText(EditActivity.this, "Contrast", Toast.LENGTH_SHORT);
                        toast_2.setGravity(Gravity.CENTER, 0, 0);
                        toast_2.show();
                        break;

                    case R.id.action_flip:
                        Toast toast_3 = Toast.makeText(EditActivity.this, "Flip", Toast.LENGTH_SHORT);
                        toast_3.setGravity(Gravity.CENTER, 0, 0);
                        toast_3.show();
                        break;

                    case R.id.action_effect:
                        Toast toast_4 = Toast.makeText(EditActivity.this, "Effect", Toast.LENGTH_SHORT);
                        toast_4.setGravity(Gravity.CENTER, 0, 0);
                        toast_4.show();
                        break;
                }
                return true;
            }
        });
    }
}