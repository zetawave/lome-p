package me.lo.lomefree.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import androidx.annotation.Nullable;

import java.util.List;

import me.lo.lomefree.Fragments.AdvancedSettingsFragment;
import me.lo.lomefree.Fragments.ElaborationSettingsFragment;
import me.lo.lomefree.Fragments.GeneralSettingsFragment;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;

import static me.lo.lomefree.UIUtils.UISecurity.WindowSecurity.initSecurityFlags;

/**
 * Created by Fabrizio Amico on 23/03/18.
 */

public class SettingsActivity extends PreferenceActivity
{
    private CustomSettings customSettings;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCustomSettings();
        initSecurityFlags(customSettings, getWindow());

    }

    private void getCustomSettings()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(this);
        customSettings = DataManager.getCustomSettings(prefs);
    }


    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralSettingsFragment.class.getName().equals(fragmentName)
                || AdvancedSettingsFragment.class.getName().equals(fragmentName)
                || ElaborationSettingsFragment.class.getName().equals(fragmentName);
    }

    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }


}
