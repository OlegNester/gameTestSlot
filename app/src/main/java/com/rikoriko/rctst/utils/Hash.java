package com.rikoriko.rctst.utils;

import java.util.Random;

public abstract class Hash {

    public static String generateHash(int hashString) {
        String DATA = "abcdefghijklmnopqrstuvwxyz";
        Random RANDOM = new Random();
        StringBuilder stringBuilder = new StringBuilder(hashString);

        for (int i = 0; i < hashString; i++) {
            stringBuilder.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }
        return String.valueOf(stringBuilder);
    }
}
