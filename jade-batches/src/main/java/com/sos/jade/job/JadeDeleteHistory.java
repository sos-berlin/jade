package com.sos.jade.job;

import java.io.File;

import org.apache.log4j.Logger;

import com.sos.JSHelper.Basics.JSJobUtilitiesClass;
import com.sos.jade.db.JadeTransferDBLayer;
import com.sos.jade.db.JadeTransferDetailDBLayer;

/**
 * \class 		JadeDeleteHistory - Workerclass for "Delete entries in Jade history table"
 *
 * \brief AdapterClass of JadeDeleteHistory for the SOSJobScheduler
 *
 * This Class JadeDeleteHistory is the worker-class.
 *

 *
 * see \see C:\Dokumente und Einstellungen\Uwe Risse\Lokale Einstellungen\Temp\scheduler_editor-7740628146985625965.html for (more) details.
 *
 * \verbatim ;
 * mechanicaly created by C:\Dokumente und Einstellungen\Uwe Risse\Eigene Dateien\sos-berlin.com\jobscheduler.1.3.9\scheduler_139\config\JOETemplates\java\xsl\JSJobDoc2JSWorkerClass.xsl from http://www.sos-berlin.com at 20111221162313
 * \endverbatim
 */
public class JadeDeleteHistory extends JSJobUtilitiesClass<JadeDeleteHistoryOptions> {
	private final String	conClassName	= "JadeDeleteHistory";
	private static Logger	logger			= Logger.getLogger(JadeDeleteHistory.class);

	/**
	 *
	 * \brief JadeDeleteHistory
	 *
	 * \details
	 *
	 */
	public JadeDeleteHistory() {
		super(new JadeDeleteHistoryOptions());
	}

	/**
	 *
	 * \brief Options - returns the JadeDeleteHistoryOptionClass
	 *
	 * \details
	 * The JadeDeleteHistoryOptionClass is used as a Container for all Options (Settings) which are
	 * needed.
	 *
	 * \return JadeDeleteHistoryOptions
	 *
	 */
	@Override
	public JadeDeleteHistoryOptions getOptions() {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::Options"; //$NON-NLS-1$

		if (objOptions == null) {
			objOptions = new JadeDeleteHistoryOptions();
		}
		return objOptions;
	}

	/**
	 *
	 * \brief Execute - Start the Execution of JadeDeleteHistory
	 *
	 * \details
	 *
	 * For more details see
	 *
	 * \see JobSchedulerAdapterClass
	 * \see JadeDeleteHistoryMain
	 *
	 * \return JadeDeleteHistory
	 *
	 * @return
	 */
	public JadeDeleteHistory Execute() throws Exception {
		final String conMethodName = conClassName + "::Execute"; //$NON-NLS-1$

		logger.debug(String.format(Messages.getMsg("JSJ-I-110"), conMethodName));

		try {
			getOptions().CheckMandatory();
			logger.debug(getOptions().toString());

			File configurationFile = new File(objOptions.configuration_file.Value());
			JadeTransferDBLayer jadeTransferDBLayer = new JadeTransferDBLayer(configurationFile);
			jadeTransferDBLayer.setAge(objOptions.age_exceeding_days.value());

			jadeTransferDBLayer.beginTransaction();
			jadeTransferDBLayer.deleteFromTo();
			jadeTransferDBLayer.commit();

			JadeTransferDetailDBLayer jadeTransferDetailDBLayer = new JadeTransferDetailDBLayer(configurationFile);
			jadeTransferDetailDBLayer.setAge(objOptions.age_exceeding_days.value());

			jadeTransferDetailDBLayer.beginTransaction();
			jadeTransferDetailDBLayer.deleteFromTo();
			jadeTransferDetailDBLayer.commit();

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

} // class JadeDeleteHistory