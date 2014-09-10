package com.sos.jadevaadincockpit.view;

import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.data.JadeSettingsFile;
import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.sos.jadevaadincockpit.i18n.JadeCockpitMsg;
import com.sos.jadevaadincockpit.util.FileReceiver;
import com.sos.jadevaadincockpit.util.FileUploadDragAndDropWrapper;
import com.sos.jadevaadincockpit.view.components.DropArea;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * @author JS
 *
 */
public class FileUploadDialog extends Window {
	private static final long serialVersionUID = -9193032791774563212L;
	
	private Window window = this;
	
	public FileUploadDialog(String caption) {
		
		super(caption);
		
		FormLayout layout = new FormLayout();
//		layout.setMargin(new MarginInfo(true, false, true, true));
		layout.setMargin(true);
		
		Component uploadComponent = getUploadComponent();
		DropArea dropArea = new DropArea(new JadeCockpitMsg("jade_l_FileUploadDialogDescription").label());
		dropArea.setHeight("100px");
		dropArea.setWidth("100%");
		
//		dropPanel.setSizeFull();
		
		layout.addComponents(uploadComponent, dropArea);
		
		layout.setSizeFull();
		
		setContent(layout);
		
		setHeight("300px");
		setWidth("450px");
		
		center();
		setModal(true);
		setResizable(false);
		setDraggable(false);
		setCloseShortcut(KeyCode.ESCAPE, null);
		setClosable(true);
	}
	
	private Upload getUploadComponent() {
		
		final FileReceiver receiver = new FileReceiver();
		
		Upload upload = new Upload("", receiver);
		
		upload.addSucceededListener(new SucceededListener() {
			private static final long serialVersionUID = -5304477520751860335L;
			
			@Override
			public void uploadSucceeded(SucceededEvent event) {
				String filePath = receiver.getOutputFilePath();
				window.close();
				JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().loadSettingsFile(filePath);
				Notification.show(new JadeCockpitMsg("jade_msg_I_0002").label(), Notification.Type.TRAY_NOTIFICATION); // Upload Successful
			}
		});
		upload.addStartedListener(new StartedListener() {
			private static final long serialVersionUID = -6854458906469443588L;

			@Override
			public void uploadStarted(StartedEvent event) {
//				window.setVisible(false);
				// TODO
			}
		});
		
		upload.addFailedListener(new FailedListener() {
			private static final long serialVersionUID = -5935514262474381301L;

			@Override
			public void uploadFailed(FailedEvent event) {
				Notification.show(new JadeCockpitMsg("jade_msg_I_0003").label(), Notification.Type.ERROR_MESSAGE); // Upload failed!
			}
		});
		
		return upload;
	}
}
