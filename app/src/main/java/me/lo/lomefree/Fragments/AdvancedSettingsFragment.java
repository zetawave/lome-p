package me.lo.lomefree.Fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import me.lo.lomefree.Interfaces.ChooserModes;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.R;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;

import static me.lo.lomefree.Globals.GlobalValues.INFO;
import static me.lo.lomefree.Globals.GlobalValues.SUCCESS;
import static me.lo.lomefree.Globals.GlobalValues.WARNING;
import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.Utils.Files.FDManager.DataManager.setBoolPref;
import static me.lo.lomefree.Utils.Files.FDManager.DataManager.setStringPref;
import static me.lo.lomefree.Utils.Misc.MyEncoder.encodeB64;

/**
 * Created by Fabrizio Amico on 23/03/18.
 */

public class AdvancedSettingsFragment extends PreferenceFragment implements SettingsParameters, ChooserModes
{
    private static SwitchPreference app_lock;
    private SharedPreferences.Editor prefsEditor;
    // --Commented out by Inspection (18/07/18 19.09):private CustomSettings customSettings;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceFragment thisPf = this;
        //getCustomSettings();
        //initSecurityFlags(customSettings, getActivity().getWindow());
        addPreferencesFromResource(R.xml.advancend_settings_screen);
        initializePrefs();
        getComponents();
        initializeListeners();
    }

/*
    private void getCustomSettings()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(getContext());
        customSettings = DataManager.getCustomSettings(prefs);
    }
*/
    private void initializePrefs()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(getContext().getApplicationContext());
        prefsEditor = prefs.edit();
    }


    private void getComponents()
    {
        app_lock = (SwitchPreference) findPreference("app_lock");
    }


    private void initializeListeners()
    {
        app_lock.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @TargetApi(Build.VERSION_CODES.O)
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                if(!app_lock.isChecked())
                {
                    setPinDialog();
                }else
                {
                    app_lock.setChecked(!app_lock.isChecked());
                    setBoolPref(LOCK_PREF, app_lock.isChecked(), prefsEditor);
                    String encoded_cpin = encodeB64(CORRECT_PIN);
                    String encoded_value = encodeB64("none");
                    setStringPref(encoded_cpin, encoded_value, prefsEditor);
                    makeToast(getContext(),getString(R.string.app_lock_deactivated), SUCCESS, Toast.LENGTH_LONG);
                }
                return false;
            }
        });
    }


    private void setPinDialog()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.settings_setting_pin);
        alert.setMessage(R.string.settings_pin_lenght);

        LinearLayout passLayout = new LinearLayout(getContext());
        final EditText p[] = new EditText[2];
        passLayout.setOrientation(LinearLayout.VERTICAL);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(6);
        for(int i=0; i<2; i++)
        {
            p[i] = new EditText(getActivity());
            p[i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            p[i].setTransformationMethod(PasswordTransformationMethod.getInstance());
            p[i].setFilters(filterArray);
            switch(i)
            {
                case 0:
                    p[i].setHint(R.string.settings_pint_hint1);
                    break;
                case 1:
                    p[i].setHint(R.string.settings_pin_hint2);
                    break;
            }
            passLayout.addView(p[i]);
        }

        alert.setView(passLayout);

        alert.setPositiveButton(R.string.confirm_title, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialog, int whichButton) {
                String p1 = String.valueOf(p[0].getText());
                String p2 = String.valueOf(p[1].getText());

                if(!p1.equals(p2))
                    makeToast(getContext(),getString(R.string.settings_pin_doesent_match), WARNING, Toast.LENGTH_LONG);
                else if(p1.length() < 4)
                    makeToast(getContext(), getString(R.string.settings_pin_over_four), WARNING, Toast.LENGTH_LONG);
                else
                {
                    app_lock.setChecked(!app_lock.isChecked());
                    String cpin_encoded = encodeB64(CORRECT_PIN);
                    String encoded_pin = encodeB64(p1);

                    setStringPref(cpin_encoded, encoded_pin, prefsEditor);
                    setBoolPref(LOCK_PREF, app_lock.isChecked(), prefsEditor);
                    makeToast(getContext(),getString(R.string.settings_app_locked), SUCCESS,Toast.LENGTH_SHORT);
                }
            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialog, int whichButton)
            {
                makeToast(getContext(),getString(R.string.settings_pin_not_created), INFO, Toast.LENGTH_LONG);
            }
        });
        alert.show();
    }
}
