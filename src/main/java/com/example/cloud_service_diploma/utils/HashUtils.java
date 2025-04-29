package com.example.cloud_service_diploma.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    public static byte[] hash(String login, String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(login.getBytes());
        md.update("-|**|-".getBytes());
        md.update(password.getBytes());
        return md.digest();
    }
}