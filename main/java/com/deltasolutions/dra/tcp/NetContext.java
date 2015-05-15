package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.base.IMessage;
import org.jboss.netty.channel.Channel;

public class NetContext {
	public Channel channel;
	public IMessage message;
	
	public NetContext(Channel channel, IMessage message) {
		this.channel = channel;		
		this.message = message;
	}	
}
