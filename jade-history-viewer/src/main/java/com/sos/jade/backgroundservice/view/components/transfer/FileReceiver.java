package com.sos.jade.backgroundservice.view.components.transfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;

public class FileReceiver implements Receiver {

    private static final long serialVersionUID = 1L;
    public File outputFile;
    public String outputFilePath = null;

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        FileOutputStream fileOutputStream = null;
        try {
            File filePath = new File(getBasePath() + "/filter-uploads/");
            if (!filePath.exists()) {
                filePath.mkdir();
            }
            outputFile = new File(filePath, filename);
            outputFilePath = outputFile.getAbsolutePath();
            fileOutputStream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            Notification.show("Could not open file<br/>", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            return null;
        }
        return fileOutputStream;
    }

    private String getBasePath() {
        return VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
    }

}
