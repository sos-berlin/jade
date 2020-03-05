package sos.scheduler.job;

import static com.sos.scheduler.messages.JSMessages.JSJ_E_0040;
import static com.sos.scheduler.messages.JSMessages.JSJ_F_0080;
import static com.sos.scheduler.messages.JSMessages.JSJ_F_0090;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0017;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0018;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0019;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0090;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.DataExchange.JadeEngine;
import com.sos.DataExchange.Options.JADEOptions;
import com.sos.DataExchange.history.YadeHistory;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.io.Files.JSTextFile;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.i18n.annotation.I18NResourceBundle;
import com.sos.jitl.xmleditor.common.JobSchedulerXmlEditor;
import com.sos.scheduler.model.SchedulerObjectFactory;
import com.sos.scheduler.model.commands.JSCmdAddOrder;
import com.sos.scheduler.model.objects.Spooler;

import sos.spooler.Order;
import sos.spooler.Variable_set;

@I18NResourceBundle(baseName = "com.sos.scheduler.messages", defaultLocale = "en")
public class SOSDExJSAdapterClass extends JobSchedulerJobAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SOSDExJSAdapterClass.class);

    private static final String CLASSNAME = SOSDExJSAdapterClass.class.getSimpleName();

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
    private static final String ORDER_PARAMETER_FILE_PATH_RESTRICTION = "yade_file_path_restriction";
    private static final String ORDER_PARAMETER_SCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET = "scheduler_SOSFileOperations_ResultSet";
    private static final String ORDER_PARAMETER_SCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET_SIZE = "scheduler_SOSFileOperations_ResultSetSize";
    private static final String ORDER_PARAMETER_SCHEDULER_SOS_FILE_OPERATIONS_FILE_COUNT = "scheduler_SOSFileOperations_file_count";
    private static final String YADE_TRANSFER_ID = "yade_transfer_id";

    private SOSFileList transfFiles = null;
    private JADEOptions jadeOptions = null;
    private JadeEngine jadeEngine = null;
    private SchedulerObjectFactory jobSchedulerFactory = null;

    public SOSDExJSAdapterClass() {
        super();
    }

    @Override
    public boolean spooler_process() throws Exception {
        try {
            super.spooler_process();
            doProcessing();
        } catch (Exception e) {
            LOGGER.error(String.format("%1$s ended with error: %2$s", CLASSNAME, e.toString()), e);
            throw e;
        }
        return signalSuccess();
    }

    private void doProcessing() throws Exception {
        YadeHistory history = null;
        try {
            jadeOptions = new JADEOptions();
            jadeOptions.setCurrentNodeName(getCurrentNodeName());
            jadeEngine = new JadeEngine(jadeOptions);
            Path xml2iniFile = null;
            HashMap<String, String> schedulerParams = getSchedulerParameterAsProperties(getJobOrOrderParameters());
            if (schedulerParams != null) {
                if (schedulerParams.containsKey("settings")) {
                    String paramSettings = schedulerParams.get("settings");
                    File f = new File(paramSettings);
                    // resolved file path (in case of simlinks: UNIX -path to the orig file, Windows - path to the symlink file)
                    String settings = f.getCanonicalPath();
                    if (!f.exists()) {
                        throw new JobSchedulerException(String.format("[%s]settings file not found", settings));
                    }
                    if (!schedulerParams.containsKey("profile")) {
                        throw new JobSchedulerException(String.format("[%s]missing 'profile' parameter", settings));
                    }

                    jadeOptions.setOriginalSettingsFile(settings);
                    LOGGER.debug(String.format("settings=%s", settings));
                    // check both (in case of symlink the orig file can have any extension...)
                    if (paramSettings.toLowerCase().endsWith(".xml") || settings.toLowerCase().endsWith(".xml")) {
                        JADEOptions jo = new JADEOptions();
                        xml2iniFile = jo.convertXml2Ini(settings);
                        schedulerParams.put("settings", xml2iniFile.toString());
                    }
                } else {
                    if (schedulerParams.containsKey("profile")) {
                        Path defaultConfiguration = Paths.get(spooler.configuration_directory()).resolve(JobSchedulerXmlEditor.getLivePathYadeIni());
                        if (Files.exists(defaultConfiguration)) {
                            schedulerParams.put("settings", defaultConfiguration.toString());
                        } else {
                            LOGGER.warn(String.format("[%s]default configuration file not found", defaultConfiguration));
                            throw new JobSchedulerException(String.format(
                                    "[%s][missing configuration]set 'settings' parameter or use a configuration created with the JOC XMLEditor",
                                    schedulerParams.get("profile")));
                        }
                    }
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

            history = new YadeHistory(spooler);
            Path hibernateConfigFile = null;
            try {
                hibernateConfigFile = getHibernateConfigurationReporting();
            } catch (Throwable t) {
                hibernateConfigFile = null;
                LOGGER.warn("No ./config/reporting.hibernate.cfg.xml found on file system! Transfer history won´t be processed.");
            }
            history.buildFactory(hibernateConfigFile);
            jadeEngine.setJobSchedulerEventHandler(history);

            jadeEngine.getOptions().setJobSchedulerId(spooler.id());

            boolean isOrderJob = spooler_task.job().order_queue() != null;
            Order order = null;
            Variable_set orderParams = null;
            if (isOrderJob) {
                order = spooler_task.order();
                orderParams = order.params();
                jadeEngine.getOptions().setJob(this.getJobFolder() + "/" + this.getJobName());
                jadeEngine.getOptions().setJobChain(order.job_chain().path());
                jadeEngine.getOptions().setJobChainNodeName(order.state());
                jadeEngine.getOptions().setOrderId(order.id());
            }
            jadeEngine.getOptions().setTaskId("" + spooler_task.id());
            if (schedulerParams.get(YADE_TRANSFER_ID) != null && !schedulerParams.get(YADE_TRANSFER_ID).isEmpty()) {
                history.setParentTransferId(Long.parseLong(schedulerParams.get(YADE_TRANSFER_ID)));
            }
            if (schedulerParams.get(ORDER_PARAMETER_FILE_PATH_RESTRICTION) != null && !schedulerParams.get(ORDER_PARAMETER_FILE_PATH_RESTRICTION)
                    .isEmpty()) {
                history.setFilePathRestriction(schedulerParams.get(ORDER_PARAMETER_FILE_PATH_RESTRICTION));
                LOGGER.debug(ORDER_PARAMETER_FILE_PATH_RESTRICTION + " was set to: " + schedulerParams.get(ORDER_PARAMETER_FILE_PATH_RESTRICTION));
            }
            try {
                jadeEngine.execute();
            } catch (Exception e) {
                throw e;
            } finally {
                if (isOrderJob && orderParams != null && history != null && history.getTransferId() != null) {
                    orderParams.set_var(YADE_TRANSFER_ID, history.getTransferId().toString());
                }
            }
            transfFiles = jadeEngine.getFileList();
            int resultSetSize = transfFiles.getList().size();
            if (resultSetSize <= 0 && isOrderJob && orderParams != null && jadeOptions.pollErrorState.isDirty()) {
                String pollErrorState = jadeOptions.pollErrorState.getValue();
                LOGGER.info("set order-state to " + pollErrorState);
                setNextNodeState(pollErrorState);
                orderParams.set_var(VARNAME_FTP_RESULT_ERROR_MESSAGE, "");
                order.set_state_text("ended with no files found");
            }
            if (isOrderJob) {
                String onEmptyResultSetState = jadeOptions.onEmptyResultSet.getValue();
                if (isNotEmpty(onEmptyResultSetState) && resultSetSize <= 0) {
                    JSJ_I_0090.toLog(onEmptyResultSetState);
                    order.set_state(onEmptyResultSetState);
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
            createOrderParameter(jadeEngine, isOrderJob, order, orderParams);

            if (jadeOptions.createOrder.isNotDirty() && (jadeOptions.createOrdersForNewFiles.isTrue() || jadeOptions.createOrdersForAllFiles
                    .isTrue())) {
                jadeOptions.createOrder.setTrue();
            }

            if (resultSetSize > 0 && jadeOptions.createOrder.isTrue()) {
                String jobChainName = jadeOptions.orderJobchainName.getValue();
                if (jadeOptions.createOrdersForAllFiles.isTrue()) {
                    for (SOSFileListEntry listItem : transfFiles.getList()) {
                        createOrder(listItem, jobChainName, orderParams);
                    }
                } else {
                    if (jadeOptions.createOrdersForNewFiles.isTrue()) {
                        for (SOSFileListEntry listItem : transfFiles.getList()) {
                            if (!listItem.isTargetFileExistsBeforeTransfer()) {
                                createOrder(listItem, jobChainName, orderParams);
                            }
                        }
                    } else {
                        createOrder(transfFiles.getList().get(0), jobChainName, orderParams);
                    }
                }
            }
        } finally {
            if (history != null) {
                history.closeFactory();
            }
        }

    }

    protected void createOrder(final SOSFileListEntry listItem, final String jobChainName, Variable_set orderParams) {
        String feedback;
        if (jadeOptions.orderJobschedulerHost.isNotEmpty()) {
            feedback = createOrderOnRemoteJobScheduler(listItem, jobChainName, orderParams);
        } else {
            feedback = createOrderOnLocalJobScheduler(listItem, jobChainName, orderParams);
        }
        LOGGER.info(feedback);
    }

    protected String createOrderOnRemoteJobScheduler(final SOSFileListEntry listItem, final String jobChainName, Variable_set currentOrderParams) {
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
        order.setParams(jobSchedulerFactory.setParams(buildOrderParams(listItem, currentOrderParams)));
        String feedback = JSJ_I_0018.get(targetFilename, jobChainName);
        if (changeOrderState()) {
            String strNextState = jadeOptions.nextState.getValue();
            order.setState(strNextState);
            feedback += " " + JSJ_I_0019.get(strNextState);
        }
        order.run();
        return feedback;
    }

    protected String createOrderOnLocalJobScheduler(final SOSFileListEntry listItem, final String jobChainName, Variable_set currentOrderParams) {
        Order order = spooler.create_order();
        String targetFilename = listItem.getTargetFileName().replace('\\', '/');
        order.set_id(targetFilename);
        String feedback = JSJ_I_0018.get(targetFilename, jobChainName);
        if (changeOrderState()) {
            String nextState = jadeOptions.nextState.getValue();
            order.set_state(nextState);
            feedback += " " + JSJ_I_0019.get(nextState);
        }
        order.set_params(buildOrderParams(listItem, currentOrderParams));
        order.set_title(JSJ_I_0017.get(spooler_task.job().name()));
        spooler.job_chain(jobChainName).add_or_replace_order(order);
        return feedback;
    }

    private Variable_set buildOrderParams(SOSFileListEntry listItem, Variable_set currentOrderParams) {
        Variable_set orderParams = spooler.create_variable_set();
        if (jadeOptions.mergeOrderParameter.isTrue()) {
            orderParams.merge(currentOrderParams);
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

    private void createOrderParameter(final JadeEngine jadeEngine, boolean isOrderJob, Order order, Variable_set orderParams) throws Exception {
        try {
            String fileNames = "";
            String filePaths = "";
            Variable_set params = null;
            if (isOrderJob) {
                if (order != null && orderParams != null) {
                    params = orderParams;
                }
            } else {
                params = spooler_task.params();
            }
            if (params != null) {
                long size = transfFiles.getList().size();
                if (size > 0) {
                    for (SOSFileListEntry entry : transfFiles.getList()) {
                        String name = entry.getFileName4ResultList();
                        filePaths += name + ";";
                        fileNames += name + ";";
                    }
                    filePaths = filePaths.substring(0, filePaths.length() - 1);
                    fileNames = fileNames.substring(0, fileNames.length() - 1);
                }
                if (orderParams != null) {
                    orderParams.set_var(ORDER_PARAMETER_SCHEDULER_SOS_FILE_OPERATIONS_FILE_COUNT, String.valueOf(size));

                    String resultList2File = jadeEngine.getOptions().resultListFile.getValue();
                    if (isNotEmpty(resultList2File) && isNotEmpty(fileNames)) {
                        JSTextFile file = new JSTextFile(resultList2File);
                        try {
                            if (file.canWrite()) {
                                file.write(fileNames);
                                file.close();
                            } else {
                                JSJ_F_0090.toLog(jadeEngine.getOptions().resultListFile.getShortKey(), resultList2File);
                            }
                        } catch (Exception e) {
                            throw new JobSchedulerException(JSJ_F_0080.get(resultList2File, jadeEngine.getOptions().resultListFile.getShortKey()), e);
                        }
                    }
                    orderParams.set_var(ORDER_PARAMETER_SCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET, fileNames);
                    orderParams.set_var(ORDER_PARAMETER_SCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET_SIZE, String.valueOf(size));
                }
                params.set_var(VARNAME_FTP_RESULT_FILES, Integer.toString((int) size));
                params.set_var(VARNAME_FTP_RESULT_ZERO_BYTE_FILES, Long.toString(transfFiles.getCounterSkippedZeroByteFiles()));
                params.set_var(VARNAME_FTP_RESULT_FILENAMES, fileNames);
                params.set_var(VARNAME_FTP_RESULT_FILEPATHS, filePaths);
            }
        } catch (JobSchedulerException e) {
            LOGGER.error("error occurred creating order Parameter: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            throw new JobSchedulerException("error occurred creating order Parameter: ", e);
        }
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

}