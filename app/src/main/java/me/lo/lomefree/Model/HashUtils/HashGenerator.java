package me.lo.lomefree.Model.HashUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;


import me.lo.lomefree.Interfaces.CipherAlgorithm;
import me.lo.lomefree.Utils.Misc.MyEncoder;

public class HashGenerator implements CipherAlgorithm
{
    private MessageDigest md5, sha256;
    private final String type;

    public HashGenerator(String type) throws NoSuchAlgorithmException
    {
        this.type = type;
        if(Objects.equals(type, MD5))
            md5 = MessageDigest.getInstance(MD5);
        else if(Objects.equals(type, SHA256))
            sha256 = MessageDigest.getInstance(SHA256);
    }

    public byte [] getDigestKey(String pass)
    {
        if(Objects.equals(type, MD5))
        {

            byte[] inputBytes = pass.getBytes();
            byte[] hashBytes = md5.digest(inputBytes);
            return repad(MyEncoder.encodeHex(hashBytes).getBytes(), type);
        }
        else if(Objects.equals(type, SHA256))
        {
            byte[] hash = sha256.digest(pass.getBytes());
            /* Per un SHA256 con 16 byte
            byte [] passrf = new byte[16];
            System.arraycopy(hash, 0, passrf, 0, 16);
            return passrf;
            */

            return repad(MyEncoder.encodeHex(hash).getBytes(), type);
        }
        return null;
    }

    public byte [] getDigestKey(byte [] pass)
    {
        if(Objects.equals(type, MD5))
        {

            byte[] hashBytes = md5.digest(pass);
            return repad(MyEncoder.encodeHex(hashBytes).getBytes(), type);
        }
        else if(Objects.equals(type, SHA256))
        {
            byte[] hash = sha256.digest(pass);
            /* Per un SHA256 con 16 byte
            byte [] passrf = new byte[16];
            System.arraycopy(hash, 0, passrf, 0, 16);
            return passrf;
            */
            return repad(MyEncoder.encodeHex(hash).getBytes(), type);
        }
        return null;
    }

    private byte [] repad(byte [] pass, String alg)
    {
        byte [] newPass;
        int passlenght = pass.length;
        switch(alg)
        {
            case MD5:
                if(passlenght > 16) {
                    newPass = new byte[16];
                    System.arraycopy(pass, 0, newPass, 0, 16);
                    return  newPass;
                }else if(passlenght < 16)
                {
                    newPass = new byte[16];
                    System.arraycopy(pass, 0, newPass, 0, passlenght);

                    for(int i=passlenght; i<newPass.length; i++)
                        newPass[i] = SPECIAL_BYTE;
                    return newPass;
                }else return pass;

            case SHA256:
                if(passlenght > 32) {
                    newPass = new byte[32];
                    System.arraycopy(pass, 0, newPass, 0, 32);
                    return  newPass;
                }else if(passlenght < 32)
                {
                    newPass = new byte[32];
                    System.arraycopy(pass, 0, newPass, 0, passlenght);

                    for(int i=passlenght; i<newPass.length; i++)
                        newPass[i] = SPECIAL_BYTE;
                    return newPass;
                }else return pass;
        }
        return pass;
    }
}
