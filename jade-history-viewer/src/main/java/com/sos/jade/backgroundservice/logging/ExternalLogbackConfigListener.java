package com.sos.jade.backgroundservice.logging;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalLogbackConfigListener implements ServletContextListener {
	private static final Logger logger = LoggerFactory.getLogger(ExternalLogbackConfigListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String configLocation = sce.getServletContext().getRealPath("/") + "/WEB-INF/classes/";
		
		try{
			new LogBackConfigLoader(configLocation + "logback.xml");
		}catch(Exception e){
			logger.error("Unable to read config file from path " + configLocation, e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}
