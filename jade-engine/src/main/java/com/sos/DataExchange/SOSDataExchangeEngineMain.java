package com.sos.DataExchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.JSHelper.Basics.JSJobUtilities;
import com.sos.i18n.I18NBase;
import com.sos.i18n.annotation.I18NMessage;
import com.sos.i18n.annotation.I18NMessages;
import com.sos.i18n.annotation.I18NResourceBundle;
import com.sos.vfs.common.options.SOSBaseOptions;

@I18NResourceBundle(baseName = "SOSDataExchange", defaultLocale = "en")
public class SOSDataExchangeEngineMain extends I18NBase implements JSJobUtilities {

    private static final Logger LOGGER = LoggerFactory.getLogger(SOSDataExchangeEngineMain.class);
    protected SOSBaseOptions jadeOptions = null;
    @I18NMessages(value = { @I18NMessage("JADE client - Main routine started ..."),
            @I18NMessage(value = "JADE client - Main routine started ...", locale = "en_UK", explanation = "JADE client - Main"),
            @I18NMessage(value = "JADE Client - Kommandozeilenprogram startet ...", locale = "de", explanation = "JADE Client - Main") }, msgnum = "SOSJADE_I_9999", msgurl = "")
    public static final String SOSDX_Intro = "SOSDataExchangeEngineMain.SOSDX-Intro";

    @I18NMessages(value = { @I18NMessage("%1$s: Error occurred ...: %2$s, exit-code %3$s raised"),
            @I18NMessage(value = "%1$s: Error occurred ...: %2$s, exit-code %3$s raised", locale = "en_UK", explanation = "%1$s: Error occurred ...: %2$s"),
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

    private SOSDataExchangeEngine engine;

    public final static void main(final String[] args) {
        SOSDataExchangeEngineMain main = new SOSDataExchangeEngineMain();

        Runtime.getRuntime().addShutdownHook(new Thread() {

            public void run() {
                try {
                    if (main.getEngine() != null) {
                        LOGGER.info("shutdown engine");
                        main.getEngine().disconnect();
                        main.getEngine().showSummary();
                    }
                } catch (Throwable e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        int exitCode = main.Execute(args);
        System.exit(exitCode);
    }

    protected SOSDataExchangeEngineMain() {
        super("SOSDataExchange");
    }

    private int Execute(final String[] args) {
        final String method = "Execute";
        int exitCode = 0;
        try {
            engine = new SOSDataExchangeEngine();
            SOSBaseOptions options = engine.getOptions();
            engine.setJSJobUtilites(this);
            options.sendTransferHistory.value(true);
            options.commandLineArgs(args);
            LOGGER.info(getMsg(SOSDX_Intro));
            engine.execute();
            LOGGER.info(String.format(getMsg(SOS_EXIT_WO_ERRORS), method));
        } catch (Exception e) {
            exitCode = 99;
            LOGGER.error(String.format(getMsg(SOSDX_E_0001), method, e.getMessage(), exitCode), e);
        } finally {
            engine = null;
        }
        return exitCode;
    }

    public SOSDataExchangeEngine getEngine() {
        return engine;
    }

    @Override
    public String replaceSchedulerVars(final String pstrString2Modify) {
        return pstrString2Modify;
    }

    @Override
    public void setJSParam(final String pstrKey, final String pstrValue) {
        //
    }

    @Override
    public void setJSParam(final String pstrKey, final StringBuilder pstrValue) {
        //
    }

    @Override
    public void setJSJobUtilites(final JSJobUtilities pobjJSJobUtilities) {
        //
    }

    @Override
    public void setStateText(final String pstrStateText) {
        //
    }

    @Override
    public void setExitCode(final int pintCC) {
        //
    }

    @Override
    public void setNextNodeState(final String pstrNodeName) {
        //
    }

}