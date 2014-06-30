/**
 * 
 */
package com.sos.jade.backgroundservice.listeners.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import sos.ftphistory.JadeFilesHistoryFilter;

import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.view.MainView;

/**
 * @author SP
 *
 */
public class JadeFileListenerProxy extends JadeFileListenerImpl implements IJadeFileListener {

	private final String				conClassName	= this.getClass().getSimpleName();
	private final Logger				logger			= Logger.getLogger(this.getClass());
	@SuppressWarnings("unused")
	private static final String			conSVNVersion	= "$Id: JadeFileListenerProxy.java 24550 2014-06-30 18:52:08Z sp $";
	private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");

	private static final long serialVersionUID = 1L;

	public JadeFileListenerProxy(MainView ui) {
		super(ui);
		logger.debug(String.format("Class %1$s initialized", conClassName));
	}

	@Override
	public void getException(Exception e) {
		super.getException(e);
	}

	@Override
	public void getFileHistoryByIdFromLayer(Long id) {
		logger.debug("getFileHistoryByIdFromLayer entered at " + sdf.format(new Date()));
		super.getFileHistoryByIdFromLayer(id);
		logger.debug("getFileHistoryByIdFromLayer exited at " + sdf.format(new Date()));
	}
	
	@Override
	public void filterJadeFilesHistory(JadeFilesHistoryFilter filter) {
		logger.debug("filterJadeFilesHistory entered at " + sdf.format(new Date()));
		super.filterJadeFilesHistory(filter);
		logger.debug("received items count: " + super.ui.getHistoryItems().size());
		logger.debug("filterJadeFilesHistory exited at " + sdf.format(new Date()));
	}
	
	@Override
	public void closeJadeFilesHistoryDbSession() {
		super.closeJadeFilesHistoryDbSession();
		logger.debug("hibernate session closed at " + sdf.format(new Date()) + "!");
	}

}
