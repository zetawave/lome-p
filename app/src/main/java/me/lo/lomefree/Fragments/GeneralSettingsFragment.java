package me.lo.lomefree.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.nononsenseapps.filepicker.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.lo.lomefree.Interfaces.ChooserModes;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.R;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Globals.GlobalValues;

import static me.lo.lomefree.Interfaces.RRCodes.SINGLE_DIR_REQUEST_CODE;
import static me.lo.lomefree.Utils.Files.FDManager.DataManager.setBoolPref;
import static me.lo.lomefree.Utils.Files.FDManager.DataManager.setStringPref;

/**
 * Created by Fabrizio Amico on 23/03/18.
 */

public class GeneralSettingsFragment extends PreferenceFragment implements SettingsParameters, ChooserModes
{
    private SwitchPreference notifications;
    private static ListPreference save_dir;
    //private Preference default_settings;
    private static String localNewPath = null;
    private String save_dir_summary;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private String saveDirNewValueTemp;
    private String saveDirOldValueTemp;
    private PreferenceFragment thisPf;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisPf = this;
        addPreferencesFromResource(R.xml.general_settings_screen);
        initializeStrings();
        initializePrefs();
        getComponents();
        initializeListeners();
        loadCustomSettings();
    }

    private void initializeStrings()
    {
        save_dir_summary = getString(R.string.settings_save_dir_summary_text);
    }


    //@RequiresApi(api = Build.VERSION_CODES.M)
    private void initializePrefs()
    {
        prefs = DataManager.getSharedPreferences(getContext().getApplicationContext());
        prefsEditor = prefs.edit();
    }

    private void loadCustomSettings()
    {

        save_dir.setSummary(prefs.getString(CUSTOM_DEF_PATH_SUMMARY, save_dir_summary));
        //share_save_dir.setSummary(prefs.getString(SHARING_PATH_SUMMARY, share_save_summary));
    }

    private void getComponents()
    {
        save_dir = (ListPreference) findPreference("save_dir");
        notifications = (SwitchPreference) findPreference("notifications");
        //share_save_dir = (ListPreference) findPreference("share_save_path");
        //default_settings = findPreference("default_settings");
    }


    private void initializeListeners()
    {
        notifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                notifications.setChecked(!notifications.isChecked());
                setBoolPref(NOTIFICATION_PREF, notifications.isChecked(), prefsEditor);
                return false;
            }
        });


        save_dir.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onPreferenceChange(Preference preference, final Object newValue) {
                final String oldValue = save_dir.getValue();
                save_dir.setValue((String) newValue);

                saveDirNewValueTemp = (String) newValue;
                saveDirOldValueTemp = oldValue;

                if(save_dir.getValue().equals(getString(R.string.custom)))
                {
                    DataManager.getSingleDirectoryChooser(getContext(), thisPf);
                }else
                {
                    localNewPath = null;
                    save_dir.setValue((String) newValue);
                    save_dir.setSummary(save_dir_summary);
                    setStringPref(SAVE_MODE_PREF, save_dir.getValue(), prefsEditor);
                    setStringPref(CUSTOM_DEF_PATH_SUMMARY, save_dir_summary, prefsEditor);
                    setStringPref(CUSTOM_DEF_PATH_PREF, GlobalValues.defaultLomePath, prefsEditor);
                }
                return false;
            }
        });

/*
        default_settings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                localNewPath = null;
                setStringPref(SHARING_PATH, GlobalValues.downloadPath, prefsEditor);

                save_dir.setValue("Default");
                save_dir.setSummary(save_dir_summary);
                setStringPref(SAVE_MODE_PREF, save_dir.getValue(), prefsEditor);
                setStringPref(CUSTOM_DEF_PATH_SUMMARY, (String) save_dir.getSummary(), prefsEditor);
                setStringPref(CUSTOM_DEF_PATH_PREF, "", prefsEditor);

                setIntPref(REMOVE_PASSES_PREF, 1, prefsEditor);


                return false;
            }
        });
        */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode)
        {
            case SINGLE_DIR_REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK)
                {
                    ArrayList<String> dirPath = new ArrayList<>();
                    List<Uri> files = Utils.getSelectedFilesFromResult(data);
                    for(Uri u: files)
                        dirPath.add(Utils.getFileForUri(u).getAbsolutePath());

                    String newDirPath = dirPath.get(0);

                    if(newDirPath != null && new File(newDirPath).isDirectory())
                    {
                        localNewPath = newDirPath;
                        save_dir.setValue(saveDirNewValueTemp);
                        save_dir.setSummary(save_dir_summary+"\n\n"+getString(R.string.current)+localNewPath);
                        setStringPref(SAVE_MODE_PREF, save_dir.getValue(), prefsEditor);
                        setStringPref(CUSTOM_DEF_PATH_SUMMARY, (String) save_dir.getSummary(), prefsEditor);
                        setStringPref(CUSTOM_DEF_PATH_PREF, localNewPath, prefsEditor);
                    }

                }else
                {
                    save_dir.setValue(saveDirOldValueTemp);
                    setStringPref(SAVE_MODE_PREF, save_dir.getValue(), prefsEditor);
                }

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }


    }
}
