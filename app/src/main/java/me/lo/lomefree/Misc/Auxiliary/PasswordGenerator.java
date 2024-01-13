package me.lo.lomefree.Misc.Auxiliary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Fabrizio Amico
 */

public class PasswordGenerator
{
    private final boolean useLower;
    private final boolean useUpper;
    private final boolean useDigits;
    private final boolean usePunctuation;

    public PasswordGenerator()
    {
        this.useLower = true;
        this.useUpper = true;
        this.useDigits = true;
        this.usePunctuation = true;
    }

    public String generate(int length)
    {
        if (length <= 0)
        {
            return "";
        }

        StringBuilder password = new StringBuilder(length);
        Random random = new Random(System.nanoTime());

        List<String> charCategories = new ArrayList<>(4);
        if (useLower) {
            String LOWER = "abcdefghijklmnopqrstuvwxyz";
            charCategories.add(LOWER);
        }
        if (useUpper) {
            String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            charCategories.add(UPPER);
        }
        if (useDigits) {
            String DIGITS = "0123456789";
            charCategories.add(DIGITS);
        }
        if (usePunctuation) {
            String PUNCTUATION = "!@#$%&*()_+-=[]|,./?><";
            charCategories.add(PUNCTUATION);
        }

        for (int i = 0; i < length; i++)
        {
            String charCategory = charCategories.get(random.nextInt(charCategories.size()));
            int position = random.nextInt(charCategory.length());
            password.append(charCategory.charAt(position));
        }
        return new String(password);
    }
}