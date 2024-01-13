package me.lo.lomefree.Model.Cryptography.SimmetricKeyCiphers;

import org.encryptor4j.Encryptor;
import org.encryptor4j.util.FileEncryptor;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.spec.SecretKeySpec;

import me.lo.lomefree.Interfaces.CipherAlgorithm;
import me.lo.lomefree.Interfaces.Extensions;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;

public class SingleFileCipher implements CipherAlgorithm, Extensions
{
    public static void encryptFile(File file, String pass, boolean delete) throws GeneralSecurityException, IOException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(pass.getBytes(), AES_KEY_KEYCODE);
        Encryptor encryptor = CipherBuilder.buildEncryptor(secretKeySpec, AES_MODE_CIPHER, AES_IV);
        FileEncryptor fileEncryptor = new FileEncryptor(encryptor);
        fileEncryptor.encrypt(file, new File(file.getAbsolutePath().concat(ENCRYPTED_LOME_KEY_DB_EXT)));
        if(delete)
            FileManager.secureDeleteFile(file, 1, null);
    }

    public static void decryptFile(File file, String pass, boolean delete) throws GeneralSecurityException, IOException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(pass.getBytes(), AES_KEY_KEYCODE);
        Encryptor encryptor = CipherBuilder.buildEncryptor(secretKeySpec, AES_MODE_CIPHER, AES_IV);
        FileEncryptor fileEncryptor = new FileEncryptor(encryptor);
        fileEncryptor.decrypt(file, new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))));
        if(delete)
            FileManager.secureDeleteFile(file, 1, null);
    }
}
