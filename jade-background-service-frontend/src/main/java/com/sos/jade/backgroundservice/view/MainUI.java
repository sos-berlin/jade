package com.sos.jade.backgroundservice.view;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import sos.ftphistory.db.JadeFilesDBLayer;
import sos.ftphistory.db.JadeFilesHistoryDBItem;
import sos.ftphistory.job.SOSFTPHistory;

import com.sos.jade.backgroundservice.enums.ColumnNames;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

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
        vLayout.addComponent(hlTableLayout);
        Table tblHistory = new Table("FTP History");
//        tblHistory.setColumnHeaders(getColumnHeadersFromEnum());
        hlTableLayout.addComponent(tblHistory);
        jadeFilesDBLayer.initSession();
        try {
			historyItems = jadeFilesDBLayer.getFilesHistoryFromTo(new Date(1, 1, 2013), new Date());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //TODO
        tblHistory.setContainerDataSource(null);
        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = -6834643991222900396L;

			public void buttonClick(ClickEvent event) {
                vLayout.addComponent(new Label("Thank you for clicking"));
            }
        });
        vLayout.addComponent(button);
	}
	
	private String[] getColumnHeadersFromEnum(){
        List<String> headers = new ArrayList<String>();
        for (ColumnNames name : ColumnNames.values()){
        	headers.add(name.getValue());
        }
        String[] a = new String[ColumnNames.getSize()];
		return headers.toArray(a);
	}
}
