/**
 * 
 */
package com.sos.jade.userinterface.adapters;


/**
 * @author KB
 *
 */
public interface ISOSSWTAppenderUI {
	/**
	 * 
	*
	* \brief doLog
	*
	* \details
	* 
	* \return ISOSSWTAppenderUI
	*
	 */
	public ISOSSWTAppenderUI doLog(final String logOutput);
	/**
	 * 
	*
	* \brief doClose
	*
	* \details
	* 
	* \return ISOSSWTAppenderUI
	*
	 */
	public ISOSSWTAppenderUI doClose();
	
	  public void doUpdate();	
}
