package org.flosan.EPSClient.security;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AES {

    public static SecretKey getAESKey() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256, SecureRandom.getInstanceStrong());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert keyGen != null;
        return keyGen.generateKey();
    }

    public static SealedObject Encrypt(String toEncrypt, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new SealedObject(toEncrypt, cipher);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;

    }
    public static String Decrypt(SealedObject toDecrypt, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return (String) toDecrypt.getObject(cipher);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | ClassNotFoundException | IOException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;

    }
}
