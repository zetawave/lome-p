package me.lo.lomefree.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.cloudrail.si.services.Dropbox;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nononsenseapps.filepicker.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import angtrim.com.fivestarslibrary.FiveStarsDialog;
import angtrim.com.fivestarslibrary.NegativeReviewListener;
import angtrim.com.fivestarslibrary.ReviewListener;
import de.hdodenhof.circleimageview.CircleImageView;
import me.lo.lomefree.Ads.AdsBuilder;
import me.lo.lomefree.Analytics.FireBaseUtils;
import me.lo.lomefree.BillingPurchaseUtils.BillingPurchaseOperation;
import me.lo.lomefree.BuildConfig;
import me.lo.lomefree.Globals.GlobalValues;
import me.lo.lomefree.Interfaces.ChoiceDialogModes;
import me.lo.lomefree.Interfaces.ChooserModes;
import me.lo.lomefree.Interfaces.DBNames;
import me.lo.lomefree.Interfaces.Extensions;
import me.lo.lomefree.Interfaces.FirstAccessViews;
import me.lo.lomefree.Interfaces.ItemIdentifier;
import me.lo.lomefree.Interfaces.RRCodes;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.R;
import me.lo.lomefree.UIUtils.Graphics.AdsHelpers;
import me.lo.lomefree.UIUtils.Graphics.FirstAccessInformations;
import me.lo.lomefree.UIUtils.Graphics.UIComponentBuilder;
import me.lo.lomefree.UIUtils.Graphics.Alerts;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Utils.Misc.NotificationSender;
import me.lo.lomefree.Utils.Files.FDManager.UriDataResolver;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;
import me.lo.lomefree.Utils.Misc.PermissionChecker;

import static me.lo.lomefree.Globals.GlobalValues.INFO;
import static me.lo.lomefree.Keys.Entities.KeyFile.isKeyFile;
import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.UIUtils.Intents.IntentsManager.createFileElaborationIntent;
import static me.lo.lomefree.UIUtils.Intents.IntentsManager.createNewUriKeyIntent;
import static me.lo.lomefree.Utils.Files.FDManager.DataManager.setStringPref;
import static me.lo.lomefree.Globals.GlobalValues.ERROR;
import static me.lo.lomefree.Globals.GlobalValues.LONG;

public class MainActivity extends AppCompatActivity implements Extensions,
        FirstAccessViews,
        ItemIdentifier,
        SettingsParameters,
        ChooserModes,
        RRCodes,
        DBNames,
        ChoiceDialogModes,
        View.OnClickListener,
        BillingProcessor.IBillingHandler
{
    private GridLayout mainGrid;
    private AppCompatActivity thisActivity;
    private final CardView [] cards = new CardView [6];
    private Toolbar toolbar;
    private Context thisContext;
    private CustomSettings customSettings;
    private CircleImageView profileImage;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private Button settings;
    private static Dropbox db;
    private FirebaseAnalytics fbanalytics;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisContext = this;
        thisActivity = this;
        getPermissions();
        initializeGraphicsStaticComponents();
        initializeCustomSettings();
        getViewComponents();
        acquireCards();
        checkProfileImage();
        initializeViewListeners();
        handleSahring();
        buildDrawer();
        initializeVmPolicy();
        initalizeFirebase();
        rateUsDialog();
        initializeAds();
    }


    // USELESS ATM
   /* public void verifyFirstAccess()
    {
        FirstAccLayoutBuilder.verifyFirstAccess(customSettings, NEW_VERSION, APP_VERSION, thisContext, thisActivity);
    }*/


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
            refreshPrefs();
        }else
        {
            findViewById(R.id.mainAdView).setVisibility(View.GONE);
        }
    }

    private void loadAd()
    {
        adView = findViewById(R.id.mainAdView);
        AdsBuilder.loadAd(adView, thisActivity, "Main Home");
    }


    private void rateUsDialog()
    {
        if(!customSettings.isFirst_access())
        {
            FiveStarsDialog fiveStarsDialog = new FiveStarsDialog(this, "lo2me.info@gmail.com");
            fiveStarsDialog.setRateText(getString(R.string.rate_me))
                    .setTitle(getString(R.string.rate_me_title))
                    .setForceMode(true)
                    .setUpperBound(4)
                    .setNegativeReviewListener(new NegativeReviewListener() {
                        @Override
                        public void onNegativeReview(int i) {
                            makeToast(getApplicationContext(), getString(R.string.negative_review), INFO, LONG);
                        }
                    })
                    .setReviewListener(new ReviewListener() {
                        @Override
                        public void onReview(int i) {
                            Log.d("REVIEW", "REVIEW " + i);
                        }
                    })
                    .showAfter(4);
        }
    }


    private void initializeGraphicsStaticComponents()
    {
        initializeAboutGraphic();
        initializeFirstAccessInformations();
    }

    private void initializeFirstAccessInformations()
    {
        FirstAccessInformations.initializeFirstAccessInformations(thisContext);
    }

    private void initializeAboutGraphic()
    {
        AboutActivity.TITLES = new String[]{getString(R.string.AppName)};
        AboutActivity.LOGOS = new int[]{R.drawable.currentlogo};
        AboutActivity.TEXTS = new String[][]{{getString(R.string.Version_text), getString(R.string.Developer_text), getString(R.string.Contact_text), getString(R.string.open_source_license), getString(R.string.terms_and_Conditions), getString(R.string.privacy_rules), getString(R.string.website)}};
        AboutActivity.SUBTEXTS = new String[][]{{getString(R.string.app_version), getString(R.string.Author), getString(R.string.email), "", "", "", ""}};
        AboutActivity.SUBICON = new int[][]{{R.drawable.ic_build_black_24dp, R.drawable.ic_person_black_24dp, R.drawable.ic_email_black_24dp, R.drawable.open_source, R.drawable.terms, R.drawable.privacyrules, R.drawable.ic_web_black_24dp}};
    }

    private void initalizeFirebase()
    {
        fbanalytics = FirebaseAnalytics.getInstance(this);
    }

    private void initializeVmPolicy() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    private void buildDrawer()
    {
        UIComponentBuilder.buildDrawer(toolbar, getApplicationContext(), this, customSettings, this);
    }

    private void getPermissions()
    {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.getDeniedPermissionResponses().size() > 0)
                {
                    makeToast(thisContext, getString(R.string.permission_request), ERROR, LONG);
                    NotificationSender.sendBigTextNotification(thisContext, getString(R.string.permission_request_notification_title), getString(R.string.permission_request_notification_message), true, customSettings);
                    //finishAndRemoveTask();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                Log.d("RATIONALE", "PERMISSION RATIONALE");
            }
        }).check();

        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                Intent getPermission = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION , uri);
                startActivity(getPermission);
            }
        }
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                PermissionChecker.verifyCameraPermissionGranted(MainActivity.this);

            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                PermissionChecker.verifyStoragePermissionGranted(MainActivity.this);
            }
        }).start();
        */

    }

    private void handleSahring()
    {

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        Intent elabIntent;
        if ((Intent.ACTION_SEND.equals(action) || Intent.ACTION_SEND_MULTIPLE.equals(action)) && type != null)
        {
            elabIntent = new Intent(getApplicationContext(), SharingHandler.class);
            startActivity(elabIntent);
        }

    }

    private void checkProfileImage()
    {
        try {
            String profileimage = prefs.getString(PROFILE_IMAGE_PREF, "null");
            if (profileimage.equals("null"))
                profileImage.setBackgroundResource(R.drawable.currentlogo);
            else {
                File imgFile = new File(profileimage);
                checkProfileImage(imgFile);
            }
        }catch(Exception ex)
        {
            profileImage.setBackgroundResource(R.drawable.currentlogo);
            Log.d("CHECK PRO. IMG", ex.getMessage());
        }
    }

    private void initializeCustomSettings()
    {
        prefs = DataManager.getSharedPreferences(thisContext);
        prefsEditor = prefs.edit();
        customSettings = DataManager.getCustomSettings(prefs);
    }

    private void refreshPrefs()
    {
        initializeCustomSettings();
    }



    private void getViewComponents()
    {
        mainGrid = findViewById(R.id.mainGrid);
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolmenu);
        profileImage = findViewById(R.id.profile_image);
        settings = findViewById(R.id.settings);
    }

    private void acquireCards()
    {
        for(int j=0; j<mainGrid.getChildCount(); j++)
            cards[j] = (CardView) mainGrid.getChildAt(j);
    }


    private void uploadProfileImage() {
        if(PermissionChecker.verifyStoragePermissionGranted(this)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_profile_image)), READ_IMAGE_REQUEST_CODE );

        }

    }

    private void checkProfileImage(File imgFile)
    {
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            profileImage.setImageBitmap(myBitmap);
        }
    }

    private void initializeViewListeners()
    {
        for(CardView card : cards)
            card.setOnClickListener(this);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()== R.id.overview)
                {
                    Intent overview = new Intent(getApplicationContext(), IntroActivity.class);
                    overview.putExtra("notfirst", true);
                    startActivity(overview);
                }

                return false;
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfileImage();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
    }

    private void getImagesFromGallery()
    {

        DataManager.createImagePickerDialog(this);
    }

    private void openSaveFolder()
    {
        Alerts.showMultiChoiceDialog(thisActivity, customSettings, SAVE_DIR_CHOICE);
    }

    private void getPickMode()
    {
        DataManager.getFileAndDirsChooser(getApplicationContext(), this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == READ_IMAGE_REQUEST_CODE)
        {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri selectedImageURI = data.getData();
                    try {
                        setProfileImageFromUri(selectedImageURI);
                    } catch (Exception e) {
                        makeToast(getApplicationContext(), getString(R.string.error)+" "+e.getMessage(), ERROR, LONG);
                    }
                }
            }
        }else if (resultCode == RESULT_OK && requestCode == OPEN_MEDIA_PICKER)
        {
            ArrayList<String> mediaPaths=data.getStringArrayListExtra("result");
            if(mediaPaths != null && mediaPaths.size() > 0)
                createFileElaborationIntent(FileManager.getFileBoxFromArrayOfPaths(mediaPaths), this);

        }else if(resultCode == RESULT_OK && requestCode == PICK_FILEANDDIR_REQUEST_CODE && data != null)
        {
            ArrayList<String> selectedFilesPaths = new ArrayList<>();
            List<Uri> files = Utils.getSelectedFilesFromResult(data);
            for(Uri u: files)
                selectedFilesPaths.add(Utils.getFileForUri(u).getAbsolutePath());

            if(files.size() == 1 && isKeyFile(new File(selectedFilesPaths.get(0)).getName()))
                createNewUriKeyIntent(true, selectedFilesPaths.get(0), this);
            else
                createFileElaborationIntent(FileManager.getFileBoxFromArrayOfPathsWithMirrorMode(selectedFilesPaths, customSettings.isMirror_dir()), this);

/*
            ArrayList<Uri> selectedFiles  = data.getParcelableArrayListExtra(Constants.SELECTED_ITEMS);
            ArrayList<String> selectedFilesPaths = new ArrayList<>();
            for(Uri u: selectedFiles)
                selectedFilesPaths.add(UriDataResolver.getRealPathFromURI(u, getContentResolver()));

            if(selectedFiles.size() == 1 && isKeyFile(new File(selectedFilesPaths.get(0)).getName()))
                createNewUriKeyIntent(true, selectedFilesPaths.get(0), this);
            else
                createFileElaborationIntent(FileManager.getFileBoxFromArrayOfPathsWithMirrorMode(selectedFilesPaths, customSettings.isMirror_dir()), this);
        */
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    private void setProfileImageFromUri(Uri selectedImageURI) throws Exception {
        String path = UriDataResolver.getRealPathFromURI(selectedImageURI, getContentResolver());
        File imgFile = new File(path);
        checkProfileImage(imgFile);
        setStringPref(PROFILE_IMAGE_PREF, path, prefsEditor);
        customSettings.setProfile_image(path);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 1)
        {
            for(int i=0; i<permissions.length; i++)
            {
                if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        initializeCustomSettings();
                    else {
                        makeToast(this, getString(R.string.permission_request), ERROR, LONG);
                        NotificationSender.sendBigTextNotification(this, getString(R.string.permission_request_notification_title), getString(R.string.permission_request_notification_message), true, customSettings);
                        finishAndRemoveTask();
                    }
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v)
    {
        Bundle params = new Bundle();
        int viewId = v.getId();
        Intent intent;
        params.putInt("Main_CardID", viewId);
        ActivityOptionsCompat activityOptions;

        switch(viewId)
        {
            case R.id.addFile:
                params.putInt("Main_CardID", R.id.addFile);
                FireBaseUtils.logFireBaseEvent("aggiungi_files", params, fbanalytics);
                refreshPrefs();
                getPickMode();
                break;
            case R.id.openSaveFolder:
                params.putInt("Main_CardID", R.id.openSaveFolder);
                FireBaseUtils.logFireBaseEvent("cartella_salvataggio", params, fbanalytics);
                refreshPrefs();
                openSaveFolder();
                break;

            case R.id.shredder:
                params.putInt("Main_CardID", R.id.shredder);
                FireBaseUtils.logFireBaseEvent("shredder", params, fbanalytics);
                refreshPrefs();
                activityOptions = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(thisActivity, v, "transition");
                intent = new Intent(thisActivity, ShredderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityCompat.startActivity(thisActivity, intent, activityOptions.toBundle());
                break;

            case R.id.gallery:
                params.putInt("Main_CardID", R.id.gallery);
                FireBaseUtils.logFireBaseEvent("galleria_click", params, fbanalytics);
                refreshPrefs();
                getImagesFromGallery();
                break;

            case R.id.rsa:
                params.putInt("Main_CardID", R.id.rsa);
                FireBaseUtils.logFireBaseEvent("rsa_click", params, fbanalytics);
                refreshPrefs();

                activityOptions = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(thisActivity, v, "transition");
                intent = new Intent(thisActivity, RSAMakerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityCompat.startActivity(thisActivity, intent, activityOptions.toBundle());

                //Intent rsaIntent = new Intent(getApplicationContext(), RSAMakerActivity.class);
                //startActivity(rsaIntent);
                break;
            case R.id.hider:
                params.putInt("Main_CardID", R.id.hider);
                FireBaseUtils.logFireBaseEvent("hider_click", params, fbanalytics);
                refreshPrefs();
                activityOptions = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(thisActivity, v, "transition");
                intent = new Intent(thisActivity, HiderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityCompat.startActivity(thisActivity, intent, activityOptions.toBundle());
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (BillingPurchaseOperation.bp != null) {
            BillingPurchaseOperation.bp.release();
        }
        super.onDestroy();
    }




    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable PurchaseInfo details) {
        Log.d("BILLING: ","Product Purchased "+ productId +" "+ details);
        assert details != null;
        try {
            if (details.purchaseData != null && details.signature != null) {
                AdsHelpers.setAdFree(customSettings, AD_FREE, thisContext);
                refreshPrefs();
                thisActivity.recreate();
                makeToast(thisContext, getString(R.string.adFreePurchased), GlobalValues.SUCCESS, LONG);
            }
        }catch(Exception ex){
            Log.d("BILLING ERROR: ","Product Purchased "+ productId +" "+ details+" "+ex.getMessage()+" "+ Arrays.toString(ex.getStackTrace()));
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.d("BILLING: ","NVM");
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.d("BILLING: ","Some error occurred");
        switch(errorCode)
        {
            default:
                Log.d("BILLING ERROR", errorCode+" "+error);
                makeToast(thisContext, errorCode +": "+error, ERROR, LONG);
                break;
        }
    }

    @Override
    public void onBillingInitialized() {
        Log.d("BILLING: ","Billing is Initialized and ready to purchase");

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}


