package com.sos.DataExchange;

import java.util.UUID;

import org.apache.log4j.Logger;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.i18n.annotation.I18NResourceBundle;

/**
 *	
 * @author Robert Ehrlich
 *
 */
@I18NResourceBundle(baseName = "SOSDataExchange", defaultLocale = "en")
public class Jade4DMZ extends JadeBaseEngine implements Runnable {
	private final String className = Jade4DMZ.class.getSimpleName();
	private static final Logger logger = Logger.getLogger(Jade4DMZ.class);
	private SOSFileList		fileList   = null;
	private String uuid = null;
	private String sourceListFilename = null;

	private enum Operation {
		copyToInternet, copyFromInternet
	}

	/**
	 * 
	 */
	public void Execute() {
		String dir = normalizeDirectoryPath(objOptions.jump_dir.Value());

		String uuid 	= "jade-dmz-" + getUUID();
		String subDir  	= dir + uuid;
		Operation operation = null;
		try {
			if (objOptions.operation.isOperationCopyToInternet()
				|| objOptions.operation.isOperationSendUsingDMZ()) {
				
				operation = Operation.copyToInternet;
			} 
			else if (objOptions.operation.isOperationCopyFromInternet()
				|| objOptions.operation.isOperationReceiveUsingDMZ()) {
				
				operation = Operation.copyFromInternet;
			} 
			else {
				throw new JobSchedulerException(Messages.getMsg("Jade4DMZ-E-001"));
			}

			transfer(operation, subDir);
			
		} catch (JobSchedulerException e) {
			throw e;
		} catch (Exception e) {
			// oh: 2014-1-19 throw exception because of
			// https://change.sos-berlin.com/browse/JADE-224
			// https://change.sos-berlin.com/browse/JADE-225
			throw new JobSchedulerException("Transfer failed", e);
		}
	}
	
	/**
	 * 
	 * @param operation
	 * @param dir
	 */
	private void transfer(Operation operation, String dir) {
		logger.info(String.format("operation = %s, dir = %s",
				operation, dir));

		JadeEngine jade = null;
		fileList = null;
		try {
			jade = new JadeEngine(getTransferOptions(operation, dir));
			jade.Execute();
			fileList = jade.getFileList();
		} 
		catch (Exception e) {
			throw new JobSchedulerException("Transfer failed", e);
		} 
		finally {
			removeDirOnDMZ(jade, operation, dir);
			
			try {if (jade != null) {jade.Logout();}	}
			catch (Exception e) {logger.warn(String.format("Logout failed: %s",e.toString()));}
		}
	}
	
	/**
	 * Transfer from Intranet/Internet to DMZ
	 * 
	 * @param operation
	 * @param dir
	 * @return
	 */
	private JADEOptions getTransferOptions(Operation operation, String dir) throws Exception{
		logger.debug(String.format("operation = %s, dir = %s",
				operation, dir));

		// Source oder Target Options
		SOSConnection2OptionsAlternate destinationOptions = new SOSConnection2OptionsAlternate();
		//destinationOptions.protocol.Value(objOptions.jump_protocol.Value());
		destinationOptions.protocol.Value("sftp"); //It works only with sftp
		destinationOptions.host.Value(objOptions.jump_host.Value());
		destinationOptions.port.Value(objOptions.jump_port.Value());
		destinationOptions.user.Value(objOptions.jump_user.Value());
		destinationOptions.password.Value(objOptions.jump_password.Value());
		destinationOptions.ssh_auth_method.Value(objOptions.jump_ssh_auth_method.Value());
		destinationOptions.ssh_auth_file.Value(objOptions.jump_ssh_auth_file.Value());

		destinationOptions.proxy_protocol.Value(objOptions.jump_proxy_protocol.Value());
		destinationOptions.proxy_host.Value(objOptions.jump_proxy_host.Value().trim());
		destinationOptions.proxy_port.Value(objOptions.jump_proxy_port.Value().trim());
		destinationOptions.proxy_user.Value(objOptions.jump_proxy_user.Value().trim());
		destinationOptions.proxy_password.Value(objOptions.jump_proxy_password.Value());
		
		JADEOptions options = null;
		if (operation.equals(Operation.copyToInternet)) {
			//2) From DMZ to Internet as PostTransferCommands
			JADEOptions jadeOnDMZOptions = createTransferFromDMZOptions(dir);
			jadeOnDMZOptions.TargetDir.Value("");
			jadeOnDMZOptions.log_filename.Value("");
			SOSConnection2OptionsAlternate jadeOnDMZSourceOptions = setTransferFromDMZDestinationOptions("source_",new SOSConnection2OptionsAlternate(),objOptions.Source());
			SOSConnection2OptionsAlternate jadeOnDMZTargetOptions = objOptions.Target();
			jadeOnDMZTargetOptions.log_filename.Value("");
			jadeOnDMZOptions.getConnectionOptions().Source(jadeOnDMZSourceOptions);
			jadeOnDMZOptions.getConnectionOptions().Target(jadeOnDMZTargetOptions);
			
			//1) From Intranet to DMZ
			options = createTransferToDMZOptions(dir);
			destinationOptions.PostTransferCommands.Value(getJadeOnDMZCommand(jadeOnDMZOptions));
			destinationOptions = setDestinationOptionsPrefix("target_",destinationOptions);
			options.getConnectionOptions().Source(objOptions.Source());
			options.getConnectionOptions().Target(destinationOptions);
		} 
		else {
			//1) From Internet to DMZ as PreTransferCommands
			JADEOptions jadeOnDMZOptions = createTransferToDMZOptions(dir);
			jadeOnDMZOptions.log_filename.Value("");
			SOSConnection2OptionsAlternate jadeOnDMZSourceOptions = objOptions.Source();
			jadeOnDMZSourceOptions.log_filename.Value("");
			SOSConnection2OptionsAlternate jadeOnDMZTargetOptions = setDestinationOptionsPrefix("target_",destinationOptions);
			jadeOnDMZOptions.getConnectionOptions().Source(jadeOnDMZSourceOptions);
			jadeOnDMZOptions.getConnectionOptions().Target(jadeOnDMZTargetOptions);
			
			//3) Remove source files at Internet as PostTransferCommands
			jadeOnDMZOptions.remove_files.value(false);
			if (objOptions.remove_files.value()) {
				jadeOnDMZOptions.ResultSetFileName.Value(getSourceListFilename());
				destinationOptions.PostTransferCommands.Value(getJadeOnDMZCommand4RemoveSource(jadeOnDMZOptions));
			}
			
			//2) From DMZ to Intranet
			options = createTransferFromDMZOptions(dir);
			options.TargetDir.Value(dir);
			destinationOptions.PreTransferCommands.Value(getJadeOnDMZCommand(jadeOnDMZOptions));
			destinationOptions = setTransferFromDMZDestinationOptions("source_",destinationOptions, objOptions.Source());
			destinationOptions = setDestinationOptionsPrefix("source_",destinationOptions);
			options.getConnectionOptions().Source(destinationOptions);
			options.getConnectionOptions().Target(objOptions.Target());
		}

		return options;
	}
	
	/**
	 * 
	 * @param prefix
	 * @param options
	 * @return
	 */
	private SOSConnection2OptionsAlternate setDestinationOptionsPrefix(String prefix,SOSConnection2OptionsAlternate options){
		options.protocol.setPrefix(prefix);
		options.host.setPrefix(prefix);
		options.port.setPrefix(prefix);
		options.user.setPrefix(prefix);
		options.password.setPrefix(prefix);
		options.ssh_auth_method.setPrefix(prefix);
		options.ssh_auth_file.setPrefix(prefix);

		options.proxy_protocol.setPrefix(prefix);
		options.proxy_host.setPrefix(prefix);
		options.proxy_port.setPrefix(prefix);
		options.proxy_user.setPrefix(prefix);
		options.proxy_password.setPrefix(prefix);
		
		options.PreTransferCommands.setPrefix(prefix);
		options.PostTransferCommands.setPrefix(prefix);
		
	return options;
	}
	
	/**
	 * 
	 * @param options
	 * @param sourceOptions
	 * @return
	 */
	private SOSConnection2OptionsAlternate setTransferFromDMZDestinationOptions(String prefix, SOSConnection2OptionsAlternate options, SOSConnection2OptionsAlternate sourceOptions){
		options.replacement.Value(sourceOptions.replacement.Value());
		options.replacement.setPrefix(prefix);
		
		options.replacing.Value(sourceOptions.replacing.Value());
		options.replacing.setPrefix(prefix);
		
		return options;
	}
	
	/**
	 * Cloning and overwriting some settings 
	 * 
	 * @param dir
	 * @return
	 */
	private JADEOptions createTransferFromDMZOptions(String dir){
		
		JADEOptions options = objOptions.getClone();
		options.operation.Value("copy");
		options.protocol.Value("local");
		options.host.Value(objOptions.jump_host.Value());
		
		options.SourceDir.Value(dir);
		
		options.file_spec.Value(".*");
		options.file_path.Value("");
		options.force_files.value(false);
		options.verbose.value(objOptions.verbose.value());
		
		options.settings.Value("");
		options.profile.Value("");
		options.include.Value("");
		
		options.BackgroundServiceHost.Value("");
		options.BackgroundServicePort.Value("");
		options.BackgroundServiceJobChainName.Value("");
		options.CreateResultList.Value("false");
		options.ResultSetFileName.Value("");
		options.ResultSetFileName.setNotDirty();
		
		options.settings.setNotDirty();
		options.ClearJumpParameter();
		
		options.getConnectionOptions().Source(null);
		options.getConnectionOptions().Target(null);
		
	return options;	
	}
	
	
	/**
	 * Transfer from Intranet/Internet to DMZ without changes
	 * 
	 * @param targetDir
	 * @return
	 */
	private JADEOptions createTransferToDMZOptions(String targetDir){
		
		JADEOptions options = new JADEOptions();
		options.operation.Value("copy");
		
		options.SourceDir.Value(objOptions.Source().Directory.Value());
		options.TargetDir.Value(targetDir);
		
		options.transactional.value(true);
		options.remove_files.value(objOptions.remove_files.value());
		options.compress_files.value(false);
		options.append_files.value(false);
		options.CreateSecurityHashFile.value(false);
		options.createFoldersOnTarget.value(true);
		options.replacement.Value("");
		options.replacing.Value("");
		options.verbose.value(objOptions.verbose.value());
				
		options.file_spec.Value(objOptions.file_spec.Value());
		options.file_path.Value(objOptions.file_path.Value());
		
		options.FileListName.Value(objOptions.FileListName.Value());
		options.FileAgeMaximum.Value(objOptions.FileAgeMaximum.Value());
		options.FileAgeMinimum.Value(objOptions.FileAgeMinimum.Value());
		options.FileName.Value(objOptions.FileName.Value());
		options.FileNameEncoding.Value(objOptions.FileNameEncoding.Value());
		options.FileNamePatternRegExp.Value(objOptions.FileNamePatternRegExp.Value());
		options.FileNameRegExp.Value(objOptions.FileNameRegExp.Value());
		options.FileSizeMaximum.Value(objOptions.FileSizeMaximum.Value());
		options.FileSizeMinimum.Value(objOptions.FileSizeMinimum.Value());
		options.ResultSetFileName = objOptions.ResultSetFileName;
		
	return options;
	}
	
	/**
	 * 
	 * @param options
	 * @return
	 */
	private String getJadeOnDMZCommand(JADEOptions options) {
		// https://change.sos-berlin.com/browse/JADE-297
		options.user.DefaultValue("");
		options.file.setNotDirty();
		options.Source().user.DefaultValue("");
		options.Target().user.DefaultValue("");
		// Otherwise this command contains the getJadeOnDMZCommand4RemoveSource() command too.
		options.Target().PostTransferCommands.setNotDirty();
		
		StringBuffer command = new StringBuffer(objOptions.jump_command.Value()+ " ");
		command.append("-SendTransferHistory=false ");
		command.append(options.getOptionsAsQuotedCommandLine());
		command.append(options.Source().getOptionsAsQuotedCommandLine());
		command.append(options.Target().getOptionsAsQuotedCommandLine());
		
		options.Target().PostTransferCommands.setDirty();
	return command.toString();
	}
	
	/**
	 * 
	 * @param options
	 * @return
	 */
	private String getJadeOnDMZCommand4RemoveSource(JADEOptions options) {
		JADEOptions opts = new JADEOptions();
		opts.operation.Value("delete");
		opts.FileListName.Value(getSourceListFilename());
		// https://change.sos-berlin.com/browse/JADE-297
		opts.user.DefaultValue("");
		options.user.DefaultValue("");
		options.Source().user.DefaultValue("");
		options.Source().Directory.setNotDirty();
		
		StringBuffer command = new StringBuffer(objOptions.jump_command.Value()+ " ");
		command.append("-SendTransferHistory=false ");
		command.append(opts.getOptionsAsQuotedCommandLine());
		command.append(options.Source().getOptionsAsQuotedCommandLine());
		
		options.Source().Directory.setDirty();
		return command.toString();
	}
	
	/**
	 * wird nicht als PostProcessorCommands deklariert, wegen unterschiedlichen Fällen - Dateien nicht gefunden, Exception etc
	 * 
	 * @param jade
	 * @param operation
	 * @param dir
	 */
	private void removeDirOnDMZ(JadeEngine jade, Operation operation,String dir){
		try{
			if(jade == null){
				return;
			}
			
			String command = getRemoveDirCommand(dir);
			logger.info(command);
			if(operation.equals(Operation.copyToInternet)){
				jade.executeCommandOnTarget(command);
			}
			else{
				jade.executeCommandOnSource(command);
			}
		}
		catch(Exception ex){
			logger.warn(String.format("%s",ex.toString()));
		}
	}
	
	/**
	 * 
	 * @param dir
	 * @return
	 */
	private String getRemoveDirCommand(String dir){
		if(objOptions.jump_platform.isWindows()) {
			return "if exist \"" + getSourceListFilename() + "\" del \"" + getSourceListFilename() + "\";rmdir \""+dir+"\" /s /q";
		}
		else {
			return "rm -f -R " + dir + "*";
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public SOSFileList getFileList(){
		return fileList;
	}
	
	/**
	 * 
	 */
	@Override
	public JADEOptions Options() {
		if (objOptions == null) {
			objOptions = new JADEOptions();
		}
		return objOptions;
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		@SuppressWarnings("unused")
		final String method = className + "::run";
		try {
			Execute();
		} catch (JobSchedulerException e) {
			throw e;
		} catch (Exception e) {
			throw new JobSchedulerException("abort", e);
		}
	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	private String normalizeDirectoryPath(String path) {
		path = path.replaceAll("\\\\", "/");
		return path.endsWith("/") ? path : path + "/";
	}
	
	/**
	 * 
	 * @return
	 */
	private String getUUID() {
		if (uuid == null) {
			uuid = UUID.randomUUID().toString();
		}
		return uuid;
	}

	/**
	 * 
	 * @return
	 */
	public String getSourceListFilename() {
		if (sourceListFilename == null) {
			sourceListFilename = normalizeDirectoryPath(objOptions.jump_dir.Value()) + "jade-dmz-" + getUUID() + ".source.tmp";
		}
		return sourceListFilename;
	}

}
