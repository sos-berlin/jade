package sos.scheduler.jade;

import org.apache.log4j.Logger;

import com.sos.JSHelper.Basics.JSVersionInfo;
import com.sos.JSHelper.Options.SOSOptionAuthenticationMethod.enuAuthenticationMethods;
import com.sos.JSHelper.Options.SOSOptionJadeOperation;
import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.i18n.annotation.I18NResourceBundle;

/** 
 * \file FTPSendJob.java
 * \class 		FTPSendJob
 *
 * \brief 
 * AdapterClass of SOSDEx for the SOSJobScheduler
 *
 * This Class FTPSendJob works as an adapter-class between the SOS
 * JobScheduler and the worker-class SOSDEx.
 *

 *
 * see \see J:\E\java\development\com.sos.scheduler\src\sos\scheduler\jobdoc\SOSDEx.xml for more details.
 *
 */
@I18NResourceBundle(baseName = "com.sos.scheduler.messages", defaultLocale = "en")
public class FTPSendJob extends Jade4JessyBaseClass {
	private final String		conSVNVersion				= "$Id: FTPSendJob.java 22360 2014-02-05 09:19:04Z oh $";
	@SuppressWarnings("unused") private final String conClassName = this.getClass().getSimpleName();
	private final Logger logger = Logger.getLogger(this.getClass());
	

	@Override
	protected void setSpecialOptions () {
		objO.protocol.Value(enuTransferTypes.ftp);
		objO.port.value(SOSOptionPortNumber.getStandardFTPPort());
		objO.operation.Value(SOSOptionJadeOperation.enuJadeOperations.send);		
		objO.ssh_auth_method.Value(enuAuthenticationMethods.password);		
	}
	
	@Override
	protected void showVersionInfo () {
		logger.debug(JSVersionInfo.getVersionString());
		logger.debug(conSVNVersion);
	}
}
