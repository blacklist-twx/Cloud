package com.example.forlove.Forlove.Data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.forlove.Constant.MyLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Uri2Path {
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String uriToFileApiQ(Context context, Uri uri) {
        MyLog.i("uriToFileApiQ");
        File file = null;
        //android10以上转换
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                try {
                    InputStream is = contentResolver.openInputStream(uri);
                    File cache = new File(context.getExternalCacheDir().getAbsolutePath(), Math.round((Math.random() + 1) * 1000) + displayName);
                    FileOutputStream fos = new FileOutputStream(cache);
                    FileUtils.copy(is, fos);
                    file = cache;
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        MyLog.i("path: "+file.getAbsolutePath());
        return file.getAbsolutePath();
    }
    public static String getFileFromContentUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                return filePath;
            } catch (Exception e) {
            } finally {
                cursor.close();
            }
        }

        return "";
    }


}
