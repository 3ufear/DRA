package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.tcp.Encoder.DiameterEncoder;
import com.deltasolutions.dra.tcp.handlers.InboundHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

import static org.jboss.netty.channel.Channels.pipeline;

public class NettyServerPipeLineFactory implements ChannelPipelineFactory {
    public NettyServerPipeLineFactory() {
        super();
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
		pipeline.addLast("handler", new InboundHandler());
		
		return pipeline;
	}	
}
