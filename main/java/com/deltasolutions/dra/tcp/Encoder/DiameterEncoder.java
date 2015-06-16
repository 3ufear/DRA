package com.deltasolutions.dra.tcp.Encoder;

import com.deltasolutions.dra.parser.MessageParser;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * Created by phil on 11-May-15.
 */
public class DiameterEncoder extends FrameDecoder {
    public static final MessageParser parser = new MessageParser();
    @Override
    protected Object decode(ChannelHandlerContext channelHandlerContext, Channel channel, ChannelBuffer channelBuffer) throws Exception {
        int tmp = channelBuffer.toByteBuffer().getInt();
        //channelBuffer.
        //data.position(0);
        //System.out.println(channelBuffer.toString());
        byte vers = (byte) (tmp >> 24);
        // extract the message length, so we know how much to read
        int messageLength = (tmp & 0xFFFFFF);
        if (messageLength < channelBuffer.readableBytes()) {
            return null;
        }
        return parser.createMessage(channelBuffer.readBytes(messageLength).toByteBuffer());
    }
}
