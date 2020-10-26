package org.flosan.EPSLoadRMI;

import javax.crypto.SealedObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

public interface EPSHubInterface extends Remote {
    String heartBeat() throws RemoteException;
    String login(SealedObject so, String username, String password) throws RemoteException, ServerNotActiveException;
    boolean register( SealedObject so, String username, String password ) throws RemoteException;
    boolean logout(SealedObject so, String username) throws RemoteException;

}
