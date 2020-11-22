package org.flosan.DistributionCenterRMI;

import com.mongodb.client.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Iterator;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.inc;

public class MongoDBDriver {
    private final MongoClient client;

    public MongoDBDriver(String dbURI) {
        this.client = MongoClients.create(dbURI);
    }

    public String commitTransaction(String id) {
        MongoDatabase journal = this.client.getDatabase("journals");
        MongoCollection<Document> transaction = journal.getCollection("queued");
        Document t = transaction.findOneAndDelete(eq("_id", new ObjectId(id)));
        String identifier = id + t.getString("type") + " : " + t.getInteger("quantity");
        Date inserted = t.getDate("date");
        MongoCollection<Document> commitedJournal = journal.getCollection("journal");
        FindIterable<Document> txc = commitedJournal.find(and(
                gte("datecommited", inserted),
                eq("type", t.getString("type"))
        ));
        Iterator it = txc.iterator();
        if (it.hasNext()) {
            addToRegistry(t, false,"OCC");
            return "Transaction: " + identifier + " Cannot be acomplished. Please try again.";
        }
        if (changeStock(t.getString("type"), t.getInteger("quantity"))) {
            if (placeOrder(t)) {
                addToRegistry(t, true,"OK");
                return "Success. Order Placed with Reference: " + " " + identifier;
            }
        } else {
            addToRegistry(t, false,"NotEnoughStock");
            return "Transaction: " + identifier + "Error changing stock, please check again the current stock.";
        }
        addToRegistry(t, false,"UNKNOWN");
        return "Unexpected error when parsing transaction. Please try again later.";
    }

    public boolean placeOrder(Document tx) {
        MongoDatabase journal = this.client.getDatabase("journals");
        MongoCollection<Document> transaction = journal.getCollection("journal");
        Document commited = new Document("_id", new ObjectId());
        commited.append("datecommited", new Date())
                .append("dateplaced", tx.getDate("date"))
                .append("quantity", tx.getInteger("quantity"))
                .append("type", tx.getString("type"))
                .append("sessionid", tx.getString("sessionid"));
        return transaction.insertOne(commited).wasAcknowledged();
    }

    public boolean changeStock(String type, int quantity) {
        MongoDatabase lab = this.client.getDatabase("lab");
        MongoCollection<Document> transaction = lab.getCollection("vaccines");
        UpdateResult updateResult = transaction.updateOne(eq("type", type), inc("stock", quantity * -1));
        if (updateResult.wasAcknowledged()) {
            FindIterable<Document> currentStock = transaction.find(and(eq("type", type), lt("stock", 0)));
            Iterator it = currentStock.iterator();
            if (it.hasNext()) {
                transaction.updateOne(eq("type", type), inc("stock", quantity));
                return false;
            }
        }
        return updateResult.wasAcknowledged();
    }

    public void addToRegistry(Document t, boolean acknowledge, String reason){
        MongoDatabase journal = this.client.getDatabase("journals");
        MongoCollection<Document> registry = journal.getCollection("registry");
        Document commited = new Document("_id", new ObjectId());
        commited.append("quantity", t.getInteger("quantity"))
                .append("type", t.getString("type"))
                .append("dateplaced", t.getDate("date"))
                .append("dateserved", new Date())
                .append("acknowledge", acknowledge)
                .append("reason", reason)
                .append("sessionid", t.getString("sessionid"));

    }
}
