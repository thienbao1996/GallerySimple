package com.example.gallerysimple.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gallerysimple.GalleryApplication;
import com.example.gallerysimple.model.AlbumItems;
import com.example.gallerysimple.util.AppDatabase;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AlbumDetailViewModel extends ViewModel {
    private AppDatabase database;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<List<AlbumItems>> mAlbumItem = new MutableLiveData<>();

    public MutableLiveData<List<AlbumItems>> getAlbumItemLiveData() {
        return mAlbumItem;
    }

    public void setDatabase(AppDatabase database) {
        this.database = database;
    }

    public void loadAlbumItems(int albumId) {
        disposable.add(
                database.albumItemsDao().getItemsByAlbumId(albumId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mAlbumItem::postValue, Throwable::printStackTrace)
        );
    }

    public void restoreMedia(String path, int idAlbum) {
        disposable.add(
                database.directoryDao().restoreDir(path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );

        disposable.add(
                database.albumItemsDao().deleteItem(path, idAlbum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
