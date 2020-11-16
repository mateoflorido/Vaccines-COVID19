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
        String username;
        List<String> opArgs = new ArrayList<>();

        while (inUse.get()) {
            switch (scanner.nextLine().trim()) {
                //TODO Implement the menu as an operation to avoid null pointer.
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
                    if (opResponse != null) {
                        System.out.println(opResponse.get(0));
                    }
                    break;
                case "2":
                    System.out.println("Username: ");
                    username = scanner.nextLine();
                    opArgs.add(username);
                    System.out.println("Password: ");
                    opArgs.add(SHA.GetSHA512(scanner.nextLine()));
                    List<String> session = controller.sendOperation("2", opArgs);
                    if (session != null) {
                        sessionID = session.get(0);
                        System.out.println("--- Welcome " + username + " ---");
                    } else
                        System.out.println("**** ERROR LOGIN IN, CHECK CREDENTIALS ****");
                    opArgs.clear();
                    break;
                case "3":
                    if (sessionID != null) {
                        System.out.println("\n\n--* Methods Available *--\n\t 0. CLI");
                        List<String> stck = controller.sendOperation("3", opArgs);
                        System.out.println("Stock Available: \n");
                        int typeC = 1;
                        for (String stock : stck) {
                            System.out.println("COV19VAC" + typeC + " : " + stock);
                            typeC++;
                        }
                        System.out.println("\n\n--* Methods Available *--\n\t 0. CLI");
                        boolean exit = false;
                        List<String> quantities = new ArrayList<>();
                        quantities.add("0");
                        quantities.add("0");
                        quantities.add("0");
                        String next;
                        while (!exit) {
                            System.out.println("Please choose the vaccine: " +
                                    "\n\t 0. COV19VAC1" +
                                    "\n\t 1. COV19VAC2" +
                                    "\n\t 2. COV19VAC3" +
                                    "\n\t Or write \"exit\" to finish\n");

                            next = scanner.nextLine();
                            if (next.equalsIgnoreCase("exit")) {
                                exit = true;
                            } else {
                                int index = Integer.parseInt(next);
                                if (index <= 2 && index >= 0) {
                                    System.out.println("Write number of doses: ");
                                    String doses = scanner.nextLine();
                                    if (Integer.parseInt(stck.get(index)) - Integer.parseInt(doses) >= 0) {
                                        quantities.add(index, doses);
                                        quantities.remove(index + 1);
                                    }
                                    else{
                                        System.err.println("Doses exceeds the stock!");
                                        System.out.println("Stock Available: \n");
                                        typeC = 1;
                                        for (String stock : stck) {
                                            System.out.println("COV19VAC" + typeC + " : " + stock);
                                        }
                                        System.out.println("\n");

                                    }

                                }
                            }

                        }
                        System.err.println("DEBUG: QUANTITIES -> " + quantities.toString());
                        int sum = 0;
                        controller.sendOperation("3.0", quantities);
                    } else {
                        System.err.println("Please login.");
                    }
                    break;
            }
            if (sessionID != null) {
                System.out.println(controller.getUTF8());
            }
            opArgs.clear();
        }
    }
}
