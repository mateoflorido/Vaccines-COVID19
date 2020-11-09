package org.flosan.EPSClient.view;

import org.flosan.EPSClient.controller.EPSController;
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
                    System.out.println("Not Implemented");
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
                    break;
            }

        }
    }
}
