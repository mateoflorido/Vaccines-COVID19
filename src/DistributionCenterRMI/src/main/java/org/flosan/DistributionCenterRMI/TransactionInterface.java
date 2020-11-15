package org.flosan.DistributionCenterRMI;

import javax.crypto.SealedObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;


public interface TransactionInterface extends Remote{
    boolean getHeartBeat() throws RemoteException;
    boolean commitTransaction(String id) throws RemoteException;
}
