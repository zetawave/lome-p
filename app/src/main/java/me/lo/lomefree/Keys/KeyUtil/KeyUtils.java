package me.lo.lomefree.Keys.KeyUtil;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.lo.lomefree.Interfaces.DBNames;
import me.lo.lomefree.Interfaces.Extensions;
import me.lo.lomefree.Keys.Entities.KeyFile;
import me.lo.lomefree.Keys.Entities.RSAKeyFile;
import me.lo.lomefree.Model.Cryptography.AsimmetricCiphers.RSACipher;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;

public class KeyUtils implements Extensions, DBNames
{
    public static boolean verifyKeyStringLenght(String key)
    {
        return ((key.length() == 16 || key.length() == 32));
    }

    public static ArrayList<String> searchKeysInStorage(String initalPath)
    {
        return FileManager.mirrorSearch(initalPath, new String[]{KEY_FILE_EXT, PUB_KEY_EXT, PRIV_KEY_EXT, RSA_KEYPAIR_EXT});
    }

    public static ArrayList<String> searchKeysInStorage(String initalPath, String [] types)
    {
        return FileManager.mirrorSearch(initalPath, types);
    }



    public static void appendKeyInDB(Context context, String KeyDBNAME, String KeyINFODBName, ArrayList<String> keys_) throws IOException, ClassNotFoundException {
        switch(KeyDBNAME)
        {
            case KEY_FILES_DB_NAME:
                List<KeyFile> temp_keyFiles, keyFiles;
                ArrayList<String[]> temp_key_info, key_info;

                temp_keyFiles = (List<KeyFile>) DataManager.loadPrivateObject(context, KEY_FILES_DB_NAME);
                temp_key_info = (ArrayList<String[]>) DataManager.loadPrivateObject(context, KEY_INFO_DB_NAME);

                keyFiles = (temp_keyFiles == null ? new ArrayList<KeyFile>() : temp_keyFiles);
                key_info = (temp_key_info == null ? new ArrayList<String[]>() : temp_key_info);

                for(String key : keys_) {
                    KeyFile tempKeyFile = new KeyFile();
                    String name = new File(key).getName();
                    tempKeyFile.setName(name.substring(0, name.lastIndexOf(".")));
                    tempKeyFile.setDescription("");

                    boolean ok = true;

                    if(keyFiles.size() > 0)
                    {
                        for(KeyFile k : keyFiles)
                            if(k.getName().equals(tempKeyFile.getName()))
                                ok = false;
                    }
                    if(ok) {
                        keyFiles.add(tempKeyFile);
                        key_info.add(new String[]{tempKeyFile.getName(), tempKeyFile.getDescription()});
                        saveObjectSuccess(context, KeyDBNAME, KeyINFODBName, keyFiles, key_info);
                    }
                }

                break;

            case RSA_KEY_FILES_DB_NAME:
                ArrayList<String[]> keyInfo;
                List<RSAKeyFile> rsaKeyFiles;

                ArrayList<String[]> temp_RSAkey_info;
                List<RSAKeyFile> temp_RSAkeyFiles;


                temp_RSAkeyFiles = (List<RSAKeyFile>) DataManager.loadPrivateObject(context, RSA_KEY_FILES_DB_NAME);
                temp_RSAkey_info = (ArrayList<String[]>) DataManager.loadPrivateObject(context, RSA_KEY_INFO_DB_NAME);

                rsaKeyFiles = (temp_RSAkeyFiles == null ? new ArrayList<RSAKeyFile>() : temp_RSAkeyFiles);
                keyInfo = (temp_RSAkey_info == null ? new ArrayList<String[]>() : temp_RSAkey_info);

                for(String key: keys_)
                {
                    RSAKeyFile tempRsaKey = new RSAKeyFile();
                    String name = new File(key).getName();
                    tempRsaKey.setName(name.substring(0, name.lastIndexOf(".")));

                    boolean ok = true;

                    if(rsaKeyFiles.size() > 0)
                    {
                        for(RSAKeyFile rsakey : rsaKeyFiles)
                            if(rsakey.getName().equals(tempRsaKey.getName()))
                                ok = false;
                    }

                    if(ok) {
                        tempRsaKey.setPath(key);
                        HashMap<Integer, Object> keys = new HashMap<>();
                        boolean pub = key.endsWith(PUB_KEY_EXT);
                        boolean priv = key.endsWith(PRIV_KEY_EXT);

                        if (pub || priv) {
                            keys.put((priv ? RSAKeyFile.TYPE_PRIVATE : RSAKeyFile.TYPE_PUBLIC), (priv ? RSACipher.readPrivateKey(tempRsaKey.getPath()) : RSACipher.readPublicKey(tempRsaKey.getPath())));
                            tempRsaKey.setKeys(keys);
                            tempRsaKey.setTypes();
                        } else {
                            tempRsaKey = (RSAKeyFile) DataManager.readObject(tempRsaKey.getPath());
                        }

                        rsaKeyFiles.add(tempRsaKey);
                        assert tempRsaKey != null;
                        keyInfo.add(new String[]{tempRsaKey.getName(), tempRsaKey.getDescription()});
                        saveObjectSuccess(context, KeyDBNAME, KeyINFODBName, rsaKeyFiles, keyInfo);
                    }
                }
                break;
        }
    }

    private static void saveObjectSuccess(Context context, String KeyDBName, String infoDBName, Object keyFiles, Object keyInfo)
    {
        DataManager.savePrivateObject(context, KeyDBName, keyFiles);
        DataManager.savePrivateObject(context, infoDBName, keyInfo);
    }
}
