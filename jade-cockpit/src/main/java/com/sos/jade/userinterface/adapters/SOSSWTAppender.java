/**
 * 
 */
package com.sos.jade.userinterface.adapters;

/**
 * @author KB
 *
 */
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author kalpak
 *
 */
public class SOSSWTAppender extends AppenderSkeleton implements ISOSSWTAppenderUI {

	private ISOSSWTAppenderUI	appenderUI	= this;

	/**
	 * 
	 */
	public SOSSWTAppender() {
	}

	/**
	 * 
	 * @param pobjLayout
	 */
	public SOSSWTAppender(final Layout pobjLayout) {
		layout = pobjLayout;
	}

	@Override
	public void setLayout(final Layout pobjLayout) {
		layout = pobjLayout;
	}

	/**
	 * 
	*
	* \brief setAppenderUI
	*
	* \details
	* 
	* \return void
	*
	 */
	public void setAppenderUI(final ISOSSWTAppenderUI pobjAppenderUI) {
		appenderUI = pobjAppenderUI;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	protected void append(final LoggingEvent event) {
		if (!performChecks()) {
			return;
		}
		String logOutput = layout.format(event);
		appenderUI.doLog(logOutput);

		if (layout.ignoresThrowable()) {
			String[] lines = event.getThrowableStrRep();
			if (lines != null) {
				int len = lines.length;
				for (int i = 0; i < len; i++) {
					appenderUI.doLog(lines[i]);
					appenderUI.doLog(Layout.LINE_SEP);
					appenderUI.doUpdate();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.Appender#close()
	 */
	@Override
	public void close() {
		//Opportunity for the appender ui to do any cleanup.
		appenderUI.doClose();
		appenderUI = null;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	@Override
	public boolean requiresLayout() {
		return true;
	}

	/**
	 * Performs checks to make sure the appender ui is still alive.
	 *
	 * @return
	 */
	private boolean performChecks() {
		return !closed && layout != null;
	}

	@Override
	public ISOSSWTAppenderUI doLog(final String logOutput) {
		System.out.println(logOutput);
		return this;
	}

	@Override
	public ISOSSWTAppenderUI doClose() {
		return this;
	}

	@Override
	public void doUpdate() {
		// TODO Auto-generated method stub

	}
}
