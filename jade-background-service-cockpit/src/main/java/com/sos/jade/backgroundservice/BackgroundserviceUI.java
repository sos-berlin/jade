package com.sos.jade.backgroundservice;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.LogManager;
import java.util.prefs.Preferences;

import javax.servlet.annotation.WebServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.sos.JSHelper.io.Files.JSIniFile;
import com.sos.jade.backgroundservice.data.SessionAttributes;
import com.sos.jade.backgroundservice.options.JadeBackgroundServiceOptions;
import com.sos.jade.backgroundservice.view.MainView;
import com.sos.jade.backgroundservice.view.components.AboutWindow;
import com.sos.jade.backgroundservice.view.components.filter.FilterLayoutWindow;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

@Theme("bs")
@Title("Jade Background Service")
@Push
public class BackgroundserviceUI extends UI {
  static {
//		LogManager.getLogManager().reset();
    SLF4JBridgeHandler.install();
  }
	private static final long serialVersionUID = 1L;
	public static final JadeBackgroundServiceOptions jadeBsOptions = new JadeBackgroundServiceOptions();
	public static final Preferences prefs = jadeBsOptions.getPreferenceStore();
	public static String parentNodeName;
	private MainView mainView;
	private FilterLayoutWindow modalWindow;
	private AboutWindow aboutWindow;
  private static final String COMMAND_PERMISSIONS = "/jobscheduler/rest/sosPermission/permissions?session_id=%s";
  private static final String SESSION_ID = "session_id";
  private static final String SECURITY_SERVER = "security_server";
  private String jsSessionId;
  private String securityServer;
  public static String hibernateConfigFile;
  public static String log4jPropertiesFile;
  public static String log4jFileOutputPath;
  public static Logger log = LoggerFactory.getLogger(BackgroundserviceUI.class);
    
	@WebServlet(value = "/*", asyncSupported = true)
    /* productionMode = true gilt nicht, wenn die WebApp aus der IDE heraus gestartet wird! */
    @VaadinServletConfiguration(productionMode = true, ui = BackgroundserviceUI.class)
    public static class Servlet extends VaadinServlet {
			private static final long serialVersionUID = 1L;
    }

    @Override
    protected void init(final VaadinRequest request) {
  	  try {
				VaadinSession.getCurrent().getLockInstance().lock();
				if (VaadinSession.getCurrent().getAttribute(SessionAttributes.SESSION_ID.name()) == null) {
					VaadinSession.getCurrent().setAttribute(SessionAttributes.SESSION_ID.name(), SessionAttributes.SESSION_ID);
	//				jsSessionId = request.getParameter(SESSION_ID);
	//				securityServer = request.getParameter(SECURITY_SERVER);
					parentNodeName = ((VaadinServletRequest) request).getHttpServletRequest().getRemoteAddr();
					prefs.node(parentNodeName);
	//				setCookieUsage(request);
				}
			} finally {
				VaadinSession.getCurrent().getLockInstance().unlock();
			}
  	  ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
  	  ClassLoader parentClassLoader = classLoader.getParent();
  	  ClassLoader systemClassLoader = classLoader.getSystemClassLoader();
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
				System.out.println("log4j.properties found (" + count + ") times in current ClassLoader, (" + parentCount + ") time(s) in parent ClassLoader and (" + systemCount + ") time(s) in system ClassLoader!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	String absolutePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
    	JSIniFile jsConfig = new JSIniFile(absolutePath + "/WEB-INF/classes/jsconfig.ini");
    	if(jsConfig != null){
      	hibernateConfigFile = jsConfig.getPropertyString("Configuration", "hibernateConfigFile", absolutePath + "/WEB-INF/classes/hibernate.cfg.xml");
    	}
    	/*Only for developement*/else{
      	hibernateConfigFile = absolutePath + "/WEB-INF/classes/hibernate.cfg.xml";
    	}
    	
//    	PropertyConfigurator.configure(absolutePath + "/WEB-INF/classes/log4j.properties");
    	mainView = new MainView();
    	aboutWindow = new AboutWindow();
    	modalWindow = new FilterLayoutWindow();
    	setContent(mainView);
    	log.debug("****************** BackgroundServiceUI initialized! ******************");
//    	// for the future
//    	// no content for this view, the navigator does the trick
//		// Create a new instance of the navigator. The navigator will attach itself automatically to this view. 
//		new Navigator(this, this);
//		// the initial log view where the user can login to the application 
//		getNavigator().addView(LoginView.NAME, LoginView.class);
//		// add the main view of the application 
//		getNavigator().addView(MainView.NAME, mainView); 
//		// use a view change handler to ensure the user is always redirected to the login view if the user is not logged in.
//		getNavigator().addViewChangeListener(new ViewChangeListener() {
//			private static final long serialVersionUID = 1L;
//
//			@Override 
//			public boolean beforeViewChange(ViewChangeEvent event) {
//				//Check if a user has logged in
//				boolean isLoggedIn = getSession().getAttribute("user") != null;
//				boolean isLoginView = event.getNewView() instanceof LoginView;
//				if (!isLoggedIn && !isLoginView) { 
//					// Redirect to login view always if a user has not yet logged in 
//					getNavigator().navigateTo(LoginView.NAME); 
//					return false;
//				} else if (isLoggedIn && isLoginView) { 
//					// If someone tries to access to login view while logged in, 
//					// navigate to MainView
//					mainView.initView();
//					modalWindow = new FilterLayoutWindow();
//					getNavigator().navigateTo(MainView.NAME); 
//					return false; 
//				}
//				return true; 
//			}
//
//			@Override 
//			public void afterViewChange(ViewChangeEvent event) {
//
//			} 
//		}); 
//    	if(checkJsSessionId()){
//			prefs.node(parentNodeName);
//			// No login view needed, navigate directly to mainView 
//			mainView.initView();
//			modalWindow = new FilterLayoutWindow();
//			getNavigator().navigateTo(MainView.NAME);
//    	}else{
//    		// no authentication with the given sessionId or sessionId is null, navigate to login view
//    		getNavigator().navigateTo(LoginView.NAME);
//    	}
    }

//	private void setCookieUsage(final VaadinRequest request){
//		Cookie[] cookies = ((VaadinServletRequest) request).getCookies();
//	    for (Cookie cookie : cookies){
//	    	if (cookie.getName().equals("JadeBS")){
//	    		this.cookie = cookie;
//		    	this.cookie.setPath("/");
//		    	this.cookie.setMaxAge(604800); // 604800 sec => one week 
//	    	}
//	    }
//    	VaadinServletResponse response = ((VaadinServletResponse) VaadinService.getCurrentResponse());
//	    if(this.cookie == null){
//	    	this.cookie = new Cookie("JadeBS", "");
//	    	this.cookie.setPath("/");
//	    	this.cookie.setMaxAge(604800); // 604800 sec => one week 
//	    }
//        response.addCookie(this.cookie);
//	}
	
//	private boolean checkJsSessionId(){
//		if(jsSessionId == null){
//			jsSessionId = VaadinService.getCurrentRequest().getParameter(SESSION_ID);
//		}
//		if(securityServer == null){
//			securityServer = VaadinService.getCurrentRequest().getParameter(SECURITY_SERVER);
//		}
//		if (securityServer != null && jsSessionId != null) {
//			SOSRestShiroClient sosRestShiroClient = new SOSRestShiroClient();
//			SOSWebserviceAuthenticationRecord sosWebserviceAuthenticationRecord = new SOSWebserviceAuthenticationRecord();
//			sosWebserviceAuthenticationRecord.setResource(securityServer + COMMAND_PERMISSIONS);
//			sosWebserviceAuthenticationRecord.setSessionId(jsSessionId);
//			try {
//        		SOSPermissionShiro sosPermissionShiro = sosRestShiroClient.getSOSPermissionShiro(new URL(String.format(sosWebserviceAuthenticationRecord.getResource(), sosWebserviceAuthenticationRecord.getSessionId())));
//        		if(sosPermissionShiro.isAuthenticated()){
//            		SOSJaxbSubject currentUser = new SOSJaxbSubject(sosPermissionShiro);
//                    if (currentUser == null){
//						Notification.show("error authenticating with sessionID", Type.ERROR_MESSAGE);
//                    }else if (!currentUser.isAuthenticated()) {
//            			Notification.show("could not authenticate with sessionID", Type.ERROR_MESSAGE);
//            		}else{
//    					getSession().setAttribute("user", sosPermissionShiro.getUser());
//    					parentNodeName = sosPermissionShiro.getUser();
//    					return true;
//            		}
//        		}
//			} catch (Exception e) {
//				e.printStackTrace();
//				return false;
//			}
//		}
//		return false;
//	}

	public FilterLayoutWindow getModalWindow() {
		return modalWindow;
	}
	
	public void setModalWindow(FilterLayoutWindow modalWindow) {
		this.modalWindow = modalWindow;
	}

	public MainView getMainView() {
		return mainView;
	}

	public void setMainView(MainView mainView) {
		this.mainView = mainView;
	}

	public AboutWindow getAboutWindow() {
		return aboutWindow;
	}

}
