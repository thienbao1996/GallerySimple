package com.example.gallerysimple.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallerysimple.GalleryApplication;
import com.example.gallerysimple.R;
import com.example.gallerysimple.model.Album;
import com.example.gallerysimple.util.AppDatabase;
import com.example.gallerysimple.util.Constant;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private List<Album> albums = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private AppDatabase database;
    private PopupMenuCallback popupMenuCallback;

    public AlbumAdapter(AppDatabase database) {
        this.database = database;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album,
                parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.onBind(albums.get(position), position);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public void setAlbums(List<Album> list) {
        albums.clear();
        this.albums = list;
        notifyDataSetChanged();
    }

    public void addAlbumToView(Album album) {
        albums.add(album);
        notifyDataSetChanged();
    }

    public void setPopupMenuCallback(PopupMenuCallback callback) {
        this.popupMenuCallback = callback;
    }

    public void deleteAlbumAtPosition(int position) {
        albums.remove(position);
        notifyDataSetChanged();
    }

    public void updateAlbumAtPosition(Album album, int position) {
        albums.set(position, album);
        notifyItemChanged(position);
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {
        private final ImageView albumPic;
        private final MaterialTextView albumName, items;
        private final ImageView albumMenu;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);

            albumPic = itemView.findViewById(R.id.img_album);
            albumName = itemView.findViewById(R.id.txt_album_name);
            items = itemView.findViewById(R.id.txt_album_items);
            albumMenu = itemView.findViewById(R.id.img_album_menu);
        }

        public void onBind(Album album, int position) {
            albumName.setText(album.getName());
            if (album.getName().equalsIgnoreCase(Constant.ALBUM_DEFAULT) ||
                    album.getName().equalsIgnoreCase(Constant.ALBUM_FAVORITE) ||
                    album.getName().equalsIgnoreCase(Constant.ALBUM_RECYCLE_BIN)) {
                albumMenu.setVisibility(View.GONE);
            }

            disposable.add(database.albumItemsDao().getItemsByAlbumId(album.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(albumItems -> {
                        if (albumItems.size() == 1) {
                            Glide.with(itemView).load(albumItems.get(0).getPath()).fitCenter().into(albumPic);
                        } else if (albumItems.size() > 1) {
                            Random random = new Random();
                            int result = random.nextInt(albumItems.size() - 1);
                            Glide.with(itemView).load(albumItems.get(result).getPath()).fitCenter().into(albumPic);
                        }
                        String content = albumItems.size() > 0 ? albumItems.size() + " items" :
                                albumItems.size() + " item";
                        items.setText(content);


                    }, Throwable::printStackTrace)
            );

            albumMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.album_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_delete) {
                        if (popupMenuCallback != null) {
                            popupMenuCallback.deleteAlbum(album.getId(), position);
                        }
                    } else if (item.getItemId() == R.id.menu_edit) {
                        popupMenuCallback.editAlbum(albums.get(position), position);
                    }
                    return false;
                });
            });

            disposable.add(database.albumItemsDao().getItemsByAlbumId(album.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(albumItems -> {
                        itemView.setOnClickListener(v -> {
                            if (albumItems.size() > 0) {
                                popupMenuCallback.moveToAlbumDetail(album);
                            } else {
                                Toast.makeText(itemView.getContext(), R.string.message_album_unavailable
                                        , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }, Throwable::printStackTrace)
            );
        }
    }

    public interface PopupMenuCallback {
        void deleteAlbum(int id, int position);
        void editAlbum(Album album, int position);
        void moveToAlbumDetail(Album album);
    }
}
