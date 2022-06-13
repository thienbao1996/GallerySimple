package com.example.gallerysimple.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.example.gallerysimple.GalleryApplication;
import com.example.gallerysimple.R;
import com.example.gallerysimple.databinding.ActivityPictureDetailBinding;
import com.example.gallerysimple.model.Album;
import com.example.gallerysimple.model.AlbumDao;
import com.example.gallerysimple.model.AlbumItems;
import com.example.gallerysimple.model.AlbumItemsDao;
import com.example.gallerysimple.model.DirectoryDao;
import com.example.gallerysimple.util.AppDatabase;
import com.example.gallerysimple.util.Constant;
import com.example.gallerysimple.util.InforDirectoryDialog;
import com.example.gallerysimple.util.Utils;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

import java.io.File;
import java.util.Iterator;

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

            // check type image or video
            if (path.contains("mp4") || path.contains("MP4")) {
                binding.videoContainer.setVisibility(View.VISIBLE);
                binding.imgPicture.setVisibility(View.GONE);

                ExoPlayer player = new ExoPlayer.Builder(this).build();
                binding.videoView.setPlayer(player);

                // Build the media item.
                MediaItem mediaItem = MediaItem.fromUri(Uri.fromFile(new File(path)));
                // Set the media item to be played.
                player.setMediaItem(mediaItem);
                // Prepare the player.
                player.prepare();
                // Start the playback.
                player.play();
            } else {
                binding.imgPicture.setVisibility(View.VISIBLE);

                Bitmap imageBitmap = Utils.rotateBitmap(path);
                if (imageBitmap != null) {
                    Glide.with(this).load(imageBitmap).into(binding.imgPicture);
                } else
                    Glide.with(this).load(new File(path)).centerInside().into(binding.imgPicture);

            }

            disposable.add(database.directoryDao().getDirectoriesById(idDir)
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

                disposable.add(directoryDao.getDirectoriesById(idDir)
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

            binding.btnAddItemToAlbum.setOnClickListener(v -> disposable.add(database.albumDao().getAllAlbums()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(albums -> {
                        if (albums.size() > 3) {
                            String[] albumNames = new String[albums.size() - 3];
                            boolean[] checkedItems = new boolean[albums.size() - 3];
                            int[] albumIds = new int[albums.size() - 3];

                            Iterator<Album> iterator = albums.iterator();
                            while (iterator.hasNext()) {
                                Album album = iterator.next();
                                if (album.getName().equalsIgnoreCase(Constant.ALBUM_FAVORITE) ||
                                        album.getName().equalsIgnoreCase(Constant.ALBUM_DEFAULT) ||
                                        album.getName().equalsIgnoreCase(Constant.ALBUM_RECYCLE_BIN)) {
                                    iterator.remove();
                                }
                            }

                            for (int index = 0; index < albumNames.length; index++) {
                                albumNames[index] = albums.get(index).getName();
                                albumIds[index] = albums.get(index).getId();
                                checkedItems[index] = false;
                            }

                            // check picture already in album list
                            disposable.add(database.albumItemsDao().getItemByPathAndAlbumID(path, albumIds)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(list -> {
                                        for (AlbumItems item : list) {
                                            for (int index = 0; index < albumIds.length; index++) {
                                                if (item.getAid() == albumIds[index]) {
                                                    checkedItems[index] = true;
                                                }
                                            }
                                        }

                                        AlertDialog.Builder chooseAlbum = new AlertDialog.Builder(this);
                                        chooseAlbum.setTitle("Choose Album to add");
                                        chooseAlbum.setMultiChoiceItems(albumNames, checkedItems,
                                                (dialog, which, isChecked) -> checkedItems[which] = isChecked);
                                        chooseAlbum.setCancelable(true);
                                        chooseAlbum.setNegativeButton(getString(R.string.title_cancel),
                                                (dialog, which) -> dialog.dismiss());
                                        chooseAlbum.setPositiveButton("Add", (dialog, which) -> {
                                            for (int index = 0; index < albumNames.length; index++) {
                                                if (checkedItems[index]) {
                                                    // check picture already in album
                                                    int finalIndex = index;
                                                    disposable.add(database.albumItemsDao().getItem(path, albumIds[index])
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(count -> {
                                                                if (count == 0) {
                                                                    disposable.add(database.albumItemsDao().insertItem(path, albums.get(finalIndex).getId())
                                                                            .subscribeOn(Schedulers.io())
                                                                            .observeOn(AndroidSchedulers.mainThread())
                                                                            .subscribe(() -> Log.d("room", "insert item " + path),
                                                                                    Throwable::printStackTrace)
                                                                    );
                                                                }
                                                            })
                                                    );
                                                } else {
                                                    disposable.add(database.albumItemsDao().deleteItem(path, albums.get(index).getId())
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(() -> Log.d("room", "insert item " + path),
                                                                    Throwable::printStackTrace)
                                                    );
                                                }
                                            }
                                            Toast.makeText(this, getString(R.string.save_done), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        });
                                        chooseAlbum.create().show();
                                    })
                            );
                        } else {
                            Toast.makeText(this, getString(R.string.no_album_available), Toast.LENGTH_SHORT).show();
                        }
                    }, Throwable::printStackTrace)
            ));

            binding.btnDelete.setOnClickListener(v -> {
                // Logic to delete picture
                // set picture to be deleted, delete all link to album and add picture to recycle bin
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.confirm_delete_pic);
                builder.setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                    disposable.add(database.directoryDao().setDirDelete(idDir)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> Log.d("room", "Set picture delete"),
                                    Throwable::printStackTrace)
                    );

                    disposable.add(database.albumItemsDao().deleteItemByPicPath(path)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> Log.d("room", "delete items by album: "),
                                    Throwable::printStackTrace)
                    );

                    disposable.add(database.albumDao().getAlbumByName(Constant.ALBUM_RECYCLE_BIN)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(recycleBin -> disposable.add(database.albumItemsDao().insertItem(path, recycleBin.getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                                Log.d("room", "Insert picture to Recycle Bin");
                                                finish();
                                            },
                                            Throwable::printStackTrace)
                            ), Throwable::printStackTrace)
                    );
                });
                builder.setNegativeButton(getString(R.string.title_cancel),
                        (dialog, which) -> dialog.dismiss());
                builder.create().show();
            });

            binding.btnDetail.setOnClickListener(v -> disposable.add(database.directoryDao().getDirectoriesById(idDir)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(directory -> {
                        InforDirectoryDialog inforDirectoryDialog = new InforDirectoryDialog(this, directory);
                        inforDirectoryDialog.setCancelable(true);
                        inforDirectoryDialog.show();
                    }, Throwable::printStackTrace)
            ));
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