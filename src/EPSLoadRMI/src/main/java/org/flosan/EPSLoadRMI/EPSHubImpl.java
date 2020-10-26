package org.flosan.EPSLoadRMI;

import com.mongodb.client.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.flosan.EPSLoadRMI.data.MongoUsers;
import org.flosan.EPSLoadRMI.security.AES;
import org.flosan.EPSLoadRMI.security.RSA;
import org.flosan.EPSLoadRMI.security.RSAServer;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Date;

public class EPSHubImpl implements EPSHubInterface {
    private KeyPair keychain;
    private MongoUsers dbUsers;

    public String heartBeat() throws RemoteException {
        return ("-+-+-+ Welcome to COVID19 Vaccine Distribution Center +-+-+-");

    }

    public String login(SealedObject so, String username, String password) throws RemoteException, ServerNotActiveException {
        PrivateKey pk = RSAServer.generateRSAPrivate();
        SecretKey uk = RSAServer.Decrypt(pk,so);
        username = AES.Decrypt(username, uk);
        password = AES.Decrypt(password, uk);
        System.err.println("DEBUG: User: " + username + " Password: " + password);
        if (this.dbUsers == null)
            this.dbUsers = new MongoUsers();
        Document retrieve = this.dbUsers.getUserCredentials(username, password);
        if (retrieve.containsKey("username") && retrieve.containsKey("password")) {
            return (this.dbUsers.insertSession(username, new Date()));
        }
        return null;
    }

    public boolean register(String username, String password) throws RemoteException {
        if (this.dbUsers == null)
            this.dbUsers = new MongoUsers();
        this.dbUsers.insertNewUser(username, password);
        return true;
    }

}
