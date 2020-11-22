package org.flosan.DistributionCenterRMI;

import org.flosan.DistributionCenterRMI.security.AES;
import org.flosan.DistributionCenterRMI.security.RSA;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class TransactionImpl implements TransactionInterface {
    @Override
    public boolean getHeartBeat() throws RemoteException {
        return true;
    }

    public List<SealedObject> commitTransaction(List<SealedObject> transaction) throws RemoteException {
        MongoDBDriver mongo = new MongoDBDriver("mongodb://ordersUser:orders123@matisse.localdomain/journals");
        PrivateKey privateKey = RSA.generateRSAPrivate();
        SecretKey sk = RSA.Decrypt(privateKey, transaction.get(0));
        List<SealedObject> response = new ArrayList<>();
        response.add(AES.Encrypt(mongo.commitTransaction((String) AES.Decrypt(transaction.get(1), sk)), sk));
        return response;

    }
}
