/**
 * 
 */
package com.sos.jade.backgroundservice.listeners.impl;

import sos.ftphistory.JadeFilesFilter;

import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.view.MainView;

import org.apache.log4j.Logger; 
/**
 * @author Santiago
 *
 */
public class JadeFileListenerProxy extends JadeFileListenerImpl implements IJadeFileListener {

	@SuppressWarnings("unused")
	private final String				conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private final Logger				logger			= Logger.getLogger(this.getClass());
	@SuppressWarnings("unused")
	private static final String			conSVNVersion	= "$Id: SOSDataExchangeAdapter.java 24550 2014-05-14 18:52:08Z kb $";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param ui
	 */
	public JadeFileListenerProxy(MainView ui) {
		super(ui);
		logger.debug(String.format("Class %1$s initialized", conClassName));
	}

	@Override
	public void getException(Exception e) {
		super.getException(e);
	}

	@Override
	public void filterJadeFiles(JadeFilesFilter filter) {
		logger.debug("filterJadeFiles entered");
		super.filterJadeFiles(filter);
		logger.debug("filterJadeFiles exit");
	}

	@Override
	public void getFileHistoryByIdFromLayer(Long id) {
		logger.debug("getFileHistoryByIdFromLayer entered");
		super.getFileHistoryByIdFromLayer(id);
		logger.debug("getFileHistoryByIdFromLayer exit");
	}
	

}
