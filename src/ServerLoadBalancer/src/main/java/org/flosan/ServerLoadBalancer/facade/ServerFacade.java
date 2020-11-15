package org.flosan.ServerLoadBalancer.facade;

import org.flosan.DistributionCenterRMI.UptimeHandler;
import org.flosan.ServerLoadBalancer.handler.ClientHandler;
import org.flosan.ServerLoadBalancer.security.RSA;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerFacade {
    public static void main(String[] args) throws IOException {
        List<AtomicBoolean> serverUptime = new ArrayList<>();
        List<String> hosts = new ArrayList<>();
        hosts.add("picasso.localdomain");
        hosts.add("toulouse.localdomain");

        AtomicBoolean inUse = new AtomicBoolean(true);
        serverUptime.add(new AtomicBoolean(false));
        serverUptime.add(new AtomicBoolean(false));
        serverUptime.add(new AtomicBoolean(false));

        System.out.println("*--- Server Started ---*");
        ServerSocket gateway = new ServerSocket(6666);
        PrivateKey privateKey = RSA.generateRSAPrivate();
        System.out.println("*--- Private Key Readed ---*");

        System.out.println("*--- Starting Server Uptime Checker ---*");
        Thread uptime = new UptimeHandler(serverUptime, inUse, hosts);
        uptime.start();

        //noinspection InfiniteLoopStatement
        while (true) {
            Socket socket = null;
            try {
                socket = gateway.accept();
                System.err.println("DEBUG: Incoming Client " + socket);

                DataInputStream dataInput = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());

                System.err.println("DEBUG: Creating Thread for Client");
                Thread t = new ClientHandler(socket, dataInput, dataOutput, privateKey);
                t.start();

            } catch (Exception e) {
                assert socket != null;
                socket.close();
                e.printStackTrace();
            }
        }
    }
}
