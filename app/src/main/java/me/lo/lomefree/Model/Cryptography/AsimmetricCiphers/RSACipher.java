package me.lo.lomefree.Model.Cryptography.AsimmetricCiphers;

import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import me.lo.lomefree.Interfaces.Extensions;

public class RSACipher implements Extensions
{
    public static final int RSA_KEY_4096 = 4096;



    public static PrivateKey readPrivateKey(String path) throws IOException, ClassNotFoundException
    {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)));
        return (PrivateKey) ois.readObject();
    }


    public static PublicKey readPublicKey(String path) throws IOException, ClassNotFoundException
    {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)));
        return (PublicKey) ois.readObject();
    }

    public static void generateRSAKey(String path, int size)
    {
        try
        {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(size);
            final KeyPair key = keyGen.generateKeyPair();

            File privateKeyFile = new File(path.concat(PRIV_KEY_EXT));
            File publicKeyFile = new File(path.concat(PUB_KEY_EXT));

            privateKeyFile.createNewFile();
            publicKeyFile.createNewFile();

            ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
            publicKeyOS.writeObject(key.getPublic());
            publicKeyOS.close();

            ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
            privateKeyOS.writeObject(key.getPrivate());
            privateKeyOS.close();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static KeyPair generateRSAKeyPair(int size) throws NoSuchAlgorithmException {
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(size);
        return  keyGen.generateKeyPair();
    }

    public static String RSAencrypt(Object key, String message) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, (Key) key);
        return new String(Hex.encodeHex(cipher.doFinal(message.getBytes())));
    }

    public static String RSAdecrypt(Object key, String encrypted) throws Exception
    {
        System.out.println(encrypted.getBytes().length);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, (Key) key);
        return new String(cipher.doFinal((byte[]) new Hex().decode(encrypted)));
    }



}
