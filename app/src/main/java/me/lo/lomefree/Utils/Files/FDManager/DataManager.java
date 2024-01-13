package me.lo.lomefree.Utils.Files.FDManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.erikagtierrez.multiple_media_picker.Gallery;
import com.nononsenseapps.filepicker.FilePickerActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import me.lo.lomefree.Globals.GlobalValues;
import me.lo.lomefree.Interfaces.ChooserModes;
import me.lo.lomefree.Interfaces.CipherAlgorithm;
import me.lo.lomefree.Interfaces.Extensions;
import me.lo.lomefree.Interfaces.RRCodes;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.Interfaces.SpecialInformationsKeys;
import me.lo.lomefree.Misc.Auxiliary.HackedSerializer.HackedObjectInputStream;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.SettingsUtils.ParticularSetting;
import me.lo.lomefree.Utils.DiskUtils.SpaceUsage;

import static me.lo.lomefree.Globals.GlobalValues.ERROR;
import static me.lo.lomefree.Globals.GlobalValues.LONG;
import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.Utils.Files.FDManager.FileManager.verifyPath;


public class DataManager implements SettingsParameters, ChooserModes, Extensions, CipherAlgorithm, RRCodes, SpecialInformationsKeys
{
    private static  Context relativeContext;

    public static CustomSettings getCustomSettings(SharedPreferences prefs)
    {
        CustomSettings customSettings = new CustomSettings();
        customSettings.setRemove_file(prefs.getBoolean(REMOVE_PREF, false));
        customSettings.setLog_error(prefs.getBoolean(LOG_ERROR_PREF, true));
        customSettings.setCustom_save_path(prefs.getString(CUSTOM_DEF_PATH_PREF, GlobalValues.defaultLomePath));
        customSettings.setSave_mode((prefs.getString(SAVE_MODE_PREF, CustomSettings.DEFAULT_SAVE).equals(CustomSettings.DEFAULT_SAVE) ? CustomSettings.DEFAULT_SAVE_MODE : CustomSettings.CUSTOM_SAVE_MODE));
        customSettings.setLock(prefs.getBoolean(LOCK_PREF, false));
        customSettings.setProfile_image(prefs.getString(PROFILE_IMAGE_PREF, "null"));
        customSettings.setMirror_dir(prefs.getBoolean(MIRROR_PREF, true));
        customSettings.setFirst_access(prefs.getBoolean(FIRST_ACCESS_PREF, true));
        customSettings.setCipher_algorithm(prefs.getString(CIPHER_ALGORITHM, CipherAlgorithm.AES256));
        customSettings.setNotifications(prefs.getBoolean(NOTIFICATION_PREF, true));
        customSettings.setRemove_passes(prefs.getInt(REMOVE_PASSES_PREF, 0));
        customSettings.setVsitr_algorithm(prefs.getBoolean(VSITR_ALG_PREF, false));
        customSettings.setMove_imvid(prefs.getBoolean(MOVE_IMVID_PREF, true));
        customSettings.setScreen_capture(prefs.getBoolean(SCREEN_CAPTURE_PREF, true));
        customSettings.setKey_search_informations(prefs.getBoolean(FIRST_KEY_SEARCH, true));
        customSettings.setOtherPreferences(getOtherPreferences());
        return customSettings;
    }

    private static HashMap<String, ParticularSetting> getOtherPreferences()
    {
        HashMap<String, ParticularSetting> otherPreferences;
        otherPreferences = (HashMap<String, ParticularSetting>) DataManager.loadPrivateObject(relativeContext, OTHER_PREFS);
        return(otherPreferences == null ? new HashMap<String, ParticularSetting>() : otherPreferences);
    }

    public static SharedPreferences getSharedPreferences(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        relativeContext = context;
        return prefs;
    }


    public static boolean savePrivateObject(Context context, String fileName, Object object)
    {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            return true;
        } catch (FileNotFoundException e)
        {
            Log.d("DEBUG OOIS", "Errore salvataggio dati: "+e.getMessage());
            return false;

        } catch (IOException e)
        {
            Log.d("DEBUG OOIS","Errore di Input/Output: "+e.getMessage());
            return false;
        }
    }

    public static Object loadPrivateObject(Context context, String fileName)
    {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object o = ois.readObject();
            ois.close();
            return o;
        } catch (FileNotFoundException e) {
            Log.d("DEBUG OOIS","Impossibile caricare i dati, file non esistente: "+e.getMessage());
        } catch (IOException e) {
            Log.d("DEBUG OOIS", "Errore di Input/Output: "+e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.d("DEBUG OOIS", "Tipo Classe non trovato: "+e.getMessage());
        }
        return null;
    }

    public static void refreshGallery(String fileUri, Context appContext) {

        File file = new File(fileUri);

        if (file.exists()) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(new File(fileUri));
            mediaScanIntent.setData(contentUri);
            appContext.sendBroadcast(mediaScanIntent);
        } else {
            try {
                appContext.getContentResolver().delete(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.Media.DATA + "='"
                                + new File(fileUri).getPath() + "'", null);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }


    public static void saveObject(String path, Object object) {
        verifyPath(path);
        try
        {
            FileOutputStream fos = new FileOutputStream(new File(path));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
        } catch (FileNotFoundException e) {
            Log.d("DEBUG OOIS","Impossibile caricare i dati, file non esistente: "+e.getMessage());
        } catch (IOException e) {
            Log.d("DEBUG OOIS", "Errore di Input/Output: "+e.getMessage());
        }
        }



    public static Object readObject(String path) {
        verifyPath(path);
        try
        {
            FileInputStream fis = new FileInputStream(new File(path));
            ObjectInputStream ois = new HackedObjectInputStream(fis);
            Object o = ois.readObject();
            ois.close();
            return o;
        } catch (FileNotFoundException e) {
            Log.d("DEBUG OOIS","Impossibile caricare i dati, file non esistente: "+e.getMessage());
        } catch (IOException e) {
            Log.d("DEBUG OOIS", "Errore di Input/Output: "+e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.d("DEBUG OOIS", "Tipo Classe non trovato: "+e.getMessage());
        }
        return  null;
    }

    public static void setStringPref(String key, String value, SharedPreferences.Editor prefsEditor)
    {
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static void setBoolPref(String key, boolean value, SharedPreferences.Editor prefsEditor)
    {
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static void setIntPref(String key, int value, SharedPreferences.Editor prefsEditor)
    {
        prefsEditor.putInt(key, value);
        prefsEditor.commit();
    }


    public static void getFileAndDirsChooser(Context context, AppCompatActivity activity)
    {
        Intent i = new Intent(context, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE_AND_DIR);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        activity.startActivityForResult(i, PICK_FILEANDDIR_REQUEST_CODE);
    }

    public static void getSingleFileChooser(Context context, AppCompatActivity activity)
    {
        Intent i = new Intent(context, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        activity.startActivityForResult(i, SINGLE_FILE_REQUEST_CODE);
    }

    public static void getMultipleFileChooser(Context context, AppCompatActivity activity)
    {
        Intent i = new Intent(context, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        activity.startActivityForResult(i, MULTI_FILE_REQUEST_CODE);
    }

    /*
    public static void getSingleFileChooser(Context context, RSAKeyManager.PlaceholderFragment fragment)
    {
        Intent i = new Intent(context, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        fragment.startActivityForResult(i, SINGLE_FILE_REQUEST_CODE);
    }
    */

    public static void getSingleFileChooser(Context context, AppCompatActivity activity, int REQUEST)
    {
        Intent i = new Intent(context, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        activity.startActivityForResult(i, REQUEST);
    }

    public static void getSingleDirectoryChooser(Context context, PreferenceFragment activity)
    {
        Intent i = new Intent(context, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        activity.startActivityForResult(i, SINGLE_DIR_REQUEST_CODE);
    }


    public static void getFileAndDirsChooser(Context context, AppCompatActivity activity, String initialPath)
    {
        Intent i = new Intent(context, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE_AND_DIR);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, initialPath);
        activity.startActivityForResult(i, PICK_FILEANDDIR_REQUEST_CODE);
    }

    /*public static void getViewAndManageFileBrowser(String initialPath, Context context, AppCompatActivity activity)
    {
        try {
            Intent saveDirIntent = new Intent(context, FileBrowser.class);
            saveDirIntent.putExtra(Constants.INITIAL_DIRECTORY, initialPath);
            activity.startActivityForResult(saveDirIntent, SAVE_FOLDER_GET_FILE_REQUEST);
        }catch(Exception ex)
        {
            makeToast(activity.getApplicationContext(), "Errore: "+ex.getMessage(), ERROR, LONG);
        }

    }*/

    public static void createImagePickerDialog(AppCompatActivity activity)
    {
        Intent intent= new Intent(activity, Gallery.class);
        intent.putExtra("title",activity.getString(R.string.select_media));
        intent.putExtra("mode",1); // 1 video e immagini, 2 immagini, 3 video
        activity.startActivityForResult(intent,OPEN_MEDIA_PICKER);
    }


    public static HashMap<String, String[]> getAllSpecialInformations()
    {
        HashMap<String, String[]> specialInformations = new HashMap<>();
        specialInformations.put(ENCRYPTED_FILES_IN_MEM, FileManager.getSpecialInformations(GlobalValues.sdCardPath, new String[]{FILE_EXT}));
        specialInformations.put(KEYS_IN_MEMORY, FileManager.getSpecialInformations(GlobalValues.sdCardPath, new String[]{PUB_KEY_EXT, PRIV_KEY_EXT, KEY_FILE_EXT, RSA_KEYPAIR_EXT}));
        specialInformations.put(FREE_SPACE, new String[]{FileUtils.byteCountToDisplaySize(SpaceUsage.getTotalFreeMemory())});
        specialInformations.put(USED_SPACE, new String[]{FileUtils.byteCountToDisplaySize(SpaceUsage.getTotalUsedMemory())});
        return specialInformations;
    }


}
