package me.lo.lomefree.Activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.nex3z.togglebuttongroup.button.LabelToggle;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import me.lo.lomefree.Ads.AdsBuilder;
import me.lo.lomefree.Interfaces.ChooserModes;
import me.lo.lomefree.Interfaces.Extensions;
import me.lo.lomefree.Interfaces.KeyModality;
import me.lo.lomefree.Interfaces.RRCodes;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.Keys.Entities.RSAKeyFile;
import me.lo.lomefree.Model.Cryptography.AsimmetricCiphers.RSACipher;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.UIUtils.Graphics.AdsHelpers;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import mehdi.sakout.fancybuttons.FancyButton;

import static me.lo.lomefree.Globals.GlobalValues.ERROR;
import static me.lo.lomefree.Globals.GlobalValues.INFO;
import static me.lo.lomefree.Globals.GlobalValues.LONG;
import static me.lo.lomefree.Globals.GlobalValues.SHORT;
import static me.lo.lomefree.Globals.GlobalValues.SUCCESS;
import static me.lo.lomefree.Globals.GlobalValues.WARNING;
import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.UIUtils.UISecurity.WindowSecurity.initSecurityFlags;

public class RSAMakerActivity extends AppCompatActivity implements KeyModality, Extensions, ChooserModes, RRCodes, SettingsParameters
{
    private Button backButton;
    private Button copy, paste;
    private FancyButton proceed, manager_button;
    private LabelToggle [] togglesArray;
    private TextView rsaText, type_of_key;
    private static int keyModality = -1;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final int ENCRYPT_MODE = 0;
    private final int DECRYPT_MODE = 1;
    private CustomSettings customSettings;
    private AdView adView;
    private AppCompatActivity thisActivity;
    private Context thisContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        thisContext = this;
        initCustomSettings();
        initSecurityFlags(customSettings, getWindow());
        setContentView(R.layout.activity_rsa_maker);
        setComponents();
        handleSharing();
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
        AdsBuilder.loadAd(adView, thisActivity, "RSA Key Maker");
    }

    private void initCustomSettings()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(this);
        customSettings = DataManager.getCustomSettings(prefs);
    }

    private void setComponents()
    {

        backButton = findViewById(R.id.back_button);
        copy = findViewById(R.id.copy);
        paste = findViewById(R.id.paste);
        proceed = findViewById(R.id.rsa_start);
        rsaText = findViewById(R.id.rsaText);
        manager_button = findViewById(R.id.manager_button);
        type_of_key = findViewById(R.id.selected_key_type);
        initToggles();
        setListeners();
    }

    private void handleSharing() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null && type.equals("text/plain"))
        {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if(sharedText != null && !sharedText.equals("")) {
                rsaText.setText(sharedText);
                makeToast(this,getString(R.string.rsa_text_imported), SUCCESS, LONG);
            }

        }else if(type != null && !type.equals("text/plain"))
            makeToast(this,getString(R.string.rsa_text_format_not_recognized), ERROR, LONG);

    }

    private void setListeners()
    {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if(rsaText.getText() != null && rsaText.getText().length() > 0) {
                    assert clipboard != null;
                    clipboard.setPrimaryClip(ClipData.newPlainText(rsaText.getText(), rsaText.getText()));
                    makeToast(getApplicationContext(), getString(R.string.rsa_text_copied), INFO, SHORT);
                }
            }
        });
        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                assert clipboard != null;
                ClipData clip = clipboard.getPrimaryClip();

                if(clip != null && clip.getItemCount() > 0) {
                    rsaText.setText(clipboard.getPrimaryClip().getItemAt(0).coerceToText(getApplicationContext()));
                }else
                    makeToast(getApplicationContext(),getString(R.string.rsa_not_selected_text), WARNING, LONG);
            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = rsaText.getText().toString();
                if(isPossible())
                {
                    try {
                        switch(getCipherMode())
                        {
                            case ENCRYPT_MODE:
                                encryptText(text);
                                break;
                            case DECRYPT_MODE:
                                decryptText(text);
                                break;
                        }
                    } catch (Exception e)
                    {
                        if(e instanceof ArrayIndexOutOfBoundsException && getCipherMode() == ENCRYPT_MODE)
                            makeToast(getApplicationContext(), getString(R.string.text_too_much_longer_rsa_warning), WARNING, LONG);
                        else
                            makeToast(getApplicationContext(), getString(R.string.rsa_elaboration_error)+" "+e.getMessage(), ERROR, LONG);
                    }
                }else
                    makeToast(getApplicationContext(), getString(R.string.missing_parameters_rsa), WARNING, LONG);
            }
        });

        manager_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent temp = new Intent(getApplicationContext(), RSAKeyManager.class);
                temp.putExtra("VIEW_MODE", false);
                startActivityForResult(temp, RSA_KEY_REQUEST_CODE);
            }
        });
    }

    private void decryptText(String text) throws Exception {
        String decrypted;
        decrypted = RSACipher.RSAdecrypt((keyModality == KMODE_PRIVATE ? privateKey : publicKey), text);
        createResultIntent(decrypted);
    }

    private void encryptText(String text) throws Exception {
        String encrypted;
        encrypted = RSACipher.RSAencrypt((keyModality == KMODE_PRIVATE ? privateKey : publicKey), text);
        createResultIntent(encrypted);
    }

    private boolean isPossible()
    {
        return !rsaText.getText().toString().equals("") && (publicKey != null || privateKey != null);
    }


    private void createResultIntent(String result)
    {
        Intent rsaResultIntent = new Intent(getApplicationContext(), RSAResultActivity.class);
        rsaResultIntent.putExtra("TEXT", result);
        startActivity(rsaResultIntent);
    }

    private void initToggles()
    {
        togglesArray = new LabelToggle[]{findViewById(R.id.encrypt_mode), findViewById(R.id.decrypt_mode)};
        for (LabelToggle instance : togglesArray) {
            switch (instance.getId())
            {
                case R.id.encrypt_mode:
                    instance.setChecked(true);
                    break;
            }
        }
    }

    private int getCipherMode()
    {
        for(LabelToggle instance : togglesArray)
        {
            switch(instance.getId())
            {
                case R.id.encrypt_mode:
                    return (instance.isChecked() ? ENCRYPT_MODE : DECRYPT_MODE);
            }
        }
        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode)
        {
            case RSA_KEY_REQUEST_CODE:
                keyModality = -1;
                publicKey = null;
                privateKey = null;

                if(resultCode == RESULT_OK && data != null)
                {
                    HashMap<Integer, RSAKeyFile> keyInstance = (HashMap<Integer, RSAKeyFile>) data.getSerializableExtra("RSA_KEY");
                    if(keyInstance != null)
                    {
                        switch((Integer) keyInstance.keySet().toArray()[0])
                        {
                            case RSAKeyFile.TYPE_PRIVATE:
                                privateKey = keyInstance.get(RSAKeyFile.TYPE_PRIVATE).getPrivateKey();
                                keyModality = KMODE_PRIVATE;
                                break;

                            case RSAKeyFile.TYPE_PUBLIC:
                                publicKey = keyInstance.get(RSAKeyFile.TYPE_PUBLIC).getPublicKey();
                                keyModality = KMODE_PUBLIC;
                                break;
                        }
                        Log.d("LENGTH", String.valueOf(keyInstance.size()));

                        type_of_key.setText((keyModality == KMODE_PRIVATE
                                ? getString(R.string.private_).
                                concat(": ").
                                concat(keyInstance.get(RSAKeyFile.TYPE_PRIVATE).getName())
                                : getString(R.string.public_)
                                .concat(": ")
                                .concat(keyInstance.get(RSAKeyFile.TYPE_PUBLIC).getName())));
                    }else
                        makeToast(getApplicationContext(), getString(R.string.error_pick_rsakey), ERROR, LONG);
                }
                break;

            default:
                keyModality = -1;
                publicKey = null;
                privateKey = null;
                type_of_key.setText(R.string.noone);
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }


    }
}
