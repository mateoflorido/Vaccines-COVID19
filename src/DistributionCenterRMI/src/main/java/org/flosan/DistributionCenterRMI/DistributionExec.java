package org.flosan.DistributionCenterRMI;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class DistributionExec {
    public static void main(String[] args) {
        // System.setProperty("java.rmi.server.hostname","picasso.localdomain");
        System.err.println(System.getProperty("java.rmi.server.hostname"));
        try {
            TransactionImpl obj = new TransactionImpl();
            TransactionInterface stub = (TransactionInterface) UnicastRemoteObject.exportObject(obj, 6667);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("TransactionInterface", stub);
            System.out.println("*--- RMI Initialized ---*");
        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }


    }
}