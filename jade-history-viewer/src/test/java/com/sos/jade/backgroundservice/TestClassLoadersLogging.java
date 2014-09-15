package com.sos.jade.backgroundservice;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import static junit.framework.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClassLoadersLogging{
	private Logger log = LoggerFactory.getLogger(TestClassLoadersLogging.class);
  private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
  private ClassLoader parentClassLoader = classLoader.getParent();
  private ClassLoader systemClassLoader = classLoader.getSystemClassLoader();

	public TestClassLoadersLogging() {
	}

	@Test
	public void testLogConfigurationFromClassLoaders(){
	  try {
			Enumeration<URL> resourcesLogback = classLoader.getResources("logback.xml");
			Enumeration<URL> parentResourcesLogback = parentClassLoader.getResources("logback.xml");
			Enumeration<URL> systemResourcesLogback = systemClassLoader.getResources("logback.xml");
			Enumeration<URL> resourcesLog4j = classLoader.getResources("log4j.properties");
			Enumeration<URL> parentResourcesLog4j = parentClassLoader.getResources("log4j.properties");
			Enumeration<URL> systemResourcesLog4j = systemClassLoader.getResources("log4j.properties");
			Integer count = 0;
			Integer parentCount = 0;
			Integer systemCount = 0;
			log.debug("actual ClassLoaders are:\r\n\tcurrent: {}\r\n\tparent: {}\r\n\tsystem: {}", new String[]{classLoader.toString(), parentClassLoader.toString(), systemClassLoader.toString()});
			while(resourcesLogback.hasMoreElements()){
				count++;
				String urlPath = resourcesLogback.nextElement().getPath();
				log.debug("*** Path found for logback.xml through current class loader {} : {}", classLoader.toString(), urlPath);
				System.out.println("*** Path found for logback.xml through current class loader " + classLoader.toString() + ": " + urlPath);
			}
			while(parentResourcesLogback.hasMoreElements()){
				parentCount++;
				String urlPath = parentResourcesLogback.nextElement().getPath();
				log.debug("*** Path found for logback.xml through parent class loader {} : {}", parentClassLoader.toString(), urlPath);
				System.out.println("*** Path found for logback.xml through parent class loader " + parentClassLoader.toString() + ": " + urlPath);
			}
			while(systemResourcesLogback.hasMoreElements()){
				systemCount++;
				String urlPath = systemResourcesLogback.nextElement().getPath();
				log.debug("*** Path found for logback.xml through system class loader {} : {}", systemClassLoader.toString(), urlPath);
				System.out.println("*** Path found for logback.xml through system class loader " + systemClassLoader.toString() + ": " + urlPath);
			}
			log.debug("logback.xml found ({}) time(s) in current ClassLoader, ({}) time(s) in parent ClassLoader and ({}) time(s) in system ClassLoader!", new Integer[] {count, parentCount, systemCount});
			assertTrue(count > 0);
			System.out.println("logback.xml found (" + count + ") times in current ClassLoader, (" + parentCount + ") time(s) in parent ClassLoader and (" + systemCount + ") time(s) in system ClassLoader!");
			count = 0;
			parentCount = 0;
			systemCount = 0;
			while(resourcesLog4j.hasMoreElements()){
				count++;
				String urlPath = resourcesLog4j.nextElement().getPath();
				log.debug("*** Path found for log4j.properties through current class loader {} : {}", classLoader.toString(), urlPath);
				System.out.println("*** Path found for log4j.properties through current class loader " + classLoader.toString() + ": " + urlPath);
			}
			while(parentResourcesLog4j.hasMoreElements()){
				parentCount++;
				String urlPath = parentResourcesLog4j.nextElement().getPath();
				log.debug("*** Path found for log4j.properties through parent class loader {} : {}", parentClassLoader.toString(), urlPath);
				System.out.println("*** Path found for log4j.properties through parent class loader " + parentClassLoader.toString() + ": " + urlPath);
			}
			while(systemResourcesLogback.hasMoreElements()){
				systemCount++;
				String urlPath = systemResourcesLog4j.nextElement().getPath();
				log.debug("*** Path found for log4j.properties through system class loader {} : {}", systemClassLoader.toString(), urlPath);
				System.out.println("*** Path found for log4j.properties through system class loader " + systemClassLoader.toString() + ": " + urlPath);
			}
			log.debug("log4j.properties found ({}) time(s) in current ClassLoader, ({}) time(s) in parent ClassLoader and ({}) time(s) in system ClassLoader!", new Integer[] {count, parentCount, systemCount});
			assertTrue(count > 0);
			System.out.println("log4j.properties found (" + count + ") times in current ClassLoader, (" + parentCount + ") time(s) in parent ClassLoader and (" + systemCount + ") time(s) in system ClassLoader!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
