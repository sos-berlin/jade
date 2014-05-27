package com.sos.jade.backgroundservice.view;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sos.ftphistory.db.JadeFilesDBItem;
import sos.ftphistory.db.JadeFilesDBLayer;
import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.VerticalLayout;

public class MainUI extends CustomComponent {
	
	private static final long serialVersionUID = 6368275374953898482L;
	private String absolutePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
//	private String configurationFilename = "c:/temp/hibernate.cfg.xml";
	private String configurationFilename = absolutePath + "/WEB-INF/classes/hibernate.cfg.xml";
	private JadeFilesDBLayer jadeFilesDBLayer;
	private List<JadeFilesHistoryDBItem> historyItems;
	private List<JadeFilesDBItem> fileItems;
	private Panel scrollableHistoryPanel;
	private Panel scrollableFilePanel;
	private JadeFilesHistoryTable tblHistory;
	private JadeFilesTable tblFiles;
	private List markedRows;

	public MainUI() {
		File configFile = new File(configurationFilename);
		jadeFilesDBLayer = new JadeFilesDBLayer(configFile);
		initComponents();
		// first query for the jadeFilesItems
        jadeFilesDBLayer.initSession();
        try {
//			fileItems = jadeFilesDBLayer.getFilesFromTo(new Date(1, 1, 2000), new Date());
			fileItems = jadeFilesDBLayer.getFiles();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        tblFiles = new JadeFilesTable(fileItems);
        scrollableFilePanel.setContent(tblFiles);
        scrollableHistoryPanel.setVisible(false);

        tblFiles.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(ItemClickEvent event) {
				// then query all depending jadeHistoryFilesItems
				Collection itemProperties = event.getItem().getItemPropertyIds();
				if(event.getItem().getItemProperty(JadeFileColumns.ID.getName()) != null){
					Long id = (Long)event.getItem().getItemProperty(JadeFileColumns.ID.getName()).getValue();
					//TODO query all historyItems with that id
					try {
						historyItems = jadeFilesDBLayer.getFilesHistoryById(id);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

					}
					tblHistory.populateDatasource(historyItems);
					scrollableHistoryPanel.setVisible(true);
				}
			}
		});
        addActionListener(tblFiles);
        tblHistory = new JadeFilesHistoryTable();
        scrollableHistoryPanel.setContent(tblHistory);
	}
	
	private void initComponents(){
        final VerticalLayout vLayout = new VerticalLayout();
        vLayout.setMargin(true);
        setCompositionRoot(vLayout);
        final HorizontalLayout hLayout = new HorizontalLayout();
        vLayout.addComponent(hLayout);
        final Image imgTitle = new Image();
		FileResource titleResource = new FileResource(new File(absolutePath + "/images/job_scheduler_rabbit_circle_60x60.gif"));
		imgTitle.setSource(titleResource);
        hLayout.addComponent(imgTitle);
        Label lblTitle = new Label("JADE background service");
        hLayout.addComponent(lblTitle);

        HorizontalLayout hlFileTableLayout = initTableLayout(null, null);
        vLayout.addComponent(hlFileTableLayout);

        HorizontalLayout hlHistoryTableLayout = initTableLayout(null, null);
        vLayout.addComponent(hlHistoryTableLayout);
        
        scrollableHistoryPanel = initScrollablePanel(); 
        hlHistoryTableLayout.addComponent(scrollableHistoryPanel);

        scrollableFilePanel = initScrollablePanel();
        hlFileTableLayout.addComponent(scrollableFilePanel);

	}
	
	private HorizontalLayout initTableLayout(int width, int height){
		HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setWidth("100%");
        hLayout.setHeight("40%");
		return hLayout;
	}
	
	private HorizontalLayout initTableLayout(String width, String height){
		HorizontalLayout hLayout = new HorizontalLayout();
		if (width != null && height != null){
	        hLayout.setWidth(width);
	        hLayout.setHeight(height);
		}else{
			hLayout.setSizeFull();
		}
		return hLayout;
	}
	
	private Panel initScrollablePanel(){
        Panel panel = new Panel();
        panel.setSizeFull();
		return panel;
	}
	
	private void addActionListener(final Table tbl){
		final Action actionMark = new Action("Mark");
        final Action actionUnmark = new Action("Unmark");
        markedRows = new ArrayList();
        tbl.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(final Object target, final Object sender) {
                if (markedRows.contains(target)) {
                    return new Action[] { actionUnmark };
                } else {
                    return new Action[] { actionMark };
                }
            }

            @Override
            public void handleAction(final Action action, final Object sender,
                    final Object target) {
                if (actionMark == action) {
                    markedRows.add(target);
					scrollableHistoryPanel.setVisible(true);
                } else if (actionUnmark == action) {
                    markedRows.remove(target);
					scrollableHistoryPanel.setVisible(false);
                }
                tbl.markAsDirtyRecursive();
            }

        });

//        tbl.setCellStyleGenerator(new CellStyleGenerator() {
//			
//            @Override
//            public String getStyle(final Table source, final Object itemId,
//                    final Object propertyId) {
//                String style = null;
//                if (propertyId == null && markedRows.contains(itemId)) {
//                    // no propertyId, styling a row
//                    style = "marked";
//                }else{
//                	style = "unmarked";
//                }
//                return style;
//            }
//        });

	}
	
}
