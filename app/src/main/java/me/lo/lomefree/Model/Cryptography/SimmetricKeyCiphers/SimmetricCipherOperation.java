package me.lo.lomefree.Model.Cryptography.SimmetricKeyCiphers;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.io.FileUtils;
import org.encryptor4j.Encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import me.lo.lomefree.Activities.ProgressActivity;
import me.lo.lomefree.Interfaces.CipherAlgorithm;
import me.lo.lomefree.Interfaces.Extensions;
import me.lo.lomefree.Keys.Entities.KeyFile;
import me.lo.lomefree.Model.Cryptography.EncryptedArchive.EncArchive;
import me.lo.lomefree.Model.Exceptions.WrongPasswordException;
import me.lo.lomefree.Model.Manipulator.FileByteManipulator;
import me.lo.lomefree.R;
import me.lo.lomefree.Utils.Files.Entities.FileBox;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;

import static me.lo.lomefree.Model.Cryptography.SimmetricKeyCiphers.CipherBuilder.getHashedPass;
import static me.lo.lomefree.Model.Cryptography.SimmetricKeyCiphers.CipherBuilder.parseHashAlgorithm;

public class SimmetricCipherOperation implements CipherAlgorithm, Extensions
{
    private final FileBox box;
    private final boolean delete;
    private String cryptAlg;
    private String cryptAlgIdentifier;
    private String cryptAlgKeyCode;
    private final String savePath;
    private byte [] key;
    private Key secretKey;
    private int iv, passes;
    private AppCompatActivity activity;
    private List<KeyFile> keyFiles;
    private Context context;
    private Encryptor encryptor;
    public static int fileNumber;

    // Construttore per la cifratura
    public SimmetricCipherOperation(FileBox box, String key, boolean delete, String cryptAlg, String cryptAlgKeyCode, String cryptAlgIdentifier, String hashAlg, String savePath, int removePasses, Context context) throws NoSuchAlgorithmException {
        this.box = box;
        this.delete = delete;
        this.cryptAlg = cryptAlg;
        String hashAlg1 = hashAlg;
        this.cryptAlgKeyCode = cryptAlgKeyCode;
        this.cryptAlgIdentifier = cryptAlgIdentifier;
        this.savePath = savePath;
        this.key = getHashedPass(hashAlg, key);
        this.passes = removePasses;
        this.context = context;
        fileNumber = box.getFileNumber();
    }

    // Costruttori per la decifratura, poichè non è specificato algoritmo di hash e altre informazioni relative ad esso

    public SimmetricCipherOperation(FileBox box, String key, boolean delete, String savePath, AppCompatActivity activity, int removePasses, Context context) {
        this.box = box;
        this.delete = delete;
        this.savePath = savePath;
        this.key = key.getBytes();
        this.activity = activity;
        this.passes = removePasses;
        this.context = context;
        fileNumber = box.getFileNumber();
    }

    public SimmetricCipherOperation(FileBox box, List<KeyFile> keyFiles, boolean delete, String savePath, AppCompatActivity activity, int removePasses, Context context) {
        this.box = box;
        this.delete = delete;
        this.savePath = savePath;
        this.keyFiles = keyFiles;
        this.activity = activity;
        this.passes = removePasses;
        this.context = context;
        fileNumber = box.getFileNumber();
    }


    public FileBox encryptFileBox()
    {
        for(File instance : box.getAllFiles())
        {
            try {
                encrypt(instance);
            }catch(GeneralSecurityException | IOException e)
            {
                box.setError(instance.getName(), e.getMessage());
                box.setWithError(true);
            }
        }

        return box;
    }


    public FileBox createEncryptedArchive()
    {
        try
        {
            EncArchive encryptedArchive = new EncArchive(box.getListFiles(), savePath.concat(File.separator).concat(box.getArchiveName().concat(ZIP_EXT)), null, new String(key));
            encryptedArchive.createEncryptedArchive();
            if(delete || box.isFromShare()) {

                for(File instance : box.getAllFiles()) {
                    FileManager.secureDeleteFile(instance, passes, context);
                    fileNumber-=1;
                }
                for(String instance : box.getOriginalPaths())
                {
                    try {
                        if (new File(instance).isDirectory())
                            FileUtils.deleteDirectory(new File(instance));
                        else
                            new File(instance).delete();
                    }catch(Exception ex)
                    {
                        Log.d("ERRORE RIMOZIONE", "Errore nel rimuovere: "+ex.getMessage());
                    }
                }

            }
        }catch(Exception ex)
        {
            box.setError("Archive Error", ex.getMessage());
            box.setWithError(true);
        }

        return box;
    }

    private void extractEncryptedArchive(File file) throws NoSuchAlgorithmException, ZipException, IOException, WrongPasswordException {
        EncArchive ea = new EncArchive(null, file.getAbsolutePath(), savePath.concat(File.separator), new String(getHashedPass(SHA256, key)));
        ea.extractEncryptedArchive();
        deleteFile(file);
        fileNumber -= 1;
    }

    private void extractEncryptedArchive(File file, String keyfilevalue) throws NoSuchAlgorithmException, ZipException, IOException, WrongPasswordException {
        EncArchive ea = new EncArchive(null, file.getAbsolutePath(), savePath.concat(File.separator), new String(getHashedPass(SHA256, keyfilevalue)));
        ea.extractEncryptedArchive();
        deleteFile(file);
    }

    public FileBox decryptFileBox(){
        for(File instance : box.getAllFiles())
        {
            try {
                if(!instance.getName().endsWith(ZIP_EXT))
                    decrypt(instance);
                else
                    extractEncryptedArchive(instance);
            }catch(Exception e)
            {
                if(e instanceof WrongPasswordException) {
                    box.setError(instance.getName(), activity.getString(R.string.wrong_password));
                }else
                    box.setError(instance.getName(), activity.getString(R.string.error)+": "+e.getMessage());

                box.setWithError(true);
            }
        }

        return box;
    }


    public FileBox decryptFileBoxWithMultipleKeyFiles()
    {
        boolean oneFile = false;
        int count = 0;

        for(File instance : box.getAllFiles())
        {
            for(KeyFile keyfile : keyFiles) {
                try
                {
                    if(!instance.getName().endsWith(ZIP_EXT))
                        decrypt(instance, keyfile.getValue().getBytes());
                    else
                        extractEncryptedArchive(instance, keyfile.getValue());
                    oneFile = true;
                    count += 1;
                }catch(Exception ex)
                {
                    Log.d("MULTI KEY", "Chiave errata");
                }
            }
        }

        if(!oneFile) {
            box.setError(activity.getString(R.string.warning), activity.getString(R.string.no_key_file_idle));
            box.setWithError(true);
        }
        else if(count < box.getFileNumber()){
            box.setError(activity.getString(R.string.warning), activity.getString(R.string.success_decrypt_multikey) + count + " files.");
        }

        return box;
    }

    private int getIvFromAlgorithmType()
    {
        switch(cryptAlgKeyCode)
        {
            case AES_KEY_KEYCODE:
                return AES_IV;
            case BLOWFISH_KEY_KEYCODE:
                return BLOWFISH_IV;
        }

        return -1;
    }

    private static int getIvFromCipherModeType(String algorithm)
    {
        switch(algorithm)
        {
            case AES128:
            case AES256:
                return AES_IV;
            case BLOWFISH_MD5:
            case BLOWFISH_SHA256:
                return BLOWFISH_IV;
        }
        return -1;
    }



    private OutputStream getCipherOutputStream(File file, Encryptor encryptor) throws IOException, GeneralSecurityException
    {
        return encryptor.wrapOutputStream(new FileOutputStream(file));
    }

    private void encrypt(File file) throws GeneralSecurityException, IOException {
        InputStream is;
        OutputStream os;
        File outFile;

        initializeAndBuildEncryptParameters();
        outFile = getOutFile(file, true);

        is = new FileInputStream(file);
        os = getCipherOutputStream(outFile, encryptor);
        doEncryptOperation(is, os, file, outFile);
    }

    private void doEncryptOperation(InputStream is, OutputStream os, File file, File outFile) throws IOException, GeneralSecurityException {
        ByteOperations.StreamOperation(is, os, this.context, file.length());
        if(!ProgressActivity.cancelTask) {
            FileByteManipulator.setAdditionalInformations(outFile, new String(key), cryptAlgIdentifier, encryptor);
            deleteFile(file);
        }
    }

    private void initializeAndBuildEncryptParameters()
    {
        secretKey = CipherBuilder.getSecretKeySpec(key, cryptAlgKeyCode);
        encryptor = CipherBuilder.buildEncryptor(secretKey, cryptAlg, getIvFromAlgorithmType());
    }

    private File getOutFile(File file, boolean modeEnc) throws IOException {
        File outFile;
        String outPath = FileManager.relativizePath(file.getAbsolutePath(), savePath, modeEnc);
        outFile = new File(outPath);
        return outFile;
    }

    private void decrypt(File file) throws Exception
    {
        InputStream is;
        OutputStream os;
        File outfile;

        String algorithm = FileByteManipulator.getUsedHashAlgorithm(file);
        byte [] hashedPass = getHashedPass(parseHashAlgorithm(algorithm), key);

        if(analyzeIfPossibleToDecrypt(file, hashedPass, algorithm))
        {
            outfile = getOutFile(file, false);
            is = encryptor.wrapInputStream(new FileInputStream(file));
            os = new FileOutputStream(outfile);
            doDecryptionOperation(is, os, file, outfile, hashedPass);
        }
    }

    private void doDecryptionOperation(InputStream is, OutputStream os, File file, File outfile, byte [] hashedPass) throws IOException, GeneralSecurityException {
        ByteOperations.StreamOperation(is, os, this.context, file.length());
        if(!ProgressActivity.cancelTask) {
            FileByteManipulator.cutFile(outfile, (outfile.length() - (ALG_INFO_LENGHT + getPassUndoLenght(hashedPass, encryptor))));
            inDecryptDelete(file, outfile);
        }
    }

    private void inDecryptDelete(File file, File outfile) throws IOException {
        deleteFile(file);
        if(FileManager.isImageOrVideoFile(outfile))
            FileManager.copyFileInGallery(outfile, activity);
    }

    private int getPassUndoLenght(byte[] hashedPass, Encryptor encryptor) throws GeneralSecurityException {
        return FileByteManipulator.getEncryptedKeyLenght(hashedPass, encryptor);
    }

    private boolean analyzeIfPossibleToDecrypt(File file, byte [] hashedPass, String algorithm) throws IOException, GeneralSecurityException, DecoderException {
        secretKey = CipherBuilder.getSecretKeySpecFromPartialInformations(key, algorithm);
        encryptor = CipherBuilder.buildEncryptor(secretKey, CipherBuilder.parseCipherMode(algorithm), getIvFromCipherModeType(algorithm));

        return FileByteManipulator.isCorrectPassword(file, encryptor, hashedPass);
    }

    private void decrypt(File file, byte [] key) throws Exception
    {
        InputStream is;
        OutputStream os;
        File outfile;

        String algorithm = FileByteManipulator.getUsedHashAlgorithm(file);
        byte [] hashedPass = getHashedPass(parseHashAlgorithm(algorithm), key);

        if(analyzeIfPossibleToDecryptWithMultipleKey(key, algorithm, hashedPass, file))
        {
            outfile = getOutFile(file, false);
            is = encryptor.wrapInputStream(new FileInputStream(file));
            os = new FileOutputStream(outfile);
            doDecryptionOperation(is, os, file, outfile, hashedPass);
        }
    }

    private boolean analyzeIfPossibleToDecryptWithMultipleKey(byte [] key, String algorithm, byte [] hashedPass, File file) throws GeneralSecurityException, IOException, DecoderException {
        secretKey = CipherBuilder.getSecretKeySpecFromPartialInformations(key, algorithm);
        encryptor = CipherBuilder.buildEncryptor(secretKey, CipherBuilder.parseCipherMode(algorithm), getIvFromCipherModeType(algorithm));
        return FileByteManipulator.isCorrectPassword(file, encryptor, hashedPass);
    }

    private void deleteFile(File file) throws IOException {
        if(delete) {
            FileManager.secureDeleteFile(file, passes, context);
        }
    }

}
