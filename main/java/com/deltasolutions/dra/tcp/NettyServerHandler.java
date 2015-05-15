package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.base.IMessage;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;

public class NettyServerHandler extends SimpleChannelUpstreamHandler {
    ClientSocketChannelFactory cf;
	private boolean _debug;
	
	public NettyServerHandler() {
		_debug = true;
	}
    public  NettyServerHandler(ClientSocketChannelFactory cf) {
        this.cf = cf;
        _debug = true;

    }

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		// Send greeting for a new connection.
		//e.getChannel().write("Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
		//e.getChannel().write("It is " + new Date() + " now.\r\n");
		//log(e.toString());		
		//ctx.getChannel().getConfig().setConnectTimeoutMillis(300000);		
		log("Client connected from "+ctx.getChannel().getRemoteAddress()+" ("+ctx.getChannel().getId()+")");
	}	

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		log("Connection closed from "+ctx.getChannel().getRemoteAddress()+" ("+ctx.getChannel().getId()+")");
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        IMessage msg = (IMessage) e.getMessage();

		try {
			new CommandProcessorSmall("DiameterProcessor", new NetContext(e.getChannel(), msg), _debug, cf).start();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		log("Error ("+ctx.getChannel().getRemoteAddress()+"): "+e.getCause().fillInStackTrace().getStackTrace() +" ("+ctx.getChannel().getId()+")");
	}	
	
	private void log(String txt) {
		if (_debug) {
			System.out.print("NettyServerHandler: "+txt+"\n");
		}
	}
}
