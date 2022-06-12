package com.example.gallerysimple.ui.dashboard;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gallerysimple.GalleryApplication;
import com.example.gallerysimple.model.Album;
import com.example.gallerysimple.model.AlbumItems;
import com.example.gallerysimple.util.AppDatabase;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DashboardViewModel extends ViewModel {
    private final AppDatabase database = GalleryApplication.getDatabase();
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final MutableLiveData<List<Album>> mListAlbum = new MutableLiveData<>();
    private final MutableLiveData<List<AlbumItems>> mListAlbumItems = new MutableLiveData<>();

    public DashboardViewModel() {

    }

    public LiveData<List<Album>> getLiveDataListAlbum() {
        return mListAlbum;
    }

    public MutableLiveData<List<AlbumItems>> getLiveDataListAlbumItems() {
        return mListAlbumItems;
    }

    public void loadAllAlbums() {
        disposable.add(database.albumDao().getAllAlbums()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mListAlbum::postValue, Throwable::printStackTrace)
        );
    }

    public void loadItemInAlbum(int id) {
        disposable.add(database.albumItemsDao().getItemsByAlbumId(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mListAlbumItems::postValue, Throwable::printStackTrace)
        );
    }

    public void createNewAlbum(Album album) {
        disposable.add(database.albumDao().insertAlbum(album)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Log.d("room", "Init albums"),
                        Throwable::printStackTrace)
        );
    }

    public void deleteAlbum(int id) {
        disposable.add(database.albumDao().deleteAlbumById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Log.d("room", "delete album: " + id),
                        Throwable::printStackTrace)
        );

        // delete all item picture link to album
        disposable.add(database.albumItemsDao().deleteItemByAlbumId(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Log.d("room", "delete items by album: " + id),
                        Throwable::printStackTrace)
        );
    }

    public void updateAlbum(Album album) {
        disposable.add(database.albumDao().updateAlbum(album)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Log.d("room", "update album: " + album.getName() + " - album id:" + album.getId()),
                        Throwable::printStackTrace)
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}