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
    private List<ConfigCondition> configConditionList = new ArrayList<ConfigCondition>();
    BufferedReader in;
    FileReader fileReader;
    private boolean flagIsUpstream = false;
    private boolean flagIsBegining = true;
    private boolean flagIsRigthBracket = false;
    private boolean flagIsLeftBracket = false;
    private boolean flagIsProxyAgent = false;
    private boolean flagIsDefault = false;
    private boolean flagIsAvpCondition = false;
    private ProxyAgent agent = new ProxyAgent();





    public Config(String Path) throws IOException {
        this.filePath = Path;
        exists(filePath);
        fileReader = new FileReader(filePath);
        in = new BufferedReader(fileReader);
    }

    public Config parseConfig() throws Exception {
        String s;
        Upstream upstream = null;
        while ((s = readline())!= null) {
            s = deleteSpaces(s);
            if (s.length() > 0) {
                if (flagIsBegining) {
                    String buf[] = s.split(" ");
                    //buf = disposeFromSpaces(buf);
                    if (buf[0].toLowerCase().equals("upstream")) {
                        flagIsUpstream = true;
                        upstream = new Upstream(buf[1]);
                        if (buf.length == 3) {
                            if (buf[2].equals("{")) {
                                flagIsLeftBracket = true;
                            }
                        }
                        flagIsBegining = false;
                    } else if (buf[0].toLowerCase().equals("proxyagent")) {
                        flagIsBegining = false;
                        flagIsProxyAgent = true;
                        if (buf.length > 1) {
                            if (buf[1].equals("{")) {
                                flagIsLeftBracket = true;
                            }
                        }
                    }
                } else if (flagIsUpstream) {
                    if (flagIsLeftBracket) {
                        if (s.equals("}")) {
                            upstreamList.add(upstream);
                            flagIsLeftBracket = false;
                            flagIsUpstream = false;
                            flagIsBegining = true;
                        } else {
                            String[] str = s.split(" ");
                           // str = disposeFromSpaces(str);
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
                                throw new ParseException("Not all of parameters for ProxyAgent defined");
                            }
                        } else if (flagIsDefault) {
                            this.setDefaultUpstream(s);
                            flagIsDefault = false;
                        } else if (flagIsAvpCondition) {
                            String []str = s.split(" ");
                            ConfigCondition cond = new ConfigCondition(Integer.parseInt(str[0]), str[1], str[2], str[3]);
                            setActiveUpstream(str[3]);
                            configConditionList.add(cond);
                            flagIsAvpCondition = false;
                        } else {
                            String[] str = s.split(" ");
                            //str = disposeFromSpaces(str);
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
                                } else {
                                    flagIsDefault = true;
                                }
                            } else if (str[0].toLowerCase().equals("avpcondition")) {//Todo: AvpCode: 272 \n Operation: < \n OperationValue: \n Upstream: ups2
                                if (str.length >= 5 ) {
                                    ConfigCondition cond = new ConfigCondition(Integer.parseInt(str[1]), str[2], str[3], str[4]);
                                    setActiveUpstream(str[4]);
                                    configConditionList.add(cond);
                                } else {
                                    flagIsAvpCondition = true;
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
            String s;
            if (((s = in.readLine()))!=null) {
                return s;
            } else {
                fileReader.close();
            }
        } catch (IOException e) {
            fileReader.close();
            throw new RuntimeException(e);
        }
        return null;
    }

    private String[] disposeFromSpaces(String []str) {
        String[] parameters = new String[3];
        for(int i = 0, j = 0; i < str.length; i++ ) {
             if(!(str[i].equals(""))) {
                 parameters[j] = str[i];
                 j++;
             }
        }
        return parameters;
    }

    private void setActiveUpstream(String upstream) {
        setActiveUpstream(upstream, false);
    }
    private void setActiveUpstream(String upstream, boolean isDefault) {
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

    private void setDefaultUpstream(String upstream) {
        setActiveUpstream(upstream, true);
    }

    public List<Upstream> getUpstreamList() {
        return  this.upstreamList;
    }

    public ProxyAgent getProxyAgent() {
        return this.agent;
    }

    public List<ConfigCondition> getConfigConditionList() {
        return this.configConditionList;
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
