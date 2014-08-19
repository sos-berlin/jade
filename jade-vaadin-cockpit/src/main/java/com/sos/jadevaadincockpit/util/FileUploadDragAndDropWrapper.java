package com.sos.jadevaadincockpit.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.sos.jadevaadincockpit.globals.JadeSettingsFile;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.StreamVariable;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Notification;

/**
 * 
 * @author JS
 *
 */
public class FileUploadDragAndDropWrapper extends DragAndDropWrapper implements DropHandler {
	private static final long serialVersionUID = -5931046373658578928L;

	public FileUploadDragAndDropWrapper(Component root) {
		super(root);
		setDropHandler(this);
	}

	@Override
	public void drop(DragAndDropEvent event) {
		
		WrapperTransferable tr = (WrapperTransferable) event
                .getTransferable();
        Html5File[] files = tr.getFiles();
        if (files != null) {
        	
//    		if (JadevaadincockpitUI.getCurrent().getJadeMainUi().getProfileTree().getItemIds().size() > 0) { // another file is currently opened
//    			// TODO save and close opened file before loading a new one
//    			Notification.show("Here needs to appear a notification to let the user choose whether to save or discard all changes.", Notification.Type.ERROR_MESSAGE);
//    			ProfileContainer container = JadevaadincockpitUI.getCurrent().getJadeMainUi().getProfileTree().getProfileContainer();
//    			Globals.getJadeDataProvider().saveData(container, false);
//    			Globals.getJadeDataProvider().closeSettingsFile();
//    		}
    		
            for (final Html5File html5File : files) {
            	
            	html5File.setStreamVariable(new StreamVariable() {
					private static final long serialVersionUID = -6631774835122327318L;

					@Override
					public void streamingStarted(StreamingStartEvent event) {
						// TODO Auto-generated method stub
					}
					
					@Override
					public void streamingFinished(StreamingEndEvent event) {
						JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().loadSettingsFile(ApplicationAttributes.getUploadPath() + html5File.getFileName());
					}
					
					@Override
					public void streamingFailed(StreamingErrorEvent event) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onProgress(StreamingProgressEvent event) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public boolean listenProgress() {
						// TODO Auto-generated method stub
						return false;
					}
					
					@Override
					public boolean isInterrupted() {
						// TODO Auto-generated method stub
						return false;
					}
					
					@Override
					public OutputStream getOutputStream() {
						
						// Create upload stream
				        FileOutputStream fos = null;
				        File outputFile = null;
				        
				        try {
				        	// check if uploads-directory exists
				        	File uploadsDir = new File(ApplicationAttributes.getUploadPath());
				        	if (!uploadsDir.exists()) {
				        		uploadsDir.mkdir();
				        	}
				            // Open the file for writing.
				        	outputFile = new File(uploadsDir, html5File.getFileName());
				            fos = new FileOutputStream(outputFile);
				        } catch (FileNotFoundException e) {
				            Notification.show("Could not open file<br/>", e.getMessage(), Notification.Type.ERROR_MESSAGE);
				            return null;
				        }
				        return fos; // Return the output stream to write to
					}
				});
            }
        }
    }

	@Override
	public AcceptCriterion getAcceptCriterion() {
		return AcceptAll.get();
	}

}
