package com.sos.jade.backgroundservice.logging;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

public class Log4jInitServlet  extends HttpServlet {

		private static final long serialVersionUID = 1L;

		public void init() throws ServletException {
			System.out.println("Log4JInitServlet is initializing log4j");
			String log4jLocation = getInitParameter("log4j-properties-location");
	
			ServletContext sc = getServletContext();
	
			if (log4jLocation == null) {
				System.err.println("*** No log4j-properties-location init param, so initializing log4j with BasicConfigurator");
				BasicConfigurator.configure();
			} else {
				String webAppPath = sc.getRealPath("/");
				String log4jProp = webAppPath + log4jLocation;
				File log4jProperties = new File(log4jProp);
				if (log4jProperties.exists()) {
					System.out.println("Initializing log4j with: " + log4jProp);
					PropertyConfigurator.configure(log4jProp);
				} else {
					System.err.println("*** " + log4jProp + " file not found, so initializing log4j with BasicConfigurator");
					BasicConfigurator.configure();
				}
			}
		}

}