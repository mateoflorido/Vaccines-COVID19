package org.flosan.EPSLoadRMI;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


import java.rmi.RemoteException;

public class EPSHubImpl implements EPSHubInterface{
    public void heartBeat() throws RemoteException {
        System.out.println("Working");

    }
    public boolean login(String username, String password) throws RemoteException {

        // MongoCredential credential = MongoCredential.createCredential("loadBalancer", "users", pass);
        MongoClientURI uri = new MongoClientURI("mongodb://loadBalancer:user123@192.168.1.115/users");
        // MongoClient client = MongoClients.create("mongodb://loadBalancer:user123@192.168.1.115/users");
        MongoClient client = new MongoClient(uri);
        MongoDatabase database = client.getDatabase("users");
        MongoCollection<Document> collection = database.getCollection("login");
        Document retrieve = collection.find(Filters.eq("username", "testuser")).first();
        System.out.println(retrieve);

        return false;
    }

    public boolean singup(String username, String password) {

        return false;
    }

}
