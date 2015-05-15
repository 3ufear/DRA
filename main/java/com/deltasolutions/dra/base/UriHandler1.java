package com.deltasolutions.dra.base;

/**
 * Created by phil on 19-Mar-15.
 */
import io.netty.handler.codec.http.HttpRequest;

//@Mapped(uri = "/h1")
public class UriHandler1 extends UriHandlerBased {

    @Override
    public void process(HttpRequest request, StringBuilder buff) {
        buff.append("HELLO HANDLER1!");
    }
}