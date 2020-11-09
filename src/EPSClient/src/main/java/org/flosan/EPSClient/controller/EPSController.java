package org.flosan.EPSClient.controller;

import org.flosan.EPSClient.security.AES;
import org.flosan.EPSClient.security.RSA;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.net.*;
import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class EPSController {
    private Socket socket;
    private SecretKey aesKey;
    private DataOutputStream outputStream;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;
    private DataInputStream inputStream;
    private RSAPublicKey rsaPubKey;
    private String sessionID;

    public EPSController() {
        try {
            String pubKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAg" +
                    "EAsoZlbpFmxoEN6ft5MaROls2HXEFzZmOMxZbrF7XRKD1cod/4" +
                    "Q7RPGEtnD+3Dc7/2ogB3i9L+eLOxDg6gr0CiMnxF10dTlnw2Iv" +
                    "TZE6gzMz6Ulse1ImCIWsujq5fgDX3KC0XjWQD6oHqz8ZBImbTv" +
                    "57CKKeRiWIL6CCfjYSWSSLix/ck5ZXGBVcJ9NCw58LETASs4MJ" +
                    "EniDfO7UzCMGSgiPmGd51w2meUfvsrlz/cYVigLR87gbw4XHQr" +
                    "O547MSahHZTbCBaaFvfhlPNAzcVqTGcyHjcGX7pR9rVkSr4KbK" +
                    "4gU2WPQk0N2zfOjuLkrWVHHjFtvnl6Y2aIxDjglHfyntzRNQNC" +
                    "l9Ic2TZ6j4hPZHDdGCpVBAOvxgL6tn7quYCP2OoVg3t7ySWLcd" +
                    "Au0nrIhWaqWC8wDi7yznhgIrvEI/geL5pE0AHp2IhK4W3Kxqh5" +
                    "0NyUksXQa51uNz9N7vAQlvfhMyGajLqcjXKFljKGptVT7JmyfZ" +
                    "wIjUsqMA9zjwJzSeC68ihJA2vvMwCkRkZ8XJ0oJ4ojYOpza3EN" +
                    "cUNFCJoTSwV7jfAqyEp0PByb5kfZBKdA2WHBOOq6zmxNht7V6v" +
                    "tvBVbJ80zQcIRWKhtVFdL8wvYzXmAd2Xe315SD+PIos2XIaoSc" +
                    "fVdHiHrFBK8kZhuUWiHPn0K+56AroG+vGOMCAwEAAQ==";

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pubKey));
            this.rsaPubKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

            InetAddress ip = InetAddress.getByName("localhost");
            this.aesKey = AES.getAESKey();
            this.socket = new Socket(ip, 6666);
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.objectOut = new ObjectOutputStream(this.outputStream);
            this.objectIn = new ObjectInputStream(this.inputStream);
            System.err.println("DEBUG: " + inputStream.readUTF());
            System.out.println(inputStream.readUTF());
        } catch (UnknownHostException e) {
            System.err.println("ERR: Unknown Host!");
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

    }

    public List<String> sendOperation(String opID, List<String> args) {
        List<SealedObject> packet = new ArrayList<>();
        packet.add(RSA.Encrypt(this.rsaPubKey, this.aesKey));
        packet.add(AES.Encrypt(opID, this.aesKey));
        for (String arg : args)
            packet.add(AES.Encrypt(arg, this.aesKey));
        System.err.println("DEBUG: " + "OPID: " + opID + " ARGS: " + args.toString());
        try {
            objectOut.writeObject(packet);
            return receiveOperation(opID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> receiveOperation(String opID) {
        List<SealedObject> response;
        List<String> unencResponse = new ArrayList<>();
        switch (opID){
            case "2":
                try {
                    response = (List<SealedObject>) objectIn.readObject();
                    if(response.size() == 1){
                        unencResponse.add(AES.Decrypt(response.get(0), aesKey));
                        this.sessionID = unencResponse.get(0);
                        return unencResponse;
                    }
                    else{
                        return null;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }

        return null;
    }

}
