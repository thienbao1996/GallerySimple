package com.example.gallerysimple.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.gallerysimple.R;
import com.example.gallerysimple.databinding.ActivityAlbumDetailBinding;
import com.example.gallerysimple.ui.adapter.AlbumMediaAdapter;
import com.example.gallerysimple.ui.viewmodel.AlbumDetailViewModel;
import com.example.gallerysimple.util.AppDatabase;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AlbumDetailActivity extends AppCompatActivity {
    private ActivityAlbumDetailBinding binding;
    private AlbumDetailViewModel viewModel;
    @Inject
    AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAlbumDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AlbumDetailViewModel.class);
        viewModel.setDatabase(database);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            int id = intent.getIntExtra("ALBUM_ID", 0);
            String name = intent.getStringExtra("ALBUM_NAME");
            if (!TextUtils.isEmpty(name) && actionBar != null) actionBar.setTitle(name);

            final LinearLayoutManager layoutManager
                    = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            binding.rvAlbumDetail.setLayoutManager(layoutManager);

            AlbumMediaAdapter adapter = new AlbumMediaAdapter(name);
            adapter.setCallBack((path, position) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.confirm_restore);
                builder.setPositiveButton(getString(R.string.restore), (dialog, which) -> {
                    viewModel.restoreMedia(path, id);
                    adapter.removeItem(position);
                });
                builder.setNegativeButton(getString(R.string.title_cancel),
                        (dialog, which) -> dialog.dismiss());
                builder.create().show();
            });

            binding.rvAlbumDetail.setAdapter(adapter);
            viewModel.getAlbumItemLiveData().observe(this, adapter::setData);
            viewModel.loadAlbumItems(id);
        }
    }
}