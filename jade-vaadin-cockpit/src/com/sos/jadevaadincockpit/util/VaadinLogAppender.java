package com.sos.jadevaadincockpit.util;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

public class VaadinLogAppender extends AppenderSkeleton implements IVaadinLogAppender {

	private IVaadinLogAppender logAppenderComponent = this;
	public VaadinLogAppender() {
		// TODO Auto-generated constructor stub
	}
	
	public VaadinLogAppender(Layout layout) {
		setLayout(layout);
	}
	
    public void setLogAppenderComponent(final IVaadinLogAppender logAppenderComponent) {
    	this.logAppenderComponent = logAppenderComponent;
    }

	@Override
	public void close() {
		logAppenderComponent.doClose();
		logAppenderComponent = null;
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	@Override
	protected void append(LoggingEvent arg0) {
		if (performChecks()) {
			String logOutput = layout.format(arg0);
			logAppenderComponent.doLog(logOutput);
			
	        if (layout.ignoresThrowable()) {
	        	String[] lines = arg0.getThrowableStrRep();
				if (lines != null) {
					int len = lines.length;
					for (int i = 0; i < len; i++) {
						logAppenderComponent.doLog(lines[i]);
						logAppenderComponent.doLog(Layout.LINE_SEP);
					}
				}
			}
		}
	}
	
    /**
     * Performs checks to make sure the appender component is still alive.
     *
     * @return
     */
	private boolean performChecks() {
		return !closed && layout != null;
	}
	
	/**
	 * Gets called on logEvents if no logAppenderUi was set explicitly (via setLogAppenderUi()).
	 */
	@Override
	public void doLog(String log) {
		// TODO
	}

	@Override
	public void doClose() {
		// TODO
	}

}
