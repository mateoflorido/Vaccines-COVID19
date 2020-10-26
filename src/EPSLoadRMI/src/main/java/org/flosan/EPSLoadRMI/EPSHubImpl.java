package org.flosan.EPSLoadRMI;

import org.bson.Document;
import org.flosan.EPSLoadRMI.data.MongoUsers;
import org.flosan.EPSLoadRMI.security.AES;
import org.flosan.EPSLoadRMI.security.RSAServer;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.rmi.RemoteException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Date;

public class EPSHubImpl implements EPSHubInterface {
    private KeyPair keychain;
    private MongoUsers dbUsers;

    public String heartBeat() throws RemoteException {
        return ("-+-+-+ Welcome to COVID19 Vaccine Distribution Center +-+-+-");

    }

    public String login(SealedObject so, String username, String password) throws RemoteException {
        PrivateKey privateKey = RSAServer.generateRSAPrivate();
        SecretKey userKey = RSAServer.Decrypt(privateKey,so);
        username = AES.Decrypt(username, userKey);
        password = AES.Decrypt(password, userKey);
        System.err.println("DEBUG: User: " + username + " Password: " + password);
        if (this.dbUsers == null)
            this.dbUsers = new MongoUsers();
        Document retrieve = this.dbUsers.getUserCredentials(username, password);
        if (retrieve.containsKey("username") && retrieve.containsKey("password")) {
            return (this.dbUsers.insertSession(username, new Date()));
        }
        return null;
    }

    public boolean register(SealedObject so, String username, String password) throws RemoteException {
        PrivateKey privateKey = RSAServer.generateRSAPrivate();
        SecretKey userKey = RSAServer.Decrypt(privateKey,so);
        username = AES.Decrypt(username, userKey);
        password = AES.Decrypt(password, userKey);
        System.err.println("DEBUG: User: " + username + " Password: " + password);
        if (this.dbUsers == null)
            this.dbUsers = new MongoUsers();
        if(this.dbUsers.insertNewUser(username, password)){
            return true;
        }
        return false;

    }

    public boolean logout(SealedObject so, String username) throws RemoteException {
        PrivateKey privateKey = RSAServer.generateRSAPrivate();
        SecretKey userKey = RSAServer.Decrypt(privateKey,so);
        username = AES.Decrypt(username, userKey);
        System.err.println("DEBUG: Logging out User: " + username );
        return true;

    }

}
