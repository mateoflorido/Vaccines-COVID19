package org.flosan.DistributionCenterRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class UptimeHandler extends Thread {
    private List<AtomicBoolean> servers;
    private final List<String> hosts;
    private AtomicBoolean inUse;

    public UptimeHandler(List<AtomicBoolean> la, AtomicBoolean inUse, List<String> hosts) {
        this.servers = la;
        this.inUse = inUse;
        this.hosts = hosts;
    }

    @Override
    public void run() {
        while (this.inUse.get()) {
            for (int i = 0; i < this.hosts.size(); i++) {
                Registry registry = null;
                try {
                    // System.setProperty("java.rmi.server.hostname",hosts.get(i));
                    registry = LocateRegistry.getRegistry(this.hosts.get(i));
                    TransactionInterface stub = (TransactionInterface) registry.lookup("TransactionInterface");
                    if (stub.getHeartBeat()) {
                        this.servers.get(i).set(true);
                        System.err.println("DEBUG: SERVER " + this.hosts.get(i) + " WORKING!");
                    }
                } catch (RemoteException | NotBoundException e) {
                    this.servers.get(i).set(false);
                    System.err.println("WARNING: Server " + this.hosts.get(i) + " is down! " + e.getMessage());
                }
                System.err.println("Current State" + this.servers.toString());
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                System.err.println("Uptime Crashed while sleeping! Please check SysAdmin.");
            }
        }
    }
}
