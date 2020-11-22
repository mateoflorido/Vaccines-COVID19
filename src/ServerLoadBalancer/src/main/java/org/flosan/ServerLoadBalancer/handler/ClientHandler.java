package org.flosan.ServerLoadBalancer.handler;

import org.bson.Document;
import org.flosan.DistributionCenterRMI.TransactionHandler;
import org.flosan.ServerLoadBalancer.data.MongoDBDriver;
import org.flosan.ServerLoadBalancer.security.AES;
import org.flosan.ServerLoadBalancer.security.RSA;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ClientHandler extends Thread {
    final DataInputStream inputStream;
    final DataOutputStream outputStream;
    private Socket socket;
    private final PrivateKey privateKey;
    private final MongoDBDriver mongo;
    private ObjectInputStream objectIn;
    private ObjectOutputStream objectOut;
    private final AtomicInteger toServe;
    private final List<String> hosts;
    private final List<AtomicBoolean> uptime;
    private SecretKey serverEncKey;
    private String sessionID;

    public ClientHandler(Socket s, DataInputStream din, DataOutputStream dout, PrivateKey pk, AtomicInteger ts, List<String> hosts, List<AtomicBoolean> serverUptime) {
        this.socket = s;
        this.inputStream = din;
        this.outputStream = dout;
        this.privateKey = pk;
        this.toServe = ts;
        this.hosts = hosts;
        this.uptime = serverUptime;

        try {
            this.serverEncKey = AES.getAESKey();
            this.objectIn = new ObjectInputStream(this.inputStream);
            this.objectOut = new ObjectOutputStream(this.outputStream);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        this.mongo = new MongoDBDriver("mongodb://loadBalancer:user123@matisse.localdomain/users");
    }

    @Override
    public void run() {
        String operation;
        AtomicBoolean inUse = new AtomicBoolean(true);

        while (inUse.get()) {
            try {
                try {
                    this.outputStream.writeUTF(PrintMenu());

                    //noinspection unchecked
                    List<SealedObject> args = (List<SealedObject>) objectIn.readObject();
                    SecretKey userKey = RSA.Decrypt(this.privateKey, args.get(0));
                    operation = (String) AES.Decrypt(args.get(1), userKey);
                    switch (Objects.requireNonNull(operation)) {
                        case "0":
                            inUse.compareAndSet(true, false);
                            break;
                        case "1":
                            if (args.size() == 8) {
                                List<String> decArgs = new ArrayList<>();
                                for (int i = 2; i < args.size(); i++) {
                                    decArgs.add((String) AES.Decrypt(args.get(i), userKey));
                                }
                                objectOut.writeObject(register(decArgs, userKey));

                            }
                            break;
                        case "2":
                            objectOut.writeObject(
                                    login(
                                            (String) AES.Decrypt(args.get(2), userKey),
                                            (String) AES.Decrypt(args.get(3), userKey),
                                            userKey
                                    )
                            );
                            break;
                        case "3":
                            objectOut.writeObject(
                                    stock(userKey)
                            );
                            break;
                        case "3.0":
                            List<String> decArgs = new ArrayList<>();
                            for (int i = 3; i < args.size(); i++) {
                                decArgs.add((String) AES.Decrypt(args.get(i), userKey));
                            }
                            objectOut.writeObject(
                                    order(decArgs, userKey, (String) AES.Decrypt(args.get(2), userKey))
                            );
                            break;
                    }
                } catch (SocketException | EOFException e) {
                    this.objectOut.close();
                    this.objectIn.close();
                    this.outputStream.close();
                    this.inputStream.close();
                    inUse.compareAndSet(true, false);
                }

            } catch (IOException | ClassNotFoundException e) {
                try {
                    this.objectOut.close();
                    this.objectIn.close();
                    this.outputStream.close();
                    this.inputStream.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        try {
            this.objectOut.close();
            this.objectIn.close();
            this.outputStream.close();
            this.inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String PrintMenu() {
        return "------ COVID19 Vaccine Distribution -------\n" +
                "Menu:\n" +
                "\t0. Exit\n" +
                "\t1. Register\n" +
                "\t2. Login\n" +
                "\t3. Place Order\n";

    }

    public List<SealedObject> login(String username, String password, SecretKey userKey) {
        List<SealedObject> response = new ArrayList<>();

        Document retrieve = this.mongo.getUserCredentials(username, password);
        if (retrieve != null)
            if (retrieve.containsKey("username") && retrieve.containsKey("password")) {
                this.sessionID = this.mongo.insertSession(username, new Date());
                response.add(AES.Encrypt(this.sessionID, userKey));
            }
        return response;
    }

    public List<SealedObject> register(List<String> userData, SecretKey userKey) {
        List<SealedObject> response = new ArrayList<>();

        if (this.mongo.getNIT(userData.get(0)) != null) {
            response.add(AES.Encrypt("NIT Already registered. Please check or login in", userKey));
            return response;

        } else if (this.mongo.getUser(userData.get(4)) != null) {
            response.add(AES.Encrypt("Username Already registered. Please check or login in", userKey));
            return response;
        } else {
            if (!this.mongo.insertNewUser(userData)) {
                response.add(AES.Encrypt("Error in register process", userKey));
            } else {
                response.add(AES.Encrypt("Register successful, please log in", userKey));
            }
            return response;
        }


    }

    public List<SealedObject> stock(SecretKey userKey) {
        List<SealedObject> response = new ArrayList<>();
        List<String> stockAvail = this.mongo.getStock();
        for (String stock : stockAvail)
            response.add(AES.Encrypt(stock, userKey));
        return response;
    }

    public List<SealedObject> order(List<String> decArgs, SecretKey userKey, String sessionID) {

        List<String> batch = new ArrayList<>();
        int type = 1;
        for (String arg : decArgs) {
            if (!arg.equalsIgnoreCase("0")) {
                batch.add(this.mongo.insertTransaction("COV19VAC" + type, Integer.parseInt(arg), sessionID));
            }
            type++;
        }
        List<AtomicReference<String>> responses = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (String tx : batch) {
            AtomicReference<String> response = new AtomicReference<>("");
            String host = getNewServer();
            Thread t = new TransactionHandler(host, tx, response, this.serverEncKey);
            this.mongo.insertNewRouted(host, this.sessionID);
            t.start();
            threads.add(t);
            responses.add(response);
        }
        for (Thread th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                System.err.println("ERROR: HANDLING THREAD!");
            }
        }
        List<SealedObject> ackTransactions = new ArrayList<>();
        for (AtomicReference<String> rsp : responses) {
            ackTransactions.add(AES.Encrypt(rsp.get(), userKey));
        }

        return ackTransactions;

    }

    public String getNewServer() {
        int hostGet;
        do {
            hostGet = this.toServe.get();
            if ((hostGet + 1) % (this.hosts.size()) == 0) {
                this.toServe.set(0);
            } else {
                hostGet = this.toServe.getAndAdd(1);

            }
            if (hostGet >= this.hosts.size()) {
                hostGet = 0;
            }
        } while (!this.uptime.get(hostGet).get());
        return this.hosts.get(hostGet);
    }
}
