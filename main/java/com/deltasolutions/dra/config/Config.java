package com.deltasolutions.dra.config;

import com.deltasolutions.dra.base.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by phil on 15-May-15.
 */
public class Config {
    private String filePath;
    private List<Upstream> upstreamList = new ArrayList<Upstream>();
    BufferedReader in;
    private boolean flagIsUpstream = false;
    private boolean flagIsBegining = true;
    private boolean flagIsRigthBracket = false;
    private boolean flagIsLeftBracket = false;
    private boolean flagIsProxyAgent = false;
    private ProxyAgent agent = new ProxyAgent();





    public Config(String Path) throws FileNotFoundException {
        this.filePath = Path;
        exists(filePath);
        in = new BufferedReader(new FileReader(filePath));
    }

    public Config parseConfig() throws Exception {
        String s;
        Upstream upstream = null;
        while ((s = readline())!= null) {
            s = deleteSpaces(s);
            if (s.length() > 0) {
                if (flagIsBegining) {
                    String buf[] = s.split(" ", 3);
                    System.out.println("isBeginig");
                    if (buf[0].toLowerCase().equals("upstream")) {
                        System.out.println("isUpstream");
                        flagIsUpstream = true;
                        upstream = new Upstream(buf[1]);
                        if (buf.length == 3) {
                            //          System.out.println("LeftBracket");
                            if (buf[2].equals("{")) {
                                flagIsLeftBracket = true;
                            }
                        }
                        flagIsBegining = false;
                    } else if (buf[0].toLowerCase().equals("proxyagent")) {
                        System.out.println("PROXYAGENT");
                        flagIsBegining = false;
                        flagIsProxyAgent = true;
                        if (buf.length > 1) {
                            if (buf[1].equals("{")) {
                                flagIsLeftBracket = true;
                            }
                        }
                    }
                } else if (flagIsUpstream) {
                    //   System.out.println("AfterBeginig");
                    if (flagIsLeftBracket) {
                        if (s.equals("}")) {
                            //      System.out.println("}}}");
                            upstreamList.add(upstream);
                            flagIsLeftBracket = false;
                            flagIsUpstream = false;
                            flagIsBegining = true;
                        } else {
                            //   System.out.println("addHosts");
                            String[] str = s.split(" ");
                            if (str.length < 3)
                                upstream.addHost(str[0]);
                            else
                                throw new ParseException("Unexpected string:" + s + ". 2 parameters must be defined. ");
                        }
                    } else {
                        if (s.equals("{")) {
                            flagIsLeftBracket = true;
                        } else {
                            throw new ParseException("Can not parse config at string:" + s + ". '{' bracket expected");
                        }
                    }

                } else if (flagIsProxyAgent) {
                    if (flagIsLeftBracket) {
                        if (s.equals("}")) {
                            if (!agent.checkParameters()) {
                                throw new ParseException("Not all parameters for ProxyAgent defined");
                            }
                        } else {
                            String[] str = s.split(" ");
                            if (str[0].toLowerCase().equals("originhost")) {
                                agent.setOriginHost(str[1]);
                            } else if (str[0].toLowerCase().equals("originrealm")) {
                                agent.setOriginRealm(str[1]);
                            } else if (str[0].toLowerCase().equals("vendorid")) {
                                agent.setVendorId(Integer.parseInt(str[1]));
                            } else if (str[0].toLowerCase().equals("port")) {
                                agent.setPort(Integer.parseInt(str[1]));
                            } else if (str[0].toLowerCase().equals("productname")) {
                                agent.setProductName(str[1]);
                            } else if (str[0].toLowerCase().equals("appid")) {
                                agent.setAppId(Integer.parseInt(str[1]));
                            } else if (str[0].toLowerCase().equals("default:")) {
                                if (str.length >= 2) {
                                    this.setDefaultUpstream(str[1]);
                                }
                            }
                        }
                    } else if (s.equals("{")) {
                        flagIsLeftBracket = true;
                    }
                }
            }
        }
        return this;
    }

    private String deleteSpaces(String str) {
        Pattern p=Pattern.compile("^\\s*(.*\\S)\\s*$");
        Matcher m=p.matcher(str);
       if (m.find()) {
           return m.group(1);
       }
       return str;
    }

    private void exists(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
    }

    private String readline() throws Exception {
        try {
            try {
                String s;
                if (((s = in.readLine()))!=null) {
                    return s;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
        return null;
    }
    public void setActiveUpstream(String upstream) {
        setActiveUpstream(upstream, false);
    }
    public void setActiveUpstream(String upstream, boolean isDefault) {
        Iterator it = upstreamList.iterator();
        while (it.hasNext()) {
            Upstream up = (Upstream) it.next();
            if (up.getName().equals(upstream)) {
                up.setActive();
                if (isDefault) {
                    up.setDefault();
                }
            }
        }
    }

    public void setDefaultUpstream(String upstream) {
        setActiveUpstream(upstream, true);
    }

    public List<Upstream> getUpstreamList() {
        return  this.upstreamList;
    }

    @Override
    public String toString() {
        String str = new String();
        Iterator it = this.upstreamList.iterator();
        while (it.hasNext()) {
            str += it.next().toString() + "\n";
        }
        return str;
    }

    public static void main(String[] args) throws Exception {
        Config conf = null;
        try {
            conf = new Config("C:\\Users\\phil\\Documents\\WORK\\Diamter Routing Agent\\proxyagent.conf");
            conf.parseConfig();
        } catch (FileNotFoundException e) {
            System.out.println(e.getCause());
        } catch (ParseException e) {
            System.out.println(e.getCause());
            throw new ParseException(e);
        }
        System.out.println(conf.toString());
    }
}
