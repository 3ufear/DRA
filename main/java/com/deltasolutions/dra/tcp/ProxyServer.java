/**
 * Created by phil on 13-Dec-14.
 */
package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.chanelChooserHelper.ChanelChooser;
import com.deltasolutions.dra.config.Config;

public class ProxyServer {
    //public static String[] hosts = {"192.168.200.121:3869","192.168.200.121:3869","192.168.200.121:3869","192.168.200.121:3869"};
    //public static String realm = "vimpelcom.com";
   // public static String productName = "Proxy";

    public static void main(String[] args) throws Throwable {

      /*  if (args.length < 1) {
            System.out.println("Usage: <port>");
        } else {*/
        Config conf = new Config("C:\\Users\\phil\\Documents\\WORK\\Diamter Routing Agent\\proxyagent.conf");
        conf.parseConfig();
        ChanelChooser.getInstance().processUpstreams(conf.getUpstreamList(), conf.getConfigConditionList());
         /*   for (int i =0; i < hosts.length; i++) {
                ServerConnectionsFactory factory = new ServerConnectionsFactory(hosts[i],realm,productName);
                factory.start();
            }*/
         new NettyServer(conf.getProxyAgent().getPort());
           /* ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]));
            while (true) {
                Socket s = ss.accept();
                System.out.println("Client accepted");
                new Thread(new SocketProcessor(s)).start();*/
      //      Server s = new Server(Integer.parseInt(args[0]));
      //      s.start();
       //     }
        }

    }

