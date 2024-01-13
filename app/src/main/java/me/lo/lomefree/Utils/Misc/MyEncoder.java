package me.lo.lomefree.Utils.Misc;

import com.google.common.io.BaseEncoding;

import org.apache.commons.codec.binary.Base64;

public class MyEncoder
{
    public static String encodeB64(String input) {
        return new String(new Base64().encode(input.getBytes()));
    }

    public static String encodeHex(String input)
    {
        return BaseEncoding.base16().encode(input.getBytes());
    }

    public static String encodeHex(byte [] input)
    {
        return BaseEncoding.base16().encode(input);
    }

    public static String decodeHex(String input)
    {
        return new String(BaseEncoding.base16().decode(input));
    }

    public static String decodeHex(byte [] input)
    {
        return new String(BaseEncoding.base16().decode(new String(input)));
    }

    public static String decodeB64(String input) {
        return new String(Base64.decodeBase64(input.getBytes()));
    }
}
