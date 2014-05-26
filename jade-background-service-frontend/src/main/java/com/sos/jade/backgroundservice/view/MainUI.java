package com.sos.jade.backgroundservice.view;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sos.ftphistory.db.JadeFilesDBLayer;
import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.enums.Columns;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class MainUI extends CustomComponent {
	
	private static final long serialVersionUID = 6368275374953898482L;
	private String absolutePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	private String configurationFilename = "c:/temp/hibernate.cfg.xml";
	private File configFile;
	private JadeFilesDBLayer jadeFilesDBLayer;
	private List<JadeFilesHistoryDBItem> historyItems;

//	private SOSFTPHistory ftpHistory = SOSFTPHistory.getConnection(spooler, conn, parameters, log);

	public MainUI() {
		configFile = new File(configurationFilename);
		jadeFilesDBLayer = new JadeFilesDBLayer(configFile);
        final VerticalLayout vLayout = new VerticalLayout();
        vLayout.setMargin(true);
        setCompositionRoot(vLayout);
        final HorizontalLayout hLayout = new HorizontalLayout();
        vLayout.addComponent(hLayout);
        final Image imgRabbit = new Image();
//		FileResource rabbitResource = new FileResource(new File(absolutePath + "/images/job_scheduler_logo.jpg"));
//      imgRabbit.setSource(rabbitResource);
//      hLayout.addComponent(imgRabbit);
        final Image imgTitle = new Image();
		FileResource titleResource = new FileResource(new File(absolutePath + "/images/job_scheduler_rabbit_circle_60x60.gif"));
		imgTitle.setSource(titleResource);
        hLayout.addComponent(imgTitle);
        Label lblTitle = new Label("JADE background service");
        hLayout.addComponent(lblTitle);

        HorizontalLayout hlTableLayout = new HorizontalLayout();
        hlTableLayout.setWidth("100%");
        hlTableLayout.setHeight("90%");
        vLayout.addComponent(hlTableLayout);
        
        jadeFilesDBLayer.initSession();
        try {
			historyItems = jadeFilesDBLayer.getFilesHistory/*FromTo(new Date(1, 1, 2000), new Date())*/();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Panel pScrollable = new Panel();
        pScrollable.setSizeFull();
        hlTableLayout.addComponent(pScrollable);
        JadeHistoryTable tblHistory = new JadeHistoryTable(historyItems);
        pScrollable.setContent(tblHistory);
//        Button button = new Button("Click Me");
//        button.addClickListener(new Button.ClickListener() {
//			private static final long serialVersionUID = -6834643991222900396L;
//
//			public void buttonClick(ClickEvent event) {
//                vLayout.addComponent(new Label("Thank you for clicking"));
//            }
//        });
//        vLayout.addComponent(button);
	}
	
	private String[] getColumnHeadersFromEnum(){
        List<String> headers = new ArrayList<String>();
        for (Columns name : Columns.values()){
        	headers.add(name.name());
        }
        String[] a = new String[Columns.values().length];
		return headers.toArray(a);
	}
}
