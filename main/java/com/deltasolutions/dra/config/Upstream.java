package com.deltasolutions.dra.config;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by phil on 15-May-15.
 */
public class Upstream {
    private boolean isDefault = false;
    private boolean isActive = false;
    private String name;
    private List<String> hosts = new ArrayList<String>();
    private String failoverUpstream;

    Upstream() {
        name = null;
    }

    Upstream(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addHost(String host) {
        hosts.add(host);
    }

    public String[] getHosts() {
        String[] hosts = new String[this.hosts.size()];
        this.hosts.toArray(hosts);
        return hosts;//.toArray();
    }

    public void setActive() {
        this.isActive = true;
    }

    public boolean isActive() {
        return this.isActive;
    }
    public boolean isDefault() {
        return this.isDefault;
    }

    public void setDefault() {
        this.isDefault = true;
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

    public void setFailoverUpstream(String name) {
        failoverUpstream = name;
    }

    public String getFailoverUpstream() {
        return  failoverUpstream;
    }


}
