package com.sos.jade.converter;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import org.junit.Ignore;
import org.junit.Test;

public class IniToXmlConverterTest {
	
	public IniToXmlConverterTest() {
		//
	}
	
	private String outputDir = "C:/Temp/jade/test";
	private String inputDir = "R:/backup/projects/YADE/testing/1.10/configurations";
	
	private DirectoryStream.Filter<Path> subFolders = new DirectoryStream.Filter<Path>() {

		@Override
		public boolean accept(Path entry) throws IOException {
			return Files.isDirectory(entry);
		}
		
	};

	@Ignore
	@Test
	public void testCredentialStore() throws JAXBException {
		String settingsFile = inputDir + "/Integration/04_01_credential_store.ini";
		String[] options = {"-settings=" + settingsFile,"-outputDir=" + outputDir + "/Integration"}; 
		IniToXmlConverter.main(options);
	}
	
	@Ignore
	@Test
	public void testTestSuite() throws JAXBException, IOException {
		Path testSuiteLocation = Paths.get(inputDir);
		Path outputPath = null;
		DirectoryStream<Path> subFolderStream = Files.newDirectoryStream(testSuiteLocation, subFolders);
		DirectoryStream<Path> iniFileStream = null;
		for (Path subFolder : subFolderStream) {
			iniFileStream = Files.newDirectoryStream(subFolder, "*.ini");
			for (Path iniFile : iniFileStream) {
				outputPath = Paths.get(outputDir, subFolder.getFileName().toString());
				String[] options = { "-settings=" + iniFile.toString(),"-outputDir=" + outputPath.toString() };
				IniToXmlConverter.main(options);
			}
		}
	}
	
	@Ignore
	@Test
	public void testConverterTestData() throws JAXBException, IOException {
		Path testSuiteLocation = Paths.get(inputDir + "/../../../schema/converter/configurations").toRealPath();
		Path outputPath = null;
		DirectoryStream<Path> subFolderStream = Files.newDirectoryStream(testSuiteLocation, subFolders);
		DirectoryStream<Path> iniFileStream = null;
		for (Path subFolder : subFolderStream) {
			iniFileStream = Files.newDirectoryStream(subFolder, "*.ini");
			for (Path iniFile : iniFileStream) {
				outputPath = Paths.get(outputDir, subFolder.getFileName().toString());
				String[] options = { "-settings=" + iniFile.toString(),"-outputDir=" + outputPath.toString() };
				IniToXmlConverter.main(options);
			}
		}
	}
	
	@Ignore
	@Test
	public void testAlternatives() throws JAXBException {
		String settingsFile = inputDir + "/Integration/05_01_alternative_fragments.ini";
		String[] options = {"-settings=" + settingsFile,"-outputDir=" + outputDir + "/Integration"}; 
		IniToXmlConverter.main(options);
	}
	
}
