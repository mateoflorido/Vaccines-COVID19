package org.flosan.DistributionCenterRMI;

import java.rmi.RemoteException;

public class TransactionImpl  implements TransactionInterface{
    @Override
    public boolean getHeartBeat() throws RemoteException {
        return true;
    }
    public boolean commitTransaction(String id) throws RemoteException{
        MongoDBDriver mongo = new MongoDBDriver("mongodb://ordersUser:orders123@matisse.localdomain/journals");
        return mongo.commitTransaction(id);

    }
}
