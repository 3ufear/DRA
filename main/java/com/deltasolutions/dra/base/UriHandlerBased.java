package com.deltasolutions.dra.base;

/**
 * Created by phil on 19-Mar-15.
 */
import io.netty.handler.codec.http.HttpRequest;

public abstract class UriHandlerBased{

    public abstract void process(HttpRequest request, StringBuilder buff);

    public String getContentType() {
        return "text/plain; charset=UTF-8";
    }
}