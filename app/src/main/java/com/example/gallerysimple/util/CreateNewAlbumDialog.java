package com.example.gallerysimple.util;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gallerysimple.R;
import com.google.android.material.button.MaterialButton;

public class CreateNewAlbumDialog extends Dialog {

    public CreateNewAlbumDialog(Context context, CreateNewAlbumCallback callback) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.add_album_dialog, null);
        setContentView(view);

        EditText name = findViewById(R.id.edt_album_name);
        MaterialButton leftButton = findViewById(R.id.btn_left);
        MaterialButton rightButton = findViewById(R.id.btn_right);

        leftButton.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(name.getText().toString())) {
                callback.createAlbum(name.getText().toString());
                dismiss();
            } else {
                Toast.makeText(context, "Album name not valid", Toast.LENGTH_SHORT).show();
            }
        });
        rightButton.setOnClickListener(v -> dismiss());
    }


    public interface CreateNewAlbumCallback {
        void createAlbum(String albumName);
    }
}
