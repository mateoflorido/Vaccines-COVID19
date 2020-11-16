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

    public boolean commitTransaction(String id) {
        MongoDatabase journal = this.client.getDatabase("journals");
        MongoCollection<Document> transaction = journal.getCollection("queued");
        Document t = transaction.find(eq("_id", new ObjectId(id))).first();
        System.err.println("DEBUG: RMI FOUND --> " + t.toString());
        Date inserted = t.getDate("date");
        MongoCollection<Document> commitedJournal = journal.getCollection("journal");
        FindIterable<Document> txc = commitedJournal.find(and(
                gte("datecommited", inserted),
                eq("type", t.getString("type"))
        ));
        Iterator it = txc.iterator();
        if (it.hasNext()) {
            return false;
        } else {

        }
        //DEBUG ZONE
        if(changeStock(t.getString("type"), t.getInteger("quantity"))){
            placeOrder(t);
        }


        return true;

    }

    public boolean placeOrder(Document tx) {
        MongoDatabase journal = this.client.getDatabase("journals");
        MongoCollection<Document> transaction = journal.getCollection("journal");
        Document commited = new Document("_id", new ObjectId());
        commited.append("datecommited", tx.getDate("date"))
                .append("quantity", tx.getInteger("quantity"))
                .append("type", tx.getString("type"))
                .append("sessionid", tx.getString("sessionid"));
        return transaction.insertOne(commited).wasAcknowledged();
    }

    public boolean changeStock(String type, int quantity){
        MongoDatabase lab = this.client.getDatabase("lab");
        MongoCollection<Document> transaction = lab.getCollection("vaccines");
        UpdateResult updateResult = transaction.updateOne(eq("type", type), inc("stock", quantity * -1));
        return updateResult.wasAcknowledged();
    }
}
