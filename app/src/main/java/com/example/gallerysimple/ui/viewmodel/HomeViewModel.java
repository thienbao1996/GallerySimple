package com.example.gallerysimple.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gallerysimple.GalleryApplication;
import com.example.gallerysimple.model.Directory;
import com.example.gallerysimple.util.AppDatabase;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HomeViewModel extends ViewModel {
    private AppDatabase database;
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final MutableLiveData<List<Directory>> mListPictureAndVideo;

    public void setDatabase(AppDatabase database) {
        this.database = database;
    }

    public HomeViewModel() {
        MutableLiveData<String> mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        mListPictureAndVideo = new MutableLiveData<>();

    }

    public void loadAllPictureAndImage() {
        disposable.add(database.directoryDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(directories -> {
                            mListPictureAndVideo.postValue(directories);
                            Log.d("get dir",String.valueOf(directories));
                        },
                        Throwable::printStackTrace
                )
        );
    }

    public MutableLiveData<List<Directory>> getListPictureAndVideo() {
        return mListPictureAndVideo;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}