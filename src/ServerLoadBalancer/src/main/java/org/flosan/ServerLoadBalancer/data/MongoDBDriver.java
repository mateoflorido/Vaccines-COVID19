package org.flosan.ServerLoadBalancer.data;

import com.mongodb.client.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import java.util.Date;

public class MongoDBDriver {
    private final MongoClient client;

    public MongoDBDriver(String dbURI) {
        this.client = MongoClients.create(dbURI);
    }

    public boolean insertNewUser(String username, String password) {
        if (getUser(username) != null) {
            MongoDatabase usersDB = this.client.getDatabase("users");
            MongoCollection<Document> loginTB = usersDB.getCollection("login");
            Document eps = new Document("_id", new ObjectId());
            eps.append("cypherkey", "TODO")
                    .append("username", username)
                    .append("password", password);
            loginTB.insertOne(eps);
            System.out.println(eps);
            return true;
        }
        return false;

    }

    public Document getUserCredentials(String username, String password) {
        MongoDatabase database = this.client.getDatabase("users");
        MongoCollection<Document> collection = database.getCollection("login");
        Document retrieve = collection.find(
                Filters.and(
                        Filters.eq("username", username),
                        Filters.eq("password", password)
                )).first();
        System.err.println("Gathered: " + retrieve);
        return retrieve;
    }

    public Document getUser(String username) {
        MongoDatabase database = client.getDatabase("users");
        MongoCollection<Document> collection = database.getCollection("login");
        Document retrieve = collection.find(
                Filters.and(
                        Filters.eq("username", username)
                )).first();
        System.out.println("Gathered Checking: \n" + retrieve);
        return retrieve;
    }

    public String insertSession(String username, Date logged) {
        MongoDatabase usersDB = this.client.getDatabase("users");
        MongoCollection<Document> sessionTB = usersDB.getCollection("sessions");
        Document session = new Document("_id", new ObjectId());
        session.append("username", username)
                .append("date_logged", logged);
        WriteConcern wc = null;
        InsertOneResult resp = sessionTB.insertOne(session);
        if(resp.wasAcknowledged()){
            System.err.println("Session from " + username + session + " ACK ID: " + resp.getInsertedId().toString());
            return resp.getInsertedId().toString();
            // return session.getObjectId("_id").toString();
        }
        else
            return null;
    }

}
