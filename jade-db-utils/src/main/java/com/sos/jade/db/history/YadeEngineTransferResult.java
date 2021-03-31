package com.sos.jade.db.history;

import com.sos.yade.commons.result.YadeTransferResult;

public class YadeEngineTransferResult extends YadeTransferResult {

    private static final long serialVersionUID = -8302575083106742646L;

    private String mandator;
    private String jobschedulerId;
    private Long taskId;
    private String job;
    private String orderId;
    private String jobChain;
    private String jobChainNode;

    public String getMandator() {
        return mandator;
    }

    public void setMandator(String val) {
        mandator = val;
    }

    public String getJobschedulerId() {
        return jobschedulerId;
    }

    public void setJobschedulerId(String val) {
        jobschedulerId = val;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long val) {
        taskId = val;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String val) {
        job = val;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String val) {
        orderId = val;
    }

    public String getJobChain() {
        return jobChain;
    }

    public void setJobChain(String val) {
        jobChain = val;
    }

    public String getJobChainNode() {
        return jobChainNode;
    }

    public void setJobChainNode(String val) {
        jobChainNode = val;
    }

}
