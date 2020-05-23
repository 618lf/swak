package com.swak.codec;

/**
 * 短连接
 *
 * @author: lifeng
 * @date: 2020/3/29 10:53
 */
public class ShortUrls {

    private static String[] chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8",
            "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    private static int[] encodeNumber = new int[]{4, 6};

    /**
     * 生存短网址
     *
     * @param url 地址
     * @return 多个短连接
     */
    public static String[] encode(String url) {
        String defaultKey = "swak";
        return encode(url, defaultKey);
    }

    /**
     * 生存短网址
     *
     * @param url  地址
     * @param salt 混淆
     * @return 多个短连接
     */
    public static String[] encode(String url, String salt) {
        String hex = (Digests.md5(salt + url));
        String[] resUrl = new String[4];
        for (int i = 0; i < encodeNumber[0]; i++) {
            String sTempSubString = hex.substring(i * 8, i * 8 + 8);
            long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
            StringBuilder outChars = new StringBuilder();
            for (int j = 0; j < encodeNumber[1]; j++) {
                long index = 0x0000003D & lHexLong;
                outChars.append(chars[(int) index]);
                lHexLong = lHexLong >> 5;
            }
            resUrl[i] = outChars.toString();
        }
        return resUrl;
    }
}