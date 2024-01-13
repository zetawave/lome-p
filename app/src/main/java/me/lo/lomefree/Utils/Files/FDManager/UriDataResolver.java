package me.lo.lomefree.Utils.Files.FDManager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.lo.lomefree.Globals.GlobalValues;

import static me.lo.lomefree.Interfaces.UriSchemes.CONTENT_URI_SCHEMA;

/**
 * Created by mwss on 27/03/18.
 */

public class UriDataResolver
{

    public static String getFileNameFromUri(Uri uri, Cursor cursor) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }

    public static String getRealValueFromUri(Uri uri, Context context) throws Exception {
        if(uri.getScheme().contains(CONTENT_URI_SCHEMA))
            return UriDataResolver.extractUriKeyFileValue(uri, context.getContentResolver());
        else
            return FileManager.readFile(UriDataResolver.getRealPathFromURI(uri, context.getContentResolver()));

    }

    private static String extractUriKeyFileValue(Uri u, ContentResolver contentResolver) throws IOException {

        String name;
        String extension;
        name = getFileNameFromUri(u, contentResolver.query(u, null, null, null, null));
        extension = getFileExtensionFromUri(u, contentResolver);
        String filename;
        if(!(extension == null) && !extension.equals("null") && !name.contains(".".concat(extension)))
            filename = name + "." + extension;
        else
            filename = name;

        String tempPath = GlobalValues.downloadPath + File.separator + filename;
        saveFileFromUri(contentResolver, u, tempPath);

        String key = FileManager.readTextFromFile(tempPath);
        FileManager.deleteFile(new File(tempPath));
        return key;
    }


    public static String getFileExtensionFromUri(Uri uri, ContentResolver resolver)
    {
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }

    public static long getFileSizeFromUri(Cursor cursor)
    {
        cursor.moveToFirst();
        long size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
        cursor.close();
        return size;
    }

    public static String getRealPathFromURI(Uri contentURI, ContentResolver resolver)
    {
        String result;
        Cursor cursor = resolver.query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    public static void saveFileFromUri(ContentResolver resolver, Uri uri, String pathToSave)
    {
        try {
            InputStream in =  resolver.openInputStream(uri);
            OutputStream out = new FileOutputStream(new File(pathToSave));
            byte[] buf = new byte[1024];
            int len;
            while((len= in != null ? in.read(buf) : 0)>0){
                out.write(buf,0,len);
            }
            out.close();
            assert in != null;
            in.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkURIResource(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        return (cursor != null && cursor.moveToFirst());
    }

}
