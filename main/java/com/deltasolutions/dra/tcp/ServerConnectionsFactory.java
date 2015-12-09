package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.tcp.Encoder.DiameterDecoder;
import com.deltasolutions.dra.tcp.Encoder.DiameterEncoder;
import com.deltasolutions.dra.tcp.handlers.OutboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by phil on 13-May-15.
 */
public class ServerConnectionsFactory extends Thread {
    private String remoteHost;
    private int remotePort;
    private Bootstrap clientBootstrap;
    private String name;
    public static int port = 30000;

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
       // ClientSocketChannelFactory cf = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
        clientBootstrap = new Bootstrap();
        clientBootstrap.group(workerGroup);
        clientBootstrap.channel(NioSocketChannel.class);
      //  clientBootstrap.setOption("receiveBufferSize", 20480);
        clientBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("framer", new DiameterEncoder());
                ch.pipeline().addLast("handler", new OutboundHandler(name));
                ch.pipeline().addLast("decoder", new DiameterDecoder());
            }
        });

        ChannelFuture f = clientBootstrap.connect(remoteHost, remotePort );//, new InetSocketAddress("10.169.17.68", port));

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            workerGroup.shutdownGracefully();
        }

        //Shut down thread pools to exit.

    }
}


