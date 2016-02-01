package com.sos.DataExchange.Options;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.sos.DataExchange.jaxb.configuration.ConfigurationElement;
import com.sos.DataExchange.jaxb.configuration.JADEParam;
import com.sos.DataExchange.jaxb.configuration.JADEParamValues;
import com.sos.DataExchange.jaxb.configuration.JADEParams;
import com.sos.DataExchange.jaxb.configuration.JADEProfile;
import com.sos.DataExchange.jaxb.configuration.JADEProfileIncludes;
import com.sos.DataExchange.jaxb.configuration.JADEProfiles;
import com.sos.DataExchange.jaxb.configuration.Value;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.VirtualFileSystem.Options.SOSFTPOptions;
import com.sos.i18n.annotation.I18NResourceBundle;

@I18NResourceBundle(baseName = "SOSVirtualFileSystem", defaultLocale = "en")
public class JADEOptions extends SOSFTPOptions {

	private static final long	serialVersionUID	= -5788970501747521212L;
	private static final String	CONFIG_FILE_EXTENSION	= ".jadeconf";
	private static final Logger LOGGER = Logger.getLogger(JADEOptions.class);
	
	public JADEOptions() {
		super();
	}
		
	public JADEOptions(final HashMap<String, String> settings) throws Exception {
		super(settings);
	}
	
	public JADEOptions(final enuTransferTypes source, final enuTransferTypes target) {
		super(source, target);
	}
	
	@Override
	public HashMap<String, String> ReadSettingsFile() {
		Properties properties = new Properties();
		HashMap<String, String> map = new HashMap<>();

		String file = settings.Value();
		if (file.endsWith(CONFIG_FILE_EXTENSION)) {
			try {
				JAXBContext jc = JAXBContext.newInstance(ConfigurationElement.class);
				Unmarshaller u = jc.createUnmarshaller();
				ConfigurationElement config = (ConfigurationElement) u.unmarshal(new FileInputStream(settings.Value()));
				Vector<Object> profileOrProfiles = (Vector<Object>) config.getIncludeOrProfileOrProfiles();
				searchXMLProfile(properties, profileOrProfiles, "globals");
				searchXMLProfile(properties, profileOrProfiles, profile.Value());
			}
			catch (JAXBException ex) {
				LOGGER.error(ex.getLocalizedMessage());
			}
			catch (IOException ex) {
				LOGGER.error(ex.getLocalizedMessage());
			}
		}
		else {  // TODO any file extension is allowed for the ini-configuration file
			map = super.ReadSettingsFile();
		}
		return map;
	}
	
	private void processXMLProfile(final Properties properties, final JADEProfile profile) {
		for (Object obj : profile.getIncludeOrIncludesOrParams()) {
			if (obj instanceof JADEProfileIncludes) {
			}
			else {
				if (obj instanceof JADEParam) {
					processXMLParam(properties, (JADEParam) obj);
				}
				else {
					if (obj instanceof JADEParams) {
						processXMLParams(properties, (JADEParams) obj);
					}
				}
			}
		}
	}

	private void searchXMLProfile(final Properties properties, final Vector<Object> profileOrProfiles, final String profileName) {
		LOGGER.debug("Profile = " + profile.Value());
		for (Object object : profileOrProfiles) {
			if (object instanceof JADEProfile) {
				JADEProfile profile = (JADEProfile) object;
				String name = profile.getName();
				LOGGER.debug(" ... Profile Name = " + name);
				if (profile.getName().equalsIgnoreCase(profileName)) {
					processXMLProfile(properties, profile);
					break;
				}
			}
			else {
				if (object instanceof JADEProfiles) {
					Vector<Object> lstProfileOrProfiles = (Vector<Object>) ((JADEProfiles) object).getIncludeOrProfile();
					searchXMLProfile(properties, lstProfileOrProfiles, profileName);
					break;
				}
			}
		}
	}

	private void processXMLParams(final Properties properties, final JADEParams params) {
		for (Object obj : params.getParamOrParams()) {
			if (obj instanceof JADEParam) {
				processXMLParam(properties, (JADEParam) obj);
			}
			else {
				if (obj instanceof JADEParams) {
					processXMLParams(properties, (JADEParams) obj);
				}
			}
		}
	}

	private void processXMLParam(final Properties properties, final JADEParam param) {
		JADEParam objParam = param;
		System.out.println(" ... Param name = " + objParam.getName());
		if (objParam.getValue() != null) {
			properties.put(objParam.getName(), objParam.getValue());
		}
		else {
			List<Object> includeOrValues = objParam.getIncludeOrValues();
			for (Object obj : includeOrValues) {
				if (obj instanceof JADEParamValues) {
					JADEParamValues paramValues = (JADEParamValues) obj;
					for (Object objV2 : paramValues.getValue()) {
						Value value = (Value) objV2;
						System.out.println(String.format(" +++ value '%1$s' with prefix '%2$s'", value.getVal(), value.getPrefix()));
						String val = value.getVal();
						String prefix = value.getPrefix();
						if (prefix != null) {
							val = prefix + "_" + objParam.getName();
						}
						else {
							prefix = objParam.getName();
						}
						properties.put(prefix, val);
						LOGGER.debug("Put to Properties Param = " + prefix + ", Value = " + val);
					}
				}
			}
		}
	}

	
	public JADEOptions getClone() {
		JADEOptions options = new JADEOptions();
		options.CommandLineArgs(this.getOptionsAsCommandLine());
		return options;
	}

}
