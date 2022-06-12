package com.example.gallerysimple.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallerysimple.R;
import com.example.gallerysimple.model.AlbumItems;
import com.example.gallerysimple.util.Constant;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class AlbumMediaAdapter extends RecyclerView.Adapter<AlbumMediaAdapter.MediaViewHolder>{
    private List<AlbumItems> data = new ArrayList<>();
    private AlbumMediaCallBack callBack;
    private String name = "";

    public AlbumMediaAdapter(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail, null);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        holder.onBind(data.get(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<AlbumItems> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeItem(int position) {
        data.remove(position);
        notifyDataSetChanged();
    }

    public void setCallBack(AlbumMediaCallBack callBack) {
        this.callBack = callBack;
    }

    class MediaViewHolder extends RecyclerView.ViewHolder {
        private final ImageView thumbnail;
        private final ImageView restore;
        private final MaterialTextView mediaName;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.img_media_thumbnail);
            restore = itemView.findViewById(R.id.btn_restore);
            mediaName = itemView.findViewById(R.id.txt_media_name);
        }

        public void onBind(AlbumItems item, int position) {
            Glide.with(itemView).load(item.getPath()).fitCenter().into(thumbnail);

            String media = item.getPath();
            int lastIndexChar = item.getPath().lastIndexOf("/");
            if (lastIndexChar > 0) {
                media = media.substring(++lastIndexChar);
            }
            mediaName.setText(media);

            if (name.equalsIgnoreCase(Constant.ALBUM_RECYCLE_BIN)) {
                restore.setVisibility(View.VISIBLE);
                restore.setOnClickListener(v -> callBack.restoreMedia(item.getPath(), position));
            }

            if (position == data.size() - 1) {
                itemView.findViewById(R.id.separator).setVisibility(View.GONE);
            }
        }
    }

    public interface AlbumMediaCallBack {
        void restoreMedia(String path, int position);
    }
}
