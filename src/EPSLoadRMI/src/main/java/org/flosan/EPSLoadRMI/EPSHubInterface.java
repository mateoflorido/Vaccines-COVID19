package org.flosan.EPSLoadRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EPSHubInterface extends Remote {
    void heartBeat() throws RemoteException;
    boolean login(String username, String password) throws RemoteException;
    boolean singup( String username, String password );

}
