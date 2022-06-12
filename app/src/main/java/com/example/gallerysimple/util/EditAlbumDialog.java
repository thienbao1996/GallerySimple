package com.example.gallerysimple.util;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.gallerysimple.R;
import com.google.android.material.button.MaterialButton;

public class EditAlbumDialog extends Dialog {

    public EditAlbumDialog(@NonNull Context context, EditAlbumCallback callback) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.edit_album_dialog, null);
        setContentView(view);

        EditText name = findViewById(R.id.edt_album_name);
        MaterialButton leftButton = findViewById(R.id.btn_left);
        MaterialButton rightButton = findViewById(R.id.btn_right);

        leftButton.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(name.getText().toString())) {
                callback.editAlbumName(name.getText().toString());
                dismiss();
            } else {
                Toast.makeText(context, "Album name not valid", Toast.LENGTH_SHORT).show();
            }
        });
        rightButton.setOnClickListener(v -> dismiss());
    }

    public interface EditAlbumCallback {
        void editAlbumName(String name);
    }
}
