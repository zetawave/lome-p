package me.lo.lomefree.Activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.File;
import java.io.IOException;

import me.lo.lomefree.Interfaces.Extensions;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;

import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.Globals.GlobalValues.*;
import static me.lo.lomefree.UIUtils.UISecurity.WindowSecurity.initSecurityFlags;

public class RSAResultActivity extends AppCompatActivity implements Extensions
{
    private ExpandableTextView result;
    private final ImageButton [] buttons = new ImageButton[3];
    private CustomSettings customSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getCustomSettings();
        initSecurityFlags(customSettings, getWindow());
        setContentView(R.layout.activity_rsa_result);
        setComponents();

    }

    private void getCustomSettings()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(this);
        customSettings = DataManager.getCustomSettings(prefs);
    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
        super.onBackPressed();
    }

    private void setComponents()
    {
        result = findViewById(R.id.expand_text_view);
        CharSequence extratext = getExtraText();
        result.setText((extratext != null ? extratext : "ERRORE IMPREVISTO"));
        for(int i=0; i<buttons.length; i++)
        {
            switch(i) {
                case 0:
                    buttons[i] = findViewById(R.id.copy_);
                    buttons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            if(result.getText() != null && result.getText().length() > 0) {
                                assert clipboard != null;
                                clipboard.setPrimaryClip(ClipData.newPlainText(result.getText(), result.getText()));
                                makeToast(getApplicationContext(), getString(R.string.rsa_text_copied), INFO, SHORT);
                            }
                        }
                    });
                    break;
                case 1:
                    buttons[i] = findViewById(R.id.export);
                    buttons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CharSequence sequence = result.getText();
                            assert sequence != null;
                            String text = sequence.toString();
                            String path = customSettings.getCustom_save_path().concat(File.separator).concat("SecretText".concat(SECRET_RSA_TEXT_EXT));
                            try {
                                FileManager.writeTextToFile(text, path);
                                exportSucces();
                            } catch (IOException e) {
                                makeToast(getApplicationContext(), getString(R.string.rsa_result_export_error)+" "+e.getMessage(), ERROR, LONG);
                            }
                            //Toasty.info(getApplicationContext(), "Export!", Toast.LENGTH_LONG, true).show();
                        }
                    });
                    break;
                case 2:
                    buttons[i] = findViewById(R.id.share);
                    buttons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, result.getText());
                            sendIntent.setType("text/plain");
                            startActivity(Intent.createChooser(sendIntent, getString(R.string.rsa_with_who_share_text)));
                        }
                    });
                    break;
            }

        }

    }

    private CharSequence getExtraText()
    {
        CharSequence charSequence;
        charSequence = getIntent().getStringExtra("TEXT");
        return charSequence;
    }

    private void exportSucces()
    {
        makeToast(this, getString(R.string.success_export_rsatext), SUCCESS, LONG);
    }
}
