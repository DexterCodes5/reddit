package dev.dex.reddit.util;

import java.util.Random;

public class StringUtils {
    public static String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomString = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            int randomIdx = rand.nextInt(characters.length());
            char randomChar = characters.charAt(randomIdx);
            randomString.append(randomChar);
        }
        return randomString.toString();
    }
}
