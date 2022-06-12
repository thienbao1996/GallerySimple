package com.example.gallerysimple.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.gallerysimple.model.Directory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaLoader {

    public static List<Directory> getPictures (Context context){
        List<Directory> listImages = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri =  MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }

        String[] projection = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                //MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.MIME_TYPE
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        try {
            while(cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                //String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String dataPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                long dateTaken = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
                long lastModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
                int size = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));

                Directory directory = new Directory(id, dataPath, name, lastModified, dateTaken, size, type);
                listImages.add(directory);


                Log.d("picture folders",name + " - path: " + dataPath + " - taken: " + dateTaken + " - modified:" + lastModified + " - type: " + type
                        + " - size: " + size);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listImages;
    }

    public static List<Directory> getVideos (Context context){
        List<Directory> listVideos = new ArrayList<>();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri =  MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }

        String[] projection = {
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                //MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DURATION
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        try {
            while(cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                //String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String dataPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                long dateTaken = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN));
                long lastModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                int size = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

                Directory directory = new Directory(id, dataPath, name, lastModified, dateTaken, size, type);
                directory.setDuration(duration);
                listVideos.add(directory);


                Log.d("video folders",name + " - path: " + dataPath + " - taken: " + dateTaken + " - modified:" + lastModified + " - type: " + type
                        + " - size: " + size + " - duration: " + duration);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listVideos;
    }

    public static Bitmap loadThumbnail(Context context, int id) {
        return MediaStore.Images.Thumbnails.getThumbnail(
                context.getContentResolver(), id,
                MediaStore.Images.Thumbnails.MINI_KIND,
                (BitmapFactory.Options) null );
    }

    public static Bitmap loadVideoThumbnail(Context context, int id) {
        return MediaStore.Video.Thumbnails.getThumbnail(
                context.getContentResolver(), id,
                MediaStore.Images.Thumbnails.MINI_KIND,
                (BitmapFactory.Options) null );
    }

    public static Map<String, String> findAllImageFiles() {
        Map<String, String> result = new HashMap<>();
        travelFile(Environment.getExternalStorageDirectory(), result);
        return result;
    }

    public static void travelFile(File dir, Map<String, String> tmp) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        travelFile(file, tmp);
                    } else {
                        if (file.getName().endsWith(".jpg") || file.getName().endsWith(".JPG"))
                            tmp.put(file.getName(), file.getPath());
                    }
                }
            }
        }
    }
}
