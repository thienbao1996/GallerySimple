package com.example.gallerysimple.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerysimple.databinding.FragmentHomeBinding;
import com.example.gallerysimple.ui.activity.PictureDetail;
import com.example.gallerysimple.ui.adapter.PictureAdapter;
import com.example.gallerysimple.ui.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final RecyclerView picturesRecyclerView = binding.rvPictures;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false);
        picturesRecyclerView.setLayoutManager(gridLayoutManager);

        PictureAdapter adapter = new PictureAdapter(getContext());
        adapter.setCallback(item -> {
            Intent intent = new Intent(getActivity(), PictureDetail.class);
            intent.putExtra("path", item.path);
            intent.putExtra("id", item.id);
            if (getActivity() != null)
                startActivity(intent);
        });
        picturesRecyclerView.setAdapter(adapter);

        homeViewModel.getListPictureAndVideo().observe(getViewLifecycleOwner(), adapter::setData);
        homeViewModel.loadAllPictureAndImage();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}