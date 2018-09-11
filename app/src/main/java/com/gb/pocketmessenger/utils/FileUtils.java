package com.gb.pocketmessenger.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static String LOG_TAG = "FileUtils";

    public static Boolean copyFileOnUri(File sourceFile, File destFile) {
        if (sourceFile.getAbsolutePath().equals(destFile.getAbsolutePath())) {
            Log.d(LOG_TAG, "Source and destination file is equals.");
            return true;
        }
        try {
            InputStream input = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int fileSize;
            while ((fileSize = input.read(buffer)) > 0) {
                out.write(buffer, 0, fileSize);
            }
            input.close();
            out.close();
            Log.d(LOG_TAG, "File copied!");
            return true;
        } catch (IOException e) {
            Log.d(LOG_TAG, "File read/write error: " + e.getLocalizedMessage());
            return false;
        }
    }

    public static String getPathFromUri(Context context, Uri uri) {
        String filePath;
        Cursor cursor = context.getContentResolver().query(uri, null,
                null, null, null);
        if (cursor == null) {
            filePath = uri.getPath();
        } else {
            cursor.moveToFirst();
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            filePath = cursor.getString(dataColumnIndex);
            cursor.close();
        }
        return filePath;
    }
}
