package com.deltasolutions.dra.tcp;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class NettyServer {	
	public NettyServer(int port) throws Exception{
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
      //  ClientSocketChannelFactory cf = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
	    ServerBootstrap bootstrap = new ServerBootstrap(factory);			    
	    bootstrap.setPipelineFactory(new NettyServerPipeLineFactory());
        bootstrap.setOption("receiveBufferSize", 20480);
        bootstrap.setOption("sendBufferSize", 20480);
	    bootstrap.setOption("child.tcpNoDelay", true);
	    bootstrap.setOption("child.keepAlive", true);
	    
	    bootstrap.bind(new InetSocketAddress(port));	
	    
	    System.out.print("NettyServer: Listen to users on " + InetAddress.getLocalHost().toString()+":"+port+"\n");
	}
}
