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
    private ClientBootstrap clientBootstrap;
    private String name;

    public ServerConnectionsFactory() {
        remoteHost = "localhost";
        remotePort = 8080;
        name = "defaultName";
    }

    public ServerConnectionsFactory(String host, String name) {
        String[] str = host.split(":");
        this.remoteHost = str[0];
        this.remotePort = Integer.parseInt(str[1]);
        this.name = name;
    }

    @Override
    public void run() {
       // ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        ClientSocketChannelFactory cf = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        clientBootstrap = new ClientBootstrap(cf);
        clientBootstrap.getPipeline().addLast("framer", new DiameterEncoder());
        clientBootstrap.getPipeline().addLast("handler", new OutboundHandler(name));
        ChannelFuture f = clientBootstrap.connect(new InetSocketAddress(remoteHost, remotePort));
        f.getChannel().getCloseFuture().awaitUninterruptibly();
        //Shut down thread pools to exit.
        clientBootstrap.releaseExternalResources();
    }
}


