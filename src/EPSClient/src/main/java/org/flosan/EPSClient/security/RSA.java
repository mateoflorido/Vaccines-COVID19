package org.flosan.EPSClient.security;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;

public class RSA {
    public static SealedObject Encrypt(RSAPublicKey pubKey, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return new SealedObject(key, cipher);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | IOException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
