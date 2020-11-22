package org.flosan.DistributionCenterRMI;

import org.flosan.ServerLoadBalancer.security.AES;
import org.flosan.ServerLoadBalancer.security.RSA;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TransactionHandler extends Thread {
    private final String host;
    private final String id;
    private final AtomicReference<String> response;
    private final SecretKey sk;

    public TransactionHandler(String host, String id, AtomicReference<String> response, SecretKey sk) {
        this.host = host;
        this.id = id;
        this.response = response;
        this.sk = sk;
    }

    @Override
    public void run() {
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(this.host);
            TransactionInterface stub = (TransactionInterface) registry.lookup("TransactionInterface");
            List<SealedObject> transaction = new ArrayList<>();
            RSAPublicKey rmirsa = RSA.getRMIRSA();
            transaction.add(RSA.Encrypt(rmirsa, this.sk));
            transaction.add(AES.Encrypt(id, sk));
            response.set((String) AES.Decrypt(stub.commitTransaction(transaction).get(0), this.sk));

        } catch (RemoteException | NotBoundException e) {
            this.response.set("404");
        }
    }

}
