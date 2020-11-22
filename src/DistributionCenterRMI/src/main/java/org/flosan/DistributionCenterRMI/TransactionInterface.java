package org.flosan.DistributionCenterRMI;

import javax.crypto.SealedObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface TransactionInterface extends Remote{
    boolean getHeartBeat() throws RemoteException;
    List<SealedObject> commitTransaction(List<SealedObject> transaction) throws RemoteException;
}
