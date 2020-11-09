package org.flosan.ServerLoadBalancer.facade;

import org.flosan.ServerLoadBalancer.handler.ClientHandler;
import org.flosan.ServerLoadBalancer.security.RSA;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;

public class ServerFacade {
    public static void main(String[] args) throws IOException {
        ServerSocket gateway = new ServerSocket(6666);
        PrivateKey privateKey = RSA.generateRSAPrivate();

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
                socket.close();
                e.printStackTrace();
            }
        }
    }
}
