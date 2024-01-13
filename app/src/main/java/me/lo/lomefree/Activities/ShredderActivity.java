package me.lo.lomefree.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.nononsenseapps.filepicker.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.grabner.circleprogress.CircleProgressView;
import me.lo.lomefree.Ads.AdsBuilder;
import me.lo.lomefree.Globals.GlobalValues;
import me.lo.lomefree.Interfaces.RRCodes;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.Model.Shredding.ShredBox;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.UIUtils.Graphics.AdsHelpers;
import me.lo.lomefree.UIUtils.Graphics.Alerts;
import me.lo.lomefree.Utils.Files.Entities.FileBox;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;
import me.lo.lomefree.Utils.Misc.NotificationSender;

import static me.lo.lomefree.Globals.GlobalValues.INFO;
import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.UIUtils.UISecurity.WindowSecurity.initSecurityFlags;

public class ShredderActivity extends AppCompatActivity implements RRCodes, SettingsParameters
{

    private CustomSettings customSettings;
    private Context thisContext;
    public static TextView numFiles, completedFiles, algorithm;
    private AppCompatActivity thisActivity;
    private CircleProgressView circleProgressBar;
    private CardView selectFile, addMedia;
    private ImageView successImg;
   // private Button backButton;
    private static boolean agreed = false;
    private AdView adView;
    private int back_count;
    public static boolean cancel_task = false;
    private boolean operation_started = false;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shredder);
        thisContext = this;
        thisActivity = this;
        getCustomSettings();
        initSecurityFlags(customSettings, getWindow());
        initComponents();
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

    private void initComponents() {
        selectFile = findViewById(R.id.select_files);
        circleProgressBar = findViewById(R.id.shredder_progress);
        numFiles = findViewById(R.id.numFileSelected);
        completedFiles= findViewById(R.id.shreddedFilesCount);
        algorithm = findViewById(R.id.shredAlgorithm);
        successImg = findViewById(R.id.success_gif);
        addMedia = findViewById(R.id.addMedia);
        //backButton = findViewById(R.id.back_button);
        customizeComponents();
        initializeListeners();
    }

    private void initializeListeners() {
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               DataManager.getFileAndDirsChooser(getApplicationContext(), thisActivity);
            }
        });

        addMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImagesFromGallery();
            }
        });

        /*backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
            }
        });*/
    }

    private void customizeComponents() {
        circleProgressBar.setFillCircleColor(getColor(R.color.primary_dark));
    }

    private void debugProgress()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                circleProgressBar.setMaxValueAllowed(100);
                for(int i=0; i<100+1; i++)
                {
                    circleProgressBar.setValue(i);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void startShreddingOperation(final String algSelected, final FileBox fileBox)
    {

        executor.execute(() -> {
            handler.post(() -> {
                cancel_task = false;
                operation_started = true;
                selectFile.setEnabled(false);
                if (circleProgressBar.getVisibility() == View.GONE) {
                    successImg.setVisibility(View.GONE);
                    circleProgressBar.setVisibility(View.VISIBLE);
                }
            });

            try {
                FileManager.secureDeleteFile(fileBox.getAllFiles(), thisContext, algSelected, circleProgressBar, thisActivity);
            } catch (IOException e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                circleProgressBar.setVisibility(View.GONE);
                successImg.setVisibility(View.VISIBLE);
                animateImage();
                operation_started = false;
                selectFile.setEnabled(true);
            });
        });

    }

    private void getImagesFromGallery()
    {

        DataManager.createImagePickerDialog(this);
    }

    @Override
    public void onBackPressed() {
        if(back_count < 2 && operation_started) {
            postToastMessage(getString(R.string.press_for_exit), INFO, Toast.LENGTH_LONG);
            back_count += 1;
        }else if(back_count == 2 && operation_started)
        {
            //destroyTask();
            cancel_task = true;
            if(executor != null)
                executor.shutdownNow();
            NotificationSender.sendNotification(ShredderActivity.this, getString(R.string.AppName),getString(R.string.operation_canceled), true, customSettings);
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            finishAndRemoveTask();
        }else
            finishAndRemoveTask();

    }

    private void postToastMessage(final String message, final int mode, final int lenght) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> makeToast(getApplicationContext(),message, mode, lenght));
    }

    private void animateImage() {
        YoYo.with(Techniques.FlipInY)
                .duration(500)
                .repeat(0)
                .playOn(successImg);
    }

    private void getCustomSettings()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(this);
        customSettings = DataManager.getCustomSettings(prefs);
    }


    private void createAlgorithmChoiceDialog(final FileBox fileBox) {
        AlertDialog.Builder algoAlert = Alerts.buildGenericAlertDialog(thisContext, getString(R.string.chooseAlgo));
        final ShredBox shredBox = new ShredBox();
        final Object [] stringInfo = shredBox.getStringInformations(thisContext).toArray();
        CharSequence [] algos = new CharSequence[stringInfo.length];
        System.arraycopy(stringInfo, 0, algos, 0, stringInfo.length);

        Log.d("FILE NUMBER ", String.valueOf(fileBox.getFileNumber()));
        for(File file: fileBox.getAllFiles())
            Log.d("Files ", file.getName()+"  "+file.isDirectory());

        algoAlert.setItems(algos, (dialog, which) -> {
            final String algSelected = shredBox.getAlgorithmSelected(which);

            final AlertDialog confirmDialog = Alerts.buildGenericAlertDialog(thisContext,
                    thisContext.getString(R.string.confirmShred))
                    .setView(R.layout.shred_layout)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.confirm_title), null)
                    .setNegativeButton(getString(R.string.cancel), (dialog1, which1) -> Log.d("CANCELD", "CANCELED"))
                    .create();

            confirmDialog.setOnShowListener(dialog12 -> confirmDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                if(!agreed)
                {
                    makeToast(thisContext, getString(R.string.notAgree), GlobalValues.WARNING, GlobalValues.LONG);
                }
                else
                {
                    startShreddingOperation(algSelected, fileBox);
                    dialog12.dismiss();
                }
            }));
            confirmDialog.show();
        });
        algoAlert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> Log.d("CANCELED", "CANCELED"));
        algoAlert.setCancelable(false);
        algoAlert.create().show();
    }

    public void onCheckedChange(View view){
        CheckBox checkBox = (CheckBox) view;
        agreed = checkBox.isChecked();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK && requestCode == PICK_FILEANDDIR_REQUEST_CODE && data != null) {
            ArrayList<String> selectedFilesPaths = new ArrayList<>();
            List<Uri> files = Utils.getSelectedFilesFromResult(data);
            for (Uri u : files)
                selectedFilesPaths.add(Utils.getFileForUri(u).getAbsolutePath());
            createAlgorithmChoiceDialog(FileManager.getFileBoxFromArrayOfPathsWithMirrorMode(selectedFilesPaths, true));
        }else if (resultCode == RESULT_OK && requestCode == OPEN_MEDIA_PICKER)
        {
            ArrayList<String> mediaPaths=data.getStringArrayListExtra("result");
            if(mediaPaths != null && mediaPaths.size() > 0)
                createAlgorithmChoiceDialog(FileManager.getFileBoxFromArrayOfPaths(mediaPaths));

        }else
            super.onActivityResult(requestCode, resultCode, data);
    }


}
