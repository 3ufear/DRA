package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.tcp.Encoder.DiameterEncoder;
import com.deltasolutions.dra.tcp.handlers.OutboundHandler;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by phil on 13-May-15.
 */
public class ServerConnectionsFactory extends Thread {
    private String remoteHost;
    private int remotePort;
    private String realm;
    private String productName;
    private ClientBootstrap cb;

    public ServerConnectionsFactory() {
        remoteHost = "localhost";
        remotePort = 8080;
        realm = "DiamRealm";
        productName = "DiameterProxy";
    }

    public ServerConnectionsFactory(String host, String realm, String productName) {
        String[] str = host.split(":");
        remoteHost = str[0];
        remotePort = Integer.parseInt(str[1]);
        this.realm = realm;
        this.productName = productName;
    }

    @Override
    public void run() {
       // ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        ClientSocketChannelFactory cf = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        cb = new ClientBootstrap(cf);
        cb.getPipeline().addLast("framer", new DiameterEncoder());
        cb.getPipeline().addLast("handler", new OutboundHandler());
        ChannelFuture f = cb.connect(new InetSocketAddress(remoteHost, remotePort));
        f.getChannel().getCloseFuture().awaitUninterruptibly();
        // Shut down thread pools to exit.
        cb.releaseExternalResources();
    }
}


