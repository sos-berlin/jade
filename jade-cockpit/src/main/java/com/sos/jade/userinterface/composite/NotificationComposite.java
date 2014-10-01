package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import sos.net.mail.options.SOSSmtpMailOptions;
import sos.net.mail.options.SOSSmtpMailOptions.enuMailClasses;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCTabItem;
import com.sos.dialog.components.CompositeBaseClass;

public class NotificationComposite extends CompositeBaseClass<JADEOptions> { 
	@SuppressWarnings({ "unused", "hiding" }) private final Logger	logger			= Logger.getLogger(NotificationComposite.class);
	public final String									conSVNVersion	= "$Id$";

	//	private JADEOptions								objJadeOptions	= null;
	public NotificationComposite(final SOSCTabItem parent, final JADEOptions objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public NotificationComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}		
	}

	@Override public void createComposite() {
//		Gridlayout.set4ColumnLayout(this);
		SOSSmtpMailOptions objMailO = objJadeOptions.getMailOptions();
		{
			objCC.getControl(objJadeOptions.mail_on_error);
			objCC.getLabel(2);
			SOSSmtpMailOptions objMail = objMailO.getOptions(enuMailClasses.MailOnError);
			objCC.getControl(objMail.from);
			objCC.getControl(objMail.from_name);
			objCC.getControl(objMail.to);
			objCC.getControl(objMail.reply_to);
			objCC.getControl(objMail.cc);
			objCC.getControl(objMail.bcc);
			objCC.getLabel();
			objCC.getSeparator();
			objCC.getControl(objMail.subject, 3);
			objCC.getControl(objMail.body);
			objCC.getSeparator();
			objCC.getControl(objMail.attachment, 3);
			objCC.getControl(objMail.attachment_charset);
			objCC.getControl(objMail.attachment_content_type);
			objCC.getControl(objMail.attachment_encoding);
			objCC.getSeparator();
			objCC.getControl(objMail.host);
			objCC.getControl(objMail.port);
			objCC.getControl(objJadeOptions.mail_on_empty_files);
			objCC.getLabel(2);
			objCC.getControl(objJadeOptions.mail_on_success);
			objCC.getLabel(2);
		}
	}
}
