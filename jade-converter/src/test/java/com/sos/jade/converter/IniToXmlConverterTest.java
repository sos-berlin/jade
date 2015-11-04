package com.sos.jade.converter;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import org.junit.Test;

public class IniToXmlConverterTest {
	
	public IniToXmlConverterTest() {
		//
	}
	
	private DirectoryStream.Filter<Path> subFolders = new DirectoryStream.Filter<Path>() {

		@Override
		public boolean accept(Path entry) throws IOException {
			return Files.isDirectory(entry);
		}
		
	};

	@Test
	public void testMain() throws JAXBException {
		String settingsFile = "C:/Temp/jade/settings/oh_settings.ini";
		String[] options = {"-settings="+settingsFile,"-outputDir=C:/Temp/jade/test"}; 
		IniToXmlConverter.main(options);
	}
	
	@Test
	public void testCredentialStore() throws JAXBException {
		String settingsFile = "R:/backup/projects/YADE/testing/1.10/configurations/Integration/04_01_credential_store.ini";
		String[] options = {"-settings="+settingsFile,"-outputDir=C:/Temp/jade/test/Integration"}; 
		IniToXmlConverter.main(options);
	}
	
	@Test
	public void testTestSuite() throws JAXBException, IOException {
		Path testSuiteLocation = Paths.get("R:/backup/projects/YADE/testing/1.10/configurations");
		String outputParentDir = "C:/Temp/jade/test";
		Path outputPath = null;
		DirectoryStream<Path> subFolderStream = Files.newDirectoryStream(testSuiteLocation, subFolders);
		DirectoryStream<Path> iniFileStream = null;
		for (Path subFolder : subFolderStream) {
			iniFileStream = Files.newDirectoryStream(subFolder, "*.ini");
			for (Path iniFile : iniFileStream) {
				outputPath = Paths.get(outputParentDir, subFolder.getFileName().toString());
				String[] options = { "-settings=" + iniFile.toString(),"-outputDir=" + outputPath.toString() };
				IniToXmlConverter.main(options);
			}
		}
	}
	
	@Test
	public void testAlternatives() throws JAXBException {
		String settingsFile = "R:/backup/projects/YADE/testing/1.10/configurations/Integration/05_01_alternative_fragments.ini";
		String[] options = {"-settings="+settingsFile,"-outputDir=C:/Temp/jade/test/Integration"}; 
		IniToXmlConverter.main(options);
	}
	
}
