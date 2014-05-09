package com.sos.jade.backgroundservice;

import java.io.File;

import javax.servlet.annotation.WebServlet;

import com.vaadin.ui.Image;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.AbstractEmbeddedState;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
public class BackgroundserviceUI extends UI
{
	private String absolutePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = BackgroundserviceUI.class, widgetset = "com.sos-berlin.products.jade.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout vLayout = new VerticalLayout();
        vLayout.setMargin(true);
        setContent(vLayout);
        final HorizontalLayout hLayout = new HorizontalLayout();
        final Image imgRabbit = new Image();
		FileResource rabbitResource = new FileResource(new File(absolutePath + "/images/job_scheduler_logo.jpg"));
        imgRabbit.setSource(rabbitResource);
        final Image imgTitle = new Image();
		FileResource titleResource = new FileResource(new File(absolutePath + "/images/job_scheduler_rabbit_circle_60x60.gif "));
        imgRabbit.setSource(titleResource);
        
        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                vLayout.addComponent(new Label("Thank you for clicking"));
            }
        });
        vLayout.addComponent(button);
    }

}
