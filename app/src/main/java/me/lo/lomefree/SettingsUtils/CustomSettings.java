package me.lo.lomefree.SettingsUtils;

import android.content.Context;
import android.util.Log;


import java.io.File;
import java.util.HashMap;

import me.lo.lomefree.Globals.GlobalValues;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;

/**
 * Created by Fabrizio Amico on 25/03/18.
 */

public class CustomSettings implements SettingsParameters
{
    private boolean remove_file;
    private boolean log_error;
    private boolean mirror_dir;
    private int remove_passes;
    private boolean vsitr_algorithm;
    private boolean move_imvid;
    private boolean screen_capture;
    private boolean key_search_informations;
    public static final int DEFAULT_SAVE_MODE = 1;
    public static final int CUSTOM_SAVE_MODE = 2;
    public static final String DEFAULT_SAVE = "Dinamica";
    private String custom_save_path;
    private int save_mode;
    private boolean first_access;
    private String profile_image;
    private HashMap<String, ParticularSetting> otherPreferences;

    private HashMap<String, ParticularSetting> getOtherPreferences() {
        return otherPreferences;
    }

    public void setOtherPreferences(HashMap<String, ParticularSetting> otherPreferences) {
        this.otherPreferences = otherPreferences;
    }

    public void setKey_search_informations(boolean key_search_informations) {
        this.key_search_informations = key_search_informations;
    }

    public boolean isScreen_capture() {
        return screen_capture;
    }

    public void setScreen_capture(boolean screen_capture) {
        this.screen_capture = screen_capture;
    }

    public boolean isMove_imvid() {
        return move_imvid;
    }

    public void setMove_imvid(boolean move_imvid) {
        this.move_imvid = move_imvid;
    }

    public boolean isVsitr_algorithm() {
        return vsitr_algorithm;
    }

    public void setVsitr_algorithm(boolean vsitr_algorithm) {
        this.vsitr_algorithm = vsitr_algorithm;
    }

    public int getRemove_passes() {
        return remove_passes;
    }

    public void setRemove_passes(int remove_passes) {
        this.remove_passes = remove_passes;
    }

    public boolean isMirror_dir() {
        return mirror_dir;
    }

    public void setMirror_dir(boolean mirror_dir) {
        this.mirror_dir = mirror_dir;
    }



    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    private boolean notifications;

    public String getCipher_algorithm() {
        return cipher_algorithm;
    }

    public void setCipher_algorithm(String cipher_algorithm) {
        this.cipher_algorithm = cipher_algorithm;
    }

    private String cipher_algorithm;


    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public boolean isFirst_access() {
        return first_access;
    }

    public void setFirst_access(boolean first_access) {
        this.first_access = first_access;
    }

    private boolean lock;

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    private int getSave_mode() {
        return save_mode;
    }

    public void setSave_mode(int save_mode) {
        this.save_mode = save_mode;
    }

    public boolean isRemove_file() {
        return remove_file;
    }

    public void setRemove_file(boolean remove_file) {
        this.remove_file = remove_file;
    }

    public boolean isLog_error() {
        return log_error;
    }

    public void setLog_error(boolean log_error) {
        this.log_error = log_error;
    }

    public String getCustom_save_path() {
        return custom_save_path;
    }

    public void setCustom_save_path(String custom_save_path)
    {
        try {
            if (new File(custom_save_path).isDirectory())
                this.custom_save_path = custom_save_path;
            else {
                new File(custom_save_path).mkdir();
                this.custom_save_path = custom_save_path;
            }
        }catch(Exception ex)
        {
            Log.d("DEBUG SETTINGS: ", ex.getMessage());
        }
    }

    public String getSharePath()
    {
        switch (getSave_mode()) {
            case DEFAULT_SAVE_MODE:
                return GlobalValues.downloadPath;
            case CUSTOM_SAVE_MODE:
                String customSavePath = getCustom_save_path();
                if (customSavePath != null && !customSavePath.equals(""))
                    return getCustom_save_path();
                else
                    return GlobalValues.downloadPath;
            default:
                return GlobalValues.downloadPath;
        }
    }


    public ParticularSetting getOtherPreference(String preference)
    {
        try {
            if (otherPreferences != null && otherPreferences.size() > 0 && otherPreferences.get(preference) != null )
                return otherPreferences.get(preference);
            else
                return null;
        }catch(Exception ex)
        {
            return null;
        }
    }

    public void setOtherPreference(String preference, ParticularSetting value)
    {
        otherPreferences.put(preference, value);
    }

    public void replaceOtherPreference(String preference, ParticularSetting value)
    {
        otherPreferences.put(preference, value);
    }

    public void saveOtherPreferences(Context context)
    {
        try{
            DataManager.savePrivateObject(context, OTHER_PREFS, getOtherPreferences());
            Log.d("OTHER PREFS", "PREFERENCES SAVED");
        }catch(Exception ex)
        {
            Log.d("OTHER PREFS", "SAVE ERROR "+ex.getMessage());
        }

    }
}
