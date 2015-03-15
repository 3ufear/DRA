/**
 * Created by phil on 13-Dec-14.
 */
package com.deltasolutions.dra.tcp;

import java.net.ServerSocket;
import java.net.Socket;

public class ProxyServer {
    public static void main(String[] args) throws Throwable {
        if (args.length < 1) {
            System.out.println("Usage: <port>");
        } else {
            ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]));
            while (true) {
                Socket s = ss.accept();
                System.out.println("Client accepted");
                new Thread(new SocketProcessor(s)).start();
            }
        }
    }

}
