package org.flosan.EPSLoadRMI.data;

import com.mongodb.client.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import java.util.Date;

public class MongoUsers implements MongoAccessInterface {
    private MongoClient client;

    public MongoUsers() {
        connect();
    }

    public void connect() {
        this.client = MongoClients.create("mongodb://loadBalancer:user123@192.168.1.115/users");
    }

    public void insertNewUser(String username, String password) {
        MongoDatabase usersDB = this.client.getDatabase("users");
        MongoCollection<Document> loginTB = usersDB.getCollection("login");
        Document eps = new Document("_id", new ObjectId());
        eps.append("cypherkey", "TODO")
                .append("username", username)
                .append("password", password);
        loginTB.insertOne(eps);
        System.out.println(eps);
        return;
    }

    public Document getUserCredentials(String username, String password) {
        MongoDatabase database = client.getDatabase("users");
        MongoCollection<Document> collection = database.getCollection("login");
        Document retrieve = collection.find(
                Filters.and(
                        Filters.eq("username", username),
                        Filters.eq("password", password)
                )).first();
        System.out.println("Gathered: \n" + retrieve);
        return retrieve;
    }

    public String insertSession(String username, Date logged) {
        MongoDatabase usersDB = this.client.getDatabase("users");
        MongoCollection<Document> sessionTB = usersDB.getCollection("sessions");
        Document session = new Document("_id", new ObjectId());
        session.append("username", username)
                .append("date_logged", logged);
        WriteConcern wc = null;
        try {
            sessionTB.insertOne(session);

        } catch (MongoException e) {

        }
        System.out.println("Session from " + username + session);
        return session.getObjectId("_id").toString();
    }
}
