package me.lo.lomefree.Model.Manipulator;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.encryptor4j.Encryptor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import me.lo.lomefree.Interfaces.CipherAlgorithm;
import me.lo.lomefree.Model.Exceptions.WrongPasswordException;

public class FileByteManipulator implements CipherAlgorithm
{

    public static void setAdditionalInformations(File file, String password, String algorithm, Encryptor encryptor) throws IOException, GeneralSecurityException {
        RandomAccessFile raf;

        byte [] bytePass = password.getBytes();
        byte [] encrypted = encryptor.encrypt(bytePass);
        String pass = new String(Hex.encodeHex(encrypted));

        raf = new RandomAccessFile(file, "rw");
        raf.seek(file.length());
        raf.write(pass.getBytes(StandardCharsets.UTF_8), 0, pass.getBytes().length);
        raf.close();

        raf = new RandomAccessFile(file, "rw");
        raf.seek(file.length());
        byte [] alg = algorithm.getBytes(StandardCharsets.UTF_8);
        raf.write(alg, 0, alg.length);
        raf.close();
    }

    public static String getUsedHashAlgorithm(File file) throws IOException {

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long offset = (file.length() - ALG_INFO_LENGHT);
        raf.seek(offset);
        byte [] algorithm = new byte[ALG_INFO_LENGHT];
        raf.read(algorithm, 0, algorithm.length);
        raf.close();
        return new String(algorithm, StandardCharsets.UTF_8);
    }

    public static boolean isCorrectPassword(File file, Encryptor encryptor, byte [] bytepass) throws IOException, GeneralSecurityException, DecoderException {

        byte [] encrypted = encryptor.encrypt(bytepass);
        String pass = new String(Hex.encodeHex(encrypted));

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long offset = (file.length() - (pass.getBytes(StandardCharsets.UTF_8).length + ALG_INFO_LENGHT));
        raf.seek(offset);
        byte [] read = new byte[pass.getBytes(StandardCharsets.UTF_8).length];
        raf.read(read, 0, read.length);
        raf.close();

        String readed = new String(read, StandardCharsets.UTF_8);
        String addedPass;
        String readedPass;

        addedPass = new String(Hex.encodeHex(encryptor.decrypt(new Hex().decode(pass.getBytes()))));
        readedPass = new String(Hex.encodeHex(encryptor.decrypt(new Hex().decode(readed.getBytes()))));

        if(readedPass.equals(addedPass))
            return true;
        else
            throw new WrongPasswordException();
    }


    public static void cutFile(File file, long offset) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.setLength(offset);
        raf.close();
    }

    public static int getEncryptedKeyLenght(byte [] bytepass, Encryptor encryptor) throws GeneralSecurityException {
        byte [] encrypted = encryptor.encrypt(bytepass);
        String pass = new String(Hex.encodeHex(encrypted));
        return pass.getBytes(StandardCharsets.UTF_8).length;
    }

}
