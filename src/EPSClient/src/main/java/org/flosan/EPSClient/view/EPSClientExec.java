package org.flosan.EPSClient.view;

import org.flosan.EPSClient.controller.EPSController;
import org.flosan.EPSClient.security.AES;
import org.flosan.EPSClient.security.SHA;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class EPSClientExec {

    public static void main(String[] args) throws Exception {
        AtomicBoolean inUse = new AtomicBoolean(true);
        Scanner scanner = new Scanner(System.in);
        EPSController controller = new EPSController();
        String sessionID = null;
        String username = "";
        List<String> opArgs = new ArrayList<>();

        while (inUse.get()) {
            switch (scanner.nextLine().trim()) {
                case "0":
                case "1":
                    System.out.println("\n Welcome to COVID19 Vaccine Distribution:" +
                            "\n\t Please fill the following information prior to register." +
                            "\n NIT: ");
                    opArgs.add(scanner.nextLine());
                    System.out.println("IPS Name: ");
                    opArgs.add(scanner.nextLine());
                    System.out.println("DC (Distribution Center) Address: ");
                    opArgs.add(scanner.nextLine());
                    System.out.println("Phone Number: ");
                    opArgs.add(scanner.nextLine());
                    System.out.println("Username: ");
                    opArgs.add(scanner.nextLine());
                    System.out.println("Password: ");
                    opArgs.add(SHA.GetSHA512(scanner.nextLine()));
                    List<String> opResponse = controller.sendOperation("1", opArgs);
                    if( opResponse != null){
                        System.out.println(opResponse.get(0));
                    }
                    break;
                case "2":
                    System.out.println("Username: ");
                    username = scanner.nextLine();
                    opArgs.add(username);
                    System.out.println("Password: ");
                    opArgs.add(SHA.GetSHA512(scanner.nextLine()));
                    if (controller.sendOperation("2", opArgs) != null)
                        System.out.println("--- Welcome " + username + " ---");
                    else
                        System.out.println("**** ERROR LOGIN IN, CHECK CREDENTIALS ****");
                        opArgs.clear();
                    break;
            }
            System.out.println(controller.getUTF8());
            opArgs.clear();

        }
    }
}
