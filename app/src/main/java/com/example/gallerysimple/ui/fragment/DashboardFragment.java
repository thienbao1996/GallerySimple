package com.example.gallerysimple.ui.fragment;

import android.app.AlertDialog;
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

import com.example.gallerysimple.R;
import com.example.gallerysimple.databinding.FragmentDashboardBinding;
import com.example.gallerysimple.model.Album;
import com.example.gallerysimple.ui.activity.AlbumDetailActivity;
import com.example.gallerysimple.ui.adapter.AlbumAdapter;
import com.example.gallerysimple.ui.viewmodel.DashboardViewModel;
import com.example.gallerysimple.util.AppDatabase;
import com.example.gallerysimple.util.CreateNewAlbumDialog;
import com.example.gallerysimple.util.EditAlbumDialog;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    DashboardViewModel dashboardViewModel;
    @Inject
    AppDatabase database;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.setDatabase(database);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final RecyclerView albumRecyclerView = binding.rvAlbums;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2
                , RecyclerView.VERTICAL, false);
        albumRecyclerView.setLayoutManager(gridLayoutManager);

        AlbumAdapter adapter = new AlbumAdapter(database);
        albumRecyclerView.setAdapter(adapter);
        adapter.setPopupMenuCallback(new AlbumAdapter.PopupMenuCallback() {
            @Override
            public void deleteAlbum(int id, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.confirm_delete);
                builder.setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                    dashboardViewModel.deleteAlbum(id);
                    adapter.deleteAlbumAtPosition(position);
                });
                builder.setNegativeButton(getString(R.string.title_cancel), (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }

            @Override
            public void editAlbum(Album album, int position) {
                if (getContext() != null) {
                    EditAlbumDialog dialog = new EditAlbumDialog(getContext(), name -> {
                        album.setName(name);
                        dashboardViewModel.updateAlbum(album);
                        adapter.updateAlbumAtPosition(album, position);
                    });
                    dialog.show();
                }
            }

            @Override
            public void moveToAlbumDetail(Album album) {
                if (getContext() != null) {
                    Intent intent = new Intent(getActivity(), AlbumDetailActivity.class);
                    intent.putExtra("ALBUM_ID", album.getId());
                    intent.putExtra("ALBUM_NAME", album.getName());
                    startActivity(intent);
                }
            }
        });

        dashboardViewModel.getLiveDataListAlbum().observe(getViewLifecycleOwner(), adapter::setAlbums);
        dashboardViewModel.loadAllAlbums();

        binding.btnAddAlbum.setOnClickListener(v -> {
            CreateNewAlbumDialog dialog = new CreateNewAlbumDialog(getContext(), albumName -> {
                Album newAlbum = new Album();
                newAlbum.setName(albumName);

                adapter.addAlbumToView(newAlbum);
                dashboardViewModel.createNewAlbum(newAlbum);
            });
            dialog.show();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}