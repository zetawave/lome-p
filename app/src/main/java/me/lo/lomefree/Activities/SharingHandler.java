package me.lo.lomefree.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.lo.lomefree.Interfaces.Extensions;
import me.lo.lomefree.Interfaces.NotificationId;
import me.lo.lomefree.Interfaces.RRCodes;
import me.lo.lomefree.R;
import me.lo.lomefree.Services.ServiceBinder;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Utils.Files.FDManager.UriDataResolver;
import me.lo.lomefree.Utils.Files.Entities.FileBox;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;
import me.lo.lomefree.Utils.Misc.NotificationSender;
import mehdi.sakout.fancybuttons.FancyButton;

import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.UIUtils.Intents.IntentsManager.createFileElaborationIntent;
import static me.lo.lomefree.Utils.Files.FDManager.UriDataResolver.saveFileFromUri;
import static me.lo.lomefree.Globals.GlobalValues.ERROR;
import static me.lo.lomefree.Globals.GlobalValues.INFO;
import static me.lo.lomefree.Globals.GlobalValues.LONG;
import static me.lo.lomefree.Globals.GlobalValues.WARNING;

public class SharingHandler extends AppCompatActivity implements Extensions, RRCodes, NotificationId
{

    private CustomSettings customSettings;
    private FileBox box;
    private static int back_count = 0;
    private FancyButton doBackground;
    private FancyButton stopTask;
    private Context thisContext;
    private AppCompatActivity thisActivity;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        thisContext = this;
        createService();
        setContentView(R.layout.activity_sharing_handler);
        initializeCustomSettings();
        initializeComponents();
        checkLock();
    }


    private void initializeComponents()
    {
        doBackground = findViewById(R.id.do_background);
        stopTask = findViewById(R.id.stop_task);
        setListeners();
    }


    private void setListeners()
    {
        doBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationSender.sendNotificationProgressWithPendingIntent(SharingHandler.this, getString(R.string.AppName), getString(R.string.sharing_notification_acquisition_text), getIntent(), NOTIFY_PROGRESS_UNDETERMINATE);
                moveTaskToBack(true);
            }
        });

        stopTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                destroyTask();
                finishAndRemoveTask();
            }
        });
    }

    private void createService()
    {
        try {

            ServiceBinder.createKillNotificationService(this);
        }catch(Exception ex)
        {
            Log.d("Service Exception ", ex.getMessage());
        }
    }

    private void destroyTask()
    {
        try {
            executor.shutdownNow();
            makeToast(getApplicationContext(), getString(R.string.sharing_interrupted_acquisition), WARNING, Toast.LENGTH_LONG);
            finishAndRemoveTask();
        }catch(NullPointerException ex)
        {
            makeToast(getApplicationContext(), getString(R.string.not_active_task), ERROR, Toast.LENGTH_LONG);
        }
    }

    private void checkLock()
    {
        if(customSettings.isLock()) {
            Intent i = new Intent(this, LockActivity.class);
            startActivityForResult(i, REQUEST_CODE);
        }else
        {
            if(verifyStoragePermissionGranted()) {

                runHandlerThread();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private void runHandlerThread()
    {


        executor.execute(() -> {
            try {
                handleSharing();
            } catch (Exception e) {
                handler.post(() -> {
                    postToastMessage(getString(R.string.sharing_error_read_file) + e.getMessage(), ERROR, Toast.LENGTH_LONG);
                    finishAndRemoveTask();
                });
                return;
            }

            handler.post(() -> {
                createFileElaborationIntent(box, thisContext, thisActivity, REQUEST_CODE);
                NotificationSender.sendNotification(thisContext, getString(R.string.AppName), getString(R.string.sharing_completed_acquisition), true, customSettings);
                ServiceBinder.unbindservice(thisContext);
                finish();
            });
        });
    }

    private void postToastMessage(final String message, final int mode, final int lenght) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                makeToast(getApplicationContext(),message, mode, lenght);
            }
        });
    }

    private boolean verifyStoragePermissionGranted()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    private void handleSharing() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null)
        {
            handleSendFiles(intent);
            finishAndRemoveTask();
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            handleMultipleSendFiles(intent);
            finishAndRemoveTask();
        }
    }

    private void initializeCustomSettings() {
        SharedPreferences prefs = DataManager.getSharedPreferences(getApplicationContext());
        customSettings = DataManager.getCustomSettings(prefs);
        boolean isKey = false;
    }

    // Questo metodo crea copie ridondanti di file nella cartella download, anche quando la condivisione avviene da una cartella all'interno della sdcard.

    private void handleSendFiles(Intent intent)
    {
        Uri uri =  intent.getParcelableExtra(Intent.EXTRA_STREAM);
        String name = UriDataResolver.getFileNameFromUri(uri, getContentResolver().query(uri, null, null, null, null));
        String extension = UriDataResolver.getFileExtensionFromUri(uri, getContentResolver());
        String filename;
        if(!(extension == null) && !extension.equals("null") && !name.contains(".".concat(extension)))
            filename = name + "." + extension;
        else
            filename = name;

        /*
            if(isKeyFile(filename))
            {
                isKey = true;
                createNewUriKeyIntent(true, uri.toString(), this, this, REQUEST_CODE, IntentsManager.SIMM_KEY);
                finish();
            }
            else if(isRSAKey(filename))
            {
                isKey = true;
                createNewUriKeyIntent(true, uri.toString(), this, this, REQUEST_CODE, IntentsManager.RSA_TYPE);
                finish();
            }
            */
            //else{
                String pathToSave = (customSettings.getSharePath() + File.separator + filename);
                if(!new File(pathToSave).exists())
                    saveFileFromUri(getContentResolver(), uri, pathToSave);

                box = new FileBox(1);
                box.putFile(filename, new File(pathToSave));
                ArrayList<String> originalPath = new ArrayList<>();
                originalPath.add(pathToSave);
                box.setOriginalPaths(originalPath);
                if(filename.endsWith(FILE_EXT) || filename.endsWith(ZIP_EXT))
                    box.setLome(true);
                else
                    box.setOther(true);
                box.setTotalSizeInByte();
                box.setFromShare(true);
    }

    // Questo metodo crea copie ridondanti di file nella cartella download, anche quando la condivisione avviene da una cartella all'interno della sdcard.

    private void handleMultipleSendFiles(Intent intent)
    {
        HashMap<String, Object> dataMap = FileManager.getDataMapFromLink(this, intent);

        box = (FileBox) dataMap.get("FileBox");
        ArrayList<Uri> uris = (ArrayList) dataMap.get("uris");
        ArrayList<String> paths = (ArrayList) dataMap.get("paths");

            if(box.getFileNumber() > 0)
            {
                for (int i = 0; i < box.getFileNumber(); i++) {
                    if(!new File(paths.get(i)).exists())
                        saveFileFromUri(getContentResolver(), uris.get(i), paths.get(i));
                }
                box.setTotalSizeInByte();
                box.setFromShare(true);

                //createFileElaborationIntent(box, this, this, REQUEST_CODE);
                //NotificationSender.sendNotification(this, getString(R.string.AppName), getString(R.string.sharing_completed_acquisition), true, customSettings);
                //finish();
            }else {
                postToastMessage(getString(R.string.sharing_no_file_error), WARNING, LONG);
                finishAndRemoveTask();
            }

    }


/*
    @Override
    protected void onStop() {
        //super.onStop();
        NotificationSender.sendNotificationProgressWithPendingIntent(SharingHandler.this, "Lo.me", "Acquisizione file...", true, getIntent(), NOTIFY_PROGRESS_UNDETERMINATE);
        super.onStop();

    }
    */

    /*
        @Override
        protected void onDestroy() {

            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
            mNotificationManager.cancel(NOTIFY_PROGRESS_UNDETERMINATE);
            //NotificationSender.sendNotification(this, "Lo.me", "Operazione in sospeso, clicca per riprendere.", true, getIntent());
            super.onDestroy();
            //super.onDestroy();
        }
    */
    @Override
    public void onBackPressed() {

        if(back_count <2) {
            postToastMessage(getString(R.string.press_for_exit), INFO, Toast.LENGTH_LONG);
            back_count += 1;
        }else
        {
            destroyTask();
            finishAndRemoveTask();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        //super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent intent) {
        if (requestCode == REQUEST_CODE &&
                resultCode == RESULT_OK)
        {
            if(verifyStoragePermissionGranted()) {
                runHandlerThread();
            }
        }
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_CANCELED)
        {
            finishAndRemoveTask();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }
}
