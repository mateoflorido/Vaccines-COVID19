package org.flosan.ServerLoadBalancer.data;

import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MongoDBDriver {
    private final MongoClient client;

    public MongoDBDriver(String dbURI) {
        this.client = MongoClients.create(dbURI);
    }

    public boolean insertNewUser(List<String> userData) {
        MongoDatabase usersDB = this.client.getDatabase("users");
        MongoCollection<Document> loginTB = usersDB.getCollection("login");
        Document ips = new Document("_id", new ObjectId());
        ips.append("nit", userData.get(0))
                .append("name", userData.get(1))
                .append("dc", userData.get(2))
                .append("phone", userData.get(3))
                .append("username", userData.get(4))
                .append("password", userData.get(5));
        System.out.println(ips);
        return loginTB.insertOne(ips).wasAcknowledged();
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

    public Document getNIT(String nit) {
        MongoDatabase database = client.getDatabase("users");
        MongoCollection<Document> collection = database.getCollection("login");
        Document retrieve = collection.find(
                Filters.and(
                        Filters.eq("nit", nit)
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
        InsertOneResult resp = sessionTB.insertOne(session);
        if (resp.wasAcknowledged()) {
            System.err.println("Session from " + username + session + " ACK ID: " + Objects.requireNonNull(resp.getInsertedId()).toString().replace("BsonObjectId{value=", "") + " INSERTED ID: " + session.getObjectId("_id").toString());
            return session.getObjectId("_id").toString();
        } else
            return null;
    }

}
