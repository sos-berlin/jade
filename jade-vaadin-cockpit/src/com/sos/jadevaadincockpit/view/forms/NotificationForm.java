package com.sos.jadevaadincockpit.view.forms;

import sos.net.mail.options.SOSSmtpMailOptions;
import sos.net.mail.options.SOSSmtpMailOptions.enuMailClasses;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractComponent;

public class NotificationForm extends BaseForm {
	private static final long serialVersionUID = 1L;
	
	SOSSmtpMailOptions mailOptions;
	
	public NotificationForm(String caption, Item profile) {
		super(profile);
		setCaption(caption);
		mailOptions = jadeOptions.getMailOptions().getOptions(enuMailClasses.MailOnError);
		
		createForm();
	}

	private void createForm() {
		
		AbstractComponent mailOnErrorComponent = componentCreator.getComponentWithCaption(jadeOptions.mail_on_error);
		layout.addComponent(mailOnErrorComponent);
		
		AbstractComponent mailOnSuccessComponent = componentCreator.getComponentWithCaption(jadeOptions.mail_on_success);
		layout.addComponent(mailOnSuccessComponent);
		
		AbstractComponent mailOnEmptyFilesComponent = componentCreator.getComponentWithCaption(jadeOptions.mail_on_empty_files);
		layout.addComponent(mailOnEmptyFilesComponent);
		
		AbstractComponent fromComponent = componentCreator.getComponentWithCaption(mailOptions.from);
		layout.addComponent(fromComponent);
		
		AbstractComponent fromNameComponent = componentCreator.getComponentWithCaption(mailOptions.from_name);
		layout.addComponent(fromNameComponent);
		
		AbstractComponent toComponent = componentCreator.getComponentWithCaption(mailOptions.to);
		layout.addComponent(toComponent);
		
		AbstractComponent replyToComponent = componentCreator.getComponentWithCaption(mailOptions.reply_to);
		layout.addComponent(replyToComponent);
		
		AbstractComponent ccComponent = componentCreator.getComponentWithCaption(mailOptions.cc);
		layout.addComponent(ccComponent);
		
		AbstractComponent bccComponent = componentCreator.getComponentWithCaption(mailOptions.bcc);
		layout.addComponent(bccComponent);
		
		AbstractComponent subjectComponent = componentCreator.getComponentWithCaption(mailOptions.subject);
		layout.addComponent(subjectComponent);
		
		AbstractComponent bodyComponent = componentCreator.getComponentWithCaption(mailOptions.body);
		layout.addComponent(bodyComponent);
		
		AbstractComponent attachmentComponent = componentCreator.getComponentWithCaption(mailOptions.attachment);
		layout.addComponent(attachmentComponent);
		
		AbstractComponent attachmentCharsetComponent = componentCreator.getComponentWithCaption(mailOptions.attachment_charset);
		layout.addComponent(attachmentCharsetComponent);
		
		AbstractComponent attachmentContentTypeComponent = componentCreator.getComponentWithCaption(mailOptions.attachment_content_type);
		layout.addComponent(attachmentContentTypeComponent);
		
		AbstractComponent attachmentEncodingComponent = componentCreator.getComponentWithCaption(mailOptions.attachment_encoding);
		layout.addComponent(attachmentEncodingComponent);
		
		AbstractComponent hostComponent = componentCreator.getComponentWithCaption(mailOptions.host);
		layout.addComponent(hostComponent);
		
		AbstractComponent portComponent = componentCreator.getComponentWithCaption(mailOptions.port);
		layout.addComponent(portComponent);
	}
}
