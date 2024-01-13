package me.lo.lomefree.Model.RandomUtils;

import java.security.SecureRandom;

public class RandNGenerator
{
    private static int[] generateSecureRandomInt(int number_size)
    {
        SecureRandom secrand = new SecureRandom();
        int [] generated_array = new int [number_size];
        for(int i=0; i<generated_array.length; i++)
            generated_array[i] = secrand.nextInt(10);
        return generated_array;
    }

    public static String generateSecureRandomString(int number_size)
    {
        int [] array = generateSecureRandomInt(number_size);
        StringBuilder randomStringNumber = new StringBuilder();
        for(int number : array)
            randomStringNumber.append(number);
        return randomStringNumber.toString();
    }

    public static byte [] generateRandomBytes(byte [] data, SecureRandom secrand)
    {
        secrand.nextBytes(data);
        return data;
    }
}
