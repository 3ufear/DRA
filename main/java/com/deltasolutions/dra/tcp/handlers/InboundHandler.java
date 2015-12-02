package com.deltasolutions.dra.tcp.handlers;

import com.deltasolutions.dra.base.AvpDataException;
import com.deltasolutions.dra.base.IMessage;
import com.deltasolutions.dra.tcp.CommandProcessor;
import com.deltasolutions.dra.tcp.Encoder.DiameterEncoder;
import com.deltasolutions.dra.tcp.NetContext;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;

import javax.xml.crypto.Data;
import java.nio.ByteBuffer;
import java.util.Date;

public class InboundHandler extends SimpleChannelUpstreamHandler {
	private boolean _debug;
	

    public InboundHandler() {
        _debug = true;

    }

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		//Send greeting for a new connection.
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
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws AvpDataException {
		/*ChannelBuffer bf = (ChannelBuffer) e.getMessage();
        IMessage msg = null;
		try {
			msg = DiameterEncoder.parser.createMessage(bf.toByteBuffer());  //.readBytes(messageLength).toByteBuffer());
		} catch (AvpDataException e1) {

			throw e1;
		}*/
		 IMessage msg = (IMessage) e.getMessage();
		try {
			new CommandProcessor("DiameterProcessor", new NetContext(e.getChannel(), msg), _debug).run();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		log("Error ("+ctx.getChannel().getRemoteAddress()+"): " + e.getCause() + " ("+ctx.getChannel().getId()+")");
	}	
	
	private void log(String txt) {
		if (_debug) {
			Date curdate = new Date();
			System.out.print(curdate + "  InBoundHandler: " + txt + "\n");
		}
	}
}
