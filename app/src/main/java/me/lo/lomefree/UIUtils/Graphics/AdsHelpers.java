package me.lo.lomefree.UIUtils.Graphics;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.SettingsUtils.ParticularSetting;

public class AdsHelpers
{
    public static boolean isAdsFree(CustomSettings customSettings, String PREFERENCE_KEY, Context thisContext, AppCompatActivity thisActivity)
    {
        ParticularSetting isAdFreePs = customSettings.getOtherPreference(PREFERENCE_KEY);
        Boolean isAdFree;
        if(isAdFreePs != null)
            isAdFree = (Boolean) customSettings.getOtherPreference(PREFERENCE_KEY).getValue();
        else
        {
            isAdFreePs = new ParticularSetting();
            isAdFreePs.setPreference(PREFERENCE_KEY);
            isAdFreePs.setValue(false);
            customSettings.setOtherPreference(PREFERENCE_KEY, isAdFreePs);
            isAdFree = false;
            customSettings.saveOtherPreferences(thisContext);
        }
        return isAdFree;
    }

    public static void setAdFree(CustomSettings customSettings, String PREFERENCE_KEY, Context thisContext)
    {
        ParticularSetting isAdFreePs = customSettings.getOtherPreference(PREFERENCE_KEY);
        isAdFreePs.setValue(true);
        customSettings.replaceOtherPreference(PREFERENCE_KEY, isAdFreePs);
        customSettings.saveOtherPreferences(thisContext);
        Log.d("AD SET ", "SETTED AD FREE");
    }
}
