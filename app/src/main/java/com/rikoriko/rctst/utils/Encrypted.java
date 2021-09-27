package com.rikoriko.rctst.utils;

public class Encrypted {
    public static final int shift = 44353;

    public static String s(String code){
        StringBuilder result = new StringBuilder();
        int size = code.length();
        for(int i = 0; i < size; i++){
            int index = (i + shift) % size;
            int ch = (int)(code.charAt((size - 1) - index));
            result.append((char)(ch - 1));
        }
        return result.toString();
    }
}