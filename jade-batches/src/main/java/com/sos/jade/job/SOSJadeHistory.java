package com.sos.jade.job;

import java.io.File;

import org.apache.log4j.Logger;

import com.sos.JSHelper.Basics.JSJobUtilitiesClass;
import com.sos.jade.TransferHistoryImport.SOSJadeDetailImportData;
import com.sos.jade.TransferHistoryImport.SOSJadeImport;
import com.sos.jade.TransferHistoryImport.SOSJadeImportData;

/**
 * \class 		SOSJadeHistory - Workerclass for "Import from order or file to JadeHistoryTable"
 *
 * \brief AdapterClass of SOSJadeHistory for the SOSJobScheduler
 *
 * This Class SOSJadeHistory is the worker-class.
 *

 *
 * see \see C:\Dokumente und Einstellungen\Uwe Risse\Lokale Einstellungen\Temp\scheduler_editor-200438664770970222.html for (more) details.
 *
 * \verbatim ;
 * mechanicaly created by C:\Dokumente und Einstellungen\Uwe Risse\Eigene Dateien\sos-berlin.com\jobscheduler.1.3.9\scheduler_139\config\JOETemplates\java\xsl\JSJobDoc2JSWorkerClass.xsl from http://www.sos-berlin.com at 20120117120500
 * \endverbatim
 */
public class SOSJadeHistory extends JSJobUtilitiesClass<SOSJadeHistoryOptions> {
	private final String	conClassName	= "SOSJadeHistory";						//$NON-NLS-1$
	private static Logger	logger			= Logger.getLogger(SOSJadeHistory.class);

	/**
	 *
	 * \brief SOSJadeHistory
	 *
	 * \details
	 *
	 */
	public SOSJadeHistory() {
		super(new SOSJadeHistoryOptions());
	}

	/**
	 *
	 * \brief Options - returns the SOSJadeHistoryOptionClass
	 *
	 * \details
	 * The SOSJadeHistoryOptionClass is used as a Container for all Options (Settings) which are
	 * needed.
	 *
	 * \return SOSJadeHistoryOptions
	 *
	 */
	@Override
	public SOSJadeHistoryOptions Options() {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::Options"; //$NON-NLS-1$

		if (objOptions == null) {
			objOptions = new SOSJadeHistoryOptions();
		}
		return objOptions;
	}

	/**
	 *
	 * \brief Execute - Start the Execution of SOSJadeHistory
	 *
	 * \details
	 *
	 * For more details see
	 *
	 * \see JobSchedulerAdapterClass
	 * \see SOSJadeHistoryMain
	 *
	 * \return SOSJadeHistory
	 *
	 * @return
	 */
	public SOSJadeHistory Execute() throws Exception {
		final String conMethodName = conClassName + "::Execute";
		SOSJadeImport sosJadeImport = null;
		logger.debug(String.format(Messages.getMsg("JSJ-I-110"), conMethodName));

		try {
			Options().CheckMandatory();
			logger.debug(Options().toString());
			sosJadeImport = new SOSJadeImport(new File(objOptions.getconfiguration_file().Value()));

			SOSJadeImportData sosJadeImportData = new SOSJadeImportData();
			sosJadeImportData.setData(Options().Settings());

			SOSJadeDetailImportData sosJadeDetailImportData = new SOSJadeDetailImportData();
			sosJadeDetailImportData.setData(Options().Settings());

			sosJadeImport.setJadeTransferData(sosJadeImportData);
			sosJadeImport.setJadeTransferDetailData(sosJadeDetailImportData);
			sosJadeImport.doTransferSummary();
			sosJadeImport.doTransferDetail();
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			logger.error(String.format(Messages.getMsg("JSJ-I-107"), conMethodName), e);
		}
		finally {
			logger.debug(String.format(Messages.getMsg("JSJ-I-111"), conMethodName));
		}

		return this;
	}

	public void init() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::init"; //$NON-NLS-1$
		doInitialize();
	}

	private void doInitialize() {
	} // doInitialize

} // class SOSJadeHistory