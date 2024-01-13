package me.lo.lomefree.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Process;

import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;

import static me.lo.lomefree.Utils.Files.FDManager.DataManager.setBoolPref;
import static me.lo.lomefree.Utils.Files.FDManager.DataManager.setStringPref;

public class LoginActivity extends AppCompatActivity implements SettingsParameters
{

    private final int REQUEST_CODE = 40;
    private CustomSettings customSettings;
    private SharedPreferences.Editor prefsEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeCustomSettings();
        checkLock();

    }


    private void checkLock()
    {
        if(customSettings.isLock()) {
            Intent pinActivity = new Intent(this, LockActivity.class);
            startActivityForResult(pinActivity, REQUEST_CODE);

        }else
        {
            Intent mainIntent = new Intent(this, customSettings.isFirst_access() ? IntroActivity.class : MainActivity.class);
            if(customSettings.isFirst_access())
                setBoolPref(FIRST_ACCESS_PREF, false, prefsEditor);
            startActivity(mainIntent);
            finish();
        }

    }



    private void initializeCustomSettings()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(getApplicationContext());
        prefsEditor = prefs.edit();

        if(!prefs.contains(FIRST_ACCESS_PREF))
            setBoolPref(FIRST_ACCESS_PREF, true, prefsEditor);
        if(!prefs.contains(PROFILE_IMAGE_PREF))
            setStringPref(PROFILE_IMAGE_PREF, "null", prefsEditor);

        customSettings = DataManager.getCustomSettings(prefs);


    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE &&
                resultCode == RESULT_OK) {
            Intent mainIntent = new Intent(this, customSettings.isFirst_access() ? IntroActivity.class : MainActivity.class);
            if (customSettings.isFirst_access())
                setBoolPref(FIRST_ACCESS_PREF, false, prefsEditor);
            startActivity(mainIntent);
            finish();
        }
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            finishAndRemoveTask();
            Process.killProcess(Process.myPid());
        }
    }
}
