

package com.sos.jade.job;

import java.io.File;

import org.apache.log4j.Logger;

import sos.scheduler.job.JobSchedulerJobAdapter;
import sos.spooler.Spooler;

import com.sos.JSHelper.Basics.IJSCommands;
// Super-Class for JobScheduler Java-API-Jobs
/**
 * \class 		SOSJadeHistoryJSAdapterClass - JobScheduler Adapter for "Import from order or file to JadeHistoryTable"
 *
 * \brief AdapterClass of SOSJadeHistory for the SOSJobScheduler
 *
 * This Class SOSJadeHistoryJSAdapterClass works as an adapter-class between the SOS
 * JobScheduler and the worker-class SOSJadeHistory.
 *

 *
 * see \see C:\Dokumente und Einstellungen\Uwe Risse\Lokale Einstellungen\Temp\scheduler_editor-200438664770970222.html for more details.
 *
 * \verbatim ;
 * mechanicaly created by C:\Dokumente und Einstellungen\Uwe Risse\Eigene Dateien\sos-berlin.com\jobscheduler.1.3.9\scheduler_139\config\JOETemplates\java\xsl\JSJobDoc2JSAdapterClass.xsl from http://www.sos-berlin.com at 20120117120500
 * \endverbatim
 */
public class SOSJadeHistoryJSAdapterClass extends JobSchedulerJobAdapter implements IJSCommands {
	private final String					conClassName						= "SOSJadeHistoryJSAdapterClass";  //$NON-NLS-1$
	@SuppressWarnings("hiding")
	private static Logger		logger			= Logger.getLogger(SOSJadeHistoryJSAdapterClass.class);

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
			return false;
		}
		finally {
		} // finally
		// return value for classic and order driven processing
		// TODO create method in base-class for this functionality
		return spooler_task.job().order_queue() != null;

	} // spooler_process

	@Override
	public void spooler_exit() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::spooler_exit"; //$NON-NLS-1$
		super.spooler_exit();
	}

	private void doProcessing() throws Exception {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::doProcessing"; //$NON-NLS-1$
		IJSCommands 	objJSCommands		= this;

		Object objSp = objJSCommands.getSpoolerObject();
		Spooler objSpooler = (Spooler) objSp;
		SOSJadeHistory objR = new SOSJadeHistory();
		SOSJadeHistoryOptions objO = objR.Options();
		objO.CurrentNodeName(getCurrentNodeName());

		objO.setAllOptions(getSchedulerParameterAsProperties(getParameters()));

		String configuration_file = "";
 		if (objO.getItem("configuration_file") != null) {
			logger.debug("configuration_file from param");
			configuration_file = objO.configuration_file.Value();
		}else {
			logger.debug("configuration_file from scheduler");
			File f = new File(new File(objSpooler.configuration_directory()).getParent(),"hibernate.cfg.xml");
			configuration_file = f.getAbsolutePath();
 		}

		objO.configuration_file.Value(configuration_file);

		objO.CheckMandatory();
        objR.setJSJobUtilites(this);
		objR.Execute();
	} // doProcessing

}

