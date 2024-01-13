package me.lo.lomefree.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.zxing.WriterException;
import com.nononsenseapps.filepicker.Utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.lo.lomefree.Ads.AdsBuilder;
import me.lo.lomefree.Interfaces.ChooserModes;
import me.lo.lomefree.Interfaces.CipherAlgorithm;
import me.lo.lomefree.Interfaces.DBNames;
import me.lo.lomefree.Interfaces.Extensions;
import me.lo.lomefree.Interfaces.RRCodes;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.Interfaces.UriSchemes;
import me.lo.lomefree.Keys.KeyUtil.KeyFilesDB;
import me.lo.lomefree.Model.Cryptography.SimmetricKeyCiphers.SingleFileCipher;
import me.lo.lomefree.Model.HashUtils.HashGenerator;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.SettingsUtils.ParticularSetting;
import me.lo.lomefree.UIUtils.Graphics.AdsHelpers;
import me.lo.lomefree.UIUtils.Graphics.Alerts;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Keys.Entities.KeyFile;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;
import me.lo.lomefree.Utils.Misc.PermissionChecker;
import me.lo.lomefree.Utils.Files.FDManager.QRCodeUtils;


import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.Globals.GlobalValues.*;
import static me.lo.lomefree.UIUtils.Graphics.UIHelpers.showKeyFileManagerTapWizard;
import static me.lo.lomefree.UIUtils.UISecurity.WindowSecurity.initSecurityFlags;
import static me.lo.lomefree.Utils.Files.FDManager.UriDataResolver.getRealValueFromUri;


public class KeyFileManager extends AppCompatActivity implements  Extensions, ChooserModes, RRCodes, UriSchemes, DBNames, SettingsParameters
{

    private Button autotest_all, add_new_key, createKey;
    private ListView key_list_view;
    private ArrayList<String []> key_info = new ArrayList<>();
    private MyListAdapter adapter;
    private List <KeyFile> keyFiles = new ArrayList<>();
    private boolean viewMode = false;
    private boolean encryptMode = false;
    private final EditText[] et = new EditText[2];
    private final EditText [] dbKey = new EditText[2];
    private Context thisContext;
    private CustomSettings customSettings;
    private File tempFile;
    private KeyFile tempKeyFile;
    private Toolbar toolbar;
    private final int IMPORT_MODE = 0;
    private final int EXPORT_MODE = 1;
    private String importExportDBPass;
    private EditText decDbKey;
    private AdView adView;
    private AppCompatActivity thisActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thisContext = this;
        thisActivity = this;
        getCustomSettings();
        initSecurityFlags(customSettings, getWindow());
        setContentView(R.layout.activity_key_file_manager);
        verifyFirstAccess();
        getInteractionMode();
        setComponents();
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
        AdsBuilder.loadAd(adView, thisActivity, "Key File Manager");
    }

    private void verifyFirstAccess()
    {

        ParticularSetting first_access_ps = customSettings.getOtherPreference(FIRST_KEY_MANAGER_ACCESS);
        Boolean first_access;
        if(first_access_ps != null)
            first_access = (Boolean) first_access_ps.getValue();
        else {
            first_access_ps = new ParticularSetting();
            first_access_ps.setPreference(FIRST_KEY_MANAGER_ACCESS);
            first_access_ps.setValue(true);
            customSettings.setOtherPreference(FIRST_KEY_MANAGER_ACCESS, first_access_ps);
            first_access = true;
        }
        if(first_access)
        {
            showKeyFileManagerTapWizard(this, customSettings, first_access_ps);
        }

    }



    private void getCustomSettings()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(this);
        customSettings = DataManager.getCustomSettings(prefs);
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

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        //finish();
        finishAfterTransition();
    }

    private void getInteractionMode()
    {
        viewMode = getIntent().getBooleanExtra("VIEW_MODE", true);
        encryptMode = getIntent().getBooleanExtra("ENCRYPT_MODE", false);
    }

    private void setComponents()
    {
        key_list_view = findViewById(R.id.key_list_view);
        key_list_view.setLongClickable(true);

        @SuppressLint("InflateParams") View child = getLayoutInflater().inflate(R.layout.empty_keymanager_view, null);
        child.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
        ((ViewGroup)key_list_view.getParent()).addView(child);
        key_list_view.setEmptyView(child);
        loadData();


        add_new_key = findViewById(R.id.add_new_key);
        autotest_all = findViewById(R.id.autotest_all);
        autotest_all.setVisibility(((!viewMode && !encryptMode) ? View.VISIBLE : View.INVISIBLE));
        createKey = findViewById(R.id.makeKey);
        toolbar = findViewById(R.id.keymanager_toolbar);
        toolbar.inflateMenu(R.menu.key_manager_tool_menu);
        setListeners();
    }


    @Override
    protected void onResume() {
        loadData();
        super.onResume();
    }

    private void loadData()
    {
        List<KeyFile> temp_keyFiles;
        ArrayList<String[]> temp_key_info;

        temp_keyFiles = (List<KeyFile>) DataManager.loadPrivateObject(getApplicationContext(), KEY_FILES_DB_NAME);
        temp_key_info = (ArrayList<String[]>) DataManager.loadPrivateObject(getApplicationContext(), KEY_INFO_DB_NAME);

        keyFiles = (temp_keyFiles == null ? new ArrayList<KeyFile>() : temp_keyFiles);
        key_info = (temp_key_info == null ? new ArrayList<String[]>() : temp_key_info);

        adapter = new MyListAdapter(this, key_info);
        key_list_view.setAdapter(adapter);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setListeners()
    {

        key_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                if(!viewMode) {
                    try {
                        Intent thisIntent = getIntent().putExtra("KEY_FILES", keyFiles.get(position));
                        setResult(SINGLE_KEY_FILE_RESULT_CODE, thisIntent);
                        finish();
                    }catch(Exception ex)
                    {
                        makeToast(getApplicationContext(), getString(R.string.critic_key_retrieve_error), ERROR, LONG);
                    }
                }else
                {
                    KeyFile key = keyFiles.get(position);
                    String algorithm;
                    try
                    {
                        algorithm = key.getAlgorithm();
                        makeToast(getApplicationContext(), getString(R.string.key_algorithm)+(algorithm == null ? " "+getString(R.string.key_alg_information_missing) : algorithm), INFO, LONG);
                        //makeToast(getApplicationContext(), key.getValue(), INFO, LONG);
                    }catch(Exception ex)
                    {
                        Log.d("KEY FILE ", "MANCANZA ALGORITMO");
                    }
                }
            }
        });


        key_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteKeyDialog(position);
                return true;
            }
        });

        add_new_key.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                importNewKeyDialog();
            }
        });

        autotest_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyFiles.size() > 1) {
                    Intent thisIntent = getIntent().putExtra("KEY_FILES", (Serializable) keyFiles);
                    setResult(MULTI_KEY_FILE_RESULT_CODE, thisIntent);
                    finish();
                }else
                    makeToast(getApplicationContext(), getString(R.string.not_automatic_key_search_active), INFO, LONG);
            }
        });

        createKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent makeKey = new Intent(getApplicationContext(), KeyMakerChoiceActivity.class);
                startActivity(makeKey);
            }
        });


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.export_key_db:
                        if (keyFiles.size() > 0)
                            showDbInputPassphrase(EXPORT_MODE);
                        else
                            makeToast(getApplicationContext(), getString(R.string.no_key_found_for_export), WARNING, LONG);
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
                                makeToast(thisContext, getString(R.string.import_key_db_wrong_pass), WARNING, LONG);
                            break;
                        case EXPORT_MODE:
                            if(isPossibleToProocedExportDB()) {
                                importExportDBPass = String.valueOf(dbKey[0].getText());
                                importExportDBPass = new String(new HashGenerator(CipherAlgorithm.SHA256).getDigestKey(importExportDBPass));
                                exportKeyDB(importExportDBPass);
                            }else
                                makeToast(thisContext, getString(R.string.key_db_export_wrong_pass), WARNING, LONG);
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

    private void deleteAll()
    {
        if(keyFiles.size() > 0)
        {
            AlertDialog.Builder builder = Alerts.showConfirmDialog(thisContext, getString(R.string.confirm_title), getString(R.string.delete_all_key_confirm_message));
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    keyFiles.clear();
                    key_info.clear();
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

    private void importKeyDB()
    {
        DataManager.getSingleFileChooser(thisContext, this, IMPORT_KEY_DB_REQUEST_CODE);
    }

    private void exportKeyDB(final String pass)
    {
        if(keyFiles.size() > 0)
        {
            AlertDialog.Builder builder = Alerts.showConfirmDialog(thisContext, getString(R.string.confirm_title), getString(R.string.export_key_confirm_message));
            builder.setPositiveButton(R.string.export, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String path = customSettings.getCustom_save_path().concat(File.separator).concat(SINGLE_DB);
                    try {
                        KeyFilesDB.exportKeyDB(keyFiles, key_info, path);
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


    private void deleteKeyDialog(final int position)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.key_deletion)
                .setMessage(R.string.key_deletion_message)
                .setIcon(R.drawable.ic_delete_forever_black_24dp)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        keyFiles.remove(position);
                        key_info.remove(position);
                        if(saveObjectSuccess())
                        {
                            makeToast(getApplicationContext(), getString(R.string.key_deletion_success), SUCCESS, SHORT);
                        }else
                            makeToast(getApplicationContext(), getString(R.string.key_deletion_error), SUCCESS, SHORT);
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


    private boolean saveObjectSuccess()
    {
        return DataManager.savePrivateObject(getApplicationContext(), KEY_FILES_DB_NAME, keyFiles) && DataManager.savePrivateObject(getApplicationContext(), KEY_INFO_DB_NAME, key_info);
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
        else
        {
            KeyFile key = new KeyFile();
            try {
                key.setName(name);
                key.setDescription(description);
                key.setPath(getString(R.string.cipher_memory));
                key.setValue(getRealValueFromUri(uri, getApplicationContext()));
                keyFiles.add(key);
                key_info.add(new String[]{key.getName(), key.getDescription()});
                adapter.notifyDataSetChanged();

                if (saveObjectSuccess()) {
                    makeToast(getApplicationContext(), getString(R.string.key_added), SUCCESS, SHORT);
                } else
                    makeToast(getApplicationContext(), getString(R.string.key_save_error), SUCCESS, SHORT);
            }catch(Exception ex)
            {
                makeToast(getApplicationContext(), getString(R.string.key_file_parse_error)+": "+ex.getMessage(), ERROR, LONG);
            }
        }
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
                    tempKeyFile = new KeyFile();
                    tempKeyFile.setName(name);
                    tempKeyFile.setDescription(description);
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

    private class MyListAdapter extends ArrayAdapter<String []>
    {
        private final int layout;

        MyListAdapter(@NonNull Context context, @NonNull List<String[]> objects) {
            super(context, R.layout.key_item, objects);
            layout = R.layout.key_item;
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

                viewHolder.title = convertView.findViewById(R.id.keyname);
                viewHolder.description = convertView.findViewById(R.id.key_description);
                viewHolder.popupMenu = convertView.findViewById(R.id.popupMenu);
                mainViewHolder = viewHolder;

                convertView.setTag(mainViewHolder);

                final View finalConvertView = convertView;

            }else
            {
                mainViewHolder = (ViewHolder) convertView.getTag();
                adapter.notifyDataSetChanged();
            }
            mainViewHolder.title.setText(getItem(position)[0]);
            mainViewHolder.description.setText(getItem(position)[1]);
            setButtonListeners(mainViewHolder, position);

            return convertView;
        }

        private void setButtonListeners(final ViewHolder mainViewHolder, final int position)
        {
            mainViewHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popup = new PopupMenu(thisContext, mainViewHolder.popupMenu);
                    popup.getMenuInflater().inflate(R.menu.key_manager_popup_menu,popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId())
                            {
                                case R.id.export_key_to_file:
                                    exportKeyChoice(position, KEY_FILE_EXT);
                                    break;

                                case R.id.export_to_qr:
                                    exportKeyChoice(position, KEY_QR_EXT);
                                    break;

                                case R.id.share:
                                    shareKey(position);
                                    break;

                                case R.id.edit:
                                    editKey(position);
                                    break;

                                case R.id.delete_key:
                                    deleteKeyDialog(position);
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
            et[0].setText(keyFiles.get(position).getName());
            et[1].setText(keyFiles.get(position).getDescription());
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
                else if(!name.equals(keyFiles.get(position).getName()) || !description.equals(keyFiles.get(position).getDescription()))
                {
                    KeyFile old = keyFiles.get(position);
                    String [] oldinfo = key_info.get(position);

                    keyFiles.get(position).setName(name);
                    keyFiles.get(position).setDescription(description);
                    key_info.set(position, new String[]{name, description});
                    if (saveObjectSuccess()) {
                        makeToast(getApplicationContext(), getString(R.string.key_modified_success), SUCCESS, SHORT);
                    } else {
                        makeToast(getApplicationContext(), getString(R.string.save_error), ERROR, SHORT);
                        keyFiles.set(position, old);
                        key_info.set(position, oldinfo);
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

    private void exportKeyChoice(final int position, final String ext)
    {
        String messagetype = (ext.equals(KEY_FILE_EXT) ? "File?" : "QR-Code?");

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.confirm_title)
                .setMessage(getString(R.string.confirm_export_keyfile).concat(" "+messagetype))
                .setIcon(R.drawable.ic_question_answer_black_24dp)
                .setPositiveButton(R.string.export, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        exportKey(position, ext);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("INFO_DEBUG", "CHIAVE NON ESPORTATA");
                    }
                })
                .show();
    }

    private void shareKey(int position)
    {
        tempFile = null;

        KeyFile keyFile = keyFiles.get(position);
        final String value = keyFile.getValue();
        final String name = keyFile.getName();

        AlertDialog.Builder builder = Alerts.showConfirmDialog(this, getString(R.string.tipology_of_share_keyfile_title), getString(R.string.share_key_file_mode_message));
        builder.setPositiveButton("QR-Code", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                try {
                    Bitmap qrcode = QRCodeUtils.generateQRCodeBitmap(value, thisContext);
                    String path = sdCardPath.concat(File.separator).concat(name).concat(KEY_QR_EXT);
                    tempFile = new File(path);
                    boolean created = tempFile.createNewFile();
                    if(created)
                        FileManager.saveTempImageBitmap(qrcode, path);
                    else
                        throw new IOException();

                    sendShareKeyIntent(tempFile);
                } catch (WriterException | IOException e) {
                    makeToast(getApplicationContext(), getString(R.string.error_creation_Key_file_share)+" "+e.getMessage(), ERROR, LONG);
                }

            }
        })
                .setNegativeButton("File", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try {
                            String path = sdCardPath.concat(File.separator).concat(name).concat(KEY_FILE_EXT);
                            tempFile = new File(path);
                            boolean created = tempFile.createNewFile();
                            if(created)
                                FileUtils.writeStringToFile(tempFile, value, "UTF-8");
                            else
                                throw new IOException();
                            sendShareKeyIntent(tempFile);
                        } catch (IOException e) {
                            makeToast(getApplicationContext(), getString(R.string.error_creation_Key_file_share)+" "+e.getMessage(), ERROR, LONG);
                        }
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

    private void exportKey(int position, String extension)
    {
        try {
            KeyFile keyfile = keyFiles.get(position);
            String value = keyfile.getValue();
            String name = keyfile.getName();
            String path;
            switch(extension)
            {
                case KEY_QR_EXT:
                    path = customSettings.getCustom_save_path().concat(File.separator).concat(name).concat(KEY_QR_EXT);
                    Bitmap qrcode = QRCodeUtils.generateQRCodeBitmap(value, this);
                    FileManager.saveImageBitmap(qrcode, path);
                    break;

                case KEY_FILE_EXT:
                    path = customSettings.getCustom_save_path().concat(File.separator).concat(name).concat(KEY_FILE_EXT);
                    FileManager.writeTextToFile(value, path);
                    break;

            }
            saveSuccess();
        } catch (IOException | WriterException e) {
            makeToast(this, getString(R.string.exportation_error_keyfile) + " "+e.getMessage(), ERROR, LONG);
        }
    }

    private void saveSuccess()
    {
        makeToast(getApplicationContext(), getString(R.string.export_success_keyfile), SUCCESS, LONG);
    }

    private void exportDBSuccess()
    {
        makeToast(getApplicationContext(), getString(R.string.export_success_database), SUCCESS, LONG);
    }

    class ViewHolder
    {
        TextView title;
        TextView description;
        Button popupMenu;
        //Button delete_key, export_to_file, export_to_qr, share, edit_key;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == KEY_SHARE_REQUEST_CODE)
        {
            if(tempFile != null && tempFile.exists())
                FileManager.deleteFile(tempFile);
            else
                tempFile = null;
        }
        else if(requestCode == SINGLE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            ArrayList<String> keyPath = new ArrayList<>();
            List<Uri> files = Utils.getSelectedFilesFromResult(data);
            for(Uri u: files)
                keyPath.add(Utils.getFileForUri(u).getAbsolutePath());

            if(keyPath.size() == 1 && keyPath.get(0).endsWith(KEY_FILE_EXT))
            {
                String path = keyPath.get(0);

                tempKeyFile.setPath(keyPath.get(0));

                try {
                    tempKeyFile.setValue(FileManager.readTextFromFile(path));
                } catch (IOException e) {
                    makeToast(getApplicationContext(), getString(R.string.read_key_import_error)+" "+e.getMessage(), ERROR, LONG);
                }
                keyFiles.add(tempKeyFile);
                key_info.add(new String[]{tempKeyFile.getName(), tempKeyFile.getDescription()});
                adapter.notifyDataSetChanged();

                if (saveObjectSuccess()) {
                    makeToast(getApplicationContext(), getString(R.string.key_added), SUCCESS, SHORT);
                } else
                    makeToast(getApplicationContext(), getString(R.string.key_save_error), ERROR, SHORT);
            }else
                makeToast(getApplicationContext(), getString(R.string.wrong_extension_key_file), WARNING, LONG);

        }else if(requestCode == IMPORT_KEY_DB_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            ArrayList<String> dbPath = new ArrayList<>();
            List<Uri> files = Utils.getSelectedFilesFromResult(data);
            for(Uri u: files)
                dbPath.add(Utils.getFileForUri(u).getAbsolutePath());

            if(dbPath.size() == 1 && dbPath.get(0).endsWith(ENCRYPTED_LOME_KEY_DB_EXT))
            {
                String path = dbPath.get(0);
                try {
                    SingleFileCipher.decryptFile(new File(path), importExportDBPass, false);
                    String newPath = path.substring(0,path.lastIndexOf("."));
                    HashMap<String, Object> dbMap = (HashMap<String, Object>) DataManager.readObject(newPath);
                    if(dbMap.size() > 0)
                    {
                        Object o;
                        o = dbMap.get(KeyFilesDB.KEYS_CODE);
                        if(o instanceof List)
                            if(((List) o).size() > 0)
                                if(((List) o).get(0) instanceof KeyFile)
                                {
                                    keyFiles = (List<KeyFile>) dbMap.get(KeyFilesDB.KEYS_CODE);
                                    key_info = (ArrayList<String[]>) dbMap.get(KeyFilesDB.INFOS_CODE);
                                    FileManager.secureDeleteFile(new File(newPath), 1, thisContext);
                                    if(saveObjectSuccess())
                                        adapter.notifyDataSetChanged();
                                    else
                                        throw new Exception("INTERNAL IMPORT ERROR");
                                }else {
                                    new File(newPath).delete();
                                    throw new Exception(getString(R.string.wrong_database));
                                }

                    }
                } catch (Exception e) {
                    makeToast(getApplicationContext(), getString(R.string.import_key_db_error)+" "+e.getMessage(), ERROR, LONG);
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
