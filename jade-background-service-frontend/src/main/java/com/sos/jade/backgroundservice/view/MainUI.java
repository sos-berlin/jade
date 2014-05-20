package com.sos.jade.backgroundservice.view;

import java.io.File;

import sos.ftphistory.job.SOSFTPHistory;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class MainUI extends CustomComponent {
	
	private static final long serialVersionUID = 6368275374953898482L;
	private String absolutePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	private SOSFTPHistory ftpHistory = SOSFTPHistory.getConnection(spooler, conn, parameters, log);
	
	public MainUI() {
        final VerticalLayout vLayout = new VerticalLayout();
        vLayout.setMargin(true);
        setCompositionRoot(vLayout);
        final HorizontalLayout hLayout = new HorizontalLayout();
        vLayout.addComponent(hLayout);
        final Image imgRabbit = new Image();
		FileResource rabbitResource = new FileResource(new File(absolutePath + "/images/job_scheduler_logo.jpg"));
        imgRabbit.setSource(rabbitResource);
        final Image imgTitle = new Image();
		FileResource titleResource = new FileResource(new File(absolutePath + "/images/job_scheduler_rabbit_circle_60x60.gif"));
        imgRabbit.setSource(titleResource);
        hLayout.addComponent(imgRabbit);
        hLayout.addComponent(imgTitle);
        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = -6834643991222900396L;

			public void buttonClick(ClickEvent event) {
                vLayout.addComponent(new Label("Thank you for clicking"));
            }
        });
        vLayout.addComponent(button);
	}
}
