package org.flosan.EPSLoadRMI;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LBServer extends EPSHubImpl {
    public LBServer() {
    }

    public static void main(String[] args) {
        try {
            EPSHubImpl obj = new EPSHubImpl();
            EPSHubInterface stub = (EPSHubInterface) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("EPSHubInterface", stub);
            System.out.println("Server ready...");


        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
