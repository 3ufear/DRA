package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.base.IMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;


public class NetContext {
	public ChannelHandlerContext channel;
	public IMessage message;
	
	public NetContext(ChannelHandlerContext channel, IMessage message) {
		this.channel = channel;		
		this.message = message;
	}	
}
