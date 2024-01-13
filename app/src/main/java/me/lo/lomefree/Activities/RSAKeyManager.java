package me.lo.lomefree.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.nononsenseapps.filepicker.Utils;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.lo.lomefree.Ads.AdsBuilder;
import me.lo.lomefree.Interfaces.CipherAlgorithm;
import me.lo.lomefree.Interfaces.DBNames;
import me.lo.lomefree.Interfaces.Extensions;
import me.lo.lomefree.Interfaces.RRCodes;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.Keys.KeyUtil.KeyFilesDB;
import me.lo.lomefree.Keys.Entities.RSAKeyFile;
import me.lo.lomefree.Model.Cryptography.AsimmetricCiphers.RSACipher;
import me.lo.lomefree.Model.Cryptography.SimmetricKeyCiphers.SingleFileCipher;
import me.lo.lomefree.Model.HashUtils.HashGenerator;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.SettingsUtils.ParticularSetting;
import me.lo.lomefree.UIUtils.Graphics.AdsHelpers;
import me.lo.lomefree.UIUtils.Graphics.Alerts;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;
import me.lo.lomefree.Utils.Misc.PermissionChecker;
import me.lo.lomefree.Utils.Files.FDManager.UriDataResolver;

import static me.lo.lomefree.Globals.GlobalValues.ERROR;
import static me.lo.lomefree.Globals.GlobalValues.INFO;
import static me.lo.lomefree.Globals.GlobalValues.LONG;
import static me.lo.lomefree.Globals.GlobalValues.SHORT;
import static me.lo.lomefree.Globals.GlobalValues.SUCCESS;
import static me.lo.lomefree.Globals.GlobalValues.WARNING;
import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.UIUtils.Graphics.UIHelpers.showRSAKeyFileManagerTapWizard;
import static me.lo.lomefree.UIUtils.UISecurity.WindowSecurity.initSecurityFlags;

public class RSAKeyManager extends AppCompatActivity implements DBNames, CipherAlgorithm, Extensions, RRCodes, SettingsParameters
{

    private ListView rsa_key_list_view;
    private MyListAdapter adapter;
    private ArrayList<String[]> keyInfo;
    private List<RSAKeyFile> rsaKeyFiles;
    private Button makeKey, importKey;
    private final EditText[] et  = new EditText[2];
    // --Commented out by Inspection (18/07/18 19.09):private AppCompatActivity thisActivity;
    private Context thisContext;
    private CustomSettings customSettings;
    private RSAKeyFile tempRsaKey;
    // --Commented out by Inspection (18/07/18 19.10):private HashMap<Integer, Object> rsaKeyPair;
    private Toolbar toolbar;
    private final int IMPORT_MODE = 0;
    private final int EXPORT_MODE = 1;
    private EditText decDbKey;
    private final EditText [] dbKey = new EditText[2];
    private String importExportDBPass;
    private boolean viewMode = true;
    private File tempFile;
    private static ProgressDialog pd;
    private AdView adView;
    private AppCompatActivity thisActivity;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = this;
        thisActivity = this;
        getCustomSettings();
        initSecurityFlags(customSettings, getWindow());
        setContentView(R.layout.activity_rsakey_manager);
        verifyFirstAccess();
        setComponents();
        getInteractionMode();
        handleNewKey();
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
        AdsBuilder.loadAd(adView, thisActivity, "RSA Key File Manager");
    }

    private void getCustomSettings()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(this);
        customSettings = DataManager.getCustomSettings(prefs);
    }


    private void verifyFirstAccess()
    {

        ParticularSetting first_access_ps = customSettings.getOtherPreference(FIRST_RSA_MANAGER_ACCESS);
        Boolean first_access;
        if(first_access_ps != null)
            first_access = (Boolean) first_access_ps.getValue();
        else {
            first_access_ps = new ParticularSetting();
            first_access_ps.setPreference(FIRST_RSA_MANAGER_ACCESS);
            first_access_ps.setValue(true);
            customSettings.setOtherPreference(FIRST_RSA_MANAGER_ACCESS, first_access_ps);
            first_access = true;
        }
        if(first_access)
        {
            showRSAKeyFileManagerTapWizard(this, customSettings, first_access_ps);
        }

    }

    private void handleNewKey()
    {
        try {
            Uri newKey = Uri.parse(getIntent().getExtras().getString("NEW_KEY"));
            if (newKey != null)
            {
                Log.d("KEY ", "Key rinvenuta");
                saveKeyUriDialog(newKey);
            }
        }catch(NullPointerException ex)
        {
            Log.d("NO NEW KEY", "Nessuna chiave nuova");
        }
    }



    private void setComponents()
    {
        rsa_key_list_view = findViewById(R.id.rsa_key_list_view);
        @SuppressLint("InflateParams") View child = getLayoutInflater().inflate(R.layout.empty_keymanager_view, null);
        child.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
        ((ViewGroup)rsa_key_list_view.getParent()).addView(child);
        rsa_key_list_view.setEmptyView(child);

        loadData();

        makeKey = findViewById(R.id.makeKey);
        importKey = findViewById(R.id.add_new_key);
        toolbar = findViewById(R.id.rsakeymanager_toolbar);
        toolbar.inflateMenu(R.menu.rsa_key_manager_tool_menu);
        setListeners();
    }

    private void getInteractionMode()
    {
        viewMode = getIntent().getBooleanExtra("VIEW_MODE", true);
    }

    private void setListeners()
    {
        makeKey.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v)
            {
                makeNewKeyDialog();
            }
        });

        importKey.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                importNewKeyDialog();
                //IMPORT KEY
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.export_key_db:
                        if (rsaKeyFiles.size() > 0)
                            showDbInputPassphrase(EXPORT_MODE);
                        else
                            makeToast(getApplicationContext(), getString(R.string.no_key_present_for_export), WARNING, LONG);
                        //exportKeyDB();
                        break;
                    case R.id.import_key_db:
                        showDbInputPassphrase(IMPORT_MODE);
                        //importKeyDB();
                        break;
                    case R.id.clear_all:
                        deleteAll();
                        break;
                    case R.id.info:
                        Alerts.showInfoDialog(getString(R.string.information), getString(R.string.key_manager_info), thisContext, R.drawable.ic_info_outline_black_24dp);
                        break;
                }

                return false;
            }
        });

    }

    private void showDbInputPassphrase(final int mode)
    {
        AlertDialog.Builder builder;
        if(mode == IMPORT_MODE) {
            decDbKey = new EditText(thisContext);
            builder = Alerts.makeSingleEditTextDialog(thisContext, getString(R.string.password), getString(R.string.block_pass_db_message), getString(R.string.insert), decDbKey, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        else
            builder = Alerts.makeDoubleEditTextDialogWithNameFiltered(thisContext, getString(R.string.password), getString(R.string.db_export_block_message), getString(R.string.insert),getString(R.string.re_insert),dbKey,InputType.TYPE_CLASS_TEXT |  InputType.TYPE_TEXT_VARIATION_PASSWORD);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    switch (mode)
                    {
                        case IMPORT_MODE:
                            if(isPossibleToProceedImportDB()) {
                                importExportDBPass = String.valueOf(decDbKey.getText());
                                importExportDBPass = new String(new HashGenerator(CipherAlgorithm.SHA256).getDigestKey(importExportDBPass));
                                importKeyDB();
                            }
                            else
                                makeToast(thisContext, getString(R.string.enter_a_correct_password), WARNING, LONG);
                            break;
                        case EXPORT_MODE:
                            if(isPossibleToProocedExportDB()) {
                                importExportDBPass = String.valueOf(dbKey[0].getText());
                                importExportDBPass = new String(new HashGenerator(CipherAlgorithm.SHA256).getDigestKey(importExportDBPass));
                                exportKeyDB(importExportDBPass);
                            }else
                                makeToast(thisContext, getString(R.string.enter_a_correct_password), WARNING, LONG);
                            break;
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("EXPORT DB", "PASSWORD CANCELED");
                    }
                });
        builder.show();
    }


    private void deleteAll()
    {
        if(rsaKeyFiles.size() > 0)
        {
            AlertDialog.Builder builder = Alerts.showConfirmDialog(thisContext, getString(R.string.confirm_title), getString(R.string.delete_all_key_confirm_message));
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    rsaKeyFiles.clear();
                    keyInfo.clear();
                    if(saveObjectSuccess())
                        adapter.notifyDataSetChanged();
                }
            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("EXPORT DB", "CANCELED");
                        }
                    });
            builder.show();
        }
    }



    private void saveKeyUriDialog(final Uri uri)
    {
        AlertDialog.Builder builder = Alerts.makeDoubleEditTextDialogWithNameFiltered(thisContext, getString(R.string.import_key_title), getString(R.string.import_key_message), getString(R.string.import_key_hint1), getString(R.string.import_key_hint2), et, InputType.TYPE_CLASS_TEXT);

        builder.setCancelable(false);

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveKey(uri);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialog, int whichButton)
            {
                Log.d("ABORTED", "NO KEY FILE IMPORTATO");
            }
        });

        builder.show();
    }

    private void saveKey(Uri uri)
    {
        String name = String.valueOf(et[0].getText());
        String description = String.valueOf(et[1].getText());
        description = (description == null ? "" : description);


        if(name.length() == 0)
            makeToast(getApplicationContext(),getString(R.string.name_obligation), WARNING, Toast.LENGTH_LONG);
        else {
            try {
                tempRsaKey = new RSAKeyFile();
                tempRsaKey.setName(name);
                tempRsaKey.setDescription(description);
                String path = UriDataResolver.getRealPathFromURI(uri, getContentResolver());
                tempRsaKey.setPath(path);

                HashMap<Integer, Object> keys = new HashMap<>();
                boolean pub = path.endsWith(PUB_KEY_EXT);
                boolean priv = path.endsWith(PRIV_KEY_EXT);

                if (pub || priv) {
                    keys.put((priv ? RSAKeyFile.TYPE_PRIVATE : RSAKeyFile.TYPE_PUBLIC), (priv ? RSACipher.readPrivateKey(tempRsaKey.getPath()) : RSACipher.readPublicKey(tempRsaKey.getPath())));
                    tempRsaKey.setKeys(keys);
                    tempRsaKey.setTypes();
                } else {
                    tempRsaKey = (RSAKeyFile) DataManager.readObject(tempRsaKey.getPath());
                }

                rsaKeyFiles.add(tempRsaKey);
                keyInfo.add(new String[]{tempRsaKey.getName(), tempRsaKey.getDescription()});
                tempRsaKey = null;

                if (saveObjectSuccess())
                {
                    reloadData();
                    makeToast(getApplicationContext(), getString(R.string.key_added), SUCCESS, SHORT);
                }

            } catch (Exception e) {
                makeToast(getApplicationContext(), getString(R.string.read_key_import_error) + " " + e.getMessage(), ERROR, LONG);
            }


            if (saveObjectSuccess())
            {
                reloadData();
                makeToast(getApplicationContext(), getString(R.string.key_added), SUCCESS, SHORT);
            }
        }
    }

    private void importKeyDB()
    {
        DataManager.getSingleFileChooser(thisContext, this, IMPORT_KEY_DB_REQUEST_CODE);
    }

    private void exportKeyDB(final String pass)
    {
        if(rsaKeyFiles.size() > 0)
        {
            AlertDialog.Builder builder = Alerts.showConfirmDialog(thisContext, getString(R.string.confirm_title), getString(R.string.export_key_confirm_message));
            builder.setPositiveButton(R.string.export, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String path = customSettings.getCustom_save_path().concat(File.separator).concat(SINGLE_RSA_DB);
                    try {
                        KeyFilesDB.exportRSAKeyDB(rsaKeyFiles, keyInfo, path);
                        SingleFileCipher.encryptFile(new File(path), pass, true);
                        exportDBSuccess();
                    } catch (Exception e) {
                        makeToast(getApplicationContext(), getString(R.string.export_key_error)+" "+e.getMessage(), ERROR, LONG);
                    }

                }
            })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("EXPORT DB", "CANCELED");
                        }
                    });
            builder.show();
        }else
            makeToast(getApplicationContext(), getString(R.string.no_key_found_for_export), WARNING, LONG);

    }


    private void exportDBSuccess()
    {
        makeToast(getApplicationContext(), getString(R.string.export_success_database), SUCCESS, LONG);
    }

    private boolean isPossibleToProocedExportDB()
    {
        String pass1 = String.valueOf(dbKey[0].getText());
        String pass2 = String.valueOf(dbKey[1].getText());
        return (pass1 != null && pass2 != null && !(pass1.equals("") || pass1.equals("")) && pass1.equals(pass2));
    }

    private boolean isPossibleToProceedImportDB()
    {
        String pass = String.valueOf(decDbKey.getText());
        return (pass != null && !pass.equals(""));
    }


    private void importNewKeyDialog()
    {
        AlertDialog.Builder builder = Alerts.makeDoubleEditTextDialogWithNameFiltered(thisContext, getString(R.string.import_key_title), getString(R.string.import_key_message), getString(R.string.import_key_hint1), getString(R.string.import_key_hint2), et, InputType.TYPE_CLASS_TEXT);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.choice, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = String.valueOf(et[0].getText());
                String description = String.valueOf(et[1].getText());

                description = (description == null ? "" : description);

                if(name.length() == 0)
                    makeToast(getApplicationContext(),getString(R.string.name_obligation), WARNING, Toast.LENGTH_LONG);
                else
                {
                    tempRsaKey = new RSAKeyFile();
                    tempRsaKey.setName(name);
                    tempRsaKey.setDescription(description);
                    startKeyChooser();
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialog, int whichButton)
            {
                Log.d("ABORTED", "NO KEY FILE IMPORTATO");
            }
        });
        builder.show();
    }


    private void startKeyChooser()
    {
        if(PermissionChecker.verifyStoragePermissionGranted(this)) {
            DataManager.getSingleFileChooser(getApplicationContext(), this);
        }
    }




    private void makeNewKeyDialog()
    {
        final AlertDialog.Builder builder = Alerts.makeDoubleEditTextDialogWithNameFiltered(this, getString(R.string.New), getString(R.string.rsa_new_key_message), getString(R.string.import_key_hint1), getString(R.string.import_key_hint2), et, InputType.TYPE_CLASS_TEXT);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.create, (dialog, whichButton) -> {
            String name = String.valueOf(et[0].getText());
            String description = String.valueOf(et[1].getText());

            description = (description == null ? "" : description);

            if(name.length() == 0)
                makeToast(getApplicationContext(),getString(R.string.name_obligation), WARNING, Toast.LENGTH_LONG);
            else
            {
                try
                {
                    makeNewKeyPair(name, description, dialog);
                }catch(Exception ex)
                {
                    makeToast(getApplicationContext(), getString(R.string.rsa_manager_make_key_error)+" "+ex.getMessage(), ERROR, LONG);
                }

            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, whichButton) -> Log.d("ABORTED", "NO KEY FILE IMPORTATO"));
        builder.show();
    }

    @SuppressLint("StaticFieldLeak")
    private void makeNewKeyPair(final String name, final String description, final DialogInterface dialog) {

        dialog.dismiss();

        pd = new ProgressDialog(RSAKeyManager.this);
        pd.setTitle("");
        pd.setMessage(getString(R.string.create_rsa_key_pair));
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

        executor.execute(() -> {
            RSAKeyFile rsaKeyFile = new RSAKeyFile();
            HashMap<Integer, Object> keys = new HashMap<>();
            try {
                KeyPair keyPair = RSACipher.generateRSAKeyPair(RSACipher.RSA_KEY_4096);
                keys.put(RSAKeyFile.TYPE_PRIVATE, keyPair.getPrivate());
                keys.put(RSAKeyFile.TYPE_PUBLIC, keyPair.getPublic());
                rsaKeyFile.setKeys(keys);
                rsaKeyFile.setTypes();
                rsaKeyFile.setName(name);
                rsaKeyFile.setDescription(description);
                rsaKeyFiles.add(rsaKeyFile);
                keyInfo.add(new String[]{rsaKeyFile.getName(), rsaKeyFile.getDescription()});
            } catch (Exception ex) {
                final String errorMessage = getString(R.string.rsa_manager_make_key_error) + " " + ex.getMessage();
                handler.post(() -> makeToast(getApplicationContext(), errorMessage, ERROR, LONG));
            }

            handler.post(() -> {
                pd.dismiss();
                adapter.notifyDataSetChanged();
                if (saveObjectSuccess()) {
                    makeToast(getApplicationContext(), getString(R.string.key_added), SUCCESS, SHORT);
                } else {
                    makeToast(getApplicationContext(), getString(R.string.key_save_error), SUCCESS, SHORT);
                }
            });
        });
    }

    private void cancelAsyncOperation()
    {
        executor.shutdown();
    }

    private void loadData()
    {
        keyInfo = new ArrayList<>();
        rsaKeyFiles = new ArrayList<>();

        ArrayList<String[]> temp_key_info;
        List<RSAKeyFile> temp_keyFiles;


        temp_keyFiles = (List<RSAKeyFile>) DataManager.loadPrivateObject(getApplicationContext(), RSA_KEY_FILES_DB_NAME);
        temp_key_info = (ArrayList<String[]>) DataManager.loadPrivateObject(getApplicationContext(), RSA_KEY_INFO_DB_NAME);

        rsaKeyFiles = (temp_keyFiles == null ? new ArrayList<RSAKeyFile>() : temp_keyFiles);
        keyInfo = (temp_key_info == null ? new ArrayList<String[]>() : temp_key_info);

        adapter = new MyListAdapter(this, keyInfo);
        rsa_key_list_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void reloadData()
    {
        adapter = new MyListAdapter(this, keyInfo);
        rsa_key_list_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private boolean saveObjectSuccess()
    {
        return DataManager.savePrivateObject(getApplicationContext(), RSA_KEY_FILES_DB_NAME, rsaKeyFiles) &&
                DataManager.savePrivateObject(getApplicationContext(), RSA_KEY_INFO_DB_NAME, keyInfo);
    }


    private class MyListAdapter extends ArrayAdapter<String []> implements Extensions
    {
        private final int layout;

        MyListAdapter(@NonNull Context context, @NonNull List<String[]> objects) {
            super(context, R.layout.rsa_key_item, objects);
            layout = R.layout.rsa_key_item;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder;

            if(convertView == null)
            {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                final ViewHolder viewHolder = new ViewHolder();

                viewHolder.key_title = convertView.findViewById(R.id.key_name);
                viewHolder.get_private_key = convertView.findViewById(R.id.get_private_key);
                viewHolder.get_public_key = convertView.findViewById(R.id.get_public_key);
                viewHolder.popupMenu = convertView.findViewById(R.id.popupMenu);
                viewHolder.key_description = convertView.findViewById(R.id.key_description);

                mainViewHolder = viewHolder;

                convertView.setTag(mainViewHolder);

                final View finalConvertView = convertView;

            }else
            {
                mainViewHolder = (ViewHolder) convertView.getTag();
                adapter.notifyDataSetChanged();
            }
            mainViewHolder.key_title.setText(getItem(position)[0]);
            mainViewHolder.key_description.setText(getItem(position)[1]);

            setTypes(mainViewHolder, position);
            setButtonListeners(mainViewHolder, position);

            return convertView;
        }

        private void setTypes(ViewHolder mainViewHolder, int position)
        {
            if(rsaKeyFiles != null && rsaKeyFiles.size() > 0)
            {
                RSAKeyFile instance = rsaKeyFiles.get(position);
                switch(instance.getType())
                {
                    case RSAKeyFile.TYPE_ALL:
                        mainViewHolder.get_private_key.setVisibility(View.VISIBLE);
                        mainViewHolder.get_public_key.setVisibility(View.VISIBLE);
                        break;
                    case RSAKeyFile.TYPE_PRIVATE:
                        mainViewHolder.get_private_key.setVisibility(View.VISIBLE);
                        break;

                    case RSAKeyFile.TYPE_PUBLIC:
                        mainViewHolder.get_public_key.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }

        private void setButtonListeners(final ViewHolder mainViewHolder, final int position)
        {

            mainViewHolder.get_public_key.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(!viewMode)
                    {
                        HashMap<Integer, RSAKeyFile> keymap = new HashMap<>();
                        keymap.put(RSAKeyFile.TYPE_PUBLIC, rsaKeyFiles.get(position));
                        Intent thisIntent = getIntent().putExtra("RSA_KEY", keymap);
                        setResult(RESULT_OK, thisIntent);
                        finish();
                    }else
                        makeToast(getApplicationContext(), getString(R.string.thereis_public_key), INFO, SHORT);
                }
            });

            mainViewHolder.get_private_key.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!viewMode)
                    {
                        HashMap<Integer, RSAKeyFile> keymap = new HashMap<>();
                        keymap.put(RSAKeyFile.TYPE_PRIVATE, rsaKeyFiles.get(position));
                        Intent thisIntent = getIntent().putExtra("RSA_KEY", keymap);
                        setResult(RESULT_OK, thisIntent);
                        finish();
                    }else
                        makeToast(getApplicationContext(), getString(R.string.thereis_private_key), INFO, SHORT);
                }
            });

            mainViewHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popup = new PopupMenu(thisContext, mainViewHolder.popupMenu);
                    popup.getMenuInflater().inflate(R.menu.rsa_key_manager_popup_menu,popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId())
                            {
                                case R.id.export_key:
                                    exportSingleKey(position);
                                    break;

                                case R.id.export_key_pair:
                                    exportKeyPair(position);
                                    break;

                                case R.id.share:
                                    shareKey(position);
                                    break;

                                case R.id.edit:
                                    editKey(position);
                                    break;

                                case R.id.delete_key:
                                    deleteKey(position);
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });
        }
    }

    private void editKey(final int position)
    {
        AlertDialog.Builder builder = Alerts.makeDoubleEditTextDialogWithNameFiltered(thisContext, getString(R.string.modify), getString(R.string.new_key_values_edit), getString(R.string.import_key_hint1), getString(R.string.import_key_hint2), et, InputType.TYPE_CLASS_TEXT);
        builder.setCancelable(false);
        try
        {
            et[0].setText(rsaKeyFiles.get(position).getName());
            et[1].setText(rsaKeyFiles.get(position).getDescription());
        }catch(Exception ex)
        {
            Log.d("EDIT KEY", ex.getCause()+": "+ex.getMessage());
        }
        builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = String.valueOf(et[0].getText());
                String description = String.valueOf(et[1].getText());

                description = (description == null ? "" : description);

                if(name.length() == 0)
                    makeToast(getApplicationContext(),getString(R.string.name_obligation), WARNING, Toast.LENGTH_LONG);
                if(!name.equals(rsaKeyFiles.get(position).getName()) || !description.equals(rsaKeyFiles.get(position).getDescription()))
                {
                    RSAKeyFile old = rsaKeyFiles.get(position);
                    String [] oldinfo = keyInfo.get(position);

                    rsaKeyFiles.get(position).setName(name);
                    rsaKeyFiles.get(position).setDescription(description);
                    keyInfo.set(position, new String[]{name, description});
                    if (saveObjectSuccess()) {
                        makeToast(getApplicationContext(), getString(R.string.key_modified_success), SUCCESS, SHORT);
                    } else {
                        makeToast(getApplicationContext(), getString(R.string.save_error), ERROR, SHORT);
                        rsaKeyFiles.set(position, old);
                        keyInfo.set(position, oldinfo);
                    }
                    adapter.notifyDataSetChanged();

                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialog, int whichButton)
            {
                Log.d("ABORTED", "NO KEY FILE EDITED");
            }
        });
        builder.show();
    }


    private void shareKey(final int position)
    {
        tempFile = null;
        final String customPath = customSettings.getCustom_save_path().concat(File.separator);

        AlertDialog.Builder builder = Alerts.showConfirmDialog(this, getString(R.string.choice_the_key), getString(R.string.rsa_key_share));
        builder.setPositiveButton(R.string.public_, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String tempPublicKeyPath = customPath.concat(keyInfo.get(position)[0].concat(PUB_KEY_EXT));
                DataManager.saveObject(tempPublicKeyPath, rsaKeyFiles.get(position).getPublicKey());
                tempFile = new File(tempPublicKeyPath);
                sendShareKeyIntent(tempFile);

            }
        })
                .setNegativeButton(R.string.private_, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String tempPublicKeyPath = customPath.concat(keyInfo.get(position)[0].concat(PRIV_KEY_EXT));
                        DataManager.saveObject(tempPublicKeyPath, rsaKeyFiles.get(position).getPrivateKey());
                        tempFile = new File(tempPublicKeyPath);
                        sendShareKeyIntent(tempFile);
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("CONDIVISIONE ", "CONDIVISIONE ANNULLATA");
                    }
                });
        builder.show();
    }



    private void sendShareKeyIntent(File tempFile)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
        sendIntent.setType("*/*");
        startActivityForResult(Intent.createChooser(sendIntent, getString(R.string.share_the_key)), KEY_SHARE_REQUEST_CODE);
    }

    private void exportSingleKey(final int position)
    {
        final String customPath = customSettings.getCustom_save_path().concat(File.separator);

        AlertDialog.Builder alert = Alerts.showConfirmDialog(this, getString(R.string.choice_the_key), getString(R.string.rsa_key_tipology));
        alert.setPositiveButton(R.string.public_, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String publicKeyPath = customPath.concat(keyInfo.get(position)[0].concat(PUB_KEY_EXT));
                DataManager.saveObject(publicKeyPath, rsaKeyFiles.get(position).getPublicKey());
                makeToast(getApplicationContext(), getString(R.string.export_key_rsa_success), SUCCESS, LONG);
            }
        })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("EXPORT RSA", "CANCELED");
                    }
                })
                .setNegativeButton(R.string.private_, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String privKeyPath = customPath.concat(keyInfo.get(position)[0].concat(PRIV_KEY_EXT));
                        DataManager.saveObject(privKeyPath, rsaKeyFiles.get(position).getPrivateKey());
                        makeToast(getApplicationContext(), getString(R.string.export_key_rsa_success), SUCCESS, LONG);

                    }
                }).create().show();


    }

    private void exportKeyPair(final int position)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.export)
                .setMessage(R.string.export_key_pair_question)
                .setIcon(R.drawable.ic_question_answer_black_24dp)
                .setPositiveButton(R.string.export, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String customPath = customSettings.getCustom_save_path().concat(File.separator);
                        String keypairPath = customPath.concat(keyInfo.get(position)[0]).concat(RSA_KEYPAIR_EXT);

                        try {
                            DataManager.saveObject(keypairPath, rsaKeyFiles.get(position));
                            makeToast(getApplicationContext(), getString(R.string.save_new_rsa_keys_success), SUCCESS, LONG);
                        }catch(Exception ex)
                        {
                            makeToast(getApplicationContext(), getString(R.string.rsa_export_error)+" "+ex.getMessage(), ERROR, LONG);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("INFO_DEBUG", "Item non eliminato");
                    }
                })
                .show();
    }

    private void deleteKey(final int position)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.key_deletion)
                .setMessage(R.string.key_deletion_message)
                .setIcon(R.drawable.ic_delete_forever_black_24dp)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        rsaKeyFiles.remove(position);
                        keyInfo.remove(position);
                        if(saveObjectSuccess())
                        {
                            makeToast(getApplicationContext(), getString(R.string.key_deletion_success), SUCCESS, SHORT);
                        }else
                            makeToast(getApplicationContext(), getString(R.string.key_deletion_error), SUCCESS, SHORT);
                        adapter = new MyListAdapter(thisContext, keyInfo);
                        rsa_key_list_view.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("INFO_DEBUG", "Item non eliminato");
                    }
                })
                .show();
    }

    class ViewHolder
    {
        TextView key_title,key_description, get_private_key, get_public_key;
        //Button popupMenu, share_key, export_keypair, delete_key;
        Button popupMenu;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode)
        {
            case SINGLE_FILE_REQUEST_CODE:

                if(resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<String> keyPath = new ArrayList<>();
                    List<Uri> files = Utils.getSelectedFilesFromResult(data);
                    for (Uri u : files)
                        keyPath.add(Utils.getFileForUri(u).getAbsolutePath());

                    if (keyPath.size() == 1 && (keyPath.get(0).endsWith(PRIV_KEY_EXT) || keyPath.get(0).endsWith(PUB_KEY_EXT) || keyPath.get(0).endsWith(RSA_KEYPAIR_EXT))) {
                        tempRsaKey.setPath(keyPath.get(0));

                        try {
                            HashMap<Integer, Object> keys = new HashMap<>();
                            boolean pub = keyPath.get(0).endsWith(PUB_KEY_EXT);
                            boolean priv = keyPath.get(0).endsWith(PRIV_KEY_EXT);

                            if (pub || priv) {
                                keys.put((priv ? RSAKeyFile.TYPE_PRIVATE : RSAKeyFile.TYPE_PUBLIC), (priv ? RSACipher.readPrivateKey(tempRsaKey.getPath()) : RSACipher.readPublicKey(tempRsaKey.getPath())));
                                tempRsaKey.setKeys(keys);
                                tempRsaKey.setTypes();
                            }
                            else {
                                tempRsaKey = (RSAKeyFile) DataManager.readObject(tempRsaKey.getPath());
                            }

                            rsaKeyFiles.add(tempRsaKey);
                            keyInfo.add(new String[]{tempRsaKey.getName(), tempRsaKey.getDescription()});
                            tempRsaKey = null;

                        } catch (IOException | ClassNotFoundException e) {
                            makeToast(getApplicationContext(), getString(R.string.read_key_import_error) + " " + e.getMessage(), ERROR, LONG);
                        }


                        if (saveObjectSuccess()) {
                            {
                                reloadData();
                                makeToast(getApplicationContext(), getString(R.string.key_added), SUCCESS, SHORT);
                            }
                        } else
                            makeToast(getApplicationContext(), getString(R.string.key_save_error), SUCCESS, SHORT);
                    } else
                        makeToast(getApplicationContext(), getString(R.string.wrong_extension_key_file), WARNING, LONG);
                }
                break;

            case IMPORT_KEY_DB_REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK && data != null)
                {
                    ArrayList<String> dbPath = new ArrayList<>();
                    List<Uri> dbUris = Utils.getSelectedFilesFromResult(data);
                    for(Uri u: dbUris)
                        dbPath.add(Utils.getFileForUri(u).getAbsolutePath());

                    if(dbPath.size() == 1 && dbPath.get(0).endsWith(ENCRYPTED_LOME_KEY_DB_EXT))
                    {
                        String path = dbPath.get(0);
                        try {
                            SingleFileCipher.decryptFile(new File(path), importExportDBPass, false);
                            String newPath = path.substring(0,path.lastIndexOf("."));
                            HashMap<String, Object> dbMap = KeyFilesDB.readRSAKeyDB(newPath);
                            if(dbMap != null && dbMap.size() > 0)
                            {
                                rsaKeyFiles = (List<RSAKeyFile>) dbMap.get(KeyFilesDB.KEYS_CODE);
                                keyInfo = (ArrayList<String[]>) dbMap.get(KeyFilesDB.INFOS_CODE);
                                FileManager.secureDeleteFile(new File(newPath), 1, thisContext);
                                if(saveObjectSuccess())
                                    reloadData();
                                else
                                    throw new Exception("INTERNAL IMPORT ERROR");
                            }
                        } catch (Exception e) {
                            makeToast(getApplicationContext(), getString(R.string.import_key_db_error)+" "+e.getMessage(), ERROR, LONG);
                        }
                    }
                }
                break;

            case KEY_SHARE_REQUEST_CODE:
                if(tempFile != null && tempFile.exists())
                    FileManager.deleteFile(tempFile);
                tempFile = null;
                break;


            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
