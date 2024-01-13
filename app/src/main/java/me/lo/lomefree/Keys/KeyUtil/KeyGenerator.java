package me.lo.lomefree.Keys.KeyUtil;

import androidx.appcompat.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

import me.lo.lomefree.Interfaces.CipherAlgorithm;
import me.lo.lomefree.Interfaces.KeyMakeParameter;
import me.lo.lomefree.Keys.Entities.KeyFile;
import me.lo.lomefree.Misc.Auxiliary.PasswordGenerator;
import me.lo.lomefree.Model.HashUtils.HashGenerator;
import me.lo.lomefree.Model.RandomUtils.RandNGenerator;
import me.lo.lomefree.R;

public class KeyGenerator implements CipherAlgorithm, KeyMakeParameter
{
    public static KeyFile getRandomKeyFile(AppCompatActivity activity) throws NoSuchAlgorithmException
    {

        String algorithm = SHA256;
        String randompassphrase = new PasswordGenerator().generate(100);
        String passphrase = buildPassphrase(algorithm, randompassphrase);
        String description = activity.getString(R.string.possible_modify_text)+"\nID: "+ new String(new HashGenerator(MD5).getDigestKey("" + ThreadLocalRandom.current().nextInt(1000, 100000)));
        String name = "LomeKey";

        KeyFile keyfile = new KeyFile();
        keyfile.setName(name);
        keyfile.setDescription(description);
        keyfile.setValue(passphrase);
        keyfile.setAlgorithm(algorithm);

        return keyfile;

    }


    public static String buildPassphrase(String hashalg, String passphrase) throws NoSuchAlgorithmException {
        String salt = RandNGenerator.generateSecureRandomString(ThreadLocalRandom.current().nextInt(100, 500));
        salt = new String(new HashGenerator(hashalg).getDigestKey(salt));
        passphrase = new String(new HashGenerator(hashalg).getDigestKey(passphrase));
        return new String(new HashGenerator(hashalg).getDigestKey(passphrase.concat(salt)));

    }
}
