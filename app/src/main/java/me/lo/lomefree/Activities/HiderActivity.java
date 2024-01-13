package me.lo.lomefree.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.nononsenseapps.filepicker.Utils;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.lo.lomefree.Ads.AdsBuilder;
import me.lo.lomefree.BuildConfig;
import me.lo.lomefree.Globals.GlobalValues;
import me.lo.lomefree.Interfaces.DBNames;
import me.lo.lomefree.Interfaces.HiderPaths;
import me.lo.lomefree.Interfaces.RRCodes;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.Model.HiderManager.HiderItemsManager;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.SettingsUtils.ParticularSetting;
import me.lo.lomefree.UIUtils.Graphics.AdsHelpers;
import me.lo.lomefree.UIUtils.Graphics.Alerts;
import me.lo.lomefree.UIUtils.Graphics.UIComponentBuilder;
import me.lo.lomefree.Utils.Files.Entities.HiderItem;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;

import static me.lo.lomefree.Globals.GlobalValues.ERROR;
import static me.lo.lomefree.Globals.GlobalValues.LONG;
import static me.lo.lomefree.Globals.GlobalValues.SHORT;
import static me.lo.lomefree.Globals.GlobalValues.SUCCESS;
import static me.lo.lomefree.Globals.GlobalValues.WARNING;
import static me.lo.lomefree.Model.HiderManager.HiderItemsManager.saveSuccess;
import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.UIUtils.Graphics.UIHelpers.showHiderTapWizard;
import static me.lo.lomefree.UIUtils.UISecurity.WindowSecurity.initSecurityFlags;
import static me.lo.lomefree.Utils.Files.Entities.HiderItem.ALL;
import static me.lo.lomefree.Utils.Files.Entities.HiderItem.FILES;
import static me.lo.lomefree.Utils.Files.Entities.HiderItem.IMAGE;
import static me.lo.lomefree.Utils.Files.Entities.HiderItem.VIDEO;

public class HiderActivity extends AppCompatActivity implements DBNames, SettingsParameters, HiderPaths, RRCodes
{

    private MyListAdapter adapter;
    private ListView hiderListView;
    private CustomSettings customSettings;
    private Button pickInGallery, pickInStorage;
    private List<HiderItem> hiderItems, filteredItems;
    private ParticularSetting particularSetting;
    private AppCompatActivity thisActivity;
    private Context thisContext;
    private ProgressDialog pd;
    private Toolbar hiderToolbar;
    private final int FILE = 0;
    private final int MEDIA = 1;
    private AdView adView;
    private InterstitialAd interstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        thisContext = this;
        getCustomSettings();
        initSecurityFlags(customSettings, getWindow());
        setContentView(R.layout.activity_hider);
        verifyFirstAccess();
        setComponents();
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
        AdsBuilder.loadAd(adView, thisActivity, "Hider Activity");
    }

    private void loadNewInterstitialAd()
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, GlobalValues.adsInterstitialHiderBannerID, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd myInt) {
                Log.d("MADS: ", "Interstitial loaded");
                interstitialAd = myInt;
                showInterstitialAd();
                super.onAdLoaded(interstitialAd);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d("MADS: ", "Interstitial failed to load: "+loadAdError.toString());
                super.onAdFailedToLoad(loadAdError);
            }
        });

      /*  interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(GlobalValues.adsInterstitialHiderBannerID);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                showInterstitialAd();
                Log.d("INTERSTITIAL AD HIDER", "AD LOADED");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d("INTERSTITIAL AD HIDER", "AD FAILED TO LOAD: "+errorCode);
            }

        });
       */
    }

    private void showInterstitialAd()
    {
        if (interstitialAd != null){
            interstitialAd.show(this);
        }else {
            Log.d("MADS","Interstitial Ad not ready yet");
        }

    }

    private void setComponents()
    {
        hiderToolbar = findViewById(R.id.hider_toolbar);
        hiderToolbar.inflateMenu(R.menu.hider_filter_menu);
        pickInGallery = findViewById(R.id.addMedia);
        pickInStorage = findViewById(R.id.addFile);

        hiderListView = findViewById(R.id.hider_list_view);
        @SuppressLint("InflateParams") View child = getLayoutInflater().inflate(R.layout.empty_hider_view, null);
        child.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
        ((ViewGroup)hiderListView.getParent()).addView(child);
        hiderListView.setEmptyView(child);
        loadData();

        setListeners();

    }

    private void setListeners()
    {
        hiderListView.setOnItemClickListener((parent, view, position, id) -> {
            String text = getString(R.string.category)+" "
                    +filteredItems.get(position).getCategory()+"\n"
                    +getString(R.string.extension)+" "
                    +filteredItems.get(position).getExtension()+"\n"
                    +getString(R.string.filesize)+" "
                    +filteredItems.get(position).getSize();

            Alerts.showInfoDialog(getString(R.string.information), text, thisContext, R.drawable.infovalues);
        });


        pickInStorage.setOnClickListener(v -> DataManager.getMultipleFileChooser(thisContext, thisActivity));

        pickInGallery.setOnClickListener(v -> DataManager.createImagePickerDialog(thisActivity));

        hiderToolbar.setOnMenuItemClickListener(item -> {


            switch(item.getItemId())
            {
                case R.id.filter_for_files:
                    checkMenu(item, item.getItemId(), hiderToolbar.getMenu());
                    filterBy(FILES);
                    break;
                case R.id.filter_for_image:
                    checkMenu(item, item.getItemId(), hiderToolbar.getMenu());
                    filterBy(IMAGE);
                    break;
                case R.id.filter_for_video:
                    checkMenu(item, item.getItemId(), hiderToolbar.getMenu());
                    filterBy(VIDEO);
                    break;

                case R.id.filter_for_all:
                    checkMenu(item, item.getItemId(), hiderToolbar.getMenu());
                    filterBy(ALL);
                    break;

                case R.id.info:
                    Alerts.showInfoDialog(getString(R.string.information),
                            getString(R.string.hider_info),
                            thisContext
                            , R.drawable.ic_info_black_24dp);
                    break;

                case R.id.restore_all:
                    restoreAllConfirm();
                    break;
            }
            return false;
        });

    }

    private void restoreAllConfirm()
    {
        if(hiderItems != null && hiderItems.size() > 0) {
            AlertDialog.Builder builder = Alerts.showConfirmDialog(thisContext, getString(R.string.confirm_title), getString(R.string.sure_restore_all));
            builder.setPositiveButton(R.string.yes, (dialog, which) -> restoreAll())
                    .setNegativeButton(R.string.cancel, (dialog, which) -> Log.d("RESTORE ALL", "CANCELED")).create().show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void restoreAll()
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        pd = UIComponentBuilder.getProgressDialog(thisContext, getString(R.string.restore_all_progress));
        pd.show();

        executor.execute(() -> {
            for (HiderItem instance : hiderItems) {
                try {
                    FileManager.restoreFileFromHideDirectory(instance.getOldPath(), instance);
                } catch (IOException ex) {
                    final String errorMessage = getString(R.string.restore_error) + "\n\n" + instance.getName() + ": " + ex.getMessage();
                    handler.post(() -> postToastMessage(errorMessage, ERROR, LONG));
                }
                if (instance.isVideoOrImage(thisContext)) {
                    DataManager.refreshGallery(instance.getOldPath(), thisContext);
                }
            }

            hiderItems.clear();
            filteredItems.clear();

            handler.post(() -> {
                pd.dismiss();
                postToastMessage(getString(R.string.all_files_restored), SUCCESS, SHORT);
                try {
                    saveSuccess(hiderItems);
                } catch (Exception ex) {
                    postToastMessage(getString(R.string.hide_db_update_error) + ": " + ex.getMessage(), ERROR, LONG);
                }
                adapter.notifyDataSetChanged();
            });
        });
    }


    private void checkMenu(MenuItem item, int itemId, Menu menu)
    {
        item.setChecked(true);
        for(int i=0; i<menu.findItem(R.id.filters_submenu).getSubMenu().size(); i++)
            if(menu.findItem(R.id.filters_submenu).getSubMenu().getItem(i).getItemId() != itemId)
                menu.findItem(R.id.filters_submenu).getSubMenu().getItem(i).setChecked(false);
    }

    private void filterBy(int TYPE)
    {
        filteredItems = new ArrayList<>();
        if(TYPE != ALL) {
            if (hiderItems != null && hiderItems.size() > 0)
            {
                for (HiderItem instance : hiderItems) {
                    switch (TYPE) {
                        case FILES:
                            if (instance.getCategory().equals(getString(R.string.file_category)))
                                filteredItems.add(instance);
                            break;

                        case VIDEO:
                            if (instance.getCategory().equals(getString(R.string.video)))
                                filteredItems.add(instance);
                            break;

                        case IMAGE:
                            if (instance.getCategory().equals(getString(R.string.image)))
                                filteredItems.add(instance);
                            break;
                    }
                }
            }
        }else {
            filteredItems =  hiderItems;
        }


        adapter = new MyListAdapter(thisContext, filteredItems);
        hiderListView.setAdapter(adapter);
        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }


    private void decheckAllItems()
    {
        for(int i=0; i<hiderToolbar.getMenu().findItem(R.id.filters_submenu).getSubMenu().size(); i++)
            hiderToolbar.getMenu().findItem(R.id.filters_submenu).getSubMenu().getItem(i).setChecked(false);
    }



    private void verifyFirstAccess()
    {

        particularSetting = customSettings.getOtherPreference(FIRST_HIDER_ACCESS);
        Boolean first_access;
        if(particularSetting != null)
            first_access = (Boolean) particularSetting.getValue();
        else {
            particularSetting = new ParticularSetting();
            particularSetting.setPreference(FIRST_HIDER_ACCESS);
            particularSetting.setValue(true);
            customSettings.setOtherPreference(FIRST_HIDER_ACCESS, particularSetting);
            first_access = true;
        }
        if(first_access)
        {
            showHiderTapWizard(this, customSettings, particularSetting);
        }

    }

    private void verifyShowRestoreConfirm(int position) throws IOException {

        particularSetting = customSettings.getOtherPreference(HIDER_RESTORE_CONFIRM);
        Boolean restore_confirm;
        if(particularSetting != null)
            restore_confirm = (Boolean) particularSetting.getValue();
        else {
            particularSetting = new ParticularSetting();
            particularSetting.setPreference(HIDER_RESTORE_CONFIRM);
            particularSetting.setValue(true);
            customSettings.setOtherPreference(HIDER_RESTORE_CONFIRM, particularSetting);
            restore_confirm = true;
        }
        if(restore_confirm)
        {
            showRestoreConfirm(position);
        }else
            restoreFile(position);

    }

    private void showRestoreConfirm(final int position)
    {
        AlertDialog.Builder builder = Alerts.showConfirmDialog(thisContext, getString(R.string.confirm_title),
                getString(R.string.sure_restore_file),
                R.drawable.ic_question_answer_black_24dp);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restoreFile(position);
            }
        })
                .setNegativeButton(R.string.dont_show_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        particularSetting.setValue(false);
                        customSettings.setOtherPreference(FIRST_HIDER_ACCESS, particularSetting);
                        customSettings.saveOtherPreferences(thisActivity);
                        restoreFile(position);
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("RESTORE", "CANCELED");
                    }
                }).create().show();
    }


    @SuppressLint("StaticFieldLeak")
    private void loadData()
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        pd = UIComponentBuilder.getProgressDialog(thisContext, getString(R.string.loading));
        pd.show();

        executor.execute(() -> {
            try {
                hiderItems = HiderItemsManager.getHiderItemsFromDB();
            } catch (Exception ex) {
                Log.d("EXCEPTION READ", Arrays.toString(ex.getStackTrace()));
                handler.post(() -> pd.dismiss());
            }

            handler.post(() -> {
                pd.dismiss();
                filteredItems = hiderItems;
                hiderToolbar.getMenu().findItem(R.id.filter_for_all).setChecked(true);
                adapter = new MyListAdapter(thisContext, hiderItems);
                hiderListView.setAdapter(adapter);
                runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                    try {
                        saveSuccess(hiderItems);
                    } catch (Exception ex) {
                        Log.d("SAVE ERROR", "ERROR IN SAVING " + ex.getMessage());
                    }
                });
            });
        });
    }


    private void getCustomSettings()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(this);
        customSettings = DataManager.getCustomSettings(prefs);
    }


    private class MyListAdapter extends ArrayAdapter<HiderItem>
    {
        private final int layout;

        MyListAdapter(@NonNull Context context, @NonNull List<HiderItem> objects) {
            super(context, R.layout.hider_item, objects);
            layout = R.layout.hider_item;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder;

            if(convertView == null)
            {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();

                viewHolder.filename = convertView.findViewById(R.id.filename);
                viewHolder.thumbnail = convertView.findViewById(R.id.thumbnail);
                viewHolder.restore = convertView.findViewById(R.id.restore);
                viewHolder.expand = convertView.findViewById(R.id.expand);

                mainViewHolder = viewHolder;

                convertView.setTag(mainViewHolder);

                final View finalConvertView = convertView;

            }else
            {
                mainViewHolder = (ViewHolder) convertView.getTag();
                adapter.notifyDataSetChanged();
            }

            mainViewHolder.filename.setText(getItem(position).getName());
            if(getItem(position).isResources()) {
                mainViewHolder.thumbnail.setBackgroundResource(R.drawable.filehideitem);
                mainViewHolder.expand.setBackgroundResource(R.drawable.ic_open_with_gray_24dp);
            }
            else
            {
                try {
                    BitmapDrawable bitmap = new BitmapDrawable(getResources(), getItem(position).getThumbnail_bitmap());
                    mainViewHolder.thumbnail.setBackground(bitmap);
                    if(getItem(position).isImage(thisContext))
                        mainViewHolder.expand.setBackgroundResource(R.drawable.ic_fullscreen_white_24dp);
                    else if(getItem(position).isVideo(thisContext))
                        mainViewHolder.expand.setBackgroundResource(R.drawable.ic_play_circle_outline_white_24dp);
                }catch(Exception ex)
                {
                    mainViewHolder.thumbnail.setBackgroundResource(R.drawable.media);
                }
            }

            setButtonListeners(mainViewHolder, position);

            return convertView;
        }

        private void setButtonListeners(ViewHolder mainViewHolder, final int position)
        {
            mainViewHolder.restore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        verifyShowRestoreConfirm(position);
                    }catch(Exception ex)
                    {
                        makeToast(getApplicationContext(), getString(R.string.unable_to_restore)+ex.getMessage(), ERROR, LONG);
                    }

                }
            });

            mainViewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFile(new File(filteredItems.get(position).getNewFilepath()), filteredItems.get(position).getExtension());
                }
            });

            mainViewHolder.expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFile(new File(filteredItems.get(position).getNewFilepath()), filteredItems.get(position).getExtension());
                }
            });
        }
    }

    class ViewHolder
    {
        TextView filename;
        Button restore;
        LinearLayout thumbnail;
        ImageView expand;
    }


    private void showFile(File file, String extension)
    {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimeType =
                myMime.getMimeTypeFromExtension(extension);
        if(android.os.Build.VERSION.SDK_INT >=24) {
            Uri fileURI = FileProvider.getUriForFile(thisContext,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
            intent.setDataAndType(fileURI, mimeType);

        }else {
            intent.setDataAndType(Uri.fromFile(file), mimeType);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            thisContext.startActivity(intent);
        }catch (ActivityNotFoundException e){
            makeToast(getApplicationContext(), getString(R.string.no_application_to_open), WARNING, LONG);

        }

    }


    @SuppressLint("StaticFieldLeak")
    private void restoreFile(final int position){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        pd = UIComponentBuilder.getProgressDialog(thisContext, getString(R.string.restore_the_file) + " " + hiderItems.get(position).getName());
        pd.show();

        executor.execute(() -> {
            try {
                FileManager.restoreFileFromHideDirectory(getRestoreDirPath(position), filteredItems.get(position));
                if (filteredItems.get(position).isVideoOrImage(thisContext))
                    DataManager.refreshGallery(getRestoreDirPath(position), thisContext);

                final HiderItem instance = filteredItems.get(position);
                hiderItems.remove(instance);
                filteredItems.remove(instance);
                postToastMessage(getString(R.string.file_restored), SUCCESS, SHORT);
            } catch (Exception ex) {
                final String errorMessage = getString(R.string.unable_to_restore) + ex.getMessage();
                handler.post(() -> postToastMessage(errorMessage, ERROR, LONG));
            }

            handler.post(() -> {
                pd.dismiss();
                try {
                    saveSuccess(hiderItems);
                } catch (Exception ex) {
                    postToastMessage(getString(R.string.hide_db_update_error) + ": " + ex.getMessage(), ERROR, LONG);
                }
                adapter.notifyDataSetChanged();
            });
        });
    }

    private String getRestoreDirPath(int position)
    {
        return filteredItems.get(position).getOldPath();
    }

    @SuppressLint("StaticFieldLeak")
    private void hideFiles(final ArrayList<String> paths, final int TYPE)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        pd = UIComponentBuilder.getProgressDialog(thisContext, getString(R.string.hiding_in_progress));
        pd.show();

        executor.execute(() -> {
            switch(TYPE)
            {
                case MEDIA:
                    addMediaItems(paths);
                    break;

                case FILE:
                    addFileItems(paths);
                    break;
            }

            handler.post(() -> {
                pd.dismiss();
                decheckAllItems();
                hiderToolbar.getMenu().findItem(R.id.filter_for_all).setChecked(true);
                filteredItems = hiderItems;
                adapter = new MyListAdapter(thisContext, hiderItems);
                hiderListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                try {
                    saveSuccess(hiderItems);
                    postToastMessage(getString(R.string.success_hiding), SUCCESS, LONG);
                } catch (Exception ex) {
                    postToastMessage(getString(R.string.io_error) + ": " + ex.getMessage(), ERROR, LONG);
                }

                if (!AdsHelpers.isAdsFree(customSettings, AD_FREE, thisContext, thisActivity)) {
                    loadNewInterstitialAd();
                }
            });
        });

    }

    private void addFileItems(ArrayList<String> paths)
    {
        if(paths != null && paths.size() > 0)
        {
            for(String path : paths)
            {
                try
                {
                    HiderItem instance = HiderItemsManager.addFileItems(path, thisContext);
                    hiderItems.add(instance);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }catch(Exception ex)
                {
                    postToastMessage(getString(R.string.error_during_hide)+ex.getMessage(), ERROR, LONG);
                }
            }

        }
    }


    private void addMediaItems(ArrayList<String> mediaPaths)
    {
        if(mediaPaths != null && mediaPaths.size() > 0)
        {
            for(String path : mediaPaths)
            {
                try {
                    HiderItem instance = HiderItemsManager.addMediaItems(path, thisContext);
                    hiderItems.add(instance);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                }catch(Exception ex)
                {
                    postToastMessage(getString(R.string.error_during_hide)+ex.getMessage(), ERROR, LONG);
                }finally {
                    DataManager.refreshGallery(path, thisContext);
                }
            }

        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode)
        {

            case OPEN_MEDIA_PICKER:
                if(resultCode == RESULT_OK && data != null)
                {
                    ArrayList<String> mediaPaths = data.getStringArrayListExtra("result");
                    if(mediaPaths.size() > 0)
                        hideFiles(mediaPaths, MEDIA);
                }
                break;

            case MULTI_FILE_REQUEST_CODE:
                if(resultCode == RESULT_OK && data != null)
                {
                    ArrayList<String> paths = new ArrayList<>();
                    List<Uri> files = Utils.getSelectedFilesFromResult(data);
                    for(Uri u: files)
                        paths.add(Utils.getFileForUri(u).getAbsolutePath());
                    hideFiles(paths, FILE);
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }


    }


}
