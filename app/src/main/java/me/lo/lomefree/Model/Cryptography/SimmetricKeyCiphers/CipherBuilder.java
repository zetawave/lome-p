package me.lo.lomefree.Model.Cryptography.SimmetricKeyCiphers;

import org.encryptor4j.Encryptor;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.SecretKeySpec;

import me.lo.lomefree.Interfaces.CipherAlgorithm;
import me.lo.lomefree.Model.HashUtils.HashGenerator;

public class CipherBuilder implements CipherAlgorithm
{
    public static SecretKeySpec getSecretKeySpec(byte [] password, String algorithm) {
        return new SecretKeySpec(password, algorithm);
    }

    public static SecretKeySpec getSecretKeySpecFromPartialInformations(byte [] password, String algorithm) throws NoSuchAlgorithmException {
        password = getHashedPass(parseHashAlgorithm(algorithm), password);
        return new SecretKeySpec(password, parseAlgorithmKeyCode(algorithm));
    }

    public static String parseAlgorithmKeyCode(String algorithm)
    {
        switch(algorithm)
        {
            case AES128:
            case AES256:
                return AES_KEY_KEYCODE;

            case BLOWFISH_MD5:
            case BLOWFISH_SHA256:
                return BLOWFISH_KEY_KEYCODE;
        }
        return null;
    }

    public static String parseHashAlgorithm(String algorithm)
    {
        switch(algorithm)
        {
            case AES128:
            case BLOWFISH_MD5:
                return MD5;
            case AES256:
            case BLOWFISH_SHA256:
                return SHA256;
        }
        return null;
    }

    public static String parseCipherMode(String algorithm)
    {
        switch(algorithm)
        {
            case AES128:
            case AES256:
                return AES_MODE_CIPHER;
            case BLOWFISH_MD5:
            case BLOWFISH_SHA256:
                return BLOWFISH_MODE_CIPHER;
        }
        return null;
    }


    public static byte [] getHashedPass(String hashAlg, String password) throws NoSuchAlgorithmException {
        return new HashGenerator(hashAlg).getDigestKey(password);
    }

    public static byte [] getHashedPass(String hashAlg, byte [] password) throws NoSuchAlgorithmException {
        return new HashGenerator(hashAlg).getDigestKey(password);
    }

    public static Encryptor buildEncryptor(Key secretKey, String alg, int iv)
    {
        return new Encryptor(secretKey, alg, iv);
    }
}
