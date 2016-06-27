package com.sos.jadevaadincockpit.adapters;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.log4j.PatternLayout;

import com.sos.DataExchange.JadeEngine;
import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Exceptions.JSExceptionMandatoryOptionMissing;
import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.data.ProfileContainer;
import com.sos.jadevaadincockpit.util.VaadinLogAppender;
import com.sos.jadevaadincockpit.view.LogPanel;
import com.sos.jadevaadincockpit.view.LogTabSheet;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Notification;

public class JadeVaadinAdapter implements Serializable {
	private static final long serialVersionUID = 7600792068708750918L;
	
	private Logger logger = Logger.getLogger(JadeVaadinAdapter.class.getName());

	public JadeVaadinAdapter() {

	}

	public void execute(Item profile) {
		ThemeResource logErrorIconResource = new ThemeResource("icons/16-circle-orange.png");
		ThemeResource logIconResource = new ThemeResource("icons/16-circle-green.png");
//		FileResource logIconResource = new FileResource(new File(ApplicationAttributes.getBasePath() + "/WEB-INF/icons/log.gif"));
		
		if (profile != null) {
			if (!profile
					.getItemProperty(ProfileContainer.PROPERTY.NODETYPE)
					.equals(ProfileContainer.NODETYPE.FILE)) {

				LogTabSheet logTabSheet = JadevaadincockpitUI.getCurrent()
						.getMainView().getLogTabSheet();
				LogPanel logPanel = logTabSheet.createLogTab(profile);
				
				/* TODO Log nicht im Panel anzeigen sondern in SubWindow
				Window window = new Window();
				window.setContent(logTabSheet);
				window.setClosable(true);
				JadevaadincockpitUI.getCurrent().addWindow(window);
				*/

				// TODO This uses a log4j-Logger. Is this a problem?
				PatternLayout layout = new PatternLayout(
						"%5p [%t] (%F:%L) - %m%n");
				VaadinLogAppender logAppender = new VaadinLogAppender(layout);
				logAppender.setLogAppenderComponent(logPanel);

				org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger(); 
				rootLogger.setLevel(org.apache.log4j.Level.DEBUG);
				rootLogger.addAppender(logAppender);

				Property<?> optionsProperty = profile
						.getItemProperty(ProfileContainer.PROPERTY.JADEOPTIONS);
				if (optionsProperty != null) {
					JADEOptions options = (JADEOptions) optionsProperty
							.getValue();
					JadeEngine engine = null;
					try {
						engine = new JadeEngine(options);
					} catch (Exception e) {
						logger.log(Level.SEVERE, e.getMessage());
					}
					try {
						options.CheckMandatory();
						engine.Execute();
						
						logTabSheet.getTab(logPanel).setIcon(logIconResource);
						logPanel.setIconResource(logIconResource);
						Notification.show("Execution successful!",
								Notification.Type.TRAY_NOTIFICATION);

					} catch (JSExceptionMandatoryOptionMissing e) {
						logger.log(Level.SEVERE, e.ExceptionText());
						logTabSheet.getTab(logPanel).setIcon(logErrorIconResource);
						logPanel.setIconResource(logErrorIconResource);
						Notification.show("Execution ended with errors! See log for details.",
								Notification.Type.ERROR_MESSAGE);
					} catch (Exception e) {
						logger.log(Level.SEVERE, e.getMessage());
						logTabSheet.getTab(logPanel).setIcon(logErrorIconResource);
						logPanel.setIconResource(logErrorIconResource);
						Notification.show("Execution ended with errors! See log for details.",
								Notification.Type.ERROR_MESSAGE);
					}
					 // engine may be null in some (exceptional) cases
					if (engine != null) {
						logTabSheet.setLogTime(logPanel, engine.getTime());
						engine.Logout();
					}
				}
			}
		}
	}
}
