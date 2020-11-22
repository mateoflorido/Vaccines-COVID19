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
import java.util.concurrent.atomic.AtomicInteger;

public class ServerFacade {
    public static void main(String[] args) throws IOException {
        // RMI Servers
        List<AtomicBoolean> serverUptime = new ArrayList<>();
        AtomicInteger toServe = new AtomicInteger(0);
        List<String> hosts = new ArrayList<>();

        hosts.add("picasso.localdomain");
        hosts.add("toulouse.localdomain");
        serverUptime.add(new AtomicBoolean(false));
        serverUptime.add(new AtomicBoolean(false));

        AtomicBoolean inUse = new AtomicBoolean(true);
        ServerSocket gateway = new ServerSocket(6666);
        PrivateKey privateKey = RSA.generateRSAPrivate();
        System.out.println("*--- Private Key Readed ---*");

        System.out.println("*--- Starting Server Uptime Checker ---*");
        Thread uptime = new UptimeHandler(serverUptime, inUse, hosts);
        uptime.start();
        System.out.println("*--- Server Started ---*");

        //noinspection InfiniteLoopStatement
        while (true) {
            Socket socket = null;
            try {
                socket = gateway.accept();

                DataInputStream dataInput = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());

                Thread t = new ClientHandler(socket, dataInput, dataOutput, privateKey, toServe, hosts, serverUptime);
                t.start();

            } catch (Exception e) {
                assert socket != null;
                socket.close();
                e.printStackTrace();
            }
        }
    }
}
