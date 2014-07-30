package com.sos.jadevaadincockpit.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.sos.jadevaadincockpit.globals.Globals;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;

/**
 * 
 * @author JS
 *
 */
@SuppressWarnings("serial")
public class FileReceiver implements Receiver {
    private File outputFile;
    
    public OutputStream receiveUpload(String filename, String mimeType) {
        // Create upload stream
        FileOutputStream fos = null;
        try {
        	// check if uploads-directory exists
        	File uploadsDir = new File(Globals.getUploadPath());
        	if (!uploadsDir.exists()) {
        		uploadsDir.mkdir();
        	}
            // Open the file for writing.
        	outputFile = new File(uploadsDir, filename);
            fos = new FileOutputStream(outputFile);
//            Globals.lastUploadedFile = outputFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            Notification.show("Could not open file<br/>", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            return null;
        }
        return fos; // Return the output stream to write to
    }
    
    public String getOutputFilePath() {
    	return outputFile.getAbsolutePath();
    }
}
