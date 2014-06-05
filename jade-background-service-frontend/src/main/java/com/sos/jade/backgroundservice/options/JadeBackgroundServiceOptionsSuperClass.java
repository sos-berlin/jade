package com.sos.jade.backgroundservice.options;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.sos.JSHelper.Annotations.JSOptionClass;
import com.sos.JSHelper.Annotations.JSOptionDefinition;
import com.sos.JSHelper.Exceptions.JSExceptionMandatoryOptionMissing;
import com.sos.JSHelper.Listener.JSListener;
import com.sos.JSHelper.Options.JSOptionsClass;
import com.sos.JSHelper.Options.SOSOptionBoolean;
import com.sos.JSHelper.Options.SOSOptionInFileName;

/**
 * \class 		JadeBackgroundServiceOptionsSuperClass - JadeBackgroundService
 *
 * \brief 
 * An Options-Super-Class with all Options. This Class will be extended by the "real" Options-class (\see JadeBackgroundServiceOptions.
 * The "real" Option class will hold all the things, which are normaly overwritten at a new generation
 * of the super-class.
 *
 *

 *
 * see \see C:\Users\KB\AppData\Local\Temp\scheduler_editor-5890513725838352951.html for (more) details.
 * 
 * \verbatim ;
 * mechanicaly created by C:\ProgramData\sos-berlin.com\jobscheduler\latestscheduler_4446\config\JOETemplates\java\xsl\JSJobDoc2JSOptionSuperClass.xsl from http://www.sos-berlin.com at 20140605131019 
 * \endverbatim
 * \section OptionsTable Tabelle der vorhandenen Optionen
 * 
 * Tabelle mit allen Optionen
 * 
 * MethodName
 * Title
 * Setting
 * Description
 * IsMandatory
 * DataType
 * InitialValue
 * TestValue
 * 
 * 
 *
 * \section TestData Eine Hilfe zum Erzeugen einer HashMap mit Testdaten
 *
 * Die folgenden Methode kann verwendet werden, um für einen Test eine HashMap
 * mit sinnvollen Werten für die einzelnen Optionen zu erzeugen.
 *
 * \verbatim
 private HashMap <String, String> SetJobSchedulerSSHJobOptions (HashMap <String, String> pobjHM) {
	pobjHM.put ("		JadeBackgroundServiceOptionsSuperClass.auth_file", "test");  // This parameter specifies the path and name of a user's pr
		return pobjHM;
  }  //  private void SetJobSchedulerSSHJobOptions (HashMap <String, String> pobjHM)
 * \endverbatim
 */
@JSOptionClass(
				name = "JadeBackgroundServiceOptionsSuperClass",
				description = "JadeBackgroundServiceOptionsSuperClass")
public class JadeBackgroundServiceOptionsSuperClass extends JSOptionsClass {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 338878527661019237L;
	private final String		conClassName		= "JadeBackgroundServiceOptionsSuperClass";
	@SuppressWarnings("unused")
	private static Logger		logger				= Logger.getLogger(JadeBackgroundServiceOptionsSuperClass.class);

	/**
	 * \var Click_for_Details : 
	 * 
	 *
	 */
	@JSOptionDefinition(
						name = "click_for_Details",
						description = "",
						key = "click_for_Details",
						type = "SOSOptionString",
						mandatory = false)
	public SOSOptionBoolean		click_for_Details	= new SOSOptionBoolean(this, conClassName + ".click_for_Details", // HashMap-Key
															"", // Titel
															"false", // InitValue
															"false", // DefaultValue
															false // isMandatory
													);
	
	/**
	 * \brief getClick_for_Details : 
	 * 
	 * \details
	 * 
	 *
	 * \return 
	 *
	 */
	public SOSOptionBoolean getClick_for_Details() {
		return click_for_Details;
	}

	/**
	 * \brief setClick_for_Details : 
	 * 
	 * \details
	 * 
	 *
	 * @param Click_for_Details : 
	 */
	public void setClick_for_Details(final SOSOptionBoolean p_click_for_Details) {
		click_for_Details = p_click_for_Details;
	}
	/**
	 * \var Hibernate_Configuration_File_Name : 
	 * 
	 *
	 */
	@JSOptionDefinition(
						name = "hibernate_Configuration_File_Name",
						description = "",
						key = "hibernate_Configuration_File_Name",
						type = "SOSOptionString",
						mandatory = true)
	public SOSOptionInFileName	hibernate_Configuration_File_Name	= new SOSOptionInFileName(this, conClassName + ".hibernate_Configuration_File_Name", // HashMap-Key
																			"", // Titel
																			"env:JADE_BS_HIBERNATE_CONFIG", // InitValue
																			"hibernate.cfg.xml", // DefaultValue
																			true // isMandatory
																	);
	public SOSOptionInFileName hibernateConf = (SOSOptionInFileName) hibernate_Configuration_File_Name.SetAlias("HibernateConf", "H");
	/**
	 * \brief getHibernate_Configuration_File_Name : 
	 * 
	 * \details
	 * 
	 *
	 * \return 
	 *
	 */
	public SOSOptionInFileName getHibernate_Configuration_File_Name() {
		return hibernate_Configuration_File_Name;
	}

	/**
	 * \brief setHibernate_Configuration_File_Name : 
	 * 
	 * \details
	 * 
	 *
	 * @param hibernate_Configuration_File_Name : 
	 */
	public void setHibernate_Configuration_File_Name(final SOSOptionInFileName p_hibernate_Configuration_File_Name) {
		hibernate_Configuration_File_Name = p_hibernate_Configuration_File_Name;
	}

	public JadeBackgroundServiceOptionsSuperClass() {
		objParentClass = this.getClass();
	} // public JadeBackgroundServiceOptionsSuperClass

	public JadeBackgroundServiceOptionsSuperClass(final JSListener pobjListener) {
		this();
		this.registerMessageListener(pobjListener);
	} // public JadeBackgroundServiceOptionsSuperClass

	//
	public JadeBackgroundServiceOptionsSuperClass(final HashMap<String, String> JSSettings) throws Exception {
		this();
		this.setAllOptions(JSSettings);
	} // public JadeBackgroundServiceOptionsSuperClass (HashMap JSSettings)

	/**
	 * \brief getAllOptionsAsString - liefert die Werte und Beschreibung aller
	 * Optionen als String
	 *
	 * \details
	 * 
	 * \see toString 
	 * \see toOut
	 */
	private String getAllOptionsAsString() {
		@SuppressWarnings("unused") final String conMethodName = conClassName + "::getAllOptionsAsString";
		String strT = conClassName + "\n";
		final StringBuffer strBuffer = new StringBuffer();
		// strT += IterateAllDataElementsByAnnotation(objParentClass, this,
		// JSOptionsClass.IterationTypes.toString, strBuffer);
		// strT += IterateAllDataElementsByAnnotation(objParentClass, this, 13,
		// strBuffer);
		strT += this.toString(); // fix
		//
		return strT;
	} // private String getAllOptionsAsString ()

	/**
	 * \brief setAllOptions - übernimmt die OptionenWerte aus der HashMap
	 *
	 * \details In der als Parameter anzugebenden HashMap sind Schlüssel (Name)
	 * und Wert der jeweiligen Option als Paar angegeben. Ein Beispiel für den
	 * Aufbau einer solchen HashMap findet sich in der Beschreibung dieser
	 * Klasse (\ref TestData "setJobSchedulerSSHJobOptions"). In dieser Routine
	 * werden die Schlüssel analysiert und, falls gefunden, werden die
	 * dazugehörigen Werte den Properties dieser Klasse zugewiesen.
	 *
	 * Nicht bekannte Schlüssel werden ignoriert.
	 *
	 * \see JSOptionsClass::getItem
	 *
	 * @param pobjJSSettings
	 * @throws Exception
	 */
	@Override public void setAllOptions(final HashMap<String, String> pobjJSSettings) {
		@SuppressWarnings("unused") final String conMethodName = conClassName + "::setAllOptions";
		flgSetAllOptions = true;
		objSettings = pobjJSSettings;
		super.Settings(objSettings);
		super.setAllOptions(pobjJSSettings);
		flgSetAllOptions = false;
	} // public void setAllOptions (HashMap <String, String> JSSettings)

	/**
	 * \brief CheckMandatory - prüft alle Muss-Optionen auf Werte
	 *
	 * \details
	 * @throws Exception
	 *
	 * @throws Exception
	 * - wird ausgelöst, wenn eine mandatory-Option keinen Wert hat
	 */
	@Override public void CheckMandatory() throws JSExceptionMandatoryOptionMissing //
			, Exception {
		try {
			super.CheckMandatory();
		}
		catch (Exception e) {
			throw new JSExceptionMandatoryOptionMissing(e.toString());
		}
	} // public void CheckMandatory ()

	/**
	 *
	 * \brief CommandLineArgs - übernehmen der Options/Settings aus der
	 * Kommandozeile
	 *
	 * \details Die in der Kommandozeile beim Starten der Applikation
	 * angegebenen Parameter werden hier in die HashMap übertragen und danach
	 * den Optionen als Wert zugewiesen.
	 *
	 * \return void
	 *
	 * @param pstrArgs
	 * @throws Exception
	 */
	@Override public void CommandLineArgs(final String[] pstrArgs) {
		super.CommandLineArgs(pstrArgs);
		this.setAllOptions(super.objSettings);
	}
} // public class JadeBackgroundServiceOptionsSuperClass