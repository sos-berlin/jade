package sos.scheduler.jade;

import com.sos.DataExchange.Jade4DMZ;
import com.sos.JSHelper.Basics.VersionInfo;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.io.Files.JSTextFile;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.VirtualFileSystem.Options.SOSFTPOptions;
import com.sos.i18n.annotation.I18NResourceBundle;
import sos.scheduler.job.SOSDExJSAdapterClass;
import sos.spooler.Variable_set;

import java.util.HashMap;

import static com.sos.scheduler.messages.JSMessages.*;

/**
 * \file SOSJade4DMZJSAdapter.java
 * \class 		SOSJade4DMZJSAdapter
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
public class SOSJade4DMZJSAdapter extends SOSDExJSAdapterClass {
	SOSJade4DMZJSAdapter() {
		super();
		// TODO Auto-generated constructor stub
	}


	private final String	conClassName							= "SOSJade4DMZJSAdapter";
	private final String	conSVNVersion							= "$Id: SOSJade4DMZJSAdapter.java 20142 2013-05-15 16:46:28Z kb $";

	private final String	conVarname_ftp_result_files				= "ftp_result_files";
	private final String	conVarname_ftp_result_zero_byte_files	= "ftp_result_zero_byte_files";
	private final String	conVarname_ftp_result_filenames			= "ftp_result_filenames";
	private final String	conVarname_ftp_result_filepaths			= "ftp_result_filepaths";
	private final String	conVarname_ftp_result_error_message		= "ftp_result_error_message";

	private SOSFileList		transfFiles								= null;

	  
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
		}
		finally {
		} // finally
		return signalSuccess();

	} // spooler_process

	  
	private SOSFTPOptions	objO	= null;

	private void doProcessing() throws Exception {
		final String conMethodName = conClassName + "::doProcessing"; //$NON-NLS-1$

		logger.debug(VersionInfo.VERSION_STRING);
		logger.debug(conSVNVersion);
		Jade4DMZ objR = new Jade4DMZ();
		objO = objR.Options();
 		
		objO.CurrentNodeName(getCurrentNodeName());
		HashMap<String, String> hsmParameters = getSchedulerParameterAsProperties(getJobOrOrderParameters());
		objO.setAllOptions(objO.DeletePrefix(hsmParameters, "ftp_"));
		if (!objO.scheduler_host.isDirty()){
		   objO.scheduler_host.Value("");
		}
		objO.CheckMandatory();

		logger.info(String.format("%1$s with operation %2$s started.", conMethodName, objO.operation.Value()));
		objR.setJSJobUtilites(this);

		objR.Execute();
		
		transfFiles = objR.getFileList();

		 
		int intNoOfHitsInResultSet = 0;
		if (isNotNull(transfFiles)) { //https://change.sos-berlin.com/browse/SOSFTP-218
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
				JSJ_I_0090.toLog( strOnEmptyResultSet);
				spooler_task.order().set_state(strOnEmptyResultSet);
			}
		}

		String strRaiseErrorIfResultSetIs = objO.raise_error_if_result_set_is.Value();
		if (isNotEmpty(strRaiseErrorIfResultSetIs)) {
			boolean flgR = objO.expected_size_of_result_set.compare(strRaiseErrorIfResultSetIs, intNoOfHitsInResultSet);
			if (flgR == true) {
				String strM =JSJ_E_0040.get( intNoOfHitsInResultSet, strRaiseErrorIfResultSetIs, objO.expected_size_of_result_set.value());
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
			}
			else {
				createOrder(transfFiles.List().get(0), strOrderJobChainName);
			}
		}

		logger.info(String.format("%1$s with operation %2$s ended.", conMethodName, objO.operation.Value()));
	} // doProcessing

	  

	 
	private void createOrderParameter(final Jade4DMZ objR) throws Exception {
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
}
