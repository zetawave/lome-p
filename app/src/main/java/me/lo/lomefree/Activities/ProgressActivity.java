package me.lo.lomefree.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.lo.lomefree.Ads.AdsBuilder;
import me.lo.lomefree.Interfaces.KeyModality;
import me.lo.lomefree.Interfaces.NotificationId;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.Model.Cryptography.SimmetricKeyCiphers.CipherBuilder;
import me.lo.lomefree.Model.Cryptography.SimmetricKeyCiphers.SimmetricCipherOperation;
import me.lo.lomefree.R;
import me.lo.lomefree.Services.KillAppService;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.UIUtils.Graphics.AdsHelpers;
import me.lo.lomefree.UIUtils.Graphics.Alerts;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Utils.Files.Entities.FileBox;
import me.lo.lomefree.Keys.Entities.KeyFile;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;
import me.lo.lomefree.Utils.Misc.NotificationSender;
import mehdi.sakout.fancybuttons.FancyButton;

import static me.lo.lomefree.Globals.GlobalValues.LONG;
import static me.lo.lomefree.Globals.GlobalValues.SUCCESS;
import static me.lo.lomefree.Interfaces.RRCodes.PROGRESS_ACTIVITY_MULTIPLE_ERRORS;
import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.Globals.GlobalValues.INFO;
import static me.lo.lomefree.Globals.GlobalValues.WARNING;

public class ProgressActivity extends AppCompatActivity implements KeyModality, NotificationId, SettingsParameters
{

    private FancyButton stop_task;
    private FancyButton do_background;
    private FileBox box, finalBox;
    private int keyType;
    private String opType;
    private int back_count;
    private boolean toArchiveFlag = false;
    private CustomSettings customSettings;
    private Context thisContext;
    private AppCompatActivity thisActivity;
    public static boolean cancelTask = false;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thisContext = this;
        thisActivity = this;
        setContentView(R.layout.activity_progress);
        getCustomSettings();
        getData();
        setComponents();
        startService(new Intent(getBaseContext(), KillAppService.class));
        initializeAds();
    }

    private void initializeAds()
    {
        if(!AdsHelpers.isAdsFree(customSettings, AD_FREE, thisContext, thisActivity)) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                    Log.d("MADS: ", "Initialized");
                }
            });
            loadAd();
        }else
        {
            findViewById(R.id.mainAdView).setVisibility(View.GONE);
        }
    }

    private void loadAd()
    {
        adView = findViewById(R.id.mainAdView);
        AdsBuilder.loadAd(adView, thisActivity, "Key File Manager");
    }

    private void getData()
    {
        box = (FileBox) getIntent().getSerializableExtra("FileBox");
        keyType = getIntent().getIntExtra("KeyMode", MODE_PASSWORD);
        toArchiveFlag = getIntent().getBooleanExtra("to_archive", false);

        List<KeyFile> keyFiles = null;
        String key = null;
        opType = getIntent().getStringExtra("OP_TYPE");
        //String newPath = getIntent().getStringExtra("NEW_PATH");


        switch(keyType)
        {
            case MODE_KEYFILE:
            case MODE_PASSWORD:
            case MODE_QRCODE:
                key = getIntent().getStringExtra("KEY");
                break;

            case MODE_KEYFILE_MULTI:
                keyFiles = (List<KeyFile>) getIntent().getSerializableExtra("MULTIKEY");
                break;
        }

        buildOperation(opType, key, keyFiles, box);

    }

    private void getCustomSettings()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(getApplicationContext());
        customSettings = DataManager.getCustomSettings(prefs);
    }

    private void buildOperation(String opType, String key, List<KeyFile> keyFiles, FileBox box)
    {
        String savePath = customSettings.getCustom_save_path();
        boolean delete = customSettings.isRemove_file();
        SimmetricCipherOperation simmetricCipherOperation = null;
        int remove_passes = customSettings.getRemove_passes();
        boolean encrypt = false;
        boolean multikey = false;

        switch(opType)
        {
            case "ENC":
                String algorithmIdentifier = customSettings.getCipher_algorithm();
                Log.d("ALGORITMO ", algorithmIdentifier);
                String cipherMode = CipherBuilder.parseCipherMode(algorithmIdentifier);
                String algKeyCode = CipherBuilder.parseAlgorithmKeyCode(algorithmIdentifier);
                String hashAlg = CipherBuilder.parseHashAlgorithm(algorithmIdentifier);

                try
                {
                    simmetricCipherOperation = new SimmetricCipherOperation(box, key, delete, cipherMode, algKeyCode, algorithmIdentifier, hashAlg, savePath, remove_passes, thisContext);
                    encrypt = true;
                } catch (NoSuchAlgorithmException e)
                {
                    postErrorAlertMessage(getString(R.string.error), getString(R.string.no_hash_algorithm_error));
                    finish();
                }
                break;

            case "DEC":
                switch (keyType)
                {
                    case MODE_KEYFILE:
                    case MODE_PASSWORD:
                    case MODE_QRCODE:
                        simmetricCipherOperation = new SimmetricCipherOperation(box, key, delete, savePath, this, remove_passes, thisContext);
                        break;
                    case MODE_KEYFILE_MULTI:
                        simmetricCipherOperation = new SimmetricCipherOperation(box, keyFiles, delete, savePath, this, remove_passes, thisContext);
                        multikey = true;
                        break;
                }
                encrypt = false;
                break;
        }
        makeAsynProcess(encrypt, simmetricCipherOperation, multikey);
    }

    @SuppressLint("StaticFieldLeak")
    private void makeAsynProcess(final boolean finalEncrypt, final SimmetricCipherOperation finalSimmetricCipherOperation, final boolean multikey)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            FileBox finalBox;
            if (!toArchiveFlag) {
                if (!multikey)
                    finalBox = (finalEncrypt
                            ? finalSimmetricCipherOperation.encryptFileBox()
                            : finalSimmetricCipherOperation.decryptFileBox());
                else
                    finalBox = (finalSimmetricCipherOperation.decryptFileBoxWithMultipleKeyFiles());
            } else {
                finalBox = finalSimmetricCipherOperation.createEncryptedArchive();
            }

            final FileBox finalFileBox = finalBox;
            handler.post(() -> checkIfErrorsAndDoFinishTaskOperations(finalFileBox));
        });
    }


    private void checkIfErrorsAndDoFinishTaskOperations(final FileBox box)
    {
        if(box.isWithError() && !cancelTask)
        {
            if(box.getFileNumber() == 1)
            {
                postToastMessage(getString(R.string.wrong_password), WARNING, LONG);
            }else if(box.getFileNumber() > 1 && customSettings.isLog_error())
            {
                Log.d("EXTRAS ", box.getLog());
                Intent thisIntent = getIntent().putExtra("BOX_LOG", box.getLog());
                setResult(PROGRESS_ACTIVITY_MULTIPLE_ERRORS, thisIntent);
                sendProcessCompleteNotification();
            }else if(box.getFileNumber() > 1 && !customSettings.isLog_error() && box.getErrors().size() == box.getFileNumber())
            {
                Log.d("EXTRAS ", box.getLog());
                Intent thisIntent = getIntent().putExtra("BOX_LOG", box.getLog());
                setResult(PROGRESS_ACTIVITY_MULTIPLE_ERRORS, thisIntent);
            }
        }else
        {
            if(box.isFromShare())
            {
                for(File instance : box.getAllFiles()) {
                    try {
                        FileManager.secureDeleteFile(instance, customSettings.getRemove_passes(), thisContext);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                for(String instance : box.getOriginalPaths())
                {
                    try {
                        if (new File(instance).isDirectory())
                            FileUtils.deleteDirectory(new File(instance));
                        else
                            new File(instance).delete();
                    }catch(Exception ex)
                    {
                        Log.d("ERRORE RIMOZIONE", "Errore nel rimuovere: "+ex.getMessage());
                    }
                }
            }
            sendProcessCompleteNotification();
            postToastMessage(getString(R.string.operation_completed), SUCCESS, LONG);
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            finishAndRemoveTask();
        }
        cancelTask = false;
        finishAndRemoveTask();
    }


    private void sendProcessCompleteNotification()
    {
        NotificationSender.sendNotification(ProgressActivity.this, getString(R.string.AppName),getString(R.string.operation_completed), true, customSettings);
    }


    private void setComponents()
    {
        stop_task = findViewById(R.id.stop_task);
        do_background = findViewById(R.id.do_background);
    }


    /*private void destroyTask()
    {
        try {
            if(asyncProcess != null) {
                asyncProcess.cancel(true);
                cancelTask = true;
                NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                assert mNotificationManager != null;
                mNotificationManager.cancelAll();
            }
            if(box != null && box.isFromShare())
            {
                for(File instance : box.getAllFiles())
                    FileManager.secureDeleteFile(instance, customSettings.getRemove_passes(), thisContext);

                for(String instance : box.getOriginalPaths())
                {
                    try {
                        if (new File(instance).isDirectory())
                            FileUtils.deleteDirectory(new File(instance));
                        else
                            new File(instance).delete();
                    }catch(Exception ex)
                    {
                        Log.d("ERRORE RIMOZIONE", "Errore nel rimuovere: "+ex.getMessage());
                    }
                }
            }
            makeToast(getApplicationContext(), getString(R.string.interrupt_task), WARNING, Toast.LENGTH_LONG);
        }catch(Exception ex)
        {
            if(ex instanceof NullPointerException)
                makeToast(getApplicationContext(), getString(R.string.not_active_task), ERROR, LONG);
            else
                makeToast(getApplicationContext(), ex.getMessage(), ERROR, LONG);
        }
    }*/

    @Override
    public void onBackPressed() {

        if(back_count <2) {
            postToastMessage(getString(R.string.press_for_exit), INFO, Toast.LENGTH_LONG);
            back_count += 1;
        }else
        {
            //destroyTask();
            cancelTask = true;
            NotificationSender.sendNotification(ProgressActivity.this, getString(R.string.AppName),getString(R.string.operation_canceled), true, customSettings);
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            finishAndRemoveTask();
        }

        //super.onBackPressed();
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

    private void postErrorAlertMessage(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerts.showErrorDialog(title, message, thisContext, R.drawable.ic_error_black_24dp);
            }
        });

    }




}
