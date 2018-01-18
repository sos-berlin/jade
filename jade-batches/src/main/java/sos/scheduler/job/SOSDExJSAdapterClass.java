package sos.scheduler.job;

import com.sos.DataExchange.JadeEngine;
import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.io.Files.JSTextFile;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.VirtualFileSystem.Options.SOSFTPOptions;
import com.sos.i18n.annotation.I18NResourceBundle;
import com.sos.scheduler.model.SchedulerObjectFactory;
import com.sos.scheduler.model.commands.JSCmdAddOrder;
import com.sos.scheduler.model.objects.Spooler;

import sos.spooler.Order;
import sos.spooler.Variable_set;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sos.scheduler.messages.JSMessages.*;

@I18NResourceBundle(baseName = "com.sos.scheduler.messages", defaultLocale = "en")
public class SOSDExJSAdapterClass extends JobSchedulerJobAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JADEOptions.class);

    private static final String CLASSNAME = "SOSDExJSAdapterClass";
    private static final String VARNAME_FTP_RESULT_FILES = "ftp_result_files";
    private static final String VARNAME_FTP_RESULT_ZERO_BYTE_FILES = "ftp_result_zero_byte_files";
    private static final String VARNAME_FTP_RESULT_FILENAMES = "ftp_result_filenames";
    private static final String VARNAME_FTP_RESULT_FILEPATHS = "ftp_result_filepaths";
    private static final String VARNAME_FTP_RESULT_ERROR_MESSAGE = "ftp_result_error_message";
    private static final String ORDER_PARAMETER_SCHEDULER_FILE_PATH = "scheduler_file_path";
    private static final String ORDER_PARAMETER_SCHEDULER_FILE_PARENT = "scheduler_file_parent";
    private static final String ORDER_PARAMETER_SCHEDULER_FILE_NAME = "scheduler_file_name";
    private static final String ORDER_PARAMETER_SCHEDULER_TARGET_FILE_PARENT = "scheduler_target_file_parent";
    private static final String ORDER_PARAMETER_SCHEDULER_TARGET_FILE_NAME = "scheduler_target_file_name";
    private static final String ORDER_PARAMETER_SCHEDULER_SOURCE_FILE_PARENT = "scheduler_source_file_parent";
    private static final String ORDER_PARAMETER_SCHEDULER_SOURCE_FILE_NAME = "scheduler_source_file_name";
    private SOSFileList transfFiles = null;
    private SOSFTPOptions jadeOptions = null;
    private JadeEngine jadeEngine = null;
    private SchedulerObjectFactory jobSchedulerFactory = null;
    public static final String conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET = "scheduler_SOSFileOperations_ResultSet";
    public static final String conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET_SIZE = "scheduler_SOSFileOperations_ResultSetSize";
    public static final String conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_FILE_COUNT = "scheduler_SOSFileOperations_file_count";

    public SOSDExJSAdapterClass() {
        super();
    }

    @Override
    public boolean spooler_process() throws Exception {
        try {
            super.spooler_process();
            doProcessing();
        } catch (Exception e) {
            LOGGER.error(String.format("%1$s ended with error: %2$s", CLASSNAME, e.getMessage()), e);
            LOGGER.debug("", e);
            throw e;
        }
        return signalSuccess();
    }

    private void doProcessing() throws Exception {
        String method = "doProcessing";
        jadeOptions = null;
        jadeEngine = new JadeEngine();
        jadeOptions = jadeEngine.getOptions();
        jadeOptions.setCurrentNodeName(getCurrentNodeName());

        Path xml2iniFile = null;
        HashMap<String, String> schedulerParams = getSchedulerParameterAsProperties(getJobOrOrderParameters());
        if (schedulerParams != null && schedulerParams.containsKey("settings")) {
            String settings = schedulerParams.get("settings");
            jadeOptions.setOriginalSettingsFile(settings);
            LOGGER.debug(String.format("%s: schedulerParams settings=%s", method, settings));
            if (settings.toLowerCase().endsWith(".xml")) {
                JADEOptions jo = new JADEOptions();
                xml2iniFile = jo.convertXml2Ini(settings);
                schedulerParams.put("settings", xml2iniFile.toString());
            }
        }
        jadeOptions.setAllOptions2(jadeOptions.deletePrefix(schedulerParams, "ftp_"));
        if (xml2iniFile != null) {// !!! setAllOptions2 override the jadeOptions.settings
            jadeOptions.settings.setValue(xml2iniFile.toString());
        }
        int intLogLevel = -1 * spooler_log.level();
        if (intLogLevel > jadeOptions.verbose.value()) {
            jadeOptions.verbose.value(intLogLevel);
        }
        jadeEngine.setJSJobUtilites(this);
        jadeEngine.getOptions().setDeleteSettingsFileOnExit(xml2iniFile != null);
        try {
            jadeEngine.execute();
        } catch (Exception e) {
            throw e;
        } finally {
            jadeEngine.logout();
        }
        transfFiles = jadeEngine.getFileList();
        int resultSetSize = transfFiles.getList().size();
        if (resultSetSize <= 0 && isOrderJob() && jadeOptions.pollErrorState.isDirty()) {
            String pollErrorState = jadeOptions.pollErrorState.getValue();
            LOGGER.info("set order-state to " + pollErrorState);
            setNextNodeState(pollErrorState);
            spooler_task.order().params().set_var(VARNAME_FTP_RESULT_ERROR_MESSAGE, "");
            spooler_task.order().set_state_text("ended with no files found");
        }
        if (isJobchain()) {
            String onEmptyResultSetState = jadeOptions.onEmptyResultSet.getValue();
            if (isNotEmpty(onEmptyResultSetState) && resultSetSize <= 0) {
                JSJ_I_0090.toLog(onEmptyResultSetState);
                spooler_task.order().set_state(onEmptyResultSetState);
            }
        }
        String raiseErrorIfResultSetIs = jadeOptions.raiseErrorIfResultSetIs.getValue();
        if (isNotEmpty(raiseErrorIfResultSetIs)) {
            boolean flgR = jadeOptions.expectedSizeOfResultSet.compare(raiseErrorIfResultSetIs, resultSetSize);
            if (flgR) {
                String strM = JSJ_E_0040.get(resultSetSize, raiseErrorIfResultSetIs, jadeOptions.expectedSizeOfResultSet.value());
                LOGGER.error(strM);
                throw new JobSchedulerException(strM);
            }
        }
        createOrderParameter(jadeEngine);

        if (jadeOptions.createOrder.isNotDirty() && (jadeOptions.createOrdersForNewFiles.isTrue() || jadeOptions.createOrdersForAllFiles.isTrue())) {
            jadeOptions.createOrder.setTrue();
        }

        if (resultSetSize > 0 && jadeOptions.createOrder.isTrue()) {
            String jobChainName = jadeOptions.orderJobchainName.getValue();
            if (jadeOptions.createOrdersForAllFiles.isTrue()) {
                for (SOSFileListEntry listItem : transfFiles.getList()) {
                    createOrder(listItem, jobChainName);
                }
            } else {
                if (jadeOptions.createOrdersForNewFiles.isTrue()) {
                    for (SOSFileListEntry listItem : transfFiles.getList()) {
                        if (!listItem.isTargetFileAlreadyExists()) {
                            createOrder(listItem, jobChainName);
                        }
                    }
                } else {
                    createOrder(transfFiles.getList().get(0), jobChainName);
                }
            }
        }

    }

    protected void createOrder(final SOSFileListEntry listItem, final String jobChainName) {
        String feedback;
        if (jadeOptions.orderJobschedulerHost.isNotEmpty()) {
            feedback = createOrderOnRemoteJobScheduler(listItem, jobChainName);
        } else {
            feedback = createOrderOnLocalJobScheduler(listItem, jobChainName);
        }
        LOGGER.info(feedback);
    }

    protected String createOrderOnRemoteJobScheduler(final SOSFileListEntry listItem, final String jobChainName) {
        if (jobSchedulerFactory == null) {
            jobSchedulerFactory = new SchedulerObjectFactory(jadeOptions.orderJobschedulerHost.getValue(), jadeOptions.orderJobschedulerPort.value());
            jobSchedulerFactory.initMarshaller(Spooler.class);
            jobSchedulerFactory.getOptions().TransferMethod.set(jadeOptions.schedulerTransferMethod);
            jobSchedulerFactory.getOptions().PortNumber.set(jadeOptions.orderJobschedulerPort);
            jobSchedulerFactory.getOptions().ServerName.set(jadeOptions.orderJobschedulerHost);
        }
        JSCmdAddOrder order = jobSchedulerFactory.createAddOrder();
        String targetFilename = listItem.getTargetFileName().replace('\\', '/');
        order.setId(targetFilename);
        order.setJobChain(jobChainName);
        order.setParams(jobSchedulerFactory.setParams(buildOrderParams(listItem)));
        String feedback = JSJ_I_0018.get(targetFilename, jobChainName);
        if (changeOrderState()) {
            String strNextState = jadeOptions.nextState.getValue();
            order.setState(strNextState);
            feedback += " " + JSJ_I_0019.get(strNextState);
        }
        order.run();
        return feedback;
    }

    protected String createOrderOnLocalJobScheduler(final SOSFileListEntry listItem, final String jobChainName) {
        Order order = spooler.create_order();
        String targetFilename = listItem.getTargetFileName().replace('\\', '/');
        order.set_id(targetFilename);
        String feedback = JSJ_I_0018.get(targetFilename, jobChainName);
        if (changeOrderState()) {
            String nextState = jadeOptions.nextState.getValue();
            order.set_state(nextState);
            feedback += " " + JSJ_I_0019.get(nextState);
        }
        order.set_params(buildOrderParams(listItem));
        order.set_title(JSJ_I_0017.get(spooler_task.job().name()));
        spooler.job_chain(jobChainName).add_or_replace_order(order);
        return feedback;
    }

    private Variable_set buildOrderParams(SOSFileListEntry listItem) {
        Variable_set orderParams = spooler.create_variable_set();
        if (jadeOptions.mergeOrderParameter.isTrue()) {
            orderParams.merge(spooler_task.order().params());
        }
        String[] targetFile = getFilenameParts(jadeOptions.targetDir.getValue(), listItem.getTargetFileName());
        if (jadeOptions.paramNameForPath.isDirty()) {
            orderParams.set_value(jadeOptions.paramNameForPath.getValue(), targetFile[0]);
        } else {
            orderParams.set_value(ORDER_PARAMETER_SCHEDULER_FILE_PATH, targetFile[0]);
        }
        orderParams.set_value(ORDER_PARAMETER_SCHEDULER_FILE_PARENT, targetFile[1]);
        orderParams.set_value(ORDER_PARAMETER_SCHEDULER_FILE_NAME, targetFile[2]);
        orderParams.set_value(ORDER_PARAMETER_SCHEDULER_TARGET_FILE_PARENT, targetFile[1]);
        orderParams.set_value(ORDER_PARAMETER_SCHEDULER_TARGET_FILE_NAME, targetFile[2]);
        String[] sourceFile = getFilenameParts(jadeOptions.sourceDir.getValue(), listItem.getSourceFileName());
        orderParams.set_value(ORDER_PARAMETER_SCHEDULER_SOURCE_FILE_PARENT, sourceFile[1]);
        orderParams.set_value(ORDER_PARAMETER_SCHEDULER_SOURCE_FILE_NAME, sourceFile[2]);
        return orderParams;
    }

    private String[] getFilenameParts(String folder, String filename) {
        String[] file = { "", "", "" };
        if (folder == null) {
            folder = "";
        }
        folder = folder.replace('\\', '/').replaceFirst("/$", "");
        if (filename == null) {
            filename = "";
        }
        filename = filename.replace('\\', '/');
        if (!filename.startsWith(folder)) {
            filename = folder + "/" + filename;
        }
        File f = new File(filename);
        file[0] = filename;
        file[1] = f.getParent().replace('\\', '/');
        file[2] = f.getName();
        return file;
    }

    private boolean changeOrderState() {
        return isNotEmpty(jadeOptions.nextState.getValue());
    }

    private void createOrderParameter(final JadeEngine objR) throws Exception {
        try {
            String fileNames = "";
            String filePaths = "";
            Variable_set objParams = null;
            if (spooler_job.order_queue() != null) {
                if (spooler_task.order() != null && spooler_task.order().params() != null) {
                    objParams = spooler_task.order().params();
                }
            } else {
                objParams = spooler_task.params();
            }
            if (objParams != null) {
                long intNoOfHitsInResultSet = transfFiles.getList().size();
                if (intNoOfHitsInResultSet > 0) {
                    for (SOSFileListEntry objListItem : transfFiles.getList()) {
                        String strT = objListItem.getFileName4ResultList();
                        filePaths += strT + ";";
                        fileNames += strT + ";";
                    }
                    filePaths = filePaths.substring(0, filePaths.length() - 1);
                    fileNames = fileNames.substring(0, fileNames.length() - 1);
                }
                setOrderParameter(conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_FILE_COUNT, String.valueOf(intNoOfHitsInResultSet));
                Variable_set objP = null;
                if (isNotNull(spooler_task.order())) {
                    objP = spooler_task.order().params();
                }
                if (isNotNull(objP)) {
                    String strResultList2File = objR.getOptions().resultListFile.getValue();
                    if (isNotEmpty(strResultList2File) && isNotEmpty(fileNames)) {
                        JSTextFile objResultListFile = new JSTextFile(strResultList2File);
                        try {
                            if (objResultListFile.canWrite()) {
                                objResultListFile.write(fileNames);
                                objResultListFile.close();
                            } else {
                                JSJ_F_0090.toLog(objR.getOptions().resultListFile.getShortKey(), strResultList2File);
                            }
                        } catch (Exception e) {
                            String strM = JSJ_F_0080.get(strResultList2File, objR.getOptions().resultListFile.getShortKey());
                            throw new JobSchedulerException(strM, e);
                        }
                    }
                    setOrderParameter(conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET, fileNames);
                    setOrderParameter(conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET_SIZE, String.valueOf(intNoOfHitsInResultSet));
                }
                objParams.set_var(VARNAME_FTP_RESULT_FILES, Integer.toString((int) intNoOfHitsInResultSet));
                objParams.set_var(VARNAME_FTP_RESULT_ZERO_BYTE_FILES, Integer.toString(transfFiles.getZeroByteCount()));
                objParams.set_var(VARNAME_FTP_RESULT_FILENAMES, fileNames);
                objParams.set_var(VARNAME_FTP_RESULT_FILEPATHS, filePaths);
            }
        } catch (JobSchedulerException e) {
            LOGGER.error("error occurred creating order Parameter: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            throw new JobSchedulerException("error occurred creating order Parameter: ", e);
        }
    }

}