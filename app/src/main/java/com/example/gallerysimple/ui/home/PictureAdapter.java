package com.example.gallerysimple.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallerysimple.R;
import com.example.gallerysimple.model.Directory;
import com.example.gallerysimple.util.MediaLoader;
import com.example.gallerysimple.util.Utils;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureViewHolder> {
    private final List<Directory> pictures = new ArrayList<>();
    private final Context context;
    private PictureAdapterCallback callback;

    public PictureAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture,
                parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PictureViewHolder holder, int position) {
        holder.onBind(pictures.get(position), position);
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }


    class PictureViewHolder extends RecyclerView.ViewHolder {
        private final ImageView thumbnail;
        private final ImageButton favorite;
        private final MaterialTextView videoDuration;

        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.img_thumbnail);
            favorite = itemView.findViewById(R.id.imgBtn_favorite);
            videoDuration = itemView.findViewById(R.id.txt_duration);
        }

        public void onBind(Directory item, int position) {
            Bitmap bitmap = MediaLoader.loadThumbnail(context.getApplicationContext(), item.id);

            if (item.isFavorite()) {
                favorite.setVisibility(View.VISIBLE);
            }

            if (item.types.contains("video")) {
                bitmap = MediaLoader.loadVideoThumbnail(context.getApplicationContext(), item.id);

                videoDuration.setVisibility(View.VISIBLE);
                videoDuration.setText(Utils.formatVideoDuration(item.getDuration()));
            }
            Glide.with(itemView).load(bitmap).into(thumbnail);

            itemView.setOnClickListener(view -> {
                if (callback != null)
                    callback.moveToDetailPage(item);
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Directory> data) {
        pictures.clear();
        pictures.addAll(data);
        Collections.sort(pictures, (dir1, dir2) -> (int) (dir1.taken - dir2.taken));
        notifyDataSetChanged();
    }

    public void setCallback(PictureAdapterCallback callback) {
        this.callback = callback;
    }

    public interface PictureAdapterCallback {
        void moveToDetailPage(Directory item);
    }
}
