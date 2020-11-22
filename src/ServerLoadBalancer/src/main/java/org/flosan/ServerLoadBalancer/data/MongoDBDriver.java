package org.flosan.ServerLoadBalancer.data;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

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
        return retrieve;
    }

    public Document getUser(String username) {
        MongoDatabase database = client.getDatabase("users");
        MongoCollection<Document> collection = database.getCollection("login");
        Document retrieve = collection.find(
                Filters.and(
                        Filters.eq("username", username)
                )).first();
        return retrieve;
    }

    public Document getNIT(String nit) {
        MongoDatabase database = client.getDatabase("users");
        MongoCollection<Document> collection = database.getCollection("login");
        Document retrieve = collection.find(
                Filters.and(
                        Filters.eq("nit", nit)
                )).first();
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
            return session.getObjectId("_id").toString();
        } else
            return null;
    }

    public List<String> getStock() {
        MongoDatabase labDB = this.client.getDatabase("lab");
        MongoCollection<Document> vaccines = labDB.getCollection("vaccines");
        List<String> response = new ArrayList<>();
        FindIterable<Document> allVaccines = vaccines.find();
        Iterator<Document> it = allVaccines.iterator();
        List<Integer> stock = new ArrayList<>();
        while (it.hasNext()) {
            Document vac = it.next();
            response.add(String.valueOf(vac.getInteger("stock")));
            stock.add(vac.getInteger("stock"));
        }
        return response;
    }

    public void insertNewRouted(String host, String sessionID) {
        MongoDatabase journalsDB = this.client.getDatabase("journals");
        MongoCollection<Document> balanced = journalsDB.getCollection("balancer");
        Document document = new Document("_id", new ObjectId());
        document.append("sessionid", sessionID)
                .append("date", new Date())
                .append("host", host);
        balanced.insertOne(document);

    }

    public String insertTransaction(String type, int quantity, String sessionID) {
        MongoDatabase journalsDB = this.client.getDatabase("journals");
        MongoCollection<Document> queuedTB = journalsDB.getCollection("queued");
        Document document = new Document("_id", new ObjectId());
        document.append("sessionid", sessionID)
                .append("date", new Date())
                .append("quantity", quantity)
                .append("type", type);
        InsertOneResult resp = queuedTB.insertOne(document);
        if (resp.wasAcknowledged()) {
            return document.getObjectId("_id").toString();
        } else
            return null;

    }

}
