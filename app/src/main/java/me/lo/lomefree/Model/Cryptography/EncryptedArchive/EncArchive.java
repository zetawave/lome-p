package me.lo.lomefree.Model.Cryptography.EncryptedArchive;

import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.List;

import me.lo.lomefree.Model.Exceptions.WrongPasswordException;

public class EncArchive
{
    private final List<File> files;
    private final String zipName;
    private final String saveDir;
    private final String password;

    public EncArchive(List <File> files, String zipName, String saveDir, String password)
    {
        this.files = files;
        this.zipName = zipName;
        this.saveDir = saveDir;
        this.password = password;
    }


    public void createEncryptedArchive() throws ZipException {
            ZipFile zipFile;
            zipFile = new ZipFile(this.zipName);

            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set compression method to deflate compression
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            parameters.setPassword(this.password);
            addFile(zipFile, files, parameters);

    }

    private void addFile(ZipFile zipFile, List<File> files, ZipParameters parameters) throws ZipException
    {
        for(File file: files)
        {

            if(file.isDirectory())
            {
                zipFile.addFolder(file, parameters);
            }
            else
                zipFile.addFile(file, parameters);
        }
    }

    public void extractEncryptedArchive() throws WrongPasswordException, ZipException {

        try {
            ZipFile zip = new ZipFile(this.zipName);
            if (zip.isEncrypted())
                zip.setPassword(this.password);
            zip.extractAll(this.saveDir);
        }catch(Exception ex)
        {
            if(ex.getMessage().toLowerCase().contains("password") && ex.getMessage().toLowerCase().contains("wrong"))
            {
                throw new WrongPasswordException();
            }else {
                Log.d("ERRORE ESTRAZIONE ZIP", ex.getMessage());
                throw ex;
            }
        }
    }
}