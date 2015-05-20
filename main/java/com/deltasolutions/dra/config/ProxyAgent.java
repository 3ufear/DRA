package com.deltasolutions.dra.config;

/**
 * Created by phil on 15-May-15.
 */
public class ProxyAgent {
    public static String originHost = null;
    public  static  String originRealm = null;
    public  static int vendorId = -1;
    public  static String productName = null;
    public  static int appId = -1;
    private int port = -1;

    ProxyAgent() {//defaultvalues
      /*  originHost = "originHost.com";
        originRealm = "realm.com";
        vendorId = 2445;
        productName = "DiamProxy";
        appId = 1234567890;
        port = 8080;*/
    }

    public boolean checkParameters() {
        if (originHost != null && originRealm != null && productName != null
                && vendorId != -1 && appId != -1 && port != -1) {
            return true;
        }
        return false;
    }

    public String getOriginHost() {
        return originHost;
    }

    public void setOriginHost(String originHost) {
        this.originHost = originHost;
    }

    public  int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getOriginRealm() {
        return originRealm;
    }

    public void setOriginRealm(String originRealm) {
        this.originRealm = originRealm;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
