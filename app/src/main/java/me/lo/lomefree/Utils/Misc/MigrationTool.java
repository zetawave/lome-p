package me.lo.lomefree.Utils.Misc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;

import me.lo.lomefree.Interfaces.CipherAlgorithm;
import me.lo.lomefree.Model.HashUtils.HashGenerator;

public class MigrationTool implements CipherAlgorithm
{

    @SuppressLint("HardwareIds")
    public static String generatePassForMigration(Context thisContext, AppCompatActivity thisActivity) {
        String androidId = Settings.Secure.getString(thisContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        String hashedAndroidId = null;
        try {
            hashedAndroidId = new String(new HashGenerator(MD5).getDigestKey(androidId));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedAndroidId;
    }

    public static boolean checkIfIsPossibleToMigrate (String passKey, Context thisContext, AppCompatActivity thisActivity)
    {
        String thisMigrationPass = generatePassForMigration(thisContext, thisActivity);
        Log.d("THIS MIGRATION PASS", thisMigrationPass);
        Log.d("REQUEST KEY ", passKey);
        Log.d("EQUALS KEY", String.valueOf(passKey.equals(thisMigrationPass)));
        return passKey.equals(thisMigrationPass);
    }
}
