package com.example.oneus.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.oneus.ListImageOfAlbum;
import com.example.oneus.MainActivity;
import com.example.oneus.R;
import com.example.oneus.SubAdapter.AlbumAdapter;
import com.example.oneus.SubAdapter.FavoriteImageAdapter;
import com.example.oneus.SubAdapter.ImagesOfAlbumAdapter;
import com.example.oneus.SubAdapter.TrashImageAdapter;
import com.example.oneus.subClasses.DialogMoveImage;
import com.example.oneus.subClasses.DialogReturnImage;
import com.example.oneus.subClasses.FavImage;
import com.example.oneus.subClasses.Image;
import com.example.oneus.subClasses.ImageAlbum;
import com.example.oneus.subClasses.TrashImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TrashFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    List<TrashImage> trashList;
    TrashImageAdapter trashImageAdapter;

    Toolbar toolbar;
    TextView textViewToolbar;
    ImageButton btnBack;
    ImageButton btnDelete;
    ImageButton btnMove;
    String albumName = "Trash";

    public boolean isActionMode = false;
    List<TrashImage> selectionList = new ArrayList<>();
    int counter = 0;

    public TrashFragment() {
        // Required empty public constructor
    }

    public void updateImageList(){
        trashList.clear();
        trashList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString() + "/ONEUS/Trash";
        File directory = new File (path);
        if (directory.exists()) {
            File[] images = directory.listFiles();

            for (int i = 0; i < images.length; i++) {
                trashList.add(new TrashImage(images[i], images[i].getName()));
            }
        }
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trashList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString() + "/ONEUS/Trash";
        File directory = new File (path);
        if (directory.exists()) {
            File[] images = directory.listFiles();

            for (int i = 0; i < images.length; i++) {
                trashList.add(new TrashImage(images[i], images[i].getName()));
            }
        }
    }
    public void clearActionMode() {
        isActionMode = false;
        toolbar.setVisibility(View.GONE);
        textViewToolbar.setText("0 item selected");
        counter = 0;
        selectionList.clear();

        for (int i = 0; i < trashList.size(); i++) {
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
            holder.itemView.findViewById(R.id.itemIMGChoose).setVisibility(View.GONE);
            CheckBox checkBox = (CheckBox) holder.itemView.findViewById(R.id.itemIMGChoose);
            checkBox.setChecked(false);
            holder.itemView.findViewById(R.id.delBtn).setVisibility(View.VISIBLE);
        }
        updateImageList();
        trashImageAdapter = new TrashImageAdapter(getActivity(), trashList, this);
        recyclerView.setAdapter(trashImageAdapter);
    }

    public void startSelection(int position){
        if (isActionMode == false){
            isActionMode = true;
            toolbar.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnMove.setVisibility(View.VISIBLE);
            textViewToolbar.setVisibility(View.VISIBLE);
            for (int i = 0; i < trashList.size(); i++) {
                RecyclerView.ViewHolder holder =  recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                holder.itemView.findViewById(R.id.itemIMGChoose).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.delBtn).setVisibility(View.GONE);
            }
        }
    }

    public void check(View view, int position){
        if(((CheckBox)view).isChecked() == true){
            selectionList.add(trashList.get(position));
            counter++;
        }else{
            selectionList.remove(trashList.get(position));
            counter--;
        }
        updateToolbarText(counter);
    }

    public void updateToolbarText(int counter){
        if (counter == 0){
            textViewToolbar.setText("0 item selected");
        }
        else if (counter == 1){
            textViewToolbar.setText("1 item selected");
        }
        else{
            textViewToolbar.setText(counter + " items selected");
        }
    }


    //btn trash - delete item.

    public void remove(int index) {
        trashList.get(index).getImage().delete();
        trashList.remove(index);
        Log.d("Index", index+"");
        trashImageAdapter.notifyItemRemoved(index);
    }

    public int findIndexInList(TrashImage obj){
        int index = trashList.indexOf(obj);
        return index;
    }


    public void openDialog(){
        DialogReturnImage dialogReturnImage = new DialogReturnImage();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putSerializable("SelectedItems", (Serializable) selectionList);
        dialogReturnImage.setArguments(bundle);
        dialogReturnImage.show((manager), "Return Images");
    }

    public void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trash, container, false);
        recyclerView = view.findViewById(R.id.recycle_view_trash);
        trashImageAdapter = new TrashImageAdapter(getContext(), trashList, this);
        recyclerView.setAdapter(trashImageAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        textViewToolbar = view.findViewById(R.id.text_toolbar);
        textViewToolbar.setVisibility(View.GONE);
        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setVisibility(View.GONE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearActionMode();
            }
        });

        btnDelete = view.findViewById(R.id.item_delete);
        btnDelete.setVisibility(View.GONE);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < selectionList.size(); i++){
                    try {
                        copy(selectionList.get(i).getImage(), new File(Environment.getExternalStorageDirectory().toString() + "/ONEUS/Trash/"+selectionList.get(i).getText()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int index = findIndexInList(selectionList.get(i));
                    remove(index);
                }

                updateToolbarText(0);
                clearActionMode();
            }
        });

        btnMove = view.findViewById(R.id.item_move);
        btnMove.setVisibility(View.GONE);
        btnMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });




        recyclerView.addOnItemTouchListener(new TrashImageAdapter.RecyclerTouchListener(getContext(), recyclerView, new TrashImageAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("imageList", (Serializable) trashList);
                bundle1.putInt("Position", position);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                SlideshowDialogTrashFragment newFragment = SlideshowDialogTrashFragment.newInstance();
                newFragment.setArguments(bundle1);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {
                startSelection(position);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.itemIMGChoose);
                checkBox.setVisibility(View.VISIBLE);
                if (checkBox.isChecked() == false){
                    checkBox.setChecked(true);
                    selectionList.add(trashList.get(position));
                    counter++;
                    updateToolbarText(counter);
                }
            }
        }));
        return view;
    }
}