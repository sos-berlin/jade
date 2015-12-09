package com.sos.jade.backgroundservice.enums;

public enum OperationValues {
    COPY ("copy"), 
    MOVE ("move"), 
    GETLIST ("getlist"), 
    RENAME ("rename");

    public String name;
    
    private OperationValues (String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }

}
