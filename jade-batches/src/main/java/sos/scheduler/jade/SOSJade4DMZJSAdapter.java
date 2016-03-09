package sos.scheduler.jade;

import static com.sos.scheduler.messages.JSMessages.JSJ_E_0040;
import static com.sos.scheduler.messages.JSMessages.JSJ_F_0080;
import static com.sos.scheduler.messages.JSMessages.JSJ_F_0090;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0017;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0018;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0019;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0090;

import java.io.File;
import java.util.HashMap;

import sos.scheduler.job.JobSchedulerJobAdapter;
import sos.spooler.Job_chain;
import sos.spooler.Order;
import sos.spooler.Variable_set;

import com.sos.DataExchange.Jade4DMZ;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.io.Files.JSTextFile;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.VirtualFileSystem.Options.SOSFTPOptions;
import com.sos.i18n.annotation.I18NResourceBundle;

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
    private SOSFTPOptions objO = null;

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
        objO = null;
        Jade4DMZ objR = new Jade4DMZ();
        objO = objR.Options();

        objO.CurrentNodeName(getCurrentNodeName());
        HashMap<String, String> hsmParameters = getSchedulerParameterAsProperties(getJobOrOrderParameters());
        objO.setAllOptions2(objO.DeletePrefix(hsmParameters, "ftp_"));
        int intLogLevel = spooler_log.level();
        if (intLogLevel <= 0) {
            objO.verbose.value(-1 * intLogLevel);
        }
        if (objO.scheduler_host.isNotDirty()) {
            objO.scheduler_host.Value("");
        }
        // objO.CheckMandatory(); //is made in Execute method

        logger.info(String.format("%1$s with operation %2$s started.", "JADE4DMZ", objO.operation.Value()));
        objR.setJSJobUtilites(this);

        objR.Execute();

        transfFiles = objR.getFileList();

        int intNoOfHitsInResultSet = 0;
        if (isNotNull(transfFiles)) { // https://change.sos-berlin.com/browse/SOSFTP-218
            intNoOfHitsInResultSet = transfFiles.List().size();
        }

        if (intNoOfHitsInResultSet <= 0 && isOrderJob()) {
            String strPollErrorState = objO.PollErrorState.Value();
            if (objO.PollErrorState.isDirty()) {
                logger.debug("set order-state to " + strPollErrorState);
                setNextNodeState(strPollErrorState);
                spooler_task.order().params().set_var(conVarname_ftp_result_error_message, "");
                spooler_task.order().set_state_text("ended with no files found");
            }
        }

        if (isJobchain()) {
            String strOnEmptyResultSet = objO.on_empty_result_set.Value();
            if (isNotEmpty(strOnEmptyResultSet) && intNoOfHitsInResultSet <= 0) {
                JSJ_I_0090.toLog(strOnEmptyResultSet);
                spooler_task.order().set_state(strOnEmptyResultSet);
            }
        }

        String strRaiseErrorIfResultSetIs = objO.raise_error_if_result_set_is.Value();
        if (isNotEmpty(strRaiseErrorIfResultSetIs)) {
            boolean flgR = objO.expected_size_of_result_set.compare(strRaiseErrorIfResultSetIs, intNoOfHitsInResultSet);
            if (flgR == true) {
                String strM = JSJ_E_0040.get(intNoOfHitsInResultSet, strRaiseErrorIfResultSetIs, objO.expected_size_of_result_set.value());
                logger.info(strM);
                throw new JobSchedulerException(strM);
            }
        }

        createOrderParameter(objR);

        if (intNoOfHitsInResultSet > 0 && objO.create_order.isTrue()) {
            String strOrderJobChainName = objO.order_jobchain_name.Value();
            if (objO.create_orders_for_all_files.isTrue()) {
                for (SOSFileListEntry objListItem : transfFiles.List()) {
                    createOrder(objListItem, strOrderJobChainName);
                }
            } else {
                createOrder(transfFiles.List().get(0), strOrderJobChainName);
            }
        }

        logger.info(String.format("%1$s with operation %2$s ended.", "JADE4DMZ", objO.operation.Value()));
    } // doProcessing

    /** @param pstrOrderJobChainName */
    protected void createOrder(final SOSFileListEntry pobjListItem, final String pstrOrderJobChainName) {
        final String conMethodName = conClassName + "::createOrder";
        Order objOrder = spooler.create_order();
        Variable_set objOrderParams = spooler.create_variable_set();
        // kb: merge actual parameters into created order params (2012-07-25)
        if (objO.MergeOrderParameter.isTrue()) {
            objOrderParams.merge(spooler_task.order().params());
        }
        String strTargetFileName = pobjListItem.TargetFileName();
        objOrder.set_id(strTargetFileName);
        objOrderParams.set_value(conOrderParameterSCHEDULER_FILE_PATH, strTargetFileName);
        String strT = new File(strTargetFileName).getParent();
        if (strT == null) {
            strT = "";
        }
        objOrderParams.set_value(conOrderParameterSCHEDULER_FILE_PARENT, strT);
        objOrderParams.set_value(conOrderParameterSCHEDULER_FILE_NAME, new File(strTargetFileName).getName());
        objOrderParams.set_value(conOrderParameterSCHEDULER_TARGET_FILE_PARENT, strT);
        objOrderParams.set_value(conOrderParameterSCHEDULER_TARGET_FILE_NAME, new File(strTargetFileName).getName());
        String strSourceFileName = pobjListItem.SourceFileName();
        strT = new File(strSourceFileName).getParent();
        if (strT == null) {
            strT = "";
        }
        objOrderParams.set_value(conOrderParameterSCHEDULER_SOURCE_FILE_PARENT, strT);
        objOrderParams.set_value(conOrderParameterSCHEDULER_SOURCE_FILE_NAME, new File(strSourceFileName).getName());
        strT = JSJ_I_0018.get(strSourceFileName, pstrOrderJobChainName); // "Order '%1$s' created for JobChain '%2$s'."
        if (changeOrderState() == true) {
            String strNextState = objO.next_state.Value();
            objOrder.set_state(strNextState);
            strT += " " + JSJ_I_0019.get(strNextState); // "Next State is '%1$s'."
        }
        objOrder.set_params(objOrderParams);
        objOrder.set_title(JSJ_I_0017.get(conMethodName)); // "Order created by %1$s"
        Job_chain objJobchain = spooler.job_chain(pstrOrderJobChainName);
        objJobchain.add_order(objOrder);
        logger.info(strT);
    } // private void createOrder

    /** \brief changeOrderState
     *
     * \details
     * 
     * \return boolean */
    private boolean changeOrderState() {
        @SuppressWarnings("unused")
        final String conMethodName = conClassName + "::changeOrderState";
        boolean flgR = false;
        String strNextState = objO.next_state.Value();
        if (isNotEmpty(strNextState)) {
            flgR = true;
        }
        return flgR;
    } // private boolean changeOrderState

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
                    String strResultList2File = objR.Options().result_list_file.Value();
                    if (isNotEmpty(strResultList2File) && isNotEmpty(fileNames)) {
                        JSTextFile objResultListFile = new JSTextFile(strResultList2File);
                        try {
                            if (objResultListFile.canWrite()) {
                                objResultListFile.Write(fileNames);
                                objResultListFile.close();
                            } else {
                                JSJ_F_0090.toLog(objR.Options().result_list_file.getShortKey(), strResultList2File);
                            }
                        } catch (Exception e) {
                            String strM = JSJ_F_0080.get(strResultList2File, objR.Options().result_list_file.getShortKey());
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
