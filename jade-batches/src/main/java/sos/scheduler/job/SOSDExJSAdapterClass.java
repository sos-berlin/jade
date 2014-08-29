package sos.scheduler.job;
import static com.sos.scheduler.messages.JSMessages.JSJ_E_0040;
import static com.sos.scheduler.messages.JSMessages.JSJ_F_0080;
import static com.sos.scheduler.messages.JSMessages.JSJ_F_0090;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0017;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0018;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0019;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0090;

import java.io.File;

import sos.spooler.Job_chain;
import sos.spooler.Order;
import sos.spooler.Variable_set;

import com.sos.DataExchange.JadeEngine;
import com.sos.JSHelper.Basics.VersionInfo;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.io.Files.JSTextFile;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.VirtualFileSystem.Options.SOSFTPOptions;
import com.sos.i18n.annotation.I18NResourceBundle;
import com.sos.scheduler.model.SchedulerObjectFactory;
import com.sos.scheduler.model.commands.JSCmdAddOrder;
import com.sos.scheduler.model.objects.Params;
import com.sos.scheduler.model.objects.Spooler;

/**
 * \file SOSDExJSAdapterClass.java
 * \class 		SOSDExJSAdapterClass
 *
 * \brief
 * AdapterClass of SOSDEx for the SOSJobScheduler
 *
 * This Class SOSDExJSAdapterClass works as an adapter-class between the SOS
 * JobScheduler and the worker-class SOSDEx.
 *

 *
 * see \see J:\E\java\development\com.sos.scheduler\src\sos\scheduler\jobdoc\SOSDEx.xml for more details.
 *
 * \verbatim ;
 * mechanicaly created by C:\Users\KB\eclipse\sos.scheduler.xsl\JSJobDoc2JSAdapterClass.xsl from http://www.sos-berlin.com at 20100930175652
 * \endverbatim
 */
@I18NResourceBundle(baseName = "com.sos.scheduler.messages", defaultLocale = "en")
public class SOSDExJSAdapterClass extends JobSchedulerJobAdapter {
	private final String	conClassName							= "SOSDExJSAdapterClass";
	private final String	conSVNVersion							= "$Id$";
	private final String	conVarname_ftp_result_files				= "ftp_result_files";
	private final String	conVarname_ftp_result_zero_byte_files	= "ftp_result_zero_byte_files";
	private final String	conVarname_ftp_result_filenames			= "ftp_result_filenames";
	private final String	conVarname_ftp_result_filepaths			= "ftp_result_filepaths";
	private final String	conVarname_ftp_result_error_message		= "ftp_result_error_message";
	private SOSFileList		transfFiles								= null;

	public void init() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::init"; //$NON-NLS-1$
		doInitialize();
	}

	private void doInitialize() {
	} // doInitialize

	@Override
	public boolean spooler_init() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::spooler_init"; //$NON-NLS-1$
		return super.spooler_init();
	}

	@Override
	public boolean spooler_process() throws Exception {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::spooler_process"; //$NON-NLS-1$
		try {
			super.spooler_process();
			doProcessing();
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("%1$s ended abnormal.", conClassName));
			logger.error(StackTrace2String(e));
			throw e;
			//			return signalFailure();
		}
		finally {
		} // finally
		return signalSuccess();
	} // spooler_process

	@Override
	public void spooler_exit() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::spooler_exit"; //$NON-NLS-1$
		super.spooler_exit();
	}
	private SOSFTPOptions	objJadeOptions	= null;
	private JadeEngine		objJadeEngine	= null;

	private void doProcessing() throws Exception {
		final String conMethodName = conClassName + "::doProcessing"; //$NON-NLS-1$
		logger.debug(VersionInfo.VERSION_STRING);
		logger.debug(conSVNVersion);
		if (objJadeEngine == null) {
			objJadeEngine = new JadeEngine();
			objJadeOptions = objJadeEngine.Options();
			if (objJadeOptions.reuseConnection.isFalse()) {
				objJadeEngine = new JadeEngine();
				objJadeOptions = objJadeEngine.Options();
			}
		}
		else {
			objJadeOptions = objJadeEngine.freshInstanceOfOptions();
		}
		objJadeOptions.CurrentNodeName(getCurrentNodeName());
		hsmParameters = getSchedulerParameterAsProperties(getJobOrOrderParameters());
		objJadeOptions.setAllOptions(objJadeOptions.DeletePrefix(hsmParameters, "ftp_"));
		objJadeOptions.CheckMandatory();
		int intLogLevel = spooler_log.level();
		if (intLogLevel < 0) {
			objJadeOptions.verbose.value(-1 * intLogLevel);
		}
		logger.info(String.format("%1$s with operation %2$s started.", conMethodName, objJadeOptions.operation.Value()));
		objJadeEngine.setJSJobUtilites(this);
		objJadeEngine.Execute();
		objJadeEngine.Logout();
		transfFiles = objJadeEngine.getFileList();
		int intNoOfHitsInResultSet = transfFiles.List().size();
		if (intNoOfHitsInResultSet <= 0 && isOrderJob()) {
			String strPollErrorState = objJadeOptions.PollErrorState.Value();
			if (objJadeOptions.PollErrorState.isDirty()) {
				logger.info("set order-state to " + strPollErrorState);
				setNextNodeState(strPollErrorState);
				spooler_task.order().params().set_var(conVarname_ftp_result_error_message, "");
				spooler_task.order().set_state_text("ended with no files found");
			}
		}
		if (isJobchain()) {
			String strOnEmptyResultSet = objJadeOptions.on_empty_result_set.Value();
			if (isNotEmpty(strOnEmptyResultSet) && intNoOfHitsInResultSet <= 0) {
				JSJ_I_0090.toLog(strOnEmptyResultSet);
				spooler_task.order().set_state(strOnEmptyResultSet);
			}
		}
		String strRaiseErrorIfResultSetIs = objJadeOptions.raise_error_if_result_set_is.Value();
		if (isNotEmpty(strRaiseErrorIfResultSetIs)) {
			boolean flgR = objJadeOptions.expected_size_of_result_set.compare(strRaiseErrorIfResultSetIs, intNoOfHitsInResultSet);
			if (flgR == true) {
				String strM = JSJ_E_0040.get(intNoOfHitsInResultSet, strRaiseErrorIfResultSetIs, objJadeOptions.expected_size_of_result_set.value());
				logger.info(strM);
				throw new JobSchedulerException(strM);
			}
		}
		createOrderParameter(objJadeEngine);
		if (intNoOfHitsInResultSet > 0 && objJadeOptions.create_order.isTrue()) {
			String strOrderJobChainName = objJadeOptions.order_jobchain_name.Value();
			if (objJadeOptions.create_orders_for_all_files.isTrue()) {
				for (SOSFileListEntry objListItem : transfFiles.List()) {
					createOrder(objListItem, strOrderJobChainName);
				}
			}
			else {
				createOrder(transfFiles.List().get(0), strOrderJobChainName);
			}
		}
		logger.info(String.format("%1$s with operation %2$s ended.", conMethodName, objJadeOptions.operation.Value()));
	} // doProcessing

	protected void createOrder(final SOSFileListEntry pobjListItem, final String pstrOrderJobChainName) {
		String strT;
		if (objJadeOptions.createOrderOnRemoteJobScheduler.isTrue()) {
			strT = createOrderOnRemoteJobScheduler(pobjListItem, pstrOrderJobChainName);
		}
		else {
			strT = createOrderOnLocalJobScheduler(pobjListItem, pstrOrderJobChainName);
		}
		logger.info(strT);
	}
	private SchedulerObjectFactory	objFactory	= null;

	protected String createOrderOnRemoteJobScheduler(final SOSFileListEntry pobjListItem, final String pstrOrderJobChainName) {
		if (objFactory == null) {
			objFactory = new SchedulerObjectFactory(objJadeOptions.order_jobscheduler_host_name.Value(), objJadeOptions.order_jobscheduler_port_number.value());
			objFactory.initMarshaller(Spooler.class);
			objFactory.Options().TransferMethod.Set(objJadeOptions.BackgroundServiceTransferMethod);
			objFactory.Options().PortNumber.Set(objJadeOptions.order_jobscheduler_port_number);
			objFactory.Options().ServerName.Set(objJadeOptions.order_jobscheduler_host_name);
		}
		JSCmdAddOrder objOrder = objFactory.createAddOrder();
		String strTargetFileName = pobjListItem.TargetFileName();
		objOrder.setId(strTargetFileName);
		objOrder.setJobChain(pstrOrderJobChainName /* "scheduler_sosftp_history" */);
		Params objParams = objFactory.setParams(buildOrderParams(pobjListItem));
		objOrder.setParams(objParams);
		//		logger.debug(objOrder.toXMLString());
		String strT = JSJ_I_0018.get(pobjListItem.SourceFileName(), pstrOrderJobChainName); // "Order '%1$s' created for JobChain '%2$s'."
		if (changeOrderState() == true) {
			String strNextState = objJadeOptions.next_state.Value();
			objOrder.setState(strNextState);
			strT += " " + JSJ_I_0019.get(strNextState); // "Next State is '%1$s'."
		}
		objOrder.run();
		return strT;
	}

	private Variable_set buildOrderParams(final SOSFileListEntry pobjListItem) {
		Variable_set objOrderParams = spooler.create_variable_set();
		// kb: merge actual parameters into created order params (2012-07-25)
		if (objJadeOptions.MergeOrderParameter.isTrue()) {
			objOrderParams.merge(spooler_task.order().params());
		}
		String strTargetFileName = pobjListItem.TargetFileName();
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
		return objOrderParams;
	}

	/**
	 *
	 * @param pstrOrderJobChainName
	 */
	protected String createOrderOnLocalJobScheduler(final SOSFileListEntry pobjListItem, final String pstrOrderJobChainName) {
		final String conMethodName = conClassName + "::createOrder";
		Order objOrder = spooler.create_order();
		Variable_set objOrderParams = spooler.create_variable_set();
		// kb: merge actual parameters into created order params (2012-07-25)
		if (objJadeOptions.MergeOrderParameter.isTrue()) {
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
			String strNextState = objJadeOptions.next_state.Value();
			objOrder.set_state(strNextState);
			strT += " " + JSJ_I_0019.get(strNextState); // "Next State is '%1$s'."
		}
		objOrder.set_params(objOrderParams);
		objOrder.set_title(JSJ_I_0017.get(conMethodName)); // "Order created by %1$s"
		Job_chain objJobchain = spooler.job_chain(pstrOrderJobChainName);
		objJobchain.add_order(objOrder);
		return strT;
	} // private void createOrder

	/**
	 * 
	*
	* \brief changeOrderState
	*
	* \details
	* 
	* \return boolean
	*
	 */
	private boolean changeOrderState() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::changeOrderState";
		boolean flgR = false;
		String strNextState = objJadeOptions.next_state.Value();
		if (isNotEmpty(strNextState)) {
			flgR = true;
		}
		return flgR;
	} // private boolean changeOrderState
	private static final String	conOrderParameterSCHEDULER_FILE_PATH							= "scheduler_file_path";
	private static final String	conOrderParameterSCHEDULER_FILE_PARENT							= "scheduler_file_parent";
	private static final String	conOrderParameterSCHEDULER_FILE_NAME							= "scheduler_file_name";
	private static final String	conOrderParameterSCHEDULER_TARGET_FILE_PARENT					= "scheduler_target_file_parent";
	private static final String	conOrderParameterSCHEDULER_TARGET_FILE_NAME						= "scheduler_target_file_name";
	private static final String	conOrderParameterSCHEDULER_SOURCE_FILE_PARENT					= "scheduler_source_file_parent";
	private static final String	conOrderParameterSCHEDULER_SOURCE_FILE_NAME						= "scheduler_source_file_name";
	public static final String	conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET		= "scheduler_SOSFileOperations_ResultSet";
	public static final String	conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET_SIZE	= "scheduler_SOSFileOperations_ResultSetSize";
	public static final String	conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_FILE_COUNT		= "scheduler_SOSFileOperations_file_count";

	private void createOrderParameter(final JadeEngine objR) throws Exception {
		try {
			String fileNames = "";
			String filePaths = "";
			Variable_set objParams = null;
			if (spooler_job.order_queue() != null) {
				if (spooler_task.order() != null && spooler_task.order().params() != null) {
					objParams = spooler_task.order().params();
				}
			}
			else {
				objParams = spooler_task.params();
			}
			if (objParams != null) {
				// Die Anzahl in intNoOfHitsInResultSet ist redundant. Eigentlich ist transfFiles.size entscheidend
				long intNoOfHitsInResultSet = transfFiles.List().size();
				if (intNoOfHitsInResultSet > 0) {
					for (SOSFileListEntry objListItem : transfFiles.List()) {
						//						filePaths += objListItem.getTargetFilename() + ";";
						//						fileNames += objListItem.getTargetFilename() + ";";
						String strT = objListItem.getFileName4ResultList();
						filePaths += strT + ";";
						fileNames += strT + ";";
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
							}
							else {
								JSJ_F_0090.toLog(objR.Options().result_list_file.getShortKey(), strResultList2File);
							}
						}
						catch (Exception e) {
							e.printStackTrace(System.err);
							String strM = JSJ_F_0080.get(strResultList2File, objR.Options().result_list_file.getShortKey());
							logger.fatal(strM);
							throw new JobSchedulerException(strM, e);
						}
					}
					setOrderParameter(conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET, fileNames);
					setOrderParameter(conOrderParameterSCHEDULER_SOS_FILE_OPERATIONS_RESULT_SET_SIZE, String.valueOf(intNoOfHitsInResultSet));
				}
				// Wo ist das dokumentiert, dass diese Order-/Job-Parameter versorgt werden? Im XML des Jobs unter jobdoc
				objParams.set_var(conVarname_ftp_result_files, Integer.toString((int) intNoOfHitsInResultSet));
				objParams.set_var(conVarname_ftp_result_zero_byte_files, Integer.toString(transfFiles.getZeroByteCount()));
				objParams.set_var(conVarname_ftp_result_filenames, fileNames);
				objParams.set_var(conVarname_ftp_result_filepaths, filePaths);
			}
		}
		catch (Exception e) {
			throw new JobSchedulerException("error occurred creating order Parameter: ", e);
		}
	}

	@Override
	public void spooler_close() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::close";
		if (objJadeEngine != null) {
			objJadeEngine.Logout();
			objJadeEngine = null;
		}
	} // private void close
}
