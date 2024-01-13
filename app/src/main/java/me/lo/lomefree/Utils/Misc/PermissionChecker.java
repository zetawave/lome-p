package me.lo.lomefree.Utils.Misc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

public class PermissionChecker
{
    public static  boolean verifyStoragePermissionGranted(AppCompatActivity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else
        {
            return true;
        }
    }


    public  static void verifyCameraPermissionGranted(AppCompatActivity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            } else {

                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }
}
