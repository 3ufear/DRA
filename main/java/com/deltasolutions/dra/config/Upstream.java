package com.deltasolutions.dra.config;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by phil on 15-May-15.
 */
public class Upstream {
    private String name;
    private ProxyAgent proxyAgent;
    private List<String> hosts = new ArrayList<String>();

    Upstream() {
        name = null;
    }

    Upstream(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addHost(String host) {
        hosts.add(host);
    }

    public String[] getHosts() {
        return (String[]) hosts.toArray();
    }

    @Override
    public String toString() {
        String str = new String();
        str += this.name + "\n";
        Iterator it = hosts.iterator();
        while(it.hasNext()) {
            str += it.next() + "\n";
        }
        return str;
    }

}
