package org.flosan.EPSLoadRMI;

import java.nio.charset.StandardCharsets;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class EPSClient {
    private EPSClient(){

    }
    public static void main(String[] args){
        try{
            Registry registry = LocateRegistry.getRegistry(null);
            EPSHubInterface stub = (EPSHubInterface) registry.lookup("EPSHubInterface");
            stub.heartBeat();
            Scanner input = new Scanner(System.in);
            System.out.println( "Password: " );
            String password = get_SHA512_SecurePass(input.nextLine());
            System.out.println(password);

            stub.login("","");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get_SHA512_SecurePass( String toHash ){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(toHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }


}