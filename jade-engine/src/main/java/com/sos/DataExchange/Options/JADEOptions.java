package com.sos.DataExchange.Options;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import com.sos.DataExchange.converter.JadeXml2IniConverter;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.VirtualFileSystem.Options.SOSFTPOptions;
import com.sos.i18n.annotation.I18NResourceBundle;

@I18NResourceBundle(baseName = "SOSVirtualFileSystem", defaultLocale = "en")
public class JADEOptions extends SOSFTPOptions {

    private static final long serialVersionUID = -5788970501747521212L;
    private static final Logger LOGGER = Logger.getLogger(JADEOptions.class);
    private static final String SCHEMA_RESSOURCE_NAME = "YADE_configuration_v1_0.xsd";
    
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
    public HashMap<String, String> readSettingsFile() {
        String config = settings.getValue();
        if (config.endsWith(".xml")) {
        	convertXml2Ini(config);
        }        
        return super.readSettingsFile();
    }

    private void convertXml2Ini(String xmlFile){
    	InputStream schemaStream = null;
    	Path tmpIniFile = null;
    	try{
    		schemaStream = loadSchemaFromJar();
    		if(schemaStream == null){
    			throw new Exception(String.format("schema(%s) stream from the jar file is null",SCHEMA_RESSOURCE_NAME));
    		}
    		
    		tmpIniFile = Files.createTempFile("sos.yade",".ini");
    		JadeXml2IniConverter converter = new JadeXml2IniConverter();
            converter.process(new InputSource(schemaStream), xmlFile, tmpIniFile.toString());
    		
            this.settings.setValue(tmpIniFile.toString());
    	}
    	catch(Exception e){
    		LOGGER.error(e);
    		throw new JobSchedulerException(e);
    	}
    	finally{
    		if(schemaStream != null){
    			try {schemaStream.close();} catch (IOException e) {}
    		}
    		if(tmpIniFile!=null){
    			tmpIniFile.toFile().deleteOnExit();
    		}
    	}
    }
    
    private InputStream loadSchemaFromJar(){
    	ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
		    cl = Class.class.getClassLoader();
		}
		return cl.getResourceAsStream(SCHEMA_RESSOURCE_NAME);
    }
    
    public JADEOptions getClone() {
        JADEOptions options = new JADEOptions();
        options.commandLineArgs(this.getOptionsAsCommandLine());
        return options;
    }

}
