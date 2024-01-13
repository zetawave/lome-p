package me.lo.lomefree.Interfaces;

import java.util.HashMap;

public interface ShredSchemes
{
    /*Encoding scheme to attack for passes in range (5-35) otherwise Random*/
    HashMap<Integer, Integer> GUTTMAN_SCHEME = new HashMap<Integer, Integer>(){
    {
        put(5, 0x555555);
        put(6, 0xAAAAAA);
        put(7, 0x924924);
        put(8, 0x492492);
        put(9, 0x249249);
        put(10, 0x000000);
        put(11, 0x111111);
        put(12, 0x222222);
        put(13, 0x333333);
        put(14, 0x444444);
        put(15, 0x555555);
        put(16, 0x666666);
        put(17, 0x777777);
        put(18, 0x888888);
        put(19, 0x999999);
        put(20, 0xAAAAAA);
        put(21, 0xBBBBBB);
        put(22, 0xCCCCCC);
        put(23, 0xDDDDDD);
        put(24, 0xEEEEEE);
        put(25, 0xFFFFFF);
        put(26, 0x924924);
        put(27, 0x492492);
        put(28, 0x249249);
        put(29, 0x6DB6DB);
        put(30, 0xB6DB6D);
        put(31, 0xDB6DB6);
    }
    };
}
