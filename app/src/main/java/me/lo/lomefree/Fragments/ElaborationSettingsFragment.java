package me.lo.lomefree.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import me.lo.lomefree.Interfaces.ChooserModes;
import me.lo.lomefree.Interfaces.CipherAlgorithm;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.R;
import me.lo.lomefree.UIUtils.Graphics.Alerts;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;

import static me.lo.lomefree.Utils.Files.FDManager.DataManager.setBoolPref;
import static me.lo.lomefree.Utils.Files.FDManager.DataManager.setIntPref;
import static me.lo.lomefree.Utils.Files.FDManager.DataManager.setStringPref;

/**
 * Created by Fabrizio Amico on 23/03/18.
 */

public class ElaborationSettingsFragment extends PreferenceFragment implements SettingsParameters, ChooserModes
{
    private SwitchPreference remove_file;
    private SwitchPreference log_error;
    private SwitchPreference mirror;
    private SwitchPreference vsitr_method;
    private SwitchPreference move_imvid;
    //private static ListPreference share_save_dir;
    private static ListPreference cipher_algorithm;
    private Preference remove_passes;
    //private String share_save_summary = "Percorso di salvataggio per i file condivisi sull'app. (Di default verrà utilizzato 'Download')";
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private NumberPicker numberPicker;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceFragment thisPf = this;
        addPreferencesFromResource(R.xml.elaboration_settings_screen);
        initializePrefs();
        getComponents();
        initializeListeners();
        loadCustomSettings();
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initializePrefs()
    {
        prefs = DataManager.getSharedPreferences(getContext().getApplicationContext());
        prefsEditor = prefs.edit();
    }

    private void loadCustomSettings()
    {
        remove_passes.setEnabled(!vsitr_method.isChecked() && remove_file.isChecked());
        vsitr_method.setEnabled(remove_file.isChecked());
        //share_save_dir.setSummary(prefs.getString(SHARING_PATH_SUMMARY, share_save_summary));
    }

    private void getComponents()
    {
        remove_file = (SwitchPreference) findPreference("remove_file");
        log_error = (SwitchPreference) findPreference("log_error");
        mirror = (SwitchPreference) findPreference("mirror");
        cipher_algorithm = (ListPreference) findPreference("algorithm");
        remove_passes = findPreference("remove_passes");
        vsitr_method = (SwitchPreference) findPreference("vsitr_method");
        move_imvid = (SwitchPreference) findPreference("move_imvid");
    }


    private void initializeListeners()
    {
        remove_file.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                remove_file.setChecked(!remove_file.isChecked());
                vsitr_method.setEnabled(remove_file.isChecked());
                remove_passes.setEnabled(remove_file.isChecked() && !vsitr_method.isChecked());
                setBoolPref(REMOVE_PREF, remove_file.isChecked(), prefsEditor);
                return false;
            }
        });

        log_error.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                log_error.setChecked(!log_error.isChecked());
                setBoolPref(LOG_ERROR_PREF, log_error.isChecked(), prefsEditor);
                return false;
            }
        });

        mirror.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mirror.setChecked(!mirror.isChecked());
                setBoolPref(MIRROR_PREF, mirror.isChecked(), prefsEditor);
                return false;
            }
        });

        vsitr_method.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                vsitr_method.setChecked(!vsitr_method.isChecked());
                remove_passes.setEnabled(!vsitr_method.isChecked());
                setBoolPref(VSITR_ALG_PREF, vsitr_method.isChecked(), prefsEditor);
                return false;
            }
        });

        move_imvid.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                move_imvid.setChecked(!move_imvid.isChecked());
                setBoolPref(MOVE_IMVID_PREF, move_imvid.isChecked(), prefsEditor);
                return false;
            }
        });

        cipher_algorithm.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final String oldValue = cipher_algorithm.getValue();
                cipher_algorithm.setValue((String) newValue);

                if(!oldValue.equals(newValue))
                {
                    String alg = "";
                    switch(cipher_algorithm.getValue())
                    {
                        case AES128:
                            alg = CipherAlgorithm.AES128;
                            break;
                        case AES256:
                            alg = CipherAlgorithm.AES256;
                            break;
                        case BLOWFISH_MD5:
                            alg = CipherAlgorithm.BLOWFISH_MD5;
                            break;
                        case BLOWFISH_SHA256:
                            alg = CipherAlgorithm.BLOWFISH_SHA256;
                            break;
                    }
                    setStringPref(CIPHER_ALGORITHM, alg, prefsEditor);
                }
                return false;
            }
        });

        remove_passes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                setNumberPickerWidget();
                LinearLayout layout = new LinearLayout(getContext());
                layout.addView(numberPicker);
                layout.setGravity(Gravity.CENTER_HORIZONTAL);
                //layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                AlertDialog.Builder builder = Alerts.showConfirmDialog(getContext(), getString(R.string.pass_number), "");
                builder.setView(layout);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int val = numberPicker.getValue();
                        setIntPref(REMOVE_PASSES_PREF, val, prefsEditor);
                    }
                })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("NUMBER PICKER", "DISMISSED");
                            }
                        });
                builder.show();
                return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setNumberPickerWidget()
    {
        numberPicker = new NumberPicker(getContext());
        numberPicker.setMaxValue(36);
        numberPicker.setMinValue(0);
        numberPicker.setValue(prefs.getInt(REMOVE_PASSES_PREF, 0));
    }
}
