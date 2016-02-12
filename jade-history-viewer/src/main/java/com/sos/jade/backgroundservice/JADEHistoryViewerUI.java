package com.sos.jade.backgroundservice;

import java.util.prefs.Preferences;

import javax.servlet.annotation.WebServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@Title("Jade Background Service History Viewer")
@Push
public class JADEHistoryViewerUI extends UI {

    private static final long serialVersionUID = 1L;
    public static final JadeBackgroundServiceOptions JADE_BS_OPTIONS = new JadeBackgroundServiceOptions();
    public static final Preferences PREFS = JADE_BS_OPTIONS.getPreferenceStore();
    public static String parentNodeName;
    private MainView mainView;
    private FilterLayoutWindow modalWindow;
    private AboutWindow aboutWindow;
    public static String hibernateConfigFile;
    public String log4jPropertiesFile;
    public String log4jFileOutputPath;
    private static final Logger LOGGER = LoggerFactory.getLogger(JADEHistoryViewerUI.class);

    @WebServlet(value = "/*", asyncSupported = true)
    /* productionMode = true does not work when the web app is started from an IDE integrated webserver! */
    @VaadinServletConfiguration(productionMode = true, ui = JADEHistoryViewerUI.class)
    public static class Servlet extends VaadinServlet {
        private static final long serialVersionUID = 1L;
    }

    @Override
    protected void init(final VaadinRequest request) {
        try {
            VaadinSession.getCurrent().getLockInstance().lock();
            if (VaadinSession.getCurrent().getAttribute(SessionAttributes.SESSION_ID.name()) == null) {
                VaadinSession.getCurrent().setAttribute(SessionAttributes.SESSION_ID.name(), SessionAttributes.SESSION_ID);
                parentNodeName = ((VaadinServletRequest) request).getHttpServletRequest().getRemoteAddr();
                PREFS.node(parentNodeName);
            }
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
        String absolutePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        hibernateConfigFile = absolutePath + "/WEB-INF/classes/hibernate.cfg.xml";
        mainView = new MainView();
        aboutWindow = new AboutWindow();
        modalWindow = new FilterLayoutWindow();
        setContent(mainView);
        LOGGER.debug("****************** JADEHistoryViewerUI initialized! ******************");
    }

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
