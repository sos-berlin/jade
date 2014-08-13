package com.sos.jadevaadincockpit.adapters;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.sos.DataExchange.JadeEngine;
import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Exceptions.JSExceptionMandatoryOptionMissing;
import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.sos.jadevaadincockpit.util.VaadinLogAppender;
import com.sos.jadevaadincockpit.view.LogPanel;
import com.sos.jadevaadincockpit.view.LogTabSheet;
import com.sos.jadevaadincockpit.viewmodel.ProfileContainer;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Notification;

public class JadeVaadinAdapter {

	public JadeVaadinAdapter() {

	}

	public void execute(Item profile) {
		FileResource logErrorIconResource = new FileResource(new File(ApplicationAttributes.getBasePath() + "/WEB-INF/icons/log_error.gif"));
		FileResource logIconResource = new FileResource(new File(ApplicationAttributes.getBasePath() + "/WEB-INF/icons/log.gif"));
		
		if (profile != null) {
			if (!profile
					.getItemProperty(ProfileContainer.PROPERTY.NODETYPE)
					.equals(ProfileContainer.NODETYPE.FILE)) {

				LogTabSheet logTabSheet = JadevaadincockpitUI.getCurrent()
						.getJadeMainUi().getLogTabSheet();
				LogPanel logPanel = logTabSheet.createLogTab(profile);

				PatternLayout layout = new PatternLayout(
						"%5p [%t] (%F:%L) - %m%n");
				VaadinLogAppender logAppender = new VaadinLogAppender(layout);
				logAppender.setLogAppenderComponent(logPanel);

				Logger rootLogger = Logger.getRootLogger();
				rootLogger.setLevel(Level.DEBUG);
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
						rootLogger.error(e.getLocalizedMessage()); // TODO this should be another logger
//						Notification.show("Some Exception escaped",
//								Notification.Type.ERROR_MESSAGE);
					}
					try {
						options.CheckMandatory();
						engine.Execute();
						
						logTabSheet.getTab(logPanel).setIcon(logIconResource);
						logPanel.setIconResource(logIconResource);
						Notification.show("Execution successful!",
								Notification.Type.TRAY_NOTIFICATION);

					} catch (JSExceptionMandatoryOptionMissing e) {
						rootLogger.error(e.ExceptionText()); // TODO this should be another logger
						logTabSheet.getTab(logPanel).setIcon(logErrorIconResource);
						logPanel.setIconResource(logErrorIconResource);
						Notification.show("Execution ended with errors!",
								Notification.Type.ERROR_MESSAGE);
					} catch (Exception e) {
						rootLogger.error(e.getLocalizedMessage()); // TODO this should be another logger
						logTabSheet.getTab(logPanel).setIcon(logErrorIconResource);
						logPanel.setIconResource(logErrorIconResource);
						Notification.show("Execution ended with errors!",
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
