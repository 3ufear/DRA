package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.tcp.Encoder.DiameterEncoder;
import com.deltasolutions.dra.tcp.handlers.NettyServerHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;

import static org.jboss.netty.channel.Channels.pipeline;

public class NettyServerPipeLineFactory implements ChannelPipelineFactory {
    ClientSocketChannelFactory cf;
    public NettyServerPipeLineFactory(ClientSocketChannelFactory cf) {
        super();
        this.cf = cf;
    }
	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();

        pipeline.addLast("framer", new DiameterEncoder());
		// Add the text line codec combination first,
		//pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.nulDelimiter()));
		//pipeline.addLast("decoder", new StringDecoder());
		//pipeline.addLast("encoder", new StringEncoder());
		
		// and then business logic.
		pipeline.addLast("handler", new NettyServerHandler(cf));
		
		return pipeline;
	}	
}
