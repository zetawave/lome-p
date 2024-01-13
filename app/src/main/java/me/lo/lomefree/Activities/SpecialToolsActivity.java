package me.lo.lomefree.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.lo.lomefree.Ads.AdsBuilder;
import me.lo.lomefree.Globals.GlobalValues;
import me.lo.lomefree.Interfaces.DBNames;
import me.lo.lomefree.Interfaces.Extensions;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.Interfaces.SpecialInformationsKeys;
import me.lo.lomefree.Keys.KeyUtil.KeyUtils;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.UIUtils.Graphics.AdsHelpers;
import me.lo.lomefree.UIUtils.Graphics.Alerts;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;

import static me.lo.lomefree.Globals.GlobalValues.ERROR;
import static me.lo.lomefree.Globals.GlobalValues.INFO;
import static me.lo.lomefree.Globals.GlobalValues.LONG;
import static me.lo.lomefree.Globals.GlobalValues.SHORT;
import static me.lo.lomefree.Globals.GlobalValues.SUCCESS;
import static me.lo.lomefree.Globals.GlobalValues.WARNING;
import static me.lo.lomefree.Misc.Auxiliary.TextEditing.getHtmlSpan;
import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.Utils.Files.FDManager.DataManager.setBoolPref;

public class SpecialToolsActivity extends AppCompatActivity implements SpecialInformationsKeys, SettingsParameters, Extensions, DBNames
{

    private Button backButton, deleteKeyTraces, deleteEncryptFileTraces, viewPaths, refresh, clear_clipboard, search_key_in_storage;
    private Switch prevent_screenshot, rsa_search, symm_search;
    private static final int DELETE_KEY_TRACES = 0;
    private static final int DELETE_ENCFILE_TRACES = 1;
    private TextView key_in_memory, encrypted_file_in_memory, free_space, used_space;
    private ProgressDialog pd;
    private boolean nothingFound = false;
    private static HashMap<String, String[]> specialInformations;
    private Context thisContext;
    private CustomSettings customSettings;
    private SharedPreferences.Editor prefsEditor;
    private ProgressBar information_progress;
    private static ArrayList<String> keyFunded;
    public final static int RSA_TYPE = 0;
    public final static int SYMM_TYPE = 1;
    public static final int ALL_KEY_TYPE = 2;
    public static final int NOT_KEY_CHECKED = 3;
    private boolean notKeyChecked = false;
    private AdView adView;
    private AppCompatActivity thisActivity;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thisContext = this;
        thisActivity = this;
        setContentView(R.layout.activity_special_tools);
        initSettings();
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
        AdsBuilder.loadAd(adView, thisActivity, "Special Tools Activity");
    }

    private void initSettings()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(this);
        customSettings = DataManager.getCustomSettings(prefs);
        prefsEditor = prefs.edit();
    }

    private void setComponents()
    {
        backButton = findViewById(R.id.back_button);
        deleteKeyTraces = findViewById(R.id.delete_key_traces);
        deleteEncryptFileTraces = findViewById(R.id.delete_encrypt_file_traces);
        key_in_memory = findViewById(R.id.key_in_memory);
        encrypted_file_in_memory = findViewById(R.id.encrypted_file_in_memory);
        viewPaths = findViewById(R.id.view_paths);
        refresh = findViewById(R.id.refresh);
        free_space = findViewById(R.id.free_space);
        clear_clipboard = findViewById(R.id.clear_clipboard);
        prevent_screenshot = findViewById(R.id.prevent_screenshots);
        information_progress = findViewById(R.id.information_progress);
        used_space = findViewById(R.id.used_space);
        search_key_in_storage = findViewById(R.id.search_keys_in_storage);
        symm_search = findViewById(R.id.symmetric_search_option);
        rsa_search = findViewById(R.id.rsa_search_option);

        prevent_screenshot.setChecked(!customSettings.isScreen_capture());

        retrieve_informations();
        setListener();
    }

    @SuppressLint("StaticFieldLeak")
    private void retrieve_informations()
    {
        executor.execute(() -> {
            specialInformations = DataManager.getAllSpecialInformations();

            handler.post(() -> {
                refresh.setVisibility(View.GONE);
                viewPaths.setVisibility(View.GONE);
                information_progress.setVisibility(View.VISIBLE);
            });

            handler.post(() -> {
                information_progress.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
                setInformations();
            });
        });

    }



    private void initializeKeySearch()
    {
        initKeySearch();
    }

    private void postKeySearchElaboration()
    {
        final ArrayList<String> RSAKeys = new ArrayList<>();
        final ArrayList<String> SKeys = new ArrayList<>();

        if(keyFunded != null && keyFunded.size() > 0)
        {
            for(String path : keyFunded)
            {
                if(path.endsWith(KEY_FILE_EXT))
                    SKeys.add(path);
                else
                    RSAKeys.add(path);
            }
            String message = getString(R.string.lome_found)+"\n\n"+getString(R.string.rsa_keys)+" "+RSAKeys.size()+"\n"+getString(R.string.simm_keys)+" "+SKeys.size()+"\n\n"+getString(R.string.add_to_manager);
            AlertDialog.Builder confirm = Alerts.showConfirmDialog(thisContext, getString(R.string.serach_result), message, R.drawable.searchkey);

            confirm.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("AUTOMATIC KEY ADD", "Cancellato");

                }
            });
            confirm.setPositiveButton(R.string.add_files, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    try {
                        if (RSAKeys.size() > 0)
                            KeyUtils.appendKeyInDB(thisContext, RSA_KEY_FILES_DB_NAME, RSA_KEY_INFO_DB_NAME, RSAKeys);
                        if(SKeys.size() > 0)
                            KeyUtils.appendKeyInDB(thisContext, KEY_FILES_DB_NAME, KEY_INFO_DB_NAME, SKeys);
                        makeToast(getApplicationContext(), getString(R.string.all_key_added), SUCCESS, LONG);
                    }catch(Exception ex)
                    {
                        makeToast(getApplicationContext(), getString(R.string.all_key_add_error)+ex.getMessage(), ERROR, LONG);
                    }
                }
            });
            confirm.create().show();
        }else
            makeToast(getApplicationContext(), getString(R.string.no_key_files_found), INFO, LONG);
    }

    private void setInformations()
    {
        if(!specialInformations.get(KEYS_IN_MEMORY)[0].equals("0")
                || !specialInformations.get(ENCRYPTED_FILES_IN_MEM)[0].equals("0"))
            viewPaths.setVisibility(View.VISIBLE);

        String keyInMemory = getHtmlSpan(getString(R.string.keys_in_memory), specialInformations.get(KEYS_IN_MEMORY)[0],
                "#455a64", "#f4b342");
        String encFiles = getHtmlSpan(getString(R.string.enc_files_in_memory), specialInformations.get(ENCRYPTED_FILES_IN_MEM)[0],
                "#455a64", "#f4b342");
        String freeSpace = getHtmlSpan(getString(R.string.free_space), specialInformations.get(FREE_SPACE)[0],
                "#455a64", "#f4b342");
        String usedSpace = getHtmlSpan(getString(R.string.used_memory), specialInformations.get(USED_SPACE)[0],
                "#455a64", "#f4b342");
        key_in_memory.setText(Html.fromHtml(keyInMemory), TextView.BufferType.SPANNABLE);
        encrypted_file_in_memory.setText(Html.fromHtml(encFiles), TextView.BufferType.SPANNABLE);
        free_space.setText(Html.fromHtml(freeSpace), TextView.BufferType.SPANNABLE);
        used_space.setText(Html.fromHtml(usedSpace), TextView.BufferType.SPANNABLE);

    }

    private void setListener()
    {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        deleteKeyTraces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildConfirmDialog(getString(R.string.confirm_title), getString(R.string.delete_key_traces_confirm), getString(R.string.yes), DELETE_KEY_TRACES);
            }
        });

        deleteEncryptFileTraces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildConfirmDialog(getString(R.string.confirm_title), getString(R.string.delete_encfile_confirm), getString(R.string.yes), DELETE_ENCFILE_TRACES);
            }
        });

        viewPaths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String keysinmemory = (!specialInformations.get(KEYS_IN_MEMORY)[0].equals("0") ?
                        getString(R.string.keys_)+"\n\n"+specialInformations.get(KEYS_IN_MEMORY)[1]
                        : "");
                String encfiles = (!specialInformations.get(ENCRYPTED_FILES_IN_MEM)[0].equals("0") ?
                        getString(R.string.encrypted_files)+"\n\n"+specialInformations.get(ENCRYPTED_FILES_IN_MEM)[1]
                        : "");

                if(keysinmemory.equals("") && encfiles.equals(""))
                    makeToast(getApplicationContext(), getString(R.string.no_path_of_view), INFO, SHORT);
                else
                    Alerts.showInfoDialog(getString(R.string.paths_), keysinmemory+"\n\n"+encfiles, thisContext, R.drawable.infovalues);

            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieve_informations();
            }
        });

        prevent_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                customSettings.setScreen_capture(!prevent_screenshot.isChecked());
                setBoolPref(SCREEN_CAPTURE_PREF, customSettings.isScreen_capture(), prefsEditor);
            }
        });

        clear_clipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData data = ClipData.newPlainText("", "");
                    assert clipBoard != null;
                    clipBoard.setPrimaryClip(data);
                    makeToast(getApplicationContext(), getString(R.string.clear_clipboard_success), SUCCESS, SHORT);
                }catch(Exception ex)
                {
                    Log.d("EXCEPTION CLIPBOARD", ex.getMessage());
                }
            }
        });

        search_key_in_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeKeySearch();
            }
        });
    }



    private int getTypeOfKeySearch()
    {
        if(rsa_search.isChecked() && symm_search.isChecked())
            return ALL_KEY_TYPE;
        else if(rsa_search.isChecked())
            return RSA_TYPE;
        else if(symm_search.isChecked())
            return SYMM_TYPE;
        else return NOT_KEY_CHECKED;

    }

    //Create a function that return an encrypted string with AES-256



    @SuppressLint("StaticFieldLeak")
    private void initKeySearch()
    {
        executor.execute(() -> {

            switch (getTypeOfKeySearch()) {
                case RSA_TYPE:
                    keyFunded = KeyUtils.searchKeysInStorage(GlobalValues.sdCardPath, new String[]{PUB_KEY_EXT, PRIV_KEY_EXT, RSA_KEYPAIR_EXT});
                    break;
                case SYMM_TYPE:
                    keyFunded = KeyUtils.searchKeysInStorage(GlobalValues.sdCardPath, new String[]{KEY_FILE_EXT});
                    break;
                case ALL_KEY_TYPE:
                    keyFunded = KeyUtils.searchKeysInStorage(GlobalValues.sdCardPath);
                    break;
                case NOT_KEY_CHECKED:
                    notKeyChecked = true;
                    break;
            }

            final boolean finalNotKeyChecked = notKeyChecked;

            handler.post(() -> {
                pd.dismiss();
                if (!finalNotKeyChecked)
                    postKeySearchElaboration();
                else
                    postToastMessage(getString(R.string.select_one_key_type), WARNING, LONG);
            });
        });

        handler.post(() -> {
            notKeyChecked = false;
            pd = new ProgressDialog(SpecialToolsActivity.this);
            pd.setTitle("");
            pd.setMessage(getString(R.string.search_work));
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.setButton(DialogInterface.BUTTON_NEGATIVE,getString(R.string.cancel), (dialog, which) -> {
                dialog.dismiss();
                cancelAsyncOperation();
            });
            pd.show();
        });
    }

    private void postToastMessage(final String message, final int mode, final int lenght) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> makeToast(getApplicationContext(),message, mode, lenght));
    }

    private void buildConfirmDialog(String title, String message, String confirm, final int operation)
    {
        AlertDialog.Builder builder = Alerts.showConfirmDialog(this, title, message, R.drawable.ic_question_answer_black_24dp);
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> Log.d("SPECIAL", "OPERATION CANCELED"))
                .setPositiveButton(confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(operation)
                        {
                            case DELETE_KEY_TRACES:
                                buildOperation(operation, getString(R.string.remove_key_traces_operation));
                                break;

                            case DELETE_ENCFILE_TRACES:
                                buildOperation(operation, getString(R.string.delete_ecnrypt_file_operation));
                                break;
                        }
                    }
                }).create().show();
    }

    @SuppressLint("StaticFieldLeak")
    private void buildOperation(final int operation, final String message)
    {
        executor.execute(() -> {
            boolean nothingFound = false;

            switch (operation) {
                case DELETE_KEY_TRACES:
                    doDeleteKeyTraces();
                    break;

                case DELETE_ENCFILE_TRACES:
                    doDeleteEncFileTraces();
                    break;
            }

            final boolean finalNothingFound = nothingFound;

            handler.post(() -> {
                pd.dismiss();
                if (finalNothingFound) {
                    switch (operation) {
                        case DELETE_ENCFILE_TRACES:
                            makeToast(getApplicationContext(), getString(R.string.no_encrypted_files_found), INFO, LONG);
                            break;
                        case DELETE_KEY_TRACES:
                            makeToast(getApplicationContext(), getString(R.string.no_key_files_found), INFO, LONG);
                            break;
                    }
                } else {
                    makeToast(getApplicationContext(), getString(R.string.operation_completed), SUCCESS, LONG);
                }
            });
        });

        handler.post(() -> {
            nothingFound = false;
            pd = new ProgressDialog(SpecialToolsActivity.this);
            pd.setTitle("");
            pd.setMessage(message);
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    cancelAsyncOperation();
                }
            });
            pd.show();
        });

    }

    private void cancelAsyncOperation()
    {
        executor.shutdownNow();
    }

    private void doDeleteEncFileTraces()
    {
        ArrayList<String> encFiles = FileManager.searchEncryptedFileInStorage(GlobalValues.sdCardPath);
        if(encFiles != null && encFiles.size() > 0)
        {
            File file;
            for(String instance : encFiles)
            {
                file = new File(instance);
                try {
                    FileManager.deleteWithRandomOverrides(file, 2);
                    //FileManager.deleteWithVSITRAlgorithm(file);
                } catch (IOException e) {
                    Log.d("ERRORE ELIMINAZIONE", instance+": "+e.getMessage());
                }
            }
        }else
            nothingFound = true;
    }

    private void doDeleteKeyTraces()
    {
        ArrayList<String> keysInStorage = KeyUtils.searchKeysInStorage(GlobalValues.sdCardPath);
        if(keysInStorage != null && keysInStorage.size() > 0)
        {
            File file;
            for(String instance : keysInStorage)
            {
                file = new File(instance);
                try {
                    FileManager.deleteWithVSITRAlgorithm(file);
                } catch (IOException e) {
                    Log.d("ERRORE ELIMINAZIONE", instance+": "+e.getMessage());
                }
            }
        }else
            nothingFound = true;

    }
}


