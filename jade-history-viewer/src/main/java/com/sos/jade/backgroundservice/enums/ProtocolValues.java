package com.sos.jade.backgroundservice.enums;

public enum ProtocolValues {
    FTP("ftp"), SFTP("sftp"), LOCAL("local"), HTTP("http"), HTTPS("https"), WEBDAV("webdav");

    public String name;

    private ProtocolValues(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
