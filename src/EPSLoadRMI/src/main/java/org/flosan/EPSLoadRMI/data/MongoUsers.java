package org.flosan.EPSLoadRMI.data;

import com.mongodb.Mongo;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoUsers implements MongoAccessInterface {
    private MongoClient client;

    public MongoUsers(){
        connect();
    }
    public void connect() {
        MongoClientURI uri = new MongoClientURI("mongodb://loadBalancer:user123@192.168.1.115/users");
        this.client = new MongoClient(uri);
    }
}
