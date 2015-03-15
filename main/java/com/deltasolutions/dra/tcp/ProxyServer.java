/**
 * Created by phil on 13-Dec-14.
 */
package com.deltasolutions.dra.tcp;

import java.net.ServerSocket;
import java.net.Socket;

public class ProxyServer {
    public static String root = "";
    public static void main(String[] args) throws Throwable {
        if (args.length < 2) {
            System.out.println("Usage: <doc_root> <port>");
        } else {
            root = args[0];
            ServerSocket ss = new ServerSocket(Integer.parseInt(args[1]));
            while (true) {
                Socket s = ss.accept();
                System.out.println("Client accepted");
                new Thread(new SocketProcessor(s, root)).start();
            }
        }
    }

}
