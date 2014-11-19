package com.sos.jadevaadincockpit.view.forms;

import sos.net.mail.options.SOSSmtpMailOptions;
import sos.net.mail.options.SOSSmtpMailOptions.enuMailClasses;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractComponent;

public class NotificationForm extends BaseForm {
	private static final long serialVersionUID = 1L;
	
	SOSSmtpMailOptions mailOnErrorOptions;
	
	public NotificationForm(String caption, Item profile) {
		super(profile);
		setCaption(caption);
		mailOnErrorOptions = jadeOptions.getMailOptions().getOptions(enuMailClasses.MailOnError);
		
		createForm();
	}

	private void createForm() {
		
		AbstractComponent mailOnErrorComponent = componentCreator.getComponentWithCaption(jadeOptions.mail_on_error);
		layout.addComponent(mailOnErrorComponent);
		
		AbstractComponent mailOnSuccessComponent = componentCreator.getComponentWithCaption(jadeOptions.mail_on_success);
		layout.addComponent(mailOnSuccessComponent);
		
		AbstractComponent mailOnEmptyFilesComponent = componentCreator.getComponentWithCaption(jadeOptions.mail_on_empty_files);
		layout.addComponent(mailOnEmptyFilesComponent);
		
		AbstractComponent fromComponent = componentCreator.getComponentWithCaption(mailOnErrorOptions.from);
		layout.addComponent(fromComponent);
		
		AbstractComponent fromNameComponent = componentCreator.getComponentWithCaption(mailOnErrorOptions.from_name);
		layout.addComponent(fromNameComponent);
		
		AbstractComponent toComponent = componentCreator.getComponentWithCaption(mailOnErrorOptions.to);
		layout.addComponent(toComponent);
		
		AbstractComponent replyToComponent = componentCreator.getComponentWithCaption(mailOnErrorOptions.reply_to);
		layout.addComponent(replyToComponent);
		
		AbstractComponent ccComponent = componentCreator.getComponentWithCaption(mailOnErrorOptions.cc);
		layout.addComponent(ccComponent);
		
		AbstractComponent bccComponent = componentCreator.getComponentWithCaption(mailOnErrorOptions.bcc);
		layout.addComponent(bccComponent);
		
		AbstractComponent subjectComponent = componentCreator.getComponentWithCaption(mailOnErrorOptions.subject);
		layout.addComponent(subjectComponent);
		
		AbstractComponent bodyComponent = componentCreator.getComponentWithCaption(mailOnErrorOptions.body);
		layout.addComponent(bodyComponent);
		
		AbstractComponent attachmentComponent = componentCreator.getComponentWithCaption(mailOnErrorOptions.attachment);
		layout.addComponent(attachmentComponent);
		
		AbstractComponent attachmentCharsetComponent = componentCreator.getComponentWithCaption(mailOnErrorOptions.attachment_charset);
		layout.addComponent(attachmentCharsetComponent);
		
		AbstractComponent attachmentContentTypeComponent = componentCreator.getComponentWithCaption(mailOnErrorOptions.attachment_content_type);
		layout.addComponent(attachmentContentTypeComponent);
		
		AbstractComponent attachmentEncodingComponent = componentCreator.getComponentWithCaption(mailOnErrorOptions.attachment_encoding);
		layout.addComponent(attachmentEncodingComponent);
		
		AbstractComponent hostComponent = componentCreator.getComponentWithCaption(mailOnErrorOptions.host);
		layout.addComponent(hostComponent);
		
		AbstractComponent portComponent = componentCreator.getComponentWithCaption(mailOnErrorOptions.port);
		layout.addComponent(portComponent);
	}
}
