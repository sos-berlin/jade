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
		String dir = normalizeDirectoryPath(Options().jump_dir.Value());

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
			throw new JobSchedulerException("Transfer failed", e);
		}
	}
	
	/**
	 * 
	 * @param operation
	 * @param dir
	 */
	private void transfer(Operation operation, String dir) {
		logger.info(String.format("operation = %s, jump dir = %s",
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
	
	private SOSConnection2OptionsAlternate createJumpOptions(){
		SOSConnection2OptionsAlternate options = new SOSConnection2OptionsAlternate();
		//destinationOptions.protocol.Value(objOptions.jump_protocol.Value());
		options.protocol.Value("sftp"); //It works only with sftp
		options.host.Value(objOptions.jump_host.Value());
		options.port.Value(objOptions.jump_port.Value());
		options.user.Value(objOptions.jump_user.Value());
		options.password.Value(objOptions.jump_password.Value());
		options.ssh_auth_method.Value(objOptions.jump_ssh_auth_method.Value());
		options.ssh_auth_file.Value(objOptions.jump_ssh_auth_file.Value());
		options.strictHostKeyChecking.value(objOptions.jump_strict_hostkey_checking.value());
		
		return options;
	}
	
	private SOSConnection2OptionsAlternate setJumpProxy(SOSConnection2OptionsAlternate options){
		options.proxy_protocol.Value(objOptions.jump_proxy_protocol.Value());
		options.proxy_host.Value(objOptions.jump_proxy_host.Value().trim());
		options.proxy_port.Value(objOptions.jump_proxy_port.Value().trim());
		options.proxy_user.Value(objOptions.jump_proxy_user.Value().trim());
		options.proxy_password.Value(objOptions.jump_proxy_password.Value());
	
		return options;
	}
	
	private JADEOptions createPostTransferOptions(String dir){
		//From DMZ to Internet as PostTransferCommands
		JADEOptions options = new JADEOptions();
		options = addJADEOptionsForTarget(options);
		SOSConnection2OptionsAlternate sourceOptions = setLocalOptionsPrefixed("source_",dir);
		SOSConnection2OptionsAlternate targetOptions = objOptions.Target();
		options.getConnectionOptions().Source(sourceOptions);
		options.getConnectionOptions().Target(targetOptions);
		return options;
	}
	
	private JADEOptions createPreTransferOptions(String dir){
		//From Internet to DMZ as PreTransferCommands
		JADEOptions options = new JADEOptions();
		options = addJADEOptionsForSource(options);
		SOSConnection2OptionsAlternate sourceOptions = objOptions.Source();
		SOSConnection2OptionsAlternate targetOptions = setLocalOptionsPrefixed("target_",dir);
		options.getConnectionOptions().Source(sourceOptions);
		options.getConnectionOptions().Target(targetOptions);
		
		//Remove source files at Internet as PostTransferCommands
		options.remove_files.value(false);
		if (objOptions.remove_files.value()) {
			options.ResultSetFileName.Value(getSourceListFilename());
		}
		
		return options;
	}
	
	/**
	 * Transfer from Intranet/Internet to DMZ
	 * 
	 * @param operation
	 * @param dir
	 * @return
	 */
	private JADEOptions getTransferOptions(Operation operation, String dir) throws Exception{
		logger.debug(String.format("operation = %s, jump dir = %s", operation, dir));

		JADEOptions options = null;
		JADEOptions jumpCommandOptions;
		SOSConnection2OptionsAlternate jumpOptions = createJumpOptions();
		jumpOptions = setJumpProxy(jumpOptions);
		
		if (operation.equals(Operation.copyToInternet)) {
			options = createTransferToDMZOptions(dir);
			
			jumpCommandOptions = createPostTransferOptions(dir);
			jumpOptions.PostTransferCommands.Value(getJadeOnDMZCommand(jumpCommandOptions));
			jumpOptions = setDestinationOptionsPrefix("target_",jumpOptions);
			
			options.getConnectionOptions().Source(objOptions.Source());
			options.getConnectionOptions().Target(jumpOptions);
		} 
		else {
			options = createTransferFromDMZOptions(dir);
			
			jumpCommandOptions = createPreTransferOptions(dir);
			jumpOptions.PreTransferCommands.Value(getJadeOnDMZCommand(jumpCommandOptions));
			if (objOptions.remove_files.value()) {
				//Remove source files at Internet as PostTransferCommands.
				//See createPreTransferOptions
				jumpOptions.PostTransferCommands.Value(getJadeOnDMZCommand4RemoveSource(jumpCommandOptions));
			}
			jumpOptions = setDestinationOptionsPrefix("source_",jumpOptions);
			
			options.getConnectionOptions().Source(jumpOptions);
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
	 * @param prefix
	 * @param dir
	 * @return
	 */
	private SOSConnection2OptionsAlternate setLocalOptionsPrefixed(String prefix,String dir){
		SOSConnection2OptionsAlternate options = new SOSConnection2OptionsAlternate();
		options.protocol.Value("local");
		options.host.Value(objOptions.jump_host.Value());
		options.Directory.Value(dir);
		options.protocol.setPrefix(prefix);
		options.host.setPrefix(prefix);
		options.Directory.setPrefix(prefix);
		
		return options;
	}
	
	/**
	 * Settings for receive from DMZ 
	 * 
	 * @param dir
	 * @return
	 */
	private JADEOptions createTransferFromDMZOptions(String dir){
		
		JADEOptions options = new JADEOptions();
		options = addJADEOptionsOnClient(options);
		options = addJADEOptionsForTarget(options);
		
		options.SourceDir.Value(dir);
		options.TargetDir.Value(objOptions.Target().Directory.Value()); 
		
		return options;	
	}
	
	/**
	 * Transfer from Intranet/Internet to DMZ without changes
	 * 
	 * @param targetDir
	 * @return
	 */
	private JADEOptions createTransferToDMZOptions(String dir){
		
		JADEOptions options = new JADEOptions();
		options = addJADEOptionsOnClient(options);
		options = addJADEOptionsForSource(options);
		
		options.SourceDir.Value(objOptions.Source().Directory.Value());
		options.TargetDir.Value(dir);
		options.remove_files = objOptions.remove_files;
		
		return options;
	}
	
	/**
	 * 
	 * @param options
	 * @return
	 */
	private JADEOptions addJADEOptionsForSource(JADEOptions options) {
		
		options.operation.Value("copy");
		options.transactional.value(true);
		
		options.atomic_prefix = objOptions.atomic_prefix; 
		options.atomic_suffix = objOptions.atomic_suffix;
		options.BufferSize = objOptions.BufferSize;
		
		options.CheckSteadyCount = objOptions.CheckSteadyCount;
		options.CheckSteadyStateInterval = objOptions.CheckSteadyStateInterval;
		options.CheckSteadyStateOfFiles = objOptions.CheckSteadyStateOfFiles;
		
		options.pollingWait4SourceFolder = objOptions.pollingWait4SourceFolder;
		//not supported: options.PollingServer = objOptions.PollingServer;
		options.pollingEndAt = objOptions.pollingEndAt;
		options.PollingServerPollForever = objOptions.PollingServerPollForever;
		options.pollingServerDuration = objOptions.pollingServerDuration;
		options.PollKeepConnection = objOptions.PollKeepConnection;
		options.poll_interval = objOptions.poll_interval;
		options.poll_minfiles = objOptions.poll_minfiles;
		options.PollingDuration = objOptions.PollingDuration;
		options.poll_timeout = objOptions.poll_timeout;
		
		options.MaxFiles = objOptions.MaxFiles;
		options.FileListName = objOptions.FileListName; 
		//special handling: options.SourceDir = objOptions.SourceDir;
		options.file_path = objOptions.file_path; 
		options.file_spec = objOptions.file_spec;
		options.force_files = objOptions.force_files;
		options.recursive = objOptions.recursive;
		
		options.skip_transfer = objOptions.skip_transfer;
		//special handling: options.remove_files = objOptions.remove_files;
		options.keep_modification_date = objOptions.keep_modification_date;
		options.verbose = objOptions.verbose;
		options.zero_byte_transfer = objOptions.zero_byte_transfer;
		
		//not supported: options.CreateSecurityHash = objOptions.CreateSecurityHash;
		//not supported: options.SecurityHashType = objOptions.SecurityHashType;

		return options;
	}
	
	/**
	 * 
	 * @param options
	 * @return
	 */
	private JADEOptions addJADEOptionsForTarget(JADEOptions options) {
		
		options.operation.Value("copy");
		options.transactional.value(true);
		options.file_spec.Value(".*");
		
		options.atomic_prefix = objOptions.atomic_prefix; 
		options.atomic_suffix = objOptions.atomic_suffix;
		options.BufferSize = objOptions.BufferSize;
		
		options.makeDirs = objOptions.makeDirs;
		//special handling: options.TargetDir = objOptions.TargetDir;
		options.append_files = objOptions.append_files;
		options.check_interval = objOptions.check_interval;
		options.check_retry = objOptions.check_retry;
		options.check_size = objOptions.check_size;
		options.force_files = objOptions.force_files;
		options.overwrite_files = objOptions.overwrite_files;
		options.recursive = objOptions.recursive;
		options.skip_transfer = objOptions.skip_transfer;
		options.keep_modification_date = objOptions.keep_modification_date;
		options.verbose = objOptions.verbose;
		options.zero_byte_transfer = objOptions.zero_byte_transfer;
		
		//not supported: options.CheckSecurityHash = objOptions.CheckSecurityHash;
		//not supported: options.CreateSecurityHashFile = objOptions.CreateSecurityHashFile;
		//not supported: options.CreateSecurityHash = objOptions.CreateSecurityHash;
		//not supported: options.SecurityHashType = objOptions.SecurityHashType;

		return options;
	}
	
	/**
	 * 
	 * @param options
	 * @return
	 */
	private JADEOptions addJADEOptionsOnClient(JADEOptions options) {
		
		options.mail_on_success = objOptions.mail_on_success;
		options.mail_on_error = objOptions.mail_on_error;
		options.mail_on_empty_files = objOptions.mail_on_empty_files;
		
		options.SendTransferHistory = objOptions.SendTransferHistory;
		options.BackgroundServiceHost = objOptions.BackgroundServiceHost;
		options.BackgroundServiceJobChainName = objOptions.BackgroundServiceJobChainName;
		options.BackgroundServicePort = objOptions.BackgroundServicePort;
		options.Scheduler_Transfer_Method = objOptions.Scheduler_Transfer_Method;
		options.history = objOptions.history;
		options.history_repeat = objOptions.history_repeat;
		options.history_repeat_interval = objOptions.history_repeat_interval;
		options.HistoryFileAppendMode = objOptions.HistoryFileAppendMode;
		options.mandator = objOptions.mandator;
		
		options.compress_files = objOptions.compress_files;
		options.compressed_file_extension = objOptions.compressed_file_extension;
		
		options.CumulateFiles = objOptions.CumulateFiles;
		options.CumulativeFileName = objOptions.CumulativeFileName; 
		options.CumulativeFileSeparator = objOptions.CumulativeFileSeparator; 
		options.CumulativeFileDelete = objOptions.CumulativeFileDelete;
		
		options.expected_size_of_result_set = objOptions.expected_size_of_result_set;
		options.raise_error_if_result_set_is = objOptions.raise_error_if_result_set_is;
		options.log_filename = objOptions.log_filename;
		options.log4jPropertyFileName = objOptions.log4jPropertyFileName;
		
		//not supported: options.result_list_file = objOptions.result_list_file;
		//not supported: options.CreateResultSet = objOptions.CreateResultSet;
		//not supported: options.ResultSetFileName = objOptions.ResultSetFileName; 

		return options;
	}
	
	/**
	 * 
	 * @param options
	 * @return
	 */
	private String getJadeOnDMZCommand(JADEOptions options) {
		
		options.file.setNotDirty();
		// https://change.sos-berlin.com/browse/JADE-297
		options.user.DefaultValue("");
		options.Source().user.DefaultValue("");
		options.Target().user.DefaultValue("");
		// Otherwise this command contains the getJadeOnDMZCommand4RemoveSource() command too.
		boolean targetPostTransferCommandsIsDirty = options.Target().PostTransferCommands.isDirty();
		options.Target().PostTransferCommands.setNotDirty();
		
		StringBuffer command = new StringBuffer(objOptions.jump_command.Value()+ " ");
		command.append("-SendTransferHistory=false ");
		command.append(options.getOptionsAsQuotedCommandLine());
		command.append(options.Source().getOptionsAsQuotedCommandLine());
		command.append(options.Target().getOptionsAsQuotedCommandLine());
		
		if(targetPostTransferCommandsIsDirty) options.Target().PostTransferCommands.setDirty();
		
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
		opts.verbose = options.verbose;
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
		
		return command.toString();
	}
	
	/**
	 * wird nicht als PostProcessorCommands deklariert, wegen unterschiedlichen Faellen - Dateien nicht gefunden, Exception etc
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
	private String getSourceListFilename() {
		if (sourceListFilename == null) {
			sourceListFilename = normalizeDirectoryPath(objOptions.jump_dir.Value()) + "jade-dmz-" + getUUID() + ".source.tmp";
		}
		return sourceListFilename;
	}

}
