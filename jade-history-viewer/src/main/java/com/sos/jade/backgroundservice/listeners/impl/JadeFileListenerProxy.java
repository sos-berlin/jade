/**
 * 
 */
package com.sos.jade.backgroundservice.listeners.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.jadehistory.JadeFilesHistoryFilter;

import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.view.MainView;

/**
 * @author SP
 *
 */
public class JadeFileListenerProxy extends JadeFileListenerImpl implements IJadeFileListener {

	@SuppressWarnings("unused")
	private static final String conSVNVersion = "$Id: JadeFileListenerProxy.java 24550 2014-06-30 18:52:08Z sp $";
	private final String conClassName = this.getClass().getSimpleName();
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");

	private static final long serialVersionUID = 1L;

	public JadeFileListenerProxy(MainView ui) {
		super(ui);
		log.debug(String.format("Class %1$s initialized", conClassName));
	}

	@Override
	public void getException(Exception e) {
		super.getException(e);
	}

	@Override
	public void getFileHistoryByIdFromLayer(Long id) {
		log.debug("getFileHistoryByIdFromLayer entered at " + sdf.format(new Date()));
		super.getFileHistoryByIdFromLayer(id);
		log.debug("getFileHistoryByIdFromLayer exited at " + sdf.format(new Date()));
	}
	
	@Override
	public void filterJadeFilesHistory(JadeFilesHistoryFilter filter) {
		log.debug("filterJadeFilesHistory entered at " + sdf.format(new Date()));
		super.filterJadeFilesHistory(filter);
		log.debug("received items count: " + super.ui.getHistoryItems().size());
		log.debug("filterJadeFilesHistory exited at " + sdf.format(new Date()));
	}
	
}
