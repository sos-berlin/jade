package com.sos.jade.backgroundservice.logging;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggingInitServlet extends HttpServlet{
	private static final long serialVersionUID = -7626625501656139602L;
	private Logger log = LoggerFactory.getLogger(LoggingInitServlet.class);

	public LoggingInitServlet() {
		initLogging();
	}

	private void initLog4jLogging(ClassLoader classLoader){
		URL loggingLog4jUrl = classLoader.getResource("log4j.properties");
		File log4jProperties;
		if(loggingLog4jUrl != null){
			try {
				log4jProperties = new File(loggingLog4jUrl.toURI());
				if (log4jProperties.exists()) {
					System.out.println("Initializing log4j with: " + loggingLog4jUrl.toURI());
					org.apache.log4j.PropertyConfigurator.configure(loggingLog4jUrl);
					log.debug("logging configured with: {}", loggingLog4jUrl);
				}
			} catch (URISyntaxException e) {
				System.out.println("URI Syntax wrong for log4j properties URI!");
			}
		} 
		
	}
	
	// Doesn´t work, ClassNotFoundException on validation phase before start
//	private void initLogbackLogging(ClassLoader classLoader){
//		URL loggingLogbackUrl = classLoader.getResource("logback.xml");
//		File logbackProperties;
//		if (loggingLogbackUrl != null){
//			try {
//				logbackProperties = new File(loggingLogbackUrl.toURI());
//				if (logbackProperties.exists()) {
//					ch.qos.logback.classic.LoggerContext lc = (ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory();
//					System.out.println("Initializing logback with: " + loggingLogbackUrl.toURI());
//					ch.qos.logback.classic.joran.JoranConfigurator configurator = new ch.qos.logback.classic.joran.JoranConfigurator();
//					configurator.setContext(lc);
//					lc.reset();
//					configurator.doConfigure(loggingLogbackUrl);
//					ch.qos.logback.core.util.StatusPrinter.print(lc);
//					log.debug("logging configured with: {}", loggingLogbackUrl);
//				}
//			} catch (URISyntaxException e) {
//				System.out.println("URI Syntax wrong for logback properties URI!");
//			} catch (ch.qos.logback.core.joran.spi.JoranException e) {
//				System.out.println("JoranConfigurator Problem!\r\n" + e);
//			}
//		}
//		
//	}
	
	private void initLogging(){
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			if(Class.forName("org.apache.log4j.PropertyConfigurator", false, contextClassLoader) != null){
				initLog4jLogging(contextClassLoader);
				return;
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Log4j not found on classpath, try logback instead!");
		}
		// Doesn´t work, ClassNotFoundException on validation phase before start
//		try {
//			if(Class.forName("ch.qos.logback.classic.joran.JoranConfigurator", false, contextClassLoader) != null){
//				initLogbackLogging(contextClassLoader);
//				return;
//			}
//		} catch (ClassNotFoundException e) {
//			System.out.println("Logback not found on classpath, no logging configured!");
//		}
	}
}
