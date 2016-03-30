package sos.scheduler.jade;

import static com.sos.scheduler.messages.JSMessages.JSJ_E_0040;
import static com.sos.scheduler.messages.JSMessages.JSJ_F_0080;
import static com.sos.scheduler.messages.JSMessages.JSJ_F_0090;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0017;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0018;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0019;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0090;

import java.io.File;

import sos.scheduler.job.JobSchedulerJobAdapter;
import sos.spooler.Order;
import sos.spooler.Variable_set;

import com.sos.DataExchange.Jade4DMZ;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.io.Files.JSTextFile;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.VirtualFileSystem.Options.SOSFTPOptions;
import com.sos.i18n.annotation.I18NResourceBundle;
import com.sos.scheduler.model.SchedulerObjectFactory;
import com.sos.scheduler.model.commands.JSCmdAddOrder;
import com.sos.scheduler.model.objects.Spooler;

/** \file SOSJade4DMZJSAdapter.java \class SOSJade4DMZJSAdapter
 *
 * \brief AdapterClass of SOSDEx for the SOSJobScheduler
 *
 * This Class SOSDExJSAdapterClass works as an adapter-class between the SOS
 * JobScheduler and the worker-class SOSDEx. */
@I18NResourceBundle(baseName = "com.sos.scheduler.messages", defaultLocale = "en")
public class SOSJade4DMZJSAdapter extends JobSchedulerJobAdapter {

    private final String conClassName = "SOSJade4DMZJSAdapter";

    private final String conVarname_ftp_result_files = "ftp_result_files";
    private final String conVarname_ftp_result_zero_byte_files = "ftp_result_zero_byte_files";
    private final String conVarname_ftp_result_filenames = "ftp_result_filenames";
    private final String conVarname_ftp_result_filepaths = "ftp_result_filepaths";
    private final String conVarname_ftp_result_error_message = "ftp_result_error_message";

    private static final String conOrderParameterSCHEDULER_FILE_PATH = "scheduler_file_path";
    private static final String conOrderParameterSCHEDULER_FILE_PARENT = "scheduler_file_parent";
    private static final String conOrderParameterSCHEDULER_FILE_NAME = "scheduler_file_name";
    private static final String conOrderParameterSCHEDULER_TARGET_FILE_PARENT = "scheduler_target_file_parent";
    private static final String conOrderParameterSCHEDULER_TARGET_FILE_NAME = "scheduler_target_file_name";
    private static final String conOrderParameterSCHEDULER_SOURCE_FILE_PARENT = "scheduler_source_file_parent";
    private static final String conOrderParameterSCHEDULER_SOURCE_FILE_NAME = "scheduler_source_file_name";
    public static final String conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET = "scheduler_SOSFileOperations_ResultSet";
    public static final String conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET_SIZE = "scheduler_SOSFileOperations_ResultSetSize";
    public static final String conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_FILE_COUNT = "scheduler_SOSFileOperations_file_count";

    private SOSFileList transfFiles = null;
    private SOSFTPOptions jadeOptions = null;
    private SchedulerObjectFactory jobSchedulerFactory = null;

    public SOSJade4DMZJSAdapter() {
        super();
    }

    @Override
    public boolean spooler_process() throws Exception {

        try {
            super.spooler_process();
            doProcessing();
        } catch (Exception e) {
            logger.error(String.format("%1$s ended with error: %2$s", conClassName, e.getMessage()));
            logger.debug("", e);
            throw e;
        } finally {
        }
        return signalSuccess();

    }

    private void doProcessing() throws Exception {
        jadeOptions = null;
        Jade4DMZ jade4DMZEngine = new Jade4DMZ();
        jadeOptions = jade4DMZEngine.getOptions();

        jadeOptions.CurrentNodeName(getCurrentNodeName());
        jadeOptions.setAllOptions2(jadeOptions.DeletePrefix(getSchedulerParameterAsProperties(getJobOrOrderParameters()), "ftp_"));
        int intLogLevel = -1 * spooler_log.level();
        if (intLogLevel > jadeOptions.verbose.value()) {
            jadeOptions.verbose.value(intLogLevel);
        }
        if (jadeOptions.scheduler_host.isNotDirty()) {
            jadeOptions.scheduler_host.Value("");
        }
        // objO.CheckMandatory(); //is made in Execute method

        logger.info(String.format("%1$s with operation %2$s started.", "JADE4DMZ", jadeOptions.operation.Value()));
        jade4DMZEngine.setJSJobUtilites(this);

        jade4DMZEngine.Execute();

        transfFiles = jade4DMZEngine.getFileList();

        int resultSetSize = 0;
        if (isNotNull(transfFiles)) { // https://change.sos-berlin.com/browse/SOSFTP-218
            resultSetSize = transfFiles.List().size();
        }

        if (resultSetSize <= 0 && isOrderJob()) {
            String pollErrorState = jadeOptions.PollErrorState.Value();
            if (jadeOptions.PollErrorState.isDirty()) {
                logger.debug("set order-state to " + pollErrorState);
                setNextNodeState(pollErrorState);
                spooler_task.order().params().set_var(conVarname_ftp_result_error_message, "");
                spooler_task.order().set_state_text("ended with no files found");
            }
        }

        if (isJobchain()) {
            String onEmptyResultSetState = jadeOptions.on_empty_result_set.Value();
            if (isNotEmpty(onEmptyResultSetState) && resultSetSize <= 0) {
                JSJ_I_0090.toLog(onEmptyResultSetState);
                spooler_task.order().set_state(onEmptyResultSetState);
            }
        }

        String raiseErrorIfResultSetIs = jadeOptions.raise_error_if_result_set_is.Value();
        if (isNotEmpty(raiseErrorIfResultSetIs)) {
            boolean flgR = jadeOptions.expected_size_of_result_set.compare(raiseErrorIfResultSetIs, resultSetSize);
            if (flgR == true) {
                String strM = JSJ_E_0040.get(resultSetSize, raiseErrorIfResultSetIs, jadeOptions.expected_size_of_result_set.value());
                logger.info(strM);
                throw new JobSchedulerException(strM);
            }
        }

        createOrderParameter(jade4DMZEngine);

        if (resultSetSize > 0 && jadeOptions.create_order.isTrue()) {
            String jobChainName = jadeOptions.order_jobchain_name.Value();
            if (jadeOptions.create_orders_for_all_files.isTrue()) {
                for (SOSFileListEntry listItem : transfFiles.List()) {
                    createOrder(listItem, jobChainName);
                }
            } else {
                createOrder(transfFiles.List().get(0), jobChainName);
            }
        }

        logger.info(String.format("%1$s with operation %2$s ended.", "JADE4DMZ", jadeOptions.operation.Value()));
    } // doProcessing

    /** @param listItem
     * @param jobChainName */
    protected void createOrder(final SOSFileListEntry listItem, final String jobChainName) {
        String feedback;
        if (jadeOptions.order_jobscheduler_host.isNotEmpty()) {
            feedback = createOrderOnRemoteJobScheduler(listItem, jobChainName);
        } else {
            feedback = createOrderOnLocalJobScheduler(listItem, jobChainName);
        }
        logger.info(feedback);
    }

    /** @param listItem
     * @param jobChainName
     * @return */
    protected String createOrderOnRemoteJobScheduler(final SOSFileListEntry listItem, final String jobChainName) {
        if (jobSchedulerFactory == null) {
            jobSchedulerFactory =
                    new SchedulerObjectFactory(jadeOptions.order_jobscheduler_host.Value(), jadeOptions.order_jobscheduler_port.value());
            jobSchedulerFactory.initMarshaller(Spooler.class);
            jobSchedulerFactory.Options().TransferMethod.Set(jadeOptions.Scheduler_Transfer_Method);
            jobSchedulerFactory.Options().PortNumber.Set(jadeOptions.order_jobscheduler_port);
            jobSchedulerFactory.Options().ServerName.Set(jadeOptions.order_jobscheduler_host);
        }
        JSCmdAddOrder order = jobSchedulerFactory.createAddOrder();
        String targetFilename = listItem.TargetFileName().replace('\\', '/');
        ;
        order.setId(targetFilename);
        order.setJobChain(jobChainName);
        order.setParams(jobSchedulerFactory.setParams(buildOrderParams(listItem)));
        // logger.debug(objOrder.toXMLString());
        String feedback = JSJ_I_0018.get(targetFilename, jobChainName); // "Order '%1$s' created for JobChain '%2$s'."
        if (changeOrderState()) {
            String strNextState = jadeOptions.next_state.Value();
            order.setState(strNextState);
            feedback += " " + JSJ_I_0019.get(strNextState); // "Next State is '%1$s'."
        }
        order.run();
        return feedback;
    }

    /** @param listItem
     * @param jobChainName
     * @return */
    protected String createOrderOnLocalJobScheduler(final SOSFileListEntry listItem, final String jobChainName) {
        Order order = spooler.create_order();
        String targetFilename = listItem.TargetFileName().replace('\\', '/');
        order.set_id(targetFilename);
        String feedback = JSJ_I_0018.get(targetFilename, jobChainName); // "Order '%1$s' created for JobChain '%2$s'."
        if (changeOrderState()) {
            String nextState = jadeOptions.next_state.Value();
            order.set_state(nextState);
            feedback += " " + JSJ_I_0019.get(nextState); // "Next State is '%1$s'."
        }
        order.set_params(buildOrderParams(listItem));
        order.set_title(JSJ_I_0017.get(spooler_task.job().name())); // "Order created by %1$s"
        spooler.job_chain(jobChainName).add_order(order);
        return feedback;
    }

    /** @param listItem
     * @return */
    private Variable_set buildOrderParams(SOSFileListEntry listItem) {
        Variable_set orderParams = spooler.create_variable_set();
        // kb: merge actual parameters into created order params (2012-07-25)
        if (jadeOptions.MergeOrderParameter.isTrue()) {
            orderParams.merge(spooler_task.order().params());
        }
        String[] targetFile = getFilenameParts(jadeOptions.TargetDir.Value(), listItem.TargetFileName());
        orderParams.set_value(conOrderParameterSCHEDULER_FILE_PATH, targetFile[0]);
        orderParams.set_value(conOrderParameterSCHEDULER_FILE_PARENT, targetFile[1]);
        orderParams.set_value(conOrderParameterSCHEDULER_FILE_NAME, targetFile[2]);
        orderParams.set_value(conOrderParameterSCHEDULER_TARGET_FILE_PARENT, targetFile[1]);
        orderParams.set_value(conOrderParameterSCHEDULER_TARGET_FILE_NAME, targetFile[2]);
        String[] sourceFile = getFilenameParts(jadeOptions.SourceDir.Value(), listItem.SourceFileName());
        orderParams.set_value(conOrderParameterSCHEDULER_SOURCE_FILE_PARENT, sourceFile[1]);
        orderParams.set_value(conOrderParameterSCHEDULER_SOURCE_FILE_NAME, sourceFile[2]);
        return orderParams;
    }

    /** @param folder
     * @param filename
     * @return */
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

    /** @return */
    private boolean changeOrderState() {
        return isNotEmpty(jadeOptions.next_state.Value());
    }

    private void createOrderParameter(final Jade4DMZ objR) throws Exception {
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
                // Die Anzahl in intNoOfHitsInResultSet ist redundant.
                // Eigentlich ist transfFiles.size entscheidend
                long intNoOfHitsInResultSet = transfFiles.List().size();
                if (intNoOfHitsInResultSet > 0) {
                    for (SOSFileListEntry objListItem : transfFiles.List()) {
                        filePaths += objListItem.getTargetFilename() + ";";
                        fileNames += objListItem.getTargetFilename() + ";";
                    }
                    // remove last ";"
                    filePaths = filePaths.substring(0, filePaths.length() - 1);
                    fileNames = fileNames.substring(0, fileNames.length() - 1);
                }

                setOrderParameter(conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_FILE_COUNT, String.valueOf(intNoOfHitsInResultSet));
                Variable_set objP = null;
                if (isNotNull(spooler_task.order())) {
                    objP = spooler_task.order().params();
                }
                if (isNotNull(objP)) {
                    String strResultList2File = objR.getOptions().result_list_file.Value();
                    if (isNotEmpty(strResultList2File) && isNotEmpty(fileNames)) {
                        JSTextFile objResultListFile = new JSTextFile(strResultList2File);
                        try {
                            if (objResultListFile.canWrite()) {
                                objResultListFile.Write(fileNames);
                                objResultListFile.close();
                            } else {
                                JSJ_F_0090.toLog(objR.getOptions().result_list_file.getShortKey(), strResultList2File);
                            }
                        } catch (Exception e) {
                            String strM = JSJ_F_0080.get(strResultList2File, objR.getOptions().result_list_file.getShortKey());
                            logger.fatal(strM);
                            throw new JobSchedulerException(strM, e);
                        }
                    }

                    setOrderParameter(conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET, fileNames);
                    setOrderParameter(conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET_SIZE, String.valueOf(intNoOfHitsInResultSet));
                }

                // Wo ist das dokumentiert, dass diese Order-/Job-Parameter
                // versorgt werden? Im XML des Jobs unter jobdoc
                objParams.set_var(conVarname_ftp_result_files, Integer.toString((int) intNoOfHitsInResultSet));
                objParams.set_var(conVarname_ftp_result_zero_byte_files, Integer.toString(transfFiles.getZeroByteCount()));
                objParams.set_var(conVarname_ftp_result_filenames, fileNames);
                objParams.set_var(conVarname_ftp_result_filepaths, filePaths);
            }
        } catch (Exception e) {
            throw new JobSchedulerException("error occurred creating order Parameter: ", e);
        }
    }
}
