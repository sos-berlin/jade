package sos.scheduler.jade;

import org.apache.log4j.Logger;

import com.sos.JSHelper.Basics.JSVersionInfo;
import com.sos.JSHelper.Options.SOSOptionJadeOperation;
import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.i18n.annotation.I18NResourceBundle;

/** 
 * \file SFTPReceiveJob.java
 * \class 		SFTPReceiveJob
 *
 * \brief 
 * AdapterClass of SOSDEx for the SOSJobScheduler
 *
 * This Class SFTPReceiveJob works as an adapter-class between the SOS
 * JobScheduler and the worker-class SOSDataExchangeEngine.
 *
 * see \see J:\E\java\development\com.sos.scheduler\src\sos\scheduler\jobdoc\SOSDEx.xml for more details.
 *
 */
@I18NResourceBundle(baseName = "com.sos.scheduler.messages", defaultLocale = "en")
public class SFTPReceiveJob extends Jade4JessyBaseClass {
	@SuppressWarnings("unused") private final String conClassName = this.getClass().getSimpleName();
	private final Logger logger = Logger.getLogger(this.getClass());
	

																		// Logger.getLogger(SFTPReceiveJob.class);
	private final String		conSVNVersion				= "$Id: SFTPReceiveJob.java 22360 2014-02-05 09:19:04Z oh $";

	@Override
	protected void setSpecialOptions () {
		objO.operation.Value(SOSOptionJadeOperation.enuJadeOperations.receive);	

		objO.protocol.Value(enuTransferTypes.sftp);
		objO.port.value(SOSOptionPortNumber.getStandardSFTPPort());
//		objO.ssh_auth_method.Value(enuAuthenticationMethods.publicKey);
	}
	
	@Override
	protected void showVersionInfo () {
		logger.debug(JSVersionInfo.getVersionString());
		logger.debug(conSVNVersion);
	}
}
