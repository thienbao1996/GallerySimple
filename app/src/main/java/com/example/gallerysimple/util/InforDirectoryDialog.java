package com.example.gallerysimple.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.gallerysimple.R;
import com.example.gallerysimple.model.Directory;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InforDirectoryDialog extends Dialog {
    public InforDirectoryDialog(@NonNull Context context, Directory dir) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.infor_directory_layout, null);
        setContentView(view);

        MaterialTextView name = view.findViewById(R.id.txt_dirName);
        name.setText("Name: " + dir.name);

        MaterialTextView path = view.findViewById(R.id.txt_dirPath);
        path.setText("Path: " + dir.path);

        MaterialTextView type = view.findViewById(R.id.txt_dirType);
        type.setText("Type: " + dir.types);

        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.getDefault());
        MaterialTextView taken = view.findViewById(R.id.txt_dirDateTaken);
        taken.setText("Date Taken: " + format.format(new Date(dir.taken)));

        MaterialTextView modify = view.findViewById(R.id.txt_dirLastModify);
        modify.setText("Date Modified: " + format.format(new Date(dir.modified)));

        MaterialTextView size = view.findViewById(R.id.txt_dirSize);
        size.setText("Date Size: " + Utils.readableFileSize(dir.size));

        if (dir.types.contains("video")) {
            MaterialTextView duration = view.findViewById(R.id.txt_dirDuration);
            duration.setText("Date Duration: " + Utils.formatVideoDuration(dir.duration));
        }
    }


}
