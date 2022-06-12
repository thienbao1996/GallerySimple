package com.example.gallerysimple.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gallerysimple.GalleryApplication;
import com.example.gallerysimple.R;
import com.example.gallerysimple.model.Album;
import com.example.gallerysimple.model.AlbumDao;
import com.example.gallerysimple.model.AlbumItemsDao;
import com.example.gallerysimple.model.Directory;
import com.example.gallerysimple.model.DirectoryDao;
import com.example.gallerysimple.util.AppDatabase;
import com.example.gallerysimple.util.Constant;
import com.example.gallerysimple.util.MediaLoader;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final AppDatabase database = GalleryApplication.getDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQUEST_PERMISSION_CODE);
        } else {
            disposable.add(Completable.fromRunnable(this::initDataForDB)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    }, 2000), Throwable::printStackTrace)
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constant.REQUEST_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                disposable.add(Completable.fromRunnable(this::initDBContent)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                        }, 2000), Throwable::printStackTrace)
                );
            } else {
                System.exit(0);
            }
        }
    }

    public void initDBContent() {
        DirectoryDao directoryDao = database.directoryDao();
        AlbumDao albumDao = database.albumDao();
        AlbumItemsDao albumItemsDao = database.albumItemsDao();

        initAlbum(albumDao);

        disposable.add(directoryDao.getAllIgnoreDelete()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(directories -> {
                    if (directories == null || directories.size() == 0) {
                        List<Directory> list = new ArrayList<>();
                        list.addAll(MediaLoader.getPictures(this));
                        list.addAll(MediaLoader.getVideos(this));

                        disposable.add(directoryDao.insertAll(list)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> Log.d("room", "Insert all"))
                        );

                        disposable.add(albumDao.getAlbumByName(Constant.ALBUM_DEFAULT)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe(album -> {
                                    for (Directory item : list) {
                                        disposable.add(
                                                albumItemsDao.insertItem(item.path, album.getId())
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(() -> Log.d("room", "Insert item: " + item.name + " To album: " + album.getName()))
                                        );
                                    }
                                }, Throwable::printStackTrace)
                        );
                    }
                }, Throwable::printStackTrace)
        );
    }

    private void initAlbum(AlbumDao albumDao) {
        Album defaultAlbum = new Album();
        defaultAlbum.setName(Constant.ALBUM_DEFAULT);

        Album favoriteAlbum = new Album();
        favoriteAlbum.setName(Constant.ALBUM_FAVORITE);

        Album recycleBin = new Album();
        recycleBin.setName(Constant.ALBUM_RECYCLE_BIN);

        disposable.add(albumDao.insertAlbum(defaultAlbum, favoriteAlbum, recycleBin)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Log.d("room", "Init albums"),
                        Throwable::printStackTrace)
        );
    }

    private void initDataForDB() {
        DirectoryDao directoryDao = database.directoryDao();
        AlbumDao albumDao = database.albumDao();
        AlbumItemsDao albumItemsDao = database.albumItemsDao();

        disposable.add(directoryDao.getAllIgnoreDelete()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(directories -> {
                    List<Directory> list = new ArrayList<>();
                    list.addAll(MediaLoader.getPictures(this));
                    list.addAll(MediaLoader.getVideos(this));

                    if (directories == null || directories.size() == 0 || list.size() > directories.size()) {
                        disposable.add(directoryDao.insertAll(list)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> Log.d("room", "Insert all"))
                        );

                        disposable.add(albumDao.getAlbumByName(Constant.ALBUM_DEFAULT)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe(album -> {
                                    for (Directory item : list) {
                                        disposable.add(
                                                albumItemsDao.insertItem(item.path, album.getId())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(() -> Log.d("room", "Insert item: " + item.name + " To album: " + album.getName()))
                                        );
                                    }
                                }, Throwable::printStackTrace)
                        );
                    }
                }, Throwable::printStackTrace)
        );
    }
}