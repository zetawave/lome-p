package me.lo.lomefree.Keys.KeyUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.lo.lomefree.Interfaces.DBNames;
import me.lo.lomefree.Keys.Entities.KeyFile;
import me.lo.lomefree.Keys.Entities.RSAKeyFile;
import me.lo.lomefree.R;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;

public class KeyFilesDB implements DBNames
{
    public  static final String KEYS_CODE = "KEYS";
    public  static final String INFOS_CODE = "INFOS";

    public static void exportKeyDB(List<KeyFile> keyFiles, ArrayList<String []> key_info, String path) throws IOException {
        HashMap<String, Object> DBMap = new HashMap<>();
        DBMap.put(KEYS_CODE, keyFiles);
        DBMap.put(INFOS_CODE, key_info);
        DataManager.saveObject(path, DBMap);
    }

// --Commented out by Inspection START (18/07/18 19.08):
//    public static HashMap readKeyDB(String path) throws IOException, ClassNotFoundException {
//        //List<KeyFile> keyFiles = (List<KeyFile>) DBMap.get("KEYS");
//        //ArrayList<String []> key_info = (ArrayList<String[]>) DBMap.get("INFOS");
//        return (HashMap<String, Object>) DataManager.readObject(path);
//    }
// --Commented out by Inspection STOP (18/07/18 19.08)

    public static void exportRSAKeyDB(List<RSAKeyFile> keyFiles, ArrayList<String []> key_info, String path) throws IOException {
        HashMap<String, Object> DBMap = new HashMap<>();
        DBMap.put(KEYS_CODE, keyFiles);
        DBMap.put(INFOS_CODE, key_info);
        DataManager.saveObject(path, DBMap);
    }

    public static HashMap<String, Object> readRSAKeyDB(String path) throws Exception {
        Object o;
        o = DataManager.readObject(path);
        if(o instanceof HashMap)
            if(((HashMap) o).size() > 0)
                if(((HashMap) o).get(KeyFilesDB.KEYS_CODE) instanceof List)
                if(((List) ((HashMap) o ).get(KeyFilesDB.KEYS_CODE)).size() > 0)
                if(((List) ((HashMap) o ).get(KeyFilesDB.KEYS_CODE)).get(0) instanceof RSAKeyFile)
                    return (HashMap<String, Object>) o;
                else {
                    new File(path).delete();
                    throw new Exception("Wrong database");
                }
                return null;
    }
}
