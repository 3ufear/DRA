package com.deltasolutions.dra.tcp.Encoder;

import com.deltasolutions.dra.base.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by phil on 12/7/2015.
 */
public class DiameterDecoder extends MessageToByteEncoder<IMessage> {
    public DiameterDecoder() {
        System.out.println("DiameterEncoder!!!!!!!!!!!");
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IMessage msg, ByteBuf out) throws Exception {
        System.out.println("writing message width " + msg.getEndToEndIdentifier());
        out.writeBytes(DiameterEncoder.parser.encodeMessage(msg).array());
    }
}