package com.sos.jadevaadincockpit.view;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AboutWindow extends Window {
	private static final long serialVersionUID = 8483356724115172870L;
	
	public static void show() {
		AboutWindow aboutWindow = new AboutWindow();
		aboutWindow.init();
		UI.getCurrent().addWindow(aboutWindow);
	}
	
	private void init() {
		
		setHeight("400px");
		setWidth("400px");
		
		VerticalLayout vLayout = new VerticalLayout();
		setContent(vLayout);
		
		vLayout.addComponent(new Label("JADE Cockpit"));
		
		Button closeButton = new Button("Close");
		closeButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -5943987051861318379L;

			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		
		// TODO
		
		setModal(true);
		
	}
	
}
