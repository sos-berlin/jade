package sos.jadehistory.sql;

import sos.connection.SOSConnection;
import sos.util.SOSLogger;

public abstract class Cmd {

    String table_name = "(table_name fehlt)";
    public SOSConnection conn;
    public boolean withQuote = false;

    Cmd(SOSConnection conn_, SOSLogger logger_) {
        if (conn_ == null) {
            throw new RuntimeException(getClass().getName() + ": connection nicht gesetzt");
        }
        if (conn_.connection == null) {
            throw new RuntimeException(getClass().getName() + ": Datenbank nicht verbunden");
        }
        conn = conn_;
    }

    public void set_table_name(String name) {
        table_name = name;
    }

    abstract String make_cmd_() throws Exception;

    public String make_cmd() throws Exception {
        return conn.normalizeStatement(make_cmd_());
    }

    public static String quoted(String value) {
        return "'" + value.toString().replaceAll("'", "''") + "'";
    }

}
