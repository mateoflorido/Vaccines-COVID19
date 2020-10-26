package org.flosan.EPSLoadRMI;

import org.flosan.EPSLoadRMI.security.AES;
import org.flosan.EPSLoadRMI.security.RSA;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.awt.desktop.UserSessionEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.flosan.EPSLoadRMI.security.SHA512.GetSHA512;

public class EPSClient {
    private EPSClient() {

    }

    public static void main(String[] args) {
        try {
            String username = "";
            String arg1;
            String arg2;
            Scanner input = new Scanner(System.in);
            AtomicBoolean inUse = new AtomicBoolean(true);
            String sessionID = null;
            SealedObject testKey;

            SecretKey aesKey = AES.getAESKey();
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
            RSAPublicKey rsaPubKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

            // Connection to RMI
            Registry registry = LocateRegistry.getRegistry(null);
            EPSHubInterface stub = (EPSHubInterface) registry.lookup("EPSHubInterface");
            // Testing RMI Heartbeat
            System.out.println(stub.heartBeat());
            PrintMenu();

            while (inUse.get()) {
                switch (input.nextLine().trim()) {
                    case "0":
                        if (sessionID != null) {
                            inUse.set(false);
                            testKey = RSA.Encrypt(rsaPubKey, aesKey);
                            arg1 = AES.Encrypt(username, aesKey);
                            stub.logout(testKey, username);
                            sessionID = null;
                        }
                        else{
                            System.out.println("Not Logged yet.");
                        }
                        break;
                    case "1":
                        System.out.println("Username:");
                        username = input.nextLine();
                        arg1 = AES.Encrypt(username, aesKey);
                        System.err.println("DEBUG: AES ENCRYPTED USER: " + arg1);
                        System.out.println("Password:");
                        arg2 = GetSHA512(input.nextLine());
                        testKey = RSA.Encrypt(rsaPubKey, aesKey);
                        if (stub.register(testKey, arg1, arg2)) ;
                        System.err.println(arg1 + " " + arg2);
                        break;
                    case "2":
                        System.out.println("Username:");
                        arg1 = AES.Encrypt(input.nextLine(), aesKey);
                        System.err.println("DEBUG: AES ENCRYPTED USER: " + arg1);
                        System.out.println("Password:");
                        arg2 = AES.Encrypt(GetSHA512(input.nextLine()), aesKey);
                        System.err.println("DEBUG: AES ENCRYPTED PASS: " + arg2);

                        testKey = RSA.Encrypt(rsaPubKey, aesKey);
                        sessionID = stub.login(testKey, arg1, arg2);
                        if (sessionID != null) {
                            System.out.println("----- Welcome -----");
                            System.err.println("DEBUG: SessionID --> " + sessionID);
                        } else {
                            System.err.println("--- ERROR Login In --- \n Check Credentials");
                        }
                        System.err.println("DEBUG: " + arg1 + " " + arg2);
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void PrintMenu() {
        // Move this function to Interface
        String menu = "------ COVID19 Vaccine Distribution -------\n" +
                "Menu:\n" +
                "\t0. Exit\n" +
                "\t1. Register\n" +
                "\t2. Login\n";
        System.out.println(menu);

    }
}