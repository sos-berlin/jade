package com.sos.jade.backgroundservice.view.components.transfer;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window;

public class FileChooserWindow extends Window {

    private static final long serialVersionUID = -9193032791774563212L;

    private Upload upload;
    private FileReceiver receiver;

    public FileChooserWindow(String caption) {
        setCaption(caption);
        receiver = new FileReceiver();
        upload = new Upload("", receiver);
        upload.setButtonCaption("Upload");
        setContent(upload);
        center();
        setModal(true);
        setClosable(true);
    }

    public void addSucceededListener(SucceededListener listener) {
        upload.addSucceededListener(listener);
    }

    public String getOutputFilePath() {
        return receiver.outputFilePath;
    }
}
