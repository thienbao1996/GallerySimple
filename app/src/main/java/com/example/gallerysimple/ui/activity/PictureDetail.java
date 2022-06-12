package com.example.gallerysimple.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.example.gallerysimple.GalleryApplication;
import com.example.gallerysimple.R;
import com.example.gallerysimple.databinding.ActivityPictureDetailBinding;
import com.example.gallerysimple.model.AlbumDao;
import com.example.gallerysimple.model.AlbumItemsDao;
import com.example.gallerysimple.model.DirectoryDao;
import com.example.gallerysimple.util.AppDatabase;
import com.example.gallerysimple.util.Constant;
import com.example.gallerysimple.util.Utils;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PictureDetail extends AppCompatActivity {
    private final AppDatabase database = GalleryApplication.getDatabase();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private boolean requestPicturesReload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPictureDetailBinding binding = ActivityPictureDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        binding.btnBack.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        if (intent != null) {
            String path = intent.getStringExtra("path");
            int idDir = intent.getIntExtra("id", 0);

            Bitmap imageBitmap = Utils.rotateBitmap(path);
            if (imageBitmap != null) {
                Glide.with(this).load(imageBitmap).into(binding.imgPicture);
            } else
                Glide.with(this).load(new File(path)).centerInside().into(binding.imgPicture);

            disposable.add(database.directoryDao().getDirectorieById(idDir)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(directory -> {
                        binding.btnFavorite.setBackground(AppCompatResources
                                .getDrawable(this, directory.isFavorite() ?
                                        R.drawable.ic_star_32 : R.drawable.ic_favorite_white)
                        );
                        binding.txtPicName.setText(directory.name);
                    }, Throwable::printStackTrace)
            );

            binding.btnFavorite.setOnClickListener(v -> {
                AlbumItemsDao albumItemsDao = database.albumItemsDao();
                AlbumDao albumDao = database.albumDao();
                DirectoryDao directoryDao = database.directoryDao();

                disposable.add(directoryDao.getDirectorieById(idDir)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(directory -> {
                            requestPicturesReload = true;
                            directory.setFavorite(!directory.isFavorite());

                            disposable.add(directoryDao.updateDirectories(directory)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.io())
                                    .subscribe(() -> Log.d("favorite", directory.name + " - favorite: " + directory.isFavorite()),
                                            Throwable::printStackTrace)
                            );

                            disposable.add(albumDao.getAlbumByName(Constant.ALBUM_FAVORITE)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.io())
                                    .subscribe(album -> {
                                        if (directory.isFavorite()) {
                                            //albumItemsDao.insertItem(path, album.getId());
                                            disposable.add(
                                                    albumItemsDao.insertItem(path, album.getId())
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(() -> Log.d("room", "Insert item: " + path + " To album: " + album.getName()))
                                            );
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                //Glide.with(this).load(R.drawable.ic_favorite).into(binding.btnFavorite);
                                                binding.btnFavorite.setBackground(AppCompatResources
                                                        .getDrawable(this, R.drawable.ic_star_32));
                                                Toast.makeText(this, "Add Favorite!", Toast.LENGTH_SHORT).show();
                                            });
                                        } else {
                                            //albumItemsDao.deleteByPath(path);
                                            disposable.add(
                                                    albumItemsDao.deleteItem(path, album.getId())
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(() -> Log.d("room", "Insert item: " + path + " To album: " + album.getName()))
                                            );
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                binding.btnFavorite.setBackground(AppCompatResources
                                                        .getDrawable(this, R.drawable.ic_favorite_white));
                                                Toast.makeText(this, "Remove Favorite!", Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    }, Throwable::printStackTrace)
                            );
                        }, Throwable::printStackTrace)
                );
            });

            binding.btnAddItemToAlbum.setOnClickListener(v -> {

            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (requestPicturesReload) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}