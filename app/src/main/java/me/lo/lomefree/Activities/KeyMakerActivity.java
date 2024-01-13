package me.lo.lomefree.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.google.zxing.WriterException;
import com.nex3z.togglebuttongroup.button.LabelToggle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lo.lomefree.Interfaces.DBNames;
import me.lo.lomefree.Interfaces.Extensions;
import me.lo.lomefree.Interfaces.KeyMakeParameter;
import me.lo.lomefree.Keys.Entities.KeyFile;
import me.lo.lomefree.Keys.KeyUtil.KeyGenerator;
import me.lo.lomefree.Misc.Auxiliary.PasswordGenerator;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.UIUtils.Graphics.Alerts;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;
import me.lo.lomefree.Utils.Files.FDManager.QRCodeUtils;

import static me.lo.lomefree.Globals.GlobalValues.ERROR;
import static me.lo.lomefree.Globals.GlobalValues.SUCCESS;
import static me.lo.lomefree.Globals.GlobalValues.WARNING;
import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.Globals.GlobalValues.INFO;
import static me.lo.lomefree.Globals.GlobalValues.LONG;
import static me.lo.lomefree.Globals.GlobalValues.SHORT;
import static me.lo.lomefree.UIUtils.UISecurity.WindowSecurity.initSecurityFlags;

public class KeyMakerActivity extends AppCompatActivity implements KeyMakeParameter, DBNames, Extensions
{

    private Button info;
    private Button randomKey;
    private LabelToggle[] toggles;
    private EditText[] editTexts;
    private Switch toManager;
    private Button makeKey;
    private Context thisContext;
    private boolean toManagerFlag = false;
    private final EditText[] et = new EditText[2];
    private CustomSettings customSettings;
    private KeyFile keyfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = this;
        getCustomSettings();
        initSecurityFlags(customSettings, getWindow());
        setContentView(R.layout.activity_key_maker);
        getComponents();

    }

    private void getCustomSettings()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(getApplicationContext());
        customSettings = DataManager.getCustomSettings(prefs);
    }

    private void getComponents() {
        info = findViewById(R.id.info);

        toggles = new LabelToggle[]{findViewById(R.id.toggle_hashmd5),findViewById(R.id.toggle_hashSHA256),findViewById(R.id.toggle_file), findViewById(R.id.toggle_qrcode)};
        setDefaultToggles();
        editTexts = new EditText[]{findViewById(R.id.passphrase), findViewById(R.id.reinsert_passph)};
        toManager = findViewById(R.id.toManager_switch);
        makeKey = findViewById(R.id.makeKey);
        randomKey = findViewById(R.id.randomKey);


        setListeners();
    }

    private void setListeners() {
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alerts.showInfoDialog(getString(R.string.information), getString(R.string.key_maker_info), thisContext, R.drawable.ic_info_outline_black_24dp);
            }
        });



        toManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toManagerFlag = !toManagerFlag;
                toManager.setChecked(toManagerFlag);
            }
        });

        randomKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String generated = new PasswordGenerator().generate(32);
                for(EditText instance : editTexts)
                {
                    switch(instance.getId())
                    {
                        case R.id.passphrase:
                        case R.id.reinsert_passph:
                            instance.setText(generated);
                    }
                }
                makeToast(getApplicationContext(), getString(R.string.generated_passphrase), INFO, SHORT);
            }
        });

        makeKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final String [] togglesSettings = getToggleSettings();

                if(isPossible()) {
                    if (toManagerFlag)
                    {
                        AlertDialog.Builder builder = Alerts.makeDoubleEditTextDialogWithNameFiltered(thisContext, getString(R.string.to_manager), getString(R.string.import_key_message), getString(R.string.import_key_hint1), getString(R.string.import_key_hint2), et, InputType.TYPE_CLASS_TEXT);
                        builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try
                                {
                                    keyfile = makeKeyFileForManager(String.valueOf(et[0].getText()), String.valueOf(et[1].getText()), togglesSettings[0], String.valueOf(editTexts[0]));
                                    saveKeyFileToDB(keyfile);
                                    finish();
                                }catch(Exception ex)
                                {
                                    makeToast(getApplicationContext(), getString(R.string.key_make_error)+" "+ex.getMessage(), ERROR, LONG);
                                }
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("KEY MAKE", "Creazione chiave annullata");
                            }
                        });
                        builder.show();

                    } else
                    {
                        String mode = getToggleSettings()[1];
                        String extension = (mode.equals(MODE_FILE) ? KEY_FILE_EXT : KEY_QR_EXT);
                        String path = customSettings.getCustom_save_path().concat(File.separator).concat("LomeKey".concat(extension));

                        try {
                            String passphrase = KeyGenerator.buildPassphrase(togglesSettings[0], String.valueOf(editTexts[0]));
                            saveSingleKeyFile(path, mode, passphrase);
                            saveSuccess();
                            finish();
                        }catch(Exception ex)
                        {
                            makeToast(getApplicationContext(), getString(R.string.key_make_error), ERROR, LONG);
                        }
                    }
                }else
                    makeToast(getApplicationContext(), getString(R.string.missing_key_pass), WARNING, LONG);

            }


        });
    }

    private void saveSuccess()
    {
        makeToast(getApplicationContext(), getString(R.string.export_success_keyfile), SUCCESS, LONG);
    }

    private void saveSingleKeyFile(String path, String mode, String passphrase) throws IOException, WriterException {
        switch(mode)
        {
            case MODE_FILE:
                FileManager.writeTextToFile(passphrase, path);
                break;
            case MODE_QR:
                Bitmap qrcode = QRCodeUtils.generateQRCodeBitmap(passphrase, thisContext);
                FileManager.saveImageBitmap(qrcode, path);
                break;
        }
    }

    private boolean isPossible() {
        String pass1 = String.valueOf(editTexts[0].getText());
        String pass2 = String.valueOf(editTexts[1].getText());

        return (pass1 != null && pass2 != null && !pass1.equals("") && !pass2.equals("") && pass1.equals(pass2));
    }

    private void saveKeyFileToDB(KeyFile keyfile)
    {
        List<KeyFile> temp_keyFiles;
        ArrayList<String[]> temp_key_info;
        List<KeyFile> keyFiles;
        ArrayList<String[]> key_info;

        temp_keyFiles = (List<KeyFile>) DataManager.loadPrivateObject(getApplicationContext(), KEY_FILES_DB_NAME);
        temp_key_info = (ArrayList<String[]>) DataManager.loadPrivateObject(getApplicationContext(), KEY_INFO_DB_NAME);

        keyFiles = (temp_keyFiles == null ? new ArrayList<KeyFile>() : temp_keyFiles);
        key_info = (temp_key_info == null ? new ArrayList<String[]>() : temp_key_info);

        keyFiles.add(keyfile);
        key_info.add(new String[]{keyfile.getName(), keyfile.getDescription()});

        if(saveObjectSuccess(keyFiles, key_info))
        {
            makeToast(getApplicationContext(), getString(R.string.success_save_manager_key), SUCCESS, SHORT);
        }else
            makeToast(getApplicationContext(), getString(R.string.key_make_error), SUCCESS, SHORT);
    }

    private KeyFile makeKeyFileForManager(String name, String description, String algorithm, String passphrase) throws Exception {
        if(name.equals(""))
            throw new Exception(getString(R.string.name_obligation));

        KeyFile newkey = new KeyFile();
        newkey.setAlgorithm(algorithm);
        newkey.setDescription((description == null ? "" : description));
        newkey.setName(name);
        newkey.setValue(KeyGenerator.buildPassphrase(algorithm, passphrase));
        return newkey;
    }



    private void setDefaultToggles()
    {
        for(LabelToggle instance : toggles)
        {
            switch (instance.getId()) {
                case R.id.toggle_file:
                    instance.setChecked(true);
                    break;
                case R.id.toggle_hashSHA256:
                    instance.setChecked(true);
                    break;
            }
        }
    }


    private boolean saveObjectSuccess(List<KeyFile> keyFiles, ArrayList<String[]> key_info)
    {
        return DataManager.savePrivateObject(getApplicationContext(), KEY_FILES_DB_NAME, keyFiles) && DataManager.savePrivateObject(getApplicationContext(), KEY_INFO_DB_NAME, key_info);
    }

    private String [] getToggleSettings()
    {
        String [] toggleSettings = new String[2];

        for(LabelToggle instance : toggles)
        {
            switch (instance.getId())
            {
                case R.id.toggle_file:
                    if(instance.isChecked())
                        toggleSettings[1] = MODE_FILE;
                    break;
                case R.id.toggle_qrcode:
                    if(instance.isChecked())
                        toggleSettings[1] = MODE_QR;
                    break;
                case R.id.toggle_hashmd5:
                    if(instance.isChecked())
                        toggleSettings[0] = MD5;
                    break;
                case R.id.toggle_hashSHA256:
                    if(instance.isChecked())
                        toggleSettings[0] = SHA256;
                    break;

            }
        }
        return toggleSettings;
    }



}


