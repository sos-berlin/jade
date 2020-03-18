package com.sos.DataExchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import org.apache.log4j.BasicConfigurator;
// import org.apache.log4j.Logger;
// import org.apache.log4j.PropertyConfigurator;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Basics.JSJobUtilities;
import com.sos.i18n.I18NBase;
import com.sos.i18n.annotation.I18NMessage;
import com.sos.i18n.annotation.I18NMessages;
import com.sos.i18n.annotation.I18NResourceBundle;

@I18NResourceBundle(baseName = "SOSDataExchange", defaultLocale = "en")
public class SOSDataExchangeEngine4DMZMain extends I18NBase implements JSJobUtilities {

    private static final Logger LOGGER = LoggerFactory.getLogger(SOSDataExchangeEngine4DMZMain.class);
    @I18NMessages(value = { @I18NMessage("JADE4DMZ client - Main routine started ..."),
            @I18NMessage(value = "JADE4DMZ client", locale = "en_UK", explanation = "JADE4DMZ client"),
            @I18NMessage(value = "JADE4DMZ client - Kommandozeilenprogram startet ...", locale = "de", explanation = "JADE4DMZ client") }, msgnum = "SOSJADE_I_9999", msgurl = "")
    public static final String SOSDX_Intro = "SOSDataExchangeEngineMain.SOSDX-Intro";
    @I18NMessages(value = { @I18NMessage("%1$s: Error occurred ...: %2$s, exit-code %3$s raised"),
            @I18NMessage(value = "%1$s: Error occurred ...: %2$s", locale = "en_UK", explanation = "%1$s: Error occurred ...: %2$s"),
            @I18NMessage(value = "%1$s: Fehler aufgetreten: %2$s, Programm wird mit Exit-Code %3$s beendet.", locale = "de", explanation = "%1$s: Error occurred ...: %2$s") }, msgnum = "SOSJADE_E_0001", msgurl = "")
    public static final String SOSDX_E_0001 = "SOSDataExchangeEngineMain.SOSDX_E_0001";
    @I18NMessages(value = { @I18NMessage("%1$s - ended without errors"),
            @I18NMessage(value = "%1$s - ended without errors", locale = "en_UK", explanation = "%1$s - ended without errors"),
            @I18NMessage(value = "%1$s - Programm wurde ohne Fehler beendet", locale = "de", explanation = "%1$s - ended without errors") }, msgnum = "SOSJADE_I_106", msgurl = "")
    public static final String SOS_EXIT_WO_ERRORS = "SOSDataExchangeEngineMain.SOS_EXIT_WO_ERRORS";
    @I18NMessages(value = { @I18NMessage("%1$s - terminated with exit-code %2$d"),
            @I18NMessage(value = "%1$s - terminated with exit-code %2$d", locale = "en_UK", explanation = "%1$s - terminated with exit-code %2$d"),
            @I18NMessage(value = "%1$s - Fehlercode %2$d wurde gesetzt", locale = "de", explanation = "%1$s - terminated with exit-code %2$d") }, msgnum = "SOSJADE_E_0002", msgurl = "")
    public static final String SOS_EXIT_CODE_RAISED = "SOSDataExchangeEngineMain.SOS_EXIT_CODE_RAISED";

    public final static void main(final String[] args) {
        SOSDataExchangeEngine4DMZMain main = new SOSDataExchangeEngine4DMZMain();
        main.execute(args);
    }

    protected SOSDataExchangeEngine4DMZMain() {
        super("SOSDataExchange");
    }

    private void execute(final String[] args) {
        final String conMethodName = "SOSDataExchangeEngine4DMZMain::execute";
        int exitCode = 0;
        Jade4DMZ jade4dmz = null;
        try {
            jade4dmz = new Jade4DMZ();
            JADEOptions options = jade4dmz.getOptions();
            jade4dmz.setJSJobUtilites(this);
            options.commandLineArgs(args);
            LOGGER.info(getMsg(SOSDX_Intro));
            jade4dmz.execute();
            LOGGER.info(String.format(getMsg(SOS_EXIT_WO_ERRORS), conMethodName));
        } catch (Exception e) {
            exitCode = 99;
            LOGGER.error(String.format(getMsg(SOSDX_E_0001), conMethodName, e.getMessage(), exitCode), e);
        }
        System.exit(exitCode);
    }

    @Override
    public String replaceSchedulerVars(final String pstrString2Modify) {
        return pstrString2Modify;
    }

    @Override
    public void setJSParam(final String pstrKey, final String pstrValue) {
    }

    @Override
    public void setJSParam(final String pstrKey, final StringBuffer pstrValue) {
    }
    
    @Override
    public void setJSJobUtilites(final JSJobUtilities pobjJSJobUtilities) {

    }

    @Override
    public void setStateText(final String pstrStateText) {

    }

    @Override
    public void setCC(final int pintCC) {

    }

    @Override
    public void setNextNodeState(final String pstrNodeName) {

    }

}