package org.flosan.DistributionCenterRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TransactionHandler extends Thread {
    private String host;
    private String id;

    public TransactionHandler(String host, String id) {
        this.host = host;
        this.id = id;
    }

    @Override
    public void run() {
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(this.host);
            TransactionInterface stub = (TransactionInterface) registry.lookup("TransactionInterface");
            stub.commitTransaction(id);

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

}
