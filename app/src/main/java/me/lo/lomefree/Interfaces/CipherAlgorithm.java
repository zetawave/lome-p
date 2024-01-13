package me.lo.lomefree.Interfaces;

public interface CipherAlgorithm
{
    String AES128 = "A128";
    String AES256 = "A256";
    String BLOWFISH_MD5 = "BMD5";
    String BLOWFISH_SHA256 = "B256";
    String SHA256 = "SHA-256";
    String MD5 = "MD5";

    String AES_KEY_KEYCODE = "AES";
    String BLOWFISH_KEY_KEYCODE = "Blowfish";

    String AES_MODE_CIPHER = "AES/CTR/NoPadding";
    String BLOWFISH_MODE_CIPHER = "Blowfish/CTR/NoPadding";

    int ALG_INFO_LENGHT = 4;

    int AES_IV = 16;
    int BLOWFISH_IV = 8;

    byte SPECIAL_BYTE = 0x7;
}
