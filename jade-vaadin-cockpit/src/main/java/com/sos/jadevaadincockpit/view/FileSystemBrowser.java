package com.sos.jadevaadincockpit.view;

import java.io.File;

import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.sos.jadevaadincockpit.globals.JadeSettingsFile;
import com.sos.jadevaadincockpit.globals.SessionAttributes;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class FileSystemBrowser extends Window {
	private static final long serialVersionUID = 2390646092807920477L;
	
	private VerticalLayout vLayout = new VerticalLayout();
	private FilesystemContainer filesystemContainer;
	private Table filesTable;
	private HorizontalLayout hLayout = new HorizontalLayout();
	private File selectedFile = null;
	private BrowserType type;
	private FileDownloader downloader = null;
	
	public enum BrowserType {download, open};
	
	/**
	 * Creates a window that displays the files (no folders) in the upload directory. The windowType determines the buttons the window will have in it's footer.
	 * @param type The window type. If null, the window will only have a "cancel"-button.
	 * @param profileTreeControl The profileTree's control.
	 */
	public FileSystemBrowser(BrowserType type) {
		this.type = type;
		createForm();
	}
	
	private void createForm() {
		this.filesystemContainer = new FilesystemContainer(new File(ApplicationAttributes.getBasePath() + "/WEB-INF/uploads"), true);
		setContent(vLayout);
		filesTable = new Table(null, filesystemContainer);
		filesTable.setVisibleColumns(FilesystemContainer.PROPERTY_NAME, FilesystemContainer.PROPERTY_SIZE, FilesystemContainer.PROPERTY_LASTMODIFIED);
		for (Object id : filesTable.getItemIds()) {
			if (filesystemContainer.areChildrenAllowed(id)) { // folders shall not be displayed
				filesystemContainer.removeItem(id);
			} else {
				filesTable.setItemIcon(id, new FileResource(new File(ApplicationAttributes.getBasePath() + "/WEB-INF/icons/Document.gif")));
			}
		}
		filesTable.setRowHeaderMode(RowHeaderMode.ICON_ONLY);
		filesTable.setNullSelectionAllowed(false);
//		filesTable.setMultiSelect(true); // TODO implement open/upload of multiple files
		filesTable.setSelectable(true);
		filesTable.setImmediate(true);
		filesTable.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 34626580017444813L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				selectedFile = (File) event.getProperty().getValue();
			}
		});
		vLayout.addComponent(filesTable);
		hLayout.setWidth("100%");
		vLayout.addComponent(hLayout);
		vLayout.setExpandRatio(filesTable, 1.0f);
		vLayout.setComponentAlignment(hLayout, Alignment.BOTTOM_CENTER);
		center();
		setModal(true);
		setClosable(true);
		
		switch (type) {
			case open:
				setCaption("Open File");
				final Button openButton = addButton("Open");
				openButton.setEnabled(selectedFile != null);
				openButton.addClickListener(new ClickListener() {
					private static final long serialVersionUID = 6826324733257791578L;

					@Override
					public void buttonClick(ClickEvent event) {
						if (selectedFile != null) {
							SessionAttributes.getJadeSettingsFile().loadSettingsFile(selectedFile.getAbsolutePath());
							close();
						}
					}
				});
				filesTable.addValueChangeListener(new ValueChangeListener() {
					private static final long serialVersionUID = 7962278281298315054L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						openButton.setEnabled(selectedFile != null);
					}
				});
				filesTable.addItemClickListener(new ItemClickListener() {
					private static final long serialVersionUID = -5701176403051435328L;

					@Override
					public void itemClick(ItemClickEvent event) {
						if (event.isDoubleClick()) {
							if (selectedFile != null) {
								SessionAttributes.getJadeSettingsFile().loadSettingsFile(selectedFile.getAbsolutePath());
								close();
							}
						}
					}
				});
				break;
			case download:
				setCaption("Download File");
				final Button downloadButton = addButton("Download");
				downloadButton.setEnabled(selectedFile != null);
				filesTable.addValueChangeListener(new ValueChangeListener() {
					private static final long serialVersionUID = 7962278281298315054L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						downloadButton.setEnabled(selectedFile != null);
						if (selectedFile != null) {
							if (downloader != null) {
								downloader.setFileDownloadResource(new FileResource(selectedFile));
							} else {
								downloader = new FileDownloader(new FileResource(selectedFile));
								downloader.extend(downloadButton);
							}
						}
					}
					});
				filesTable.addItemClickListener(new ItemClickListener() {
					private static final long serialVersionUID = -5701176403051435328L;

					@Override
					public void itemClick(ItemClickEvent event) {
						if (event.isDoubleClick()) {
							// TODO does not work. the button.click()-method does not trigger the downloader-extension 
							downloadButton.click();
						}
					}
				});
				break;
		}
		
		Button cancelButton = addButton("Cancel");
		cancelButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -5003459337145847307L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
	}
	
	/**
	 * Adds a button to the window's footer.
	 * @param caption The button's caption.
	 * @return The added button for further configuration.
	 */
	private Button addButton(String caption) {
		Button button = new Button(caption);
		button.setWidth("100%");
		hLayout.addComponent(button);
		hLayout.setExpandRatio(button, 1.0f);
		return button;
	}
	

	/**
	 * @return the filesystemContainer
	 */
	public FilesystemContainer getFilesystemContainer() {
		return filesystemContainer;
	}

	/**
	 * @param filesystemContainer the filesystemContainer to set
	 */
	public void setFilesystemContainer(FilesystemContainer filesystemContainer) {
		this.filesystemContainer = filesystemContainer;
	}

}
