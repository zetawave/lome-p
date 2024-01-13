package me.lo.lomefree.Utils.Files.Entities;

import android.util.Log;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import me.lo.lomefree.Interfaces.Extensions;

/**
 * Created by Fabrizio Amico on 24/03/18.
 */

public class FileBox implements Serializable, Extensions
{
    private HashMap<String, File> selectedFiles = new HashMap<>();
    private long totalSizeInByte = 0;
    private final HashMap<String, File> unprocessedFiles = new HashMap<>();
    private final HashMap<String, String> errors = new HashMap<>();
    private String log = "";
    private File [] setOfFile;
    private boolean withError = false;
    private boolean isLome = false;
    private boolean isOther = false;
    private ArrayList<String> originalPaths;
    private String archiveName;
    public static final String DEFAULT_ARCHIVE_NAME = "Lome_Archive";
    private boolean fromShare = false;
    static final long serialVersionUID = 987639234389L;

    public boolean isFromShare() {
        return fromShare;
    }

    public void setFromShare(boolean fromShare) {
        this.fromShare = fromShare;
    }

    public boolean isLome() {
        return isLome;
    }

    public ArrayList<String> getOriginalPaths() {
        return originalPaths;
    }

    public void setOriginalPaths(ArrayList<String> originalPaths) {
        this.originalPaths = originalPaths;
        Log.d("NUMERO PATH", ""+this.originalPaths.size());
    }


    public List<File> getListFiles()
    {
        List<File> fileList = new ArrayList<>();
        for(String path : this.originalPaths)
            fileList.add(new File(path));

        return fileList;
    }

    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    public void setLome(boolean lome) {
        isLome = lome;
    }

    public boolean isOther() {
        return isOther;
    }

    public void setOther(boolean other) {
        isOther = other;
    }

    public boolean isMixed() {
        return isMixed;
    }

    public void setMixed(boolean mixed) {
        isMixed = mixed;
    }

    private boolean isMixed = false;

    public boolean isWithError() {
        return withError;
    }

    public void setWithError(boolean withError) {
        this.withError = withError;
    }

    public HashMap<String, String> getErrors() {
        return errors;
    }

    public FileBox(int fileNumber)
    {
        setOfFile = new File[fileNumber];

    }


    public void setTotalSizeInByte()
    {
        totalSizeInByte = 0;
        for(File file : selectedFiles.values())
        {
            totalSizeInByte += file.length();
        }
    }

    private File getFile(String name)
    {
        return selectedFiles.containsKey(name) ? selectedFiles.get(name) : null;
    }

    public File [] getAllFiles()
    {
        Object [] instance = selectedFiles.values().toArray();
        File [] filearray = new File[instance.length];
        System.arraycopy(instance, 0, filearray, 0, filearray.length);
        return filearray;
/*
        int count=0;
        for(String key : selectedFiles.keySet()) {
            setOfFile[count] = getFile(key);
            count++;
        }
        return setOfFile;
        */
    }

    public void setUnprocessedFile(String name, File file)
    {
        this.unprocessedFiles.put(name, file);
    }

    public void setError(String name, String cause)
    {
        errors.put(name, cause);
    }

    public void putFile(String name, File file)
    {
        selectedFiles.put(name, file);
    }

    public String getLog()
    {
        if(errors.size() < getFileNumber()) {
            for (String key : errors.keySet()) {
                log += "File: " + key + ": " + errors.get(key) + "\n\n";
            }
        }else if(errors.size() == getFileNumber())
            log = "La Passphrase risulta errata per tutti i file selezionati.";
        return log;
    }

    public long getTotalSizeInByte()
    {
        return totalSizeInByte;
    }

    public int getFileNumber()
    {
        return selectedFiles.size();
    }

    public void onlyLomeFile()
    {
        Set <String> keys = selectedFiles.keySet();
        HashMap<String, File> newMap = new HashMap<>();

        for(String key : keys)
        {
            File temp = selectedFiles.get(key);
            if(isLomeFile(temp.getName()))
               newMap.put(key, temp);
        }
        selectedFiles = newMap;
        setOfFile = new File[selectedFiles.size()];
        setTotalSizeInByte();
    }

    public void onlyOtherFile()
    {
        Set <String> keys = selectedFiles.keySet();
        HashMap<String, File> newMap = new HashMap<>();

        for(String key : keys)
        {
            File temp = selectedFiles.get(key);
            if(!isLomeFile(temp.getName()))
                newMap.put(key, temp);
        }
        selectedFiles = newMap;
        setOfFile = new File[selectedFiles.size()];
        setTotalSizeInByte();
    }


    private boolean isLomeFile(String name)
    {
        return name.endsWith(ZIP_EXT) || name.endsWith(FILE_EXT);
    }
}
