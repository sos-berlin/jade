package com.sos.yade.commons.result;

import java.io.Serializable;

public class YadeTransferResultProvider implements Serializable {

    private static final long serialVersionUID = -8877189880331791745L;

    private String host;
    private Integer port;
    private String protocol;
    private String account;

    public String getHost() {
        return host;
    }

    public void setHost(String val) {
        host = val;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer val) {
        port = val;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String val) {
        protocol = val;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String val) {
        account = val;
    }

}
