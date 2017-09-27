package com.sos.jade.db;

import org.hibernate.query.Query;

import com.sos.hibernate.classes.SOSHibernateSession;
import com.sos.hibernate.exceptions.SOSHibernateException;


public class YadeDBLayer {

    private SOSHibernateSession session;
    
    public YadeDBLayer(SOSHibernateSession session) {
        this.session = session;
    }

    public DBItemYadeProtocols getProtocolFromDb(String hostname, Integer port, Integer protocol) throws SOSHibernateException {
        StringBuilder sql = new StringBuilder();
        sql.append("from ").append(DBItemYadeProtocols.class.getSimpleName());
        sql.append(" where hostname = :hostname");
        sql.append(" and");
        sql.append(" port = :port");
        sql.append(" and");
        sql.append(" protocol = :protocol");
        Query<DBItemYadeProtocols> query = getSession().createQuery(sql.toString());
        query.setParameter("hostname", hostname);
        query.setParameter("port", port);
        query.setParameter("protocol", protocol);
        return getSession().getSingleResult(query);
    }
    
    public DBItemYadeTransfers getTransferFromDb(String uuid) throws SOSHibernateException {
        StringBuilder sql = new StringBuilder();
        sql.append("from ").append(DBItemYadeTransfers.class.getSimpleName());
        sql.append(" where uuid = :uuid");
        Query<DBItemYadeTransfers> query = getSession().createQuery(sql.toString());
        query.setParameter("uuid", uuid);
        return getSession().getSingleResult(query);
    }
    
    public SOSHibernateSession getSession() {
        return this.session;
    }
}
