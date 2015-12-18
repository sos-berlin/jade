package sos.jadehistory.sql;

import sos.connection.SOSConnection;
import sos.util.SOSLogger;

public class Insert_cmd extends Write_cmd {

    public Insert_cmd(SOSConnection conn_, SOSLogger logger_) {
        super(conn_, logger_);
        ignore_null = true;
        // null-Parameter werden ignoriert (macht die Inserts übersichtlicher)
    }

    public Insert_cmd(SOSConnection conn_, SOSLogger logger_, String table_name) {
        this(conn_, logger_);
        set_table_name(table_name);
        ignore_null = true;
        // null-Parameter werden ignoriert (macht die Inserts übersichtlicher)
    }

    String make_cmd_() throws Exception {
        return make_insert_cmd_();
    }
}
