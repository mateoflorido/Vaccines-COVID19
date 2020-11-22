package org.flosan.DistributionCenterRMI.security;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AES {
    public static SecretKey getAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256, SecureRandom.getInstanceStrong());
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
    public static Object Decrypt(SealedObject toDecrypt, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return toDecrypt.getObject(cipher);
        } catch (NoSuchAlgorithmException | ClassNotFoundException | IOException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;

    }
}
