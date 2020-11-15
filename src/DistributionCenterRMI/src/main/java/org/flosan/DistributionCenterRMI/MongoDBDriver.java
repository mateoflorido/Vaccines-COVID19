package org.flosan.DistributionCenterRMI;

import com.mongodb.BasicDBObject;
import com.mongodb.DocumentToDBRefTransformer;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Iterator;

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
        return true;

    }
}
