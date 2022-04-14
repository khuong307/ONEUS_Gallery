package com.example.oneus.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneus.R;
import com.example.oneus.SubAdapter.TrashImageAdapter;
import com.example.oneus.subClasses.Dialog.DialogReturnImage;
import com.example.oneus.subClasses.Image;
import com.example.oneus.subClasses.Path;
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
    List<Image> trashList;
    TrashImageAdapter trashImageAdapter;

    Toolbar toolbar;
    TextView textViewToolbar;
    ImageButton btnBack;
    ImageButton btnDelete;
    ImageButton btnMove;

    public static boolean isActionMode = false;
    public static  List<Image> selectionList = new ArrayList<>();
    int counter = 0;
    private boolean isBackFromSlide;

    public TrashFragment() {
        // Required empty public constructor
    }

    public void updateImageList(){
        trashList.clear();
        trashList = new ArrayList<>();
        trashList = Image.setImageList("Trash");
    }

    public void updateAdapter(){
        trashImageAdapter = new TrashImageAdapter(getActivity(), trashList, this);
        recyclerView.setAdapter(trashImageAdapter);
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isActionMode = false;
        trashList = new ArrayList<>();
        initTrashList();
        isBackFromSlide = false;

    }
    public void initTrashList(){
        trashList = Image.setImageList("Trash");
    }
    public void clearActionMode() {
        isActionMode = false;
        toolbar.setVisibility(View.GONE);
        textViewToolbar.setText("0 item selected");
        counter = 0;
        selectionList.clear();

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

            trashImageAdapter = new TrashImageAdapter(getActivity(), trashList, this);
            recyclerView.setAdapter(trashImageAdapter);
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
        textViewToolbar.setText("Selected: " + counter);
    }


    //btn trash - delete item.

    public void remove(int index) {
        trashList.get(index).getImage().delete();
        trashList.remove(index);
        Log.d("Index", index+"");
        trashImageAdapter.notifyItemRemoved(index);
    }

    public int findIndexInList(Image obj){
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
                        Path.copy(selectionList.get(i).getImage(), new File(Environment.getExternalStorageDirectory().toString() + "/ONEUS/Trash/"+selectionList.get(i).getText()));
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
                bundle1.putSerializable("trashList", (Serializable) Image.setImageList("Trash"));
                bundle1.putInt("Position", position);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                SlideshowDialogTrashFragment newFragment = SlideshowDialogTrashFragment.newInstance();
                newFragment.setArguments(bundle1);
                newFragment.show(TrashFragment.this.getChildFragmentManager(),"trashShow");
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