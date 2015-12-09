package com.deltasolutions.dra.tcp.handlers;

import com.deltasolutions.dra.base.AvpDataException;
import com.deltasolutions.dra.base.IMessage;
import com.deltasolutions.dra.tcp.CommandProcessor;
import com.deltasolutions.dra.tcp.Encoder.DiameterEncoder;
import com.deltasolutions.dra.tcp.NetContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.xml.crypto.Data;
import java.nio.ByteBuffer;
import java.util.Date;

public class InboundHandler extends ChannelInboundHandlerAdapter {
	private boolean _debug;
	

    public InboundHandler() {
        _debug = true;

    }

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		//Send greeting for a new connection.
		//e.getChannel().write("Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
		//e.getChannel().write("It is " + new Date() + " now.\r\n");
		//log(e.toString());		
		//ctx.getChannel().getConfig().setConnectTimeoutMillis(300000);		
		log("Client connected from "+ ctx.channel().remoteAddress());
	}	

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		log("Connection closed from "+ctx.channel().remoteAddress());
	}


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object e) throws AvpDataException {
		/*ChannelBuffer bf = (ChannelBuffer) e.getMessage();
        IMessage msg = null;
		try {
			msg = DiameterEncoder.parser.createMessage(bf.toByteBuffer());  //.readBytes(messageLength).toByteBuffer());
		} catch (AvpDataException e1) {

			throw e1;
		}*/
		log("Class " + e.getClass());
		IMessage msg = (IMessage) e;
		try {
			new CommandProcessor("DiameterProcessor", new NetContext(ctx, msg), _debug).run();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
		log("Error ("+ctx.channel().remoteAddress()+"): " + e.getMessage());
	}	
	
	private void log(String txt) {
		if (_debug) {
			Date curdate = new Date();
			System.out.print(curdate + "  InBoundHandler: " + txt + "\n");
		}
	}
}
