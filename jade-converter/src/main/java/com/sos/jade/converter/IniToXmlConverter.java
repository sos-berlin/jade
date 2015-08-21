package com.sos.jade.converter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.sos.CredentialStore.Options.SOSCredentialStoreOptions;
import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.io.Files.JSIniFile;
import com.sos.JSHelper.io.Files.SOSProfileSection;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.jade.generated.AtomicityType;
import com.sos.jade.generated.AuthenticatedProxyType;
import com.sos.jade.generated.AuthenticationMethodPassword;
import com.sos.jade.generated.AuthenticationMethodPublickey;
import com.sos.jade.generated.BasicAuthenticationType;
import com.sos.jade.generated.BasicConnectionType;
import com.sos.jade.generated.CSAuthentication;
import com.sos.jade.generated.CSExportAttachment;
import com.sos.jade.generated.CheckIntegrityHash;
import com.sos.jade.generated.CheckResultSetSize;
import com.sos.jade.generated.CheckSteadyState;
import com.sos.jade.generated.Client;
import com.sos.jade.generated.CompressFiles;
import com.sos.jade.generated.ConcurrencyType;
import com.sos.jade.generated.Configurations;
import com.sos.jade.generated.Copy;
import com.sos.jade.generated.CopySource;
import com.sos.jade.generated.CopyTarget;
import com.sos.jade.generated.CreateOrder;
import com.sos.jade.generated.CredentialStoreFragment;
import com.sos.jade.generated.CumulateFiles;
import com.sos.jade.generated.Directives;
import com.sos.jade.generated.FTPFragment;
import com.sos.jade.generated.FTPFragmentRef;
import com.sos.jade.generated.FTPPostProcessingType;
import com.sos.jade.generated.FTPPreProcessingType;
import com.sos.jade.generated.FTPSClientSecurityType;
import com.sos.jade.generated.FTPSFragment;
import com.sos.jade.generated.FTPSFragmentRef;
import com.sos.jade.generated.FileListSelection;
import com.sos.jade.generated.FilePathSelection;
import com.sos.jade.generated.FileSpecSelection;
import com.sos.jade.generated.Fragments;
import com.sos.jade.generated.GetList;
import com.sos.jade.generated.GetListSource;
import com.sos.jade.generated.HTTPFragment;
import com.sos.jade.generated.HTTPFragmentRef;
import com.sos.jade.generated.HTTPSFragment;
import com.sos.jade.generated.HTTPSFragmentRef;
import com.sos.jade.generated.Header;
import com.sos.jade.generated.JobScheduler;
import com.sos.jade.generated.JumpFragment;
import com.sos.jade.generated.JumpFragmentRef;
import com.sos.jade.generated.KeyFileAuthentication;
import com.sos.jade.generated.LocalPostProcessingType;
import com.sos.jade.generated.LocalPreProcessingType;
import com.sos.jade.generated.LocalSource;
import com.sos.jade.generated.LocalTarget;
import com.sos.jade.generated.MailFragment;
import com.sos.jade.generated.MailServer;
import com.sos.jade.generated.Move;
import com.sos.jade.generated.MoveSource;
import com.sos.jade.generated.MoveTarget;
import com.sos.jade.generated.NotificationFragmentRef;
import com.sos.jade.generated.NotificationFragmentRefs;
import com.sos.jade.generated.NotificationFragments;
import com.sos.jade.generated.NotificationTriggers;
import com.sos.jade.generated.Notifications;
import com.sos.jade.generated.Operation;
import com.sos.jade.generated.PasswordAuthentication;
import com.sos.jade.generated.Polling;
import com.sos.jade.generated.Profile;
import com.sos.jade.generated.Profiles;
import com.sos.jade.generated.ProtocolFragments;
import com.sos.jade.generated.ProxyForFTP;
import com.sos.jade.generated.ProxyForFTPS;
import com.sos.jade.generated.ProxyForHTTP;
import com.sos.jade.generated.ProxyForSFTP;
import com.sos.jade.generated.ProxyForWebDAV;
import com.sos.jade.generated.ReadableFragmentRefType;
import com.sos.jade.generated.Remove;
import com.sos.jade.generated.RemoveSource;
import com.sos.jade.generated.RenameType;
import com.sos.jade.generated.ResultSet;
import com.sos.jade.generated.SFTPFragment;
import com.sos.jade.generated.SFTPFragmentRef;
import com.sos.jade.generated.SFTPPostProcessingType;
import com.sos.jade.generated.SFTPPreProcessingType;
import com.sos.jade.generated.SMBAuthentication;
import com.sos.jade.generated.SMBFragment;
import com.sos.jade.generated.SMBFragmentRef;
import com.sos.jade.generated.SSHAuthenticationType;
import com.sos.jade.generated.Selection;
import com.sos.jade.generated.SourceFileOptions;
import com.sos.jade.generated.TargetFileOptions;
import com.sos.jade.generated.TransferOptions;
import com.sos.jade.generated.URLConnectionType;
import com.sos.jade.generated.UnauthenticatedProxyType;
import com.sos.jade.generated.WebDAVFragment;
import com.sos.jade.generated.WebDAVFragmentRef;
import com.sos.jade.generated.WriteableFragmentRefType;
import com.sos.jade.generated.ZlibCompression;

import sos.net.mail.options.SOSSmtpMailOptions;

public class IniToXmlConverter {
	
	private static final String schemaLocation = "http://www.sos-berlin.com/schema/jade/JADE_configuration_v1.0.xsd";
	//*************************************************** CommandLineArguments ********************************************************************//
	private String iniFilePath = "";
	private boolean autoDetectProfiles = true;
	private String[] ignoredProfiles = null;
	private String[] forcedProfiles = null;
	//*********************************************************************************************************************************************//
	public static Logger logger = LoggerFactory.getLogger(IniToXmlConverter.class);
	private HashSet<String> profilesMissingMandatoryParameters = new HashSet<>();
	private boolean missingMandatoryParameter = false;
	//*********************************************************************************************************************************************//
	
	public IniToXmlConverter() {
		
	}
	
	public static void main(String[] args) throws JAXBException {
		
		IniToXmlConverter converter = new IniToXmlConverter();
		converter.handleArguments(args);
		if (converter.iniFilePath.isEmpty()) {
			throw (new JobSchedulerException("No settings file was specified for conversion."));
		}
		logger.info("The settings file specified for conversion is '" + converter.iniFilePath + "'");
		String[] profilesToConvert = converter.detectProfiles();
		Object object = converter.convertIniFile(profilesToConvert);
		converter.writeXML(object, converter.iniFilePath.replace(".ini", ".xml"));
	}
	
	/**
	 * Currently implemented arguments are:
	 * -settings="filepath"
	 * -autoDetectProfiles=false
	 * -forcedProfiles="profile1;profile2"
	 * -ignoredProfiles="profile3;profile4"
	 * @param args
	 */
	private void handleArguments(String[] args) {
		for (String argument : args) {
			// CommandLineOptions are supposed to start with a '-' and contain a '='
			if (argument.startsWith("-") && argument.contains("=")) {
				int separatorPosition = argument.indexOf("=");
				String optionName = argument.substring(1, separatorPosition);
				String optionValue = argument.substring(separatorPosition+1);
				
				if (optionName.equalsIgnoreCase("settings")) {
					iniFilePath = optionValue;
				} else if (optionName.equalsIgnoreCase("autoDetectProfiles")) {
					if (optionValue.equalsIgnoreCase("false")) {
						autoDetectProfiles = false;
					} else if (optionValue.equalsIgnoreCase("true")) {
						autoDetectProfiles = true;
					} else {
						throw (new IllegalArgumentException("Invalid value specified for option 'autoDetectProfiles'"));
					}
				} else if (optionName.equalsIgnoreCase("ignoredProfiles") && optionValue.isEmpty() == false) {
					ignoredProfiles = optionValue.split(";");
				} else if (optionName.equalsIgnoreCase("forcedProfiles") && optionValue.isEmpty() == false) {
					forcedProfiles = optionValue.split(";");
				}
			}
		}
	}

	private Object convertIniFile(String[] profilesToConvert) {
			// build top-level xml elements (Configurations, Fragments, ProtocolFragments, Profiles)
			Configurations configurations = new Configurations();
			Fragments fragments = new Fragments();
			configurations.setFragments(fragments);
			ProtocolFragments protocolFragments = new ProtocolFragments();
			fragments.setProtocolFragments(protocolFragments);
			Profiles profiles = new Profiles();
			configurations.setProfiles(profiles);
			List<Profile> profileList = profiles.getProfile();
			
			for (String profilename : profilesToConvert) {
				logger.info("----- Starting conversion of profile '" + profilename + "' -----");
				JADEOptions options = loadIniFile(iniFilePath, profilename);
				
				Profile profile = new Profile();
				profileList.add(profile); // in this case mandatory as well
				profile.setProfileId(profilename);
				
				// Operation
				Operation operation = new Operation();
				profile.setOperation(operation);
				String operationValue = options.operation.Value();
				if (operationValue.equalsIgnoreCase("copy")) {
					// Copy
					Copy copy = new Copy();
					operation.setCopy(copy);
						// CopySource
						CopySource copySource = createCopySource(options, protocolFragments);
						copy.setCopySource(copySource);
						// CopyTarget
						CopyTarget copyTarget = createCopyTarget(protocolFragments, options);
						copy.setCopyTarget(copyTarget);
						// TransferOptions
						if (isTransferOptionsSpecified(options)) {
							TransferOptions transferOptions = createTransferOptions(options);
							copy.setTransferOptions(transferOptions);
						}
				} else if (operationValue.equalsIgnoreCase("move")) {
					// Move
					Move move = new Move();
					operation.setMove(move);
						// MoveSource
						MoveSource moveSource = createMoveSource(protocolFragments, options);
						move.setMoveSource(moveSource);
						// MoveTarget
						MoveTarget moveTarget = createMoveTarget(protocolFragments, options);
						move.setMoveTarget(moveTarget);
						// TransferOptions
						TransferOptions transferOptions = createTransferOptions(options);
						move.setTransferOptions(transferOptions);
				} else if (operationValue.equalsIgnoreCase("remove")) {
					// Remove
					Remove remove = new Remove();
					operation.setRemove(remove);
						// RemoveSource
						RemoveSource removeSource = createRemoveSource(protocolFragments, options);
						remove.setRemoveSource(removeSource);
				} else if (operationValue.equalsIgnoreCase("getlist")) {
					// GetList
					GetList getList = new GetList();
					operation.setGetList(getList);
						// GetListSource
						GetListSource getListSource = createGetListSource(protocolFragments, options);
						getList.setGetListSource(getListSource);
				} else if (operationValue.equalsIgnoreCase("zip")) {
					logger.info("Value 'zip' is currently not allowed for parameter 'operation'.");
					logger.info("----- Skipped conversion of profile '" + profilename + "' -----");
					continue;
				}
				// Client
					if(options.mandator.isDirty()) {
						logger.info("Using 'mandator=" + options.mandator.Value() + "' for both 'SupplyingClient' and 'ReceivingClient'.");
						Client client = new Client();
						profile.setClient(client);
						// SupplyingClient
						client.setSupplyingClient(options.mandator.Value());
						// ReceivingClient
						client.setReceivingClient(options.mandator.Value());
					}
				// JobScheduler
					// CreateOrder
					if (options.create_order.isTrue()) {
						JobScheduler jobScheduler = new JobScheduler();
						profile.setJobScheduler(jobScheduler);
						CreateOrder createOrder = createCreateOrder(options);
						jobScheduler.setCreateOrder(createOrder);
					} else if (isCreateOrderSpecified(options)) {
						logger.info("Skipping grouping element 'JobScheduler' as parameter 'create_order' is set to false or not specified.");
					}
						
				// Notifications
				if (isNotificationsSpecified(options)) {
					Notifications notifications = new Notifications();
					profile.setNotifications(notifications);
					// NotificationTriggers
					if (isNotificationTriggersSpecified(options)) {
						NotificationTriggers notificationTriggers = new NotificationTriggers();
						notifications.setNotificationTriggers(notificationTriggers);
						// OnSuccess
						if (options.mail_on_success.isDirty()) {
							notificationTriggers.setOnSuccess(options.mail_on_success.value());
						}
						// OnError
						if (options.mail_on_error.isDirty()) {
							notificationTriggers.setOnError(options.mail_on_error.value());
						}
						// OnEmptyFiles
						if (options.mail_on_empty_files.isDirty()) {
							notificationTriggers.setOnEmptyFiles(options.mail_on_empty_files.value());
						}
					}
					// NotificationFragmentRefs TODO This does not work yet. The SOSSMTPMailOptionsClass (options.getMailOptions()) doesn't seem to provide any parameters.
					if (isNotificationFragmentSpecified(options)) {
						String fragmentId = getRandomFragmentId();
						// there currently are only MailFragments, no other NotificationFragments need to be considered for conversion
						// NotificationFragmentRefs
						NotificationFragmentRefs notificationFragmentRefs = new NotificationFragmentRefs();
						notifications.setNotificationFragmentRefs(notificationFragmentRefs);
							// NotificationFragmentRef
							NotificationFragmentRef notificationFragmentRef = new NotificationFragmentRef();
							notificationFragmentRef.setRef(fragmentId);
							notificationFragmentRefs.getNotificationFragmentRef().add(notificationFragmentRef);
						// NotificationFragments
						NotificationFragments notificationFragments = new NotificationFragments();
						fragments.setNotificationFragments(notificationFragments);
							// MailFragment
							MailFragment mailFragment = createMailFragment(options, fragmentId);
							notificationFragments.getMailFragment().add(mailFragment);
					}
				}
				// CredentialStore
				if (isCredentialStoreFragmentSpecified(options)) {
					// CredentialStoreFragmentRef (...)
					// TODO create CredentialStoreFragment + CredentialStoreFragmentRef with identical ID
					// use_credential_store_options as trigger?
				}
				logger.info("----- Finished conversion of profile: '" + profilename + "' -----");
				if (missingMandatoryParameter) {
					profilesMissingMandatoryParameters.add(profilename);
				}
				missingMandatoryParameter = false;
			}
			logger.info("Finished conversion of profiles.");
			if (!profilesMissingMandatoryParameters.isEmpty()) {
				String log = "The following profiles are missing mandatory parameters and hence may be invalid:";
				for (String s : profilesMissingMandatoryParameters.toArray(new String[0])) {
					log += System.lineSeparator() + s;
				}
				logger.warn(log);
			}
		
		return configurations;	
	}
	
	private boolean isCreateOrderSpecified(JADEOptions options) {
		boolean returnValue = false;
		if (options.create_order.isTrue() || options.order_jobchain_name.isDirty() || options.create_orders_for_all_files.isDirty() || options.next_state.isDirty() ||
				options.MergeOrderParameter.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private String[] detectProfiles() {
		String[] profilesToConvert = null;
		JSIniFile inifile = new JSIniFile(iniFilePath);
		Map<String, SOSProfileSection> sectionsLowerCase = inifile.Sections();
		Iterator<String> sectionsLowerCaseIterator = sectionsLowerCase.keySet().iterator();
		HashSet<String> sectionsCaseSensitive = new HashSet<String>();
		while (sectionsLowerCaseIterator.hasNext()) {
			String key = sectionsLowerCaseIterator.next();
			String sectionNameCaseSensitive = sectionsLowerCase.get(key).strSectionName;
			sectionsCaseSensitive.add(sectionNameCaseSensitive);
		}
		
		if (autoDetectProfiles == false) {
			// Ausschliesslich manuelle Angabe von zu konvertierenden Profilen, Negativliste wird ignoriert
			logger.info("Automatic detection of profiles was turned off. Using only manually specified profiles.");
			String profiles = "";
			for (String profile : forcedProfiles) {
				if (sectionsCaseSensitive.contains(profile)) {
					profiles += ";" + profile;
				} else {
					logger.warn("Manually specified profile '" + profile +"' could not be found in settings file.");
				}
			}
			if (!profiles.isEmpty()) {
				profilesToConvert = profiles.substring(1).split(";");
			} else {
				logger.warn("No profiles could be detected in settings file.");
			}
			logger.info("The following profiles were specified:" + profiles.replace(";", " "));
		} else {
			// Automatische Erkennung von zu konvertierenden Profilen
			logger.info("Automatically detecting profiles.");
			if (forcedProfiles != null) {
				String msg = "";
				for (String profile : forcedProfiles) {
					msg += " " + profile;
				}
				logger.info("Additionally, the following profiles were specified:" + msg);
			}
			if (ignoredProfiles != null) {
				String msg = "";
				for (String profile : ignoredProfiles) {
					msg += " " + profile;
				}
				logger.info("Ignoring the following profiles:" + msg);
			}
			// Sektionen, die von anderen Sektionen inkludiert werden
			String[] includedSections = detectIncludedSections(sectionsCaseSensitive);
			
			String profiles = "";
			Iterator<String> it = sectionsCaseSensitive.iterator();
			while (it.hasNext()) {
				String key = it.next();
				// check if forced
				boolean forced = false;
				if (forcedProfiles != null) {
					for (String profile : forcedProfiles) {
						if (profile.equals(key)) {
							profiles += ";" + profile;
							forced = true;
							break;
						}
					}
				}
				// check if ignored
				boolean ignored = false;
				if (ignoredProfiles != null && forced == false) { // a profile should not be specified as both forced and ignored
					for (String profile : ignoredProfiles) {
						if (profile.equals(key)) {
							ignored = true;
							break;
						}
					}
				// The global-profile may not be detected as a fragment because it is never explicitly included. For this reason it will be added to the ignored profiles, but only if it is not forced
				} else if (key.equalsIgnoreCase("global") && forced == false) {
					ignored = true;
				}
				if (forced == false && ignored == false) {
					if (isProfile(key, includedSections)) {
						profiles += ";" + key;
					}
				}
			}
			if (profiles.isEmpty() == false) {
				profiles = profiles.substring(1);
				profilesToConvert = profiles.split(";");
				// System.out.println("The following profiles were detected: '" + profiles.replace(";", "', '") + "'");
				String log = "The following profiles were detected:";
				for (String s : profilesToConvert) {
					log += System.lineSeparator() + s;
				}
				logger.info(log);
			} else { // Es wurden keine Profile gefunden. Das sollte (eigentlich) nie der Fall sein.
				logger.warn("No profiles could be detected in settings file.");
			}
		}
		return profilesToConvert;
	}
	
	
	private String[] detectIncludedSections(Collection<String> sections) {
		Iterator<String> it = sections.iterator();
		Set<String> includedFragments = new HashSet<String>();
		while (it.hasNext()) {
			String key = it.next();
			JADEOptions options = loadIniFile(iniFilePath, key);
			String[] includes = options.include.Value().split(",");
			for (String include : includes) {
				includedFragments.add(include.trim());
			}
			includes = options.Source().include.Value().split(",");
			for (String include : includes) {
				includedFragments.add(include.trim());
			}
			includes = options.Target().include.Value().split(",");
			for (String include : includes) {
				includedFragments.add(include.trim());
			}
			includes = options.Source().Alternatives().include.Value().split(",");
			for (String include : includes) {
				includedFragments.add(include.trim());
			}
			includes = options.Target().Alternatives().include.Value().split(",");
			for (String include : includes) {
				includedFragments.add(include.trim());
			}
		}
		return includedFragments.toArray(new String[0]);
	}



	private boolean isProfile(String key, String[] includedFragments) {
		boolean returnValue = true;
		//a section is considered a profile if it is not being referenced by an include and if it contains an operation
		JADEOptions options = loadIniFile(iniFilePath, key);
		if (options.operation.isDirty()) {
			// Operation is specified
			// check for include
			for (String include : includedFragments) {
				if (include.equals(key)) {
					returnValue = false;
				}
			}
		} else {
			returnValue = false;
		}
		return returnValue;
	}

	
	private MailFragment createMailFragment(JADEOptions options, String fragmentId) {
		SOSSmtpMailOptions mailOptions = options.getMailOptions();
		MailFragment mailFragment = new MailFragment();
		mailFragment.setName(fragmentId);
		// Header
		Header header = new Header();
		mailFragment.setHeader(header);
			// From
			if (mailOptions.from.isDirty()) {
				header.setFrom(mailOptions.from.Value());
			}
			// To
			if (mailOptions.to.isDirty()) {
				header.setTo(mailOptions.to.Value());				
			} else {
				reportMissingMandatoryParameter("To");
			}
			// CC
			if (mailOptions.cc.isDirty()) {
				header.setCC(mailOptions.cc.Value());
			}
			// BCC
			if (mailOptions.bcc.isDirty()) {
				header.setBCC(mailOptions.bcc.Value());
			}
			// Subject
			if (mailOptions.subject.isDirty()) {
				header.setSubject(mailOptions.subject.Value());
			}
		// Attachment
		if (mailOptions.attachment.isDirty()) {
			mailFragment.setAttachment(mailOptions.attachment.Value());
		}
		// Body
		if (mailOptions.body.isDirty()) {
			mailFragment.setBody(mailOptions.body.Value());
		}
		// ContentType
		if (mailOptions.content_type.isDirty()) {
			mailFragment.setContentType(mailOptions.content_type.Value());
		}
		// Encoding
		if (mailOptions.encoding.isDirty()) {
			mailFragment.setEncoding(mailOptions.encoding.Value());
		}
		// MailServer
		if (mailOptions.SMTPHost.isDirty()) {
			MailServer mailServer = new MailServer();
			// BasicConnection
			BasicConnectionType basicConnectionType = new BasicConnectionType();
			mailServer.setBasicConnection(basicConnectionType);
				// Hostname
				basicConnectionType.setHostname(mailOptions.host.Value());
				// Port
				if (mailOptions.port.isDirty()) {
					basicConnectionType.setPort(mailOptions.port.value());
				}
			// BasicAuthentication
				if (mailOptions.smtp_user.isDirty()) {
					BasicAuthenticationType basicAuthenticationType = new BasicAuthenticationType();
					mailServer.setBasicAuthentication(basicAuthenticationType);
					// Account
					basicAuthenticationType.setAccount(mailOptions.smtp_user.Value());
					// Password
					if (mailOptions.smtp_password.isDirty()) {
						basicAuthenticationType.setPassword(mailOptions.smtp_password.Value());
					}
				}
		}
		// QueueDirectory
		if (mailOptions.queue_directory.isDirty()) {
			mailFragment.setQueueDirectory(mailOptions.queue_directory.Value());
		}
		return mailFragment;
	}



	private boolean isCredentialStoreFragmentSpecified(JADEOptions options) {
		// TODO
		
		return false;
	}



	private boolean isNotificationFragmentSpecified(JADEOptions options) {
		SOSSmtpMailOptions mailOptions = options.getMailOptions();
		boolean returnValue = false;
		if (mailOptions.from.isDirty()|| mailOptions.to.isDirty() || mailOptions.cc.isDirty() || mailOptions.bcc.isDirty() || mailOptions.subject.isDirty() ||
				mailOptions.attachment.isDirty() || mailOptions.body.isDirty() || mailOptions.content_type.isDirty() || mailOptions.encoding.isDirty() ||
				mailOptions.host.isDirty() || mailOptions.port.isDirty() || mailOptions.smtp_user.isDirty() || mailOptions.smtp_password.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private boolean isNotificationsSpecified(JADEOptions options) {
		boolean returnValue = false;
		if (isNotificationTriggersSpecified(options) || isNotificationFragmentSpecified(options)) {
			returnValue = true;
		}
		return returnValue;
	}



	private boolean isNotificationTriggersSpecified(JADEOptions options) {
		boolean returnValue = false;
		if (options.mail_on_success.isDirty() || options.mail_on_error.isDirty() || options.mail_on_empty_files.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private CreateOrder createCreateOrder(JADEOptions options) {
		CreateOrder createOrder = new CreateOrder();
		// OrderJobChainName
		if (options.order_jobchain_name.isDirty()) {
			createOrder.setOrderJobChainName(options.order_jobchain_name.Value());
		} else {
			reportMissingMandatoryParameter("order_jobchain_name");
		}
		// CreateOrderForAllFiles
		if (options.create_orders_for_all_files.isDirty()) {
			createOrder.setCreateOrderForAllFiles(options.create_orders_for_all_files.value());
		}
		// NextState
		if (options.next_state.isDirty()) {
			createOrder.setNextState(options.next_state.Value());
		}
		// MergeOrderParameters
		if (options.MergeOrderParameter.isDirty()) {
			createOrder.setMergeOrderParameters(options.MergeOrderParameter.value());
		}
		// CreateOrderOnRemoteJobScheduler not implemented, options are missing
		return createOrder;
	}



	private GetListSource createGetListSource(ProtocolFragments protocolFragments, JADEOptions options) {
		GetListSource getListSource = new GetListSource();
		SOSConnection2OptionsAlternate sourceConnectionOptions = options.Source();
		// GetListSourceFragmentRef
		ReadableFragmentRefType getListSourceFragmentRefType = createReadableFragmentRefType(options, sourceConnectionOptions, protocolFragments);
		getListSource.setGetListSourceFragmentRef(getListSourceFragmentRefType);
		// SourceFileOptions
		SourceFileOptions sourceFileOptions = createSourceFileOptions(options);
		getListSource.setSourceFileOptions(sourceFileOptions);
		return getListSource;
	}



	private MoveTarget createMoveTarget(ProtocolFragments protocolFragments, JADEOptions options) {
		MoveTarget moveTarget = new MoveTarget();
		SOSConnection2OptionsAlternate targetConnectionOptions = options.Target();
		// MoveTargetFragmentRef
		WriteableFragmentRefType moveTargetFragmentRef = createWriteableFragmentRefType(options, targetConnectionOptions, protocolFragments);
		moveTarget.setMoveTargetFragmentRef(moveTargetFragmentRef);
		// Directory
		moveTarget.setDirectory(options.Target().Directory.Value());
		// TargetFileOptions
		if (isTargetFileOptionsSpecified(options)) {
			TargetFileOptions targetFileOptions = createTargetFileOptions(options);
			moveTarget.setTargetFileOptions(targetFileOptions);	
		}
		return moveTarget;
	}



	private RemoveSource createRemoveSource(ProtocolFragments protocolFragments, JADEOptions options) {
		RemoveSource removeSource = new RemoveSource();
		SOSConnection2OptionsAlternate sourceConnectionOptions = options.Source();
		// RemoveSourceFragmentRef
		WriteableFragmentRefType writeableFragmentRefType = createWriteableFragmentRefType(options, sourceConnectionOptions, protocolFragments);
		removeSource.setRemoveSourceFragmentRef(writeableFragmentRefType);
		// SourceFileOptions
		SourceFileOptions sourceFileOptions = createSourceFileOptions(options);
		removeSource.setSourceFileOptions(sourceFileOptions);
		return removeSource;
	}



	private MoveSource createMoveSource(ProtocolFragments protocolFragments, JADEOptions options) {
		MoveSource moveSource = new MoveSource();
		SOSConnection2OptionsAlternate sourceConnectionOptions = options.Source();
		// MoveSourceFragmentRef
		WriteableFragmentRefType writeableFragmentRefType = createWriteableFragmentRefType(options, sourceConnectionOptions, protocolFragments);
		moveSource.setMoveSourceFragmentRef(writeableFragmentRefType);
		// SourceFileOptions
		SourceFileOptions sourceFileOptions = createSourceFileOptions(options);
		moveSource.setSourceFileOptions(sourceFileOptions);
		return moveSource;
	}



	private WriteableFragmentRefType createWriteableFragmentRefType(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, ProtocolFragments protocolFragments) {
		String fragmentId = getRandomFragmentId();
		WriteableFragmentRefType writeableFragmentRefType = new WriteableFragmentRefType();
			// (Protocol)
			String sourceProtocolValue = connectionOptions.protocol.Value();
			if (sourceProtocolValue.equalsIgnoreCase("ftp")) {
				// FTPFragmentRef
				FTPFragmentRef ftpFragmentRef = createFTPFragmentRef(options, connectionOptions, fragmentId); 
				writeableFragmentRefType.setFTPFragmentRef(ftpFragmentRef);
				// FTPFragment
				FTPFragment ftpFragment = createFTPFragment(options, connectionOptions, fragmentId, protocolFragments);
				protocolFragments.getFTPFragment().add(ftpFragment);
			} else if (sourceProtocolValue.equalsIgnoreCase("ftps")) {
				// FTPSFragmentRef
				FTPSFragmentRef ftpsFragmentRef = createFTPSFragmentRef(options, connectionOptions, fragmentId); 
				writeableFragmentRefType.setFTPSFragmentRef(ftpsFragmentRef);
				// FTPSFragment
				FTPSFragment ftpsFragment = createFTPSFragment(options, connectionOptions, fragmentId, protocolFragments);
				protocolFragments.getFTPSFragment().add(ftpsFragment);
			}  else if (sourceProtocolValue.equalsIgnoreCase("local") || sourceProtocolValue.equalsIgnoreCase("zip")) { // zip has been removed as a protocol, local is used instead with a boolean zip-parameter
				// LocalTarget
				LocalTarget localTarget = createLocalTarget(options, connectionOptions);
				writeableFragmentRefType.setLocalTarget(localTarget);
			} else if (sourceProtocolValue.equalsIgnoreCase("sftp")) {
				// SFTPFragmentRef
				SFTPFragmentRef sftpFragmentRef = createSFTPFragmentRef(options, connectionOptions, fragmentId);
				writeableFragmentRefType.setSFTPFragmentRef(sftpFragmentRef);
				// SFTPFragment
				SFTPFragment sftpFragment = createSFTPFragment(options, connectionOptions, fragmentId, protocolFragments);
				protocolFragments.getSFTPFragment().add(sftpFragment);
			} else if (sourceProtocolValue.equalsIgnoreCase("smb")) {
				// SMBFragmentRef
				SMBFragmentRef smbFragmentRef = createSMBFragmentRef(options, connectionOptions, fragmentId);
				writeableFragmentRefType.setSMBFragmentRef(smbFragmentRef);
				// SMBFragment
				SMBFragment smbFragment = createSMBFragment(connectionOptions, fragmentId);
				protocolFragments.getSMBFragment().add(smbFragment);
			} else if (sourceProtocolValue.equalsIgnoreCase("webdav")) {
				// WebDAVFragmentRef
				WebDAVFragmentRef webDAVFragmentRef = createWebDAVFragmentRef(options, connectionOptions, fragmentId);
				writeableFragmentRefType.setWebDAVFragmentRef(webDAVFragmentRef);
				// WebDAVFragment
				WebDAVFragment webDAVFragment = createWebDAVFragment(options, connectionOptions, fragmentId, protocolFragments);
				protocolFragments.getWebDAVFragment().add(webDAVFragment);
			} // TODO WriteableAlternativeFragmentRef
		return writeableFragmentRefType;
	}


	private LocalTarget createLocalTarget(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions) {
		// LocalTarget
		LocalTarget localTarget = new LocalTarget();
			// LocalPreProcessing
			if (isLocalPreProcessingSpecified(connectionOptions)) {
				LocalPreProcessingType localPreProcessing = createLocalPreProcessing(connectionOptions);
				localTarget.setLocalPreProcessing(localPreProcessing);
			}
			// LocalPostProcessing
			if (isLocalPostProcessingSpecified(connectionOptions)) {
				LocalPostProcessingType localPostProcessing = createLocalPostProcessing(connectionOptions);
				localTarget.setLocalPostProcessing(localPostProcessing);
			}
			// Rename
			if (isRenameSpecified(connectionOptions) || (connectionOptions.getPrefix().equalsIgnoreCase("target_") && isUnprefixedRenameSpecified(options))) {
				RenameType rename = createRename(options, connectionOptions);
				localTarget.setRename(rename);
			}
		return localTarget;
	}



	private CopyTarget createCopyTarget(ProtocolFragments protocolFragments, JADEOptions options) {
		CopyTarget copyTarget = new CopyTarget();
		SOSConnection2OptionsAlternate targetConnectionOptions = options.Target();
		// CopyTargetFragmentRef
		WriteableFragmentRefType copyTargetFragmentRef = createWriteableFragmentRefType(options, targetConnectionOptions, protocolFragments);
		copyTarget.setCopyTargetFragmentRef(copyTargetFragmentRef);
		// Directory
		copyTarget.setDirectory(options.Target().Directory.Value());
		// TargetFileOptions
		if (isTargetFileOptionsSpecified(options)) {
			TargetFileOptions targetFileOptions = createTargetFileOptions(options);
			copyTarget.setTargetFileOptions(targetFileOptions);	
		}
		return copyTarget;
	}



	private CopySource createCopySource(JADEOptions options, ProtocolFragments protocolFragments) {
		CopySource copySource = new CopySource();
		SOSConnection2OptionsAlternate sourceConnectionOptions = options.Source();
		// CopySourceFragmentRef
		ReadableFragmentRefType readableFragmentRefType = createReadableFragmentRefType(options, sourceConnectionOptions, protocolFragments);
		copySource.setCopySourceFragmentRef(readableFragmentRefType);
		// SourceFileOptions
		SourceFileOptions sourceFileOptions = createSourceFileOptions(options);
		copySource.setSourceFileOptions(sourceFileOptions);
		return copySource;
	}



	private ReadableFragmentRefType createReadableFragmentRefType(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, ProtocolFragments protocolFragments) {
		String fragmentId = getRandomFragmentId();
		ReadableFragmentRefType readableFragmentRefType = new ReadableFragmentRefType();
			// (Protocol)
			String sourceProtocolValue = connectionOptions.protocol.Value();
			if (sourceProtocolValue.equalsIgnoreCase("ftp")) {
				// FTPFragmentRef
				FTPFragmentRef ftpFragmentRef = createFTPFragmentRef(options, connectionOptions, fragmentId); 
				readableFragmentRefType.setFTPFragmentRef(ftpFragmentRef);
				// FTPFragment
				FTPFragment ftpFragment = createFTPFragment(options, connectionOptions, fragmentId, protocolFragments);
				protocolFragments.getFTPFragment().add(ftpFragment);
			} else if (sourceProtocolValue.equalsIgnoreCase("ftps")) {
				// FTPSFragmentRef
				FTPSFragmentRef ftpsFragmentRef = createFTPSFragmentRef(options, connectionOptions, fragmentId); 
				readableFragmentRefType.setFTPSFragmentRef(ftpsFragmentRef);
				// FTPSFragment
				FTPSFragment ftpsFragment = createFTPSFragment(options, connectionOptions, fragmentId, protocolFragments);
				protocolFragments.getFTPSFragment().add(ftpsFragment);
			} else if (sourceProtocolValue.equalsIgnoreCase("http")) {
				// HTTPFragmentRef
				HTTPFragmentRef httpFragmentRef = createHTTPFragmentRef(fragmentId);
				readableFragmentRefType.setHTTPFragmentRef(httpFragmentRef);
				// HTTPFragment
				HTTPFragment httpFragment = createHTTPFragment(options, connectionOptions, fragmentId, protocolFragments);
				protocolFragments.getHTTPFragment().add(httpFragment);
			} else if (sourceProtocolValue.equalsIgnoreCase("https")) { // TODO https is not a valid value for parameter 'protocol', only http is. to specify usage of https, the url has to start with https:// (see webdavFragment)
				// HTTPSFragmentRef
				HTTPSFragmentRef httpsFragmentRef = createHTTPSFragmentRef(fragmentId);
				readableFragmentRefType.setHTTPSFragmentRef(httpsFragmentRef);
				// HTTPSFragment
				HTTPSFragment httpsFragment = createHTTPSFragment(options, connectionOptions, fragmentId, protocolFragments);
				protocolFragments.getHTTPSFragment().add(httpsFragment);
			} else if (sourceProtocolValue.equalsIgnoreCase("local") || sourceProtocolValue.equalsIgnoreCase("zip")) { // zip has been removes as a protocol, local is used instead with a boolean zip-parameter
				// LocalSource
				LocalSource localSource = createLocalSource(options, connectionOptions);
				readableFragmentRefType.setLocalSource(localSource);
			} else if (sourceProtocolValue.equalsIgnoreCase("sftp")) {
				// SFTPFragmentRef
				SFTPFragmentRef sftpFragmentRef = createSFTPFragmentRef(options, connectionOptions, fragmentId);
				readableFragmentRefType.setSFTPFragmentRef(sftpFragmentRef);
				// SFTPFragment
				SFTPFragment sftpFragment = createSFTPFragment(options, connectionOptions, fragmentId, protocolFragments);
				protocolFragments.getSFTPFragment().add(sftpFragment);
			} else if (sourceProtocolValue.equalsIgnoreCase("smb")) {
				// SMBFragmentRef
				SMBFragmentRef smbFragmentRef = createSMBFragmentRef(options, connectionOptions, fragmentId);
				readableFragmentRefType.setSMBFragmentRef(smbFragmentRef);
				// SMBFragment
				SMBFragment smbFragment = createSMBFragment(connectionOptions, fragmentId);
				protocolFragments.getSMBFragment().add(smbFragment);
			} else if (sourceProtocolValue.equalsIgnoreCase("webdav")) {
				// WebDAVFragmentRef
				WebDAVFragmentRef webDAVFragmentRef = createWebDAVFragmentRef(options, connectionOptions, fragmentId);
				readableFragmentRefType.setWebDAVFragmentRef(webDAVFragmentRef);
				// WebDAVFragment
				WebDAVFragment webDAVFragment = createWebDAVFragment(options, connectionOptions, fragmentId, protocolFragments);
				protocolFragments.getWebDAVFragment().add(webDAVFragment);
			} // TODO ReadableAlternativeFragmentRef
		return readableFragmentRefType;
	}



	private SourceFileOptions createSourceFileOptions(JADEOptions options) {
		SourceFileOptions sourceFileOptions = new SourceFileOptions();
		// Selection
		Selection selection = createSelection(options);
		sourceFileOptions.setSelection(selection);
		// CheckSteadyState
		if (isCheckSteadyStateSpecified(options)) {
			CheckSteadyState checkSteadyState = createCheckSteadyState(options);
			sourceFileOptions.setCheckSteadyState(checkSteadyState);
		}
		// Directives
		if (isDirectivesSpecified(options)) {
			Directives directives = createDirectives(options);
			sourceFileOptions.setDirectives(directives);
		}
		// FileAge is currently not implemented
		// FileSize is currently not implemented
		// Polling
		if (isPollingSpecified(options)) {
			Polling polling = createPolling(options);
			sourceFileOptions.setPolling(polling);
		}
		// ResultSet
		if (isResultSetSpecified(options)) {
			ResultSet resultSet = createResultSet(options);
			sourceFileOptions.setResultSet(resultSet);
		}
		// SkipFiles is currently not implemented
		// MaxFiles
		if (options.MaxFiles.isDirty()) {
			sourceFileOptions.setMaxFiles(options.MaxFiles.value());
		}
		return sourceFileOptions;
	}

	private ResultSet createResultSet(JADEOptions options) {
		ResultSet resultSet = new ResultSet();
		if(options.result_list_file.isDirty()) {
			resultSet.setResultSetFile(options.result_list_file.Value());
		}
		if (isCheckResultSetSizeSpecified(options)) {
			CheckResultSetSize checkResultSetSize = new CheckResultSetSize();
			checkResultSetSize.setExpectedSizeOfResultSet(options.expected_size_of_result_set.value());
			checkResultSetSize.setRaiseErrorIfResultSetIs(options.raise_error_if_result_set_is.Value());
		}
		if (options.on_empty_result_set.isDirty()) {
			resultSet.setEmptyResultSetState(options.on_empty_result_set.Value());
		}
		return resultSet;
	}



	private boolean isResultSetSpecified(JADEOptions options) {
		boolean returnValue = false;
		if(options.result_list_file.isDirty() || isCheckResultSetSizeSpecified(options) || options.on_empty_result_set.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private boolean isCheckResultSetSizeSpecified(JADEOptions options) {
		boolean returnValue = false;
		if(options.expected_size_of_result_set.isDirty() || options.raise_error_if_result_set_is.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private Polling createPolling(JADEOptions options) {
		Polling polling = new Polling();
		if (options.poll_interval.isDirty()) {
			polling.setPollInterval(options.poll_interval.value());
		}
		if (options.poll_timeout.isDirty()) {
			polling.setPollTimeout(options.poll_timeout.value());
		}
		if (options.poll_minfiles.isDirty()) {
			polling.setMinFiles(options.poll_minfiles.value());
		}
		if (options.pollingWait4SourceFolder.isDirty()) {
			polling.setWaitForSourceFolder(options.pollingWait4SourceFolder.value());
		}
		if (options.PollErrorState.isDirty()) {
			polling.setPollErrorState(options.PollErrorState.Value());
		}
		if (options.PollingServer.isDirty()) {
			polling.setPollingServer(options.PollingServer.value());
		}
		if (options.pollingServerDuration.isDirty()) {
			polling.setPollingServerDuration(options.pollingServerDuration.value());
		}
		if (options.PollingServerPollForever.isDirty()) {
			polling.setPollForever(options.PollingServerPollForever.value());
		}
		return polling;
	}



	private boolean isPollingSpecified(JADEOptions options) {
		boolean returnValue = false;
		if (options.poll_interval.isDirty() || options.poll_timeout.isDirty() || options.poll_minfiles.isDirty() || options.pollingWait4SourceFolder.isDirty() ||
				options.PollErrorState.isDirty() || options.PollingServer.isDirty() || options.pollingServerDuration.isDirty() || options.PollingServerPollForever.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private Directives createDirectives(JADEOptions options) {
		Directives directives = new Directives();
		if (options.force_files.isDirty()) {
			logger.info("Replacing 'force_files=" + options.force_files.Value() + "' with 'DisableErrorOnNoFilesFound=" + String.valueOf(!options.makeDirs.value()) + "'");
			directives.setDisableErrorOnNoFilesFound(!options.force_files.value());
		}
		if (options.TransferZeroByteFiles.isDirty()) {
			directives.setTransferZeroByteFiles(options.TransferZeroByteFiles.Value());
		}
		return directives;
	}



	private boolean isDirectivesSpecified(JADEOptions options) {
		boolean returnValue = false;
		if (options.force_files.isDirty() || options.TransferZeroByteFiles.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private CheckSteadyState createCheckSteadyState(JADEOptions options) {
		CheckSteadyState checkSteadyState = new CheckSteadyState();
		if (options.CheckSteadyStateInterval.isDirty()) {
			checkSteadyState.setCheckSteadyStateInterval(options.CheckSteadyStateInterval.value());
		}
		if (options.CheckSteadyCount.isDirty()) {
			checkSteadyState.setCheckSteadyStateCount(options.CheckSteadyCount.value());
		}
		if (options.SteadyStateErrorState.isDirty()) {
			checkSteadyState.setCheckSteadyStateErrorState(options.SteadyStateErrorState.Value());
		}
		return checkSteadyState;
	}



	private boolean isCheckSteadyStateSpecified(JADEOptions options) {
		boolean returnValue = false;
		if (options.CheckSteadyStateInterval.isDirty() || options.CheckSteadyCount.isDirty() || options.SteadyStateErrorState.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private Selection createSelection(JADEOptions options) {
		Selection selection = new Selection();
		if (options.file_path.isDirty()) {
			// FilePathSelection
			FilePathSelection filePathSelection = new FilePathSelection();
			selection.setFilePathSelection(filePathSelection);
			filePathSelection.setFilePath(options.file_path.Value());
			if (options.Source().Directory.isDirty()) {
				filePathSelection.setDirectory(options.Source().Directory.Value());
			}
		} else if (options.file_spec.isDirty()) {
			// FileSpecSelection
			FileSpecSelection fileSpecSelection = new FileSpecSelection();
			selection.setFileSpecSelection(fileSpecSelection);
			fileSpecSelection.setFileSpec(options.file_spec.Value());
			if (options.Source().Directory.isDirty()) {
				fileSpecSelection.setDirectory(options.Source().Directory.Value());
			}
			if (options.recursive.isDirty()) {
				fileSpecSelection.setRecursive(options.recursive.value());
			}
		} else if (options.FileListName.isDirty()) {
			// FileListSelection
			FileListSelection fileListSelection = new FileListSelection();
			selection.setFileListSelection(fileListSelection);
			fileListSelection.setFileList(options.FileListName.Value());
			if (options.Source().Directory.isDirty()) {
				fileListSelection.setDirectory(options.Source().Directory.Value());
			}
		} else {
			logger.info("No FileSelection was specified. I assume you intended to use 'file_spec' with value '.*' to select all files from the SourceDirectory.");
			// FileSpecSelection
			FileSpecSelection fileSpecSelection = new FileSpecSelection();
			selection.setFileSpecSelection(fileSpecSelection);
			fileSpecSelection.setFileSpec(".*");
			if (options.Source().Directory.isDirty()) {
				fileSpecSelection.setDirectory(options.Source().Directory.Value());
			}
			if (options.recursive.isDirty()) {
				fileSpecSelection.setRecursive(options.recursive.value());
			}
		}
		return selection;
	}



	private WebDAVFragment createWebDAVFragment(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, String fragmentId, ProtocolFragments protocolFragments) {
		WebDAVFragment webDAVFragment = new WebDAVFragment();
		webDAVFragment.setName(fragmentId);
		// URLConnection
		URLConnectionType urlConnection = createURLConnection(connectionOptions);
		webDAVFragment.setURLConnection(urlConnection);
		// AcceptUntrustedCertificate
		if (connectionOptions.accept_untrusted_certificate.isDirty()) {
			webDAVFragment.setAcceptUntrustedCertificate(connectionOptions.accept_untrusted_certificate.value());
		}
		// BasicAuthentication
		BasicAuthenticationType basicAuthentication = createBasicAuthenticationForURLConnection(connectionOptions);
		webDAVFragment.setBasicAuthentication(basicAuthentication);
		// JumpFragmentRef
		if (isJumpFragmentSpecified(options)) {
			String jumpFragmentId = getRandomFragmentId();
			JumpFragment jumpFragment = createJumpFragment(options, jumpFragmentId);
			protocolFragments.getJumpFragment().add(jumpFragment);
			JumpFragmentRef jumpFragmentRef = createJumpFragmentRef(jumpFragmentId);
			webDAVFragment.setJumpFragmentRef(jumpFragmentRef);
		}
		// ProxyForWebDAV
		if (isProxySpecified(connectionOptions)) {
			if (connectionOptions.proxy_protocol.Value().equalsIgnoreCase("http") || connectionOptions.proxy_protocol.Value().isEmpty()) {
				if (connectionOptions.proxy_protocol.Value().isEmpty()) {
					logger.info("No value was specified for parameter '" + connectionOptions.getPrefix() + "proxy_protocol'. I assume you intended to use 'http'.");
				}
				ProxyForWebDAV proxyForWebDAV = new ProxyForWebDAV();
				webDAVFragment.setProxyForWebDAV(proxyForWebDAV);
				// HTTPProxy
				AuthenticatedProxyType httpProxy = createAuthenticatedProxyType(connectionOptions);
				proxyForWebDAV.setHTTPProxy(httpProxy);
			} else {
				logger.warn("Invalid value specified for option '" + connectionOptions.getPrefix() + "proxy_protocol'. Only value 'http' is allowed in combination with WebDAV.");
			}
		}
		return webDAVFragment;
	}


	private URLConnectionType createURLConnection(SOSConnection2OptionsAlternate connectionOptions) {
		URLConnectionType urlConnectionType = new URLConnectionType();
		if (connectionOptions.url.isDirty() || connectionOptions.host.isDirty()) {
			URL url = null;
			try {
				if (connectionOptions.url.isDirty()) {
					url = new URL(connectionOptions.url.Value());
				} else if (connectionOptions.host.isDirty()) {
					url = new URL(connectionOptions.host.Value());
				}
			} catch (MalformedURLException | RuntimeException e) {}
			String connection = url.getProtocol() + "://" + url.getHost();
			if (url.getPort() != -1) {
				connection += ":" + url.getPort();
			} else if (connectionOptions.port.isDirty()) {
				connection += ":" + connectionOptions.port.value();
			}
			urlConnectionType.setURL(connection);
		} else {
			reportMissingMandatoryParameter(connectionOptions.getPrefix() + "url");
		}
		return null;
	}
	
	private BasicAuthenticationType createBasicAuthenticationForURLConnection(SOSConnection2OptionsAlternate connectionOptions) {
		BasicAuthenticationType basicAuthenticationType = new BasicAuthenticationType();
		if (connectionOptions.url.isDirty() || connectionOptions.host.isDirty()) {
			URL url = null;
			try {
				if (connectionOptions.url.isDirty()) {
					url = new URL(connectionOptions.url.Value());
				} else if (connectionOptions.host.isDirty()) {
					url = new URL(connectionOptions.host.Value());
				}
			} catch (MalformedURLException | RuntimeException e) {}
			if (url.getUserInfo() != null || connectionOptions.user.isDirty()) {
				if (url.getUserInfo() != null) {
					if (url.getUserInfo().contains(":")) {
						basicAuthenticationType.setAccount(url.getUserInfo().split(":")[0]);
						basicAuthenticationType.setPassword(url.getUserInfo().split(":")[1]);
					} else {
						basicAuthenticationType.setAccount(url.getUserInfo());
					}
				}
			}
		} else if (connectionOptions.user.isDirty()) {
			basicAuthenticationType.setAccount(connectionOptions.user.Value());
			if (connectionOptions.password.isDirty()) {
				basicAuthenticationType.setPassword(connectionOptions.password.Value());
			}
		}
		return basicAuthenticationType;
	}



	private BasicAuthenticationType createBasicAuthentication(SOSConnection2OptionsAlternate connectionOptions) {
		BasicAuthenticationType basicAuthentication = new BasicAuthenticationType();
		basicAuthentication.setAccount(connectionOptions.user.Value());
		if (connectionOptions.password.isDirty()) {
			basicAuthentication.setPassword(connectionOptions.password.Value());
		}
		return basicAuthentication;
	}



	private WebDAVFragmentRef createWebDAVFragmentRef(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, String fragmentId) {
		WebDAVFragmentRef webDAVFragmentRef = new WebDAVFragmentRef();
		webDAVFragmentRef.setRef(fragmentId);
		// Rename
		if (isRenameSpecified(connectionOptions) || (connectionOptions.getPrefix().equalsIgnoreCase("target_") && isUnprefixedRenameSpecified(options))) {
			RenameType rename = createRename(options, connectionOptions);
			webDAVFragmentRef.setRename(rename);
		}
		return webDAVFragmentRef;
	}



	private SMBFragment createSMBFragment(SOSConnection2OptionsAlternate connectionOptions, String fragmentId) {
		SMBFragment smbFragment = new SMBFragment();
		smbFragment.setName(fragmentId);
		// Hostname
		smbFragment.setHostname(connectionOptions.host.Value());
		// SMBAuthentication
		SMBAuthentication smbAuthentication = new SMBAuthentication();
		smbAuthentication.setAccount(connectionOptions.user.Value());
		if (connectionOptions.domain.isDirty()) {
			smbAuthentication.setDomain(connectionOptions.domain.Value());
		}
		if (connectionOptions.password.isDirty()) {
			smbAuthentication.setPassword(connectionOptions.password.Value());
		}
		return smbFragment;
	}



	private SMBFragmentRef createSMBFragmentRef(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, String fragmentId) {
		SMBFragmentRef smbFragmentRef = new SMBFragmentRef();
		smbFragmentRef.setRef(fragmentId);
		// Rename
		if (isRenameSpecified(connectionOptions) || (connectionOptions.getPrefix().equalsIgnoreCase("target_") && isUnprefixedRenameSpecified(options))) {
			RenameType rename = createRename(options, connectionOptions);
			smbFragmentRef.setRename(rename);
		}
		return smbFragmentRef;
	}



	private SFTPFragment createSFTPFragment(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, String fragmentId, ProtocolFragments protocolFragments) {
		SFTPFragment sftpFragment = new SFTPFragment();
		sftpFragment.setName(fragmentId);
		// BasicConnection
		BasicConnectionType basicConnection = createBasicConnection(connectionOptions);
		sftpFragment.setBasicConnection(basicConnection);
		// SSHAuthentication
		SSHAuthenticationType sshAuthentication = createSSHAuthentication(connectionOptions);
		sftpFragment.setSSHAuthentication(sshAuthentication);
		// JumpFragmentRef
		if (isJumpFragmentSpecified(options)) {
			String jumpFragmentId = getRandomFragmentId();
			JumpFragment jumpFragment = createJumpFragment(options, jumpFragmentId);
			protocolFragments.getJumpFragment().add(jumpFragment);
			JumpFragmentRef jumpFragmentRef = createJumpFragmentRef(jumpFragmentId);
			sftpFragment.setJumpFragmentRef(jumpFragmentRef);
		}
		// ProxyForSFTP
		if (isProxySpecified(connectionOptions)) {
			if (connectionOptions.proxy_protocol.Value().equalsIgnoreCase("socks4") || connectionOptions.proxy_protocol.Value().equalsIgnoreCase("socks5") || connectionOptions.proxy_protocol.Value().isEmpty()) {
				ProxyForSFTP proxyForSFTP = new ProxyForSFTP();
				sftpFragment.setProxyForSFTP(proxyForSFTP);
				if (connectionOptions.proxy_protocol.Value().equalsIgnoreCase("socks4")) {
					UnauthenticatedProxyType socks4Proxy = createUnauthenticatedProxyType(connectionOptions);
					proxyForSFTP.setSOCKS4Proxy(socks4Proxy);
				} else if (connectionOptions.proxy_protocol.Value().equalsIgnoreCase("socks5")) {
					AuthenticatedProxyType socks5Proxy = createAuthenticatedProxyType(connectionOptions);
					proxyForSFTP.setSOCKS5Proxy(socks5Proxy);
				} else if (connectionOptions.proxy_protocol.Value().isEmpty()) {
					if (connectionOptions.proxy_user.isDirty()) {
						logger.info("No value was specified for parameter '" + connectionOptions.getPrefix() + "proxy_protocol'. As you specified a value for parameter '" + connectionOptions.getPrefix() + "proxy_user' for authentication, I assume you meant 'socks5'.");
						AuthenticatedProxyType socks5Proxy = createAuthenticatedProxyType(connectionOptions);
						proxyForSFTP.setSOCKS5Proxy(socks5Proxy);
					} else {
						logger.info("Skipping grouping element 'ProxyForSFTP' as parameter '" + connectionOptions.getPrefix() + "proxy_protocol' is not specified.");
						sftpFragment.setProxyForSFTP(null);
					}
				}
			} else {
				logger.warn("Invalid value specified for option '" + connectionOptions.getPrefix() + "proxy_protocol'. Only values 'socks4' and 'socks5' are allowed in combination with SFTP.");
			}
		}
		// StrictHostKeyChecking
		if (connectionOptions.StrictHostKeyChecking.isDirty()) {
			if (connectionOptions.StrictHostKeyChecking.Value().equalsIgnoreCase("yes")) {
				sftpFragment.setStrictHostkeyChecking(true);
			} else if (connectionOptions.StrictHostKeyChecking.Value().equalsIgnoreCase("no")) {
				sftpFragment.setStrictHostkeyChecking(false);
			} else if (connectionOptions.StrictHostKeyChecking.Value().equalsIgnoreCase("ask")) {
				logger.warn("Value 'ask' is no longer permitted for option '" + connectionOptions.getPrefix() + "StrictHostKeyChecking'. Use either 'yes' or 'no'.");
			}
		}
		return sftpFragment;
	}



	private JumpFragment createJumpFragment(JADEOptions options, String jumpFragmentId) {
		JumpFragment jumpFragment = new JumpFragment();
		jumpFragment.setName(jumpFragmentId);
		// BasicConnection
		BasicConnectionType basicConnectionType = new BasicConnectionType();
		jumpFragment.setBasicConnection(basicConnectionType);
		if (options.jump_host.isDirty()) {
			basicConnectionType.setHostname(options.jump_host.Value());
		} else {
			reportMissingMandatoryParameter("jump_host");
		}
		if (options.jump_port.isDirty()) {
			basicConnectionType.setPort(options.jump_port.value());
		}
		// SSHAuthentication
		SSHAuthenticationType sshAuthenticationType = new SSHAuthenticationType();
		jumpFragment.setSSHAuthentication(sshAuthenticationType);
		if (options.jump_user.isDirty()) {
			sshAuthenticationType.setAccount(options.jump_user.Value());
		} else {
			reportMissingMandatoryParameter("jump_user");
		}
		if (options.jump_ssh_auth_method.Value().equalsIgnoreCase("password")) {
			// AuthenticationMethodPassword
			AuthenticationMethodPassword authenticationMethodPassword = new AuthenticationMethodPassword();
			if (options.jump_password.isDirty()) {
				authenticationMethodPassword.setPassword(options.jump_password.Value());
			} else {
				reportMissingMandatoryParameter("jump_password");
			}
		} else if (options.jump_ssh_auth_method.Value().equalsIgnoreCase("publickey")) {
			// AuthenticationMethodPublicKey
			AuthenticationMethodPublickey authenticationMethodPublickey = new AuthenticationMethodPublickey();
			jumpFragment.setSSHAuthentication(sshAuthenticationType);
			if (options.jump_ssh_auth_file.isDirty()) {
				authenticationMethodPublickey.setAuthenticationFile(options.jump_ssh_auth_file.Value());
			} else {
				reportMissingMandatoryParameter("jump_ssh_auth_file");
			}
			if (options.jump_password.isDirty()) {
				authenticationMethodPublickey.setPassphrase(options.jump_password.Value());
			}
		}
		// JumpDirectory
		if (options.jump_dir.isDirty()) {
			jumpFragment.setJumpDirectory(options.jump_dir.Value());
		}
		// ProxyForSFTP
		if (isJumpProxySpecified(options)) {
			if (options.jump_proxy_protocol.Value().equalsIgnoreCase("socks4") || options.jump_proxy_protocol.Value().equalsIgnoreCase("socks5") || options.jump_proxy_protocol.Value().isEmpty()) {
				ProxyForSFTP proxyForSFTP = new ProxyForSFTP();
				jumpFragment.setProxyForSFTP(proxyForSFTP);
				if (options.jump_proxy_protocol.Value().equalsIgnoreCase("socks4")) {
					UnauthenticatedProxyType socks4Proxy = createUnauthenticatedProxyTypeForJump(options);
					proxyForSFTP.setSOCKS4Proxy(socks4Proxy);
				} else if (options.jump_proxy_protocol.Value().equalsIgnoreCase("socks5")) {
					AuthenticatedProxyType socks5Proxy = createAuthenticatedProxyTypeForJump(options);
					proxyForSFTP.setSOCKS5Proxy(socks5Proxy);
				} else if (options.jump_proxy_protocol.Value().isEmpty()) {
					if (options.jump_proxy_user.isDirty()) {
						logger.info("No value was specified for parameter 'jump_proxy_protocol'. As you specified a value for parameter 'jump_proxy_user' for authentication, I assume you meant 'socks5'.");
						AuthenticatedProxyType socks5Proxy = createAuthenticatedProxyTypeForJump(options);
						proxyForSFTP.setSOCKS5Proxy(socks5Proxy);
					} else {
						logger.info("Skipping grouping element 'ProxyForSFTP' as parameter 'jump_proxy_protocol' is not specified.");
						jumpFragment.setProxyForSFTP(null);
					}
				}
			} else {
				logger.warn("Invalid value specified for option 'jump_proxy_protocol'. Only values 'socks4' and 'socks5' are allowed in combination with Jump.");
			}
		}
		
		
		if (isJumpProxySpecified(options)) {
			ProxyForSFTP proxyForSFTP = new ProxyForSFTP();
			jumpFragment.setProxyForSFTP(proxyForSFTP);
			if (options.jump_proxy_protocol.isDirty()) {
				if (options.jump_proxy_protocol.Value().equalsIgnoreCase("socks4")) {
					UnauthenticatedProxyType socks4Proxy = createUnauthenticatedProxyTypeForJump(options);
					proxyForSFTP.setSOCKS4Proxy(socks4Proxy);
				} else if (options.jump_proxy_protocol.Value().equalsIgnoreCase("socks5")) {
					AuthenticatedProxyType socks5Proxy = createAuthenticatedProxyTypeForJump(options);
					proxyForSFTP.setSOCKS5Proxy(socks5Proxy);
				} else {
					logger.warn("Invalid value specified for option 'jump_proxy_protocol'. Only values 'socks4' and 'socks5' are allowed.");
				}
			} else {
				if (options.jump_proxy_user.isDirty()) {
					logger.warn("No 'jump_proxy_protocol' was specified. As you specified a 'jump_proxy_user' for authentication, I assume you meant to use 'SOCKS5Proxy'.");
					AuthenticatedProxyType socks5Proxy = createAuthenticatedProxyTypeForJump(options);
					proxyForSFTP.setSOCKS5Proxy(socks5Proxy);
				} else {
					logger.warn("No 'jump_proxy_protocol' was specified. As you did not specify a 'jump_proxy_user' for authentication either, I assume you meant to use 'SOCKS4Proxy'.");
					UnauthenticatedProxyType socks4Proxy = createUnauthenticatedProxyTypeForJump(options);
					proxyForSFTP.setSOCKS4Proxy(socks4Proxy);
				}
			}
		}
		// StrictHostKeyChecking does not seem to be implemented
		return jumpFragment;
	}



	private JumpFragmentRef createJumpFragmentRef(String jumpFragmentId) {
		JumpFragmentRef jumpFragmentRef = new JumpFragmentRef();
		jumpFragmentRef.setRef(jumpFragmentId);
		return jumpFragmentRef;
	}



	private boolean isJumpFragmentSpecified(JADEOptions options) {
		boolean returnValue = false;
		if (options.jump_host.isDirty() || options.jump_port.isDirty() || options.jump_user.isDirty() || options.jump_password.isDirty() || options.jump_ssh_auth_file.isDirty() ||
				options.jump_ssh_auth_method.isDirty() || options.jump_dir.isDirty() || isJumpProxySpecified(options)) {
			returnValue = true;
		}
		return returnValue;
	}



	private boolean isJumpProxySpecified(JADEOptions options) {
		boolean returnValue = false;
		if (options.jump_proxy_host.isDirty() || options.jump_proxy_port.isDirty() || options.jump_proxy_user.isDirty() || options.jump_proxy_password.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private AuthenticatedProxyType createAuthenticatedProxyType(SOSConnection2OptionsAlternate connectionOptions) {
		AuthenticatedProxyType authenticatedProxyType = new AuthenticatedProxyType();
		BasicConnectionType basicConnectionType = new BasicConnectionType();
		authenticatedProxyType.setBasicConnection(basicConnectionType);
		if (connectionOptions.proxy_host.isDirty()) {
			basicConnectionType.setHostname(connectionOptions.proxy_host.Value());
		} else {
			reportMissingMandatoryParameter(connectionOptions.getPrefix() + "proxy_host");
		}
		if (connectionOptions.proxy_port.isDirty()) {
			basicConnectionType.setPort(connectionOptions.proxy_port.value());
		}
		if (connectionOptions.proxy_user.isDirty() || connectionOptions.proxy_password.isDirty()) {
			BasicAuthenticationType basicAuthenticationType = new BasicAuthenticationType();
			authenticatedProxyType.setBasicAuthentication(basicAuthenticationType);
			if (connectionOptions.proxy_user.isDirty()) {
				basicAuthenticationType.setAccount(connectionOptions.proxy_user.Value());
			} else {
				reportMissingMandatoryParameter(connectionOptions.getPrefix() + "proxy_user");
			}
			if (connectionOptions.proxy_password.isDirty()) {
				basicAuthenticationType.setPassword(connectionOptions.proxy_password.Value());
			}
		}
		return authenticatedProxyType;
	}
	
	private AuthenticatedProxyType createAuthenticatedProxyTypeForJump(JADEOptions options) {
		AuthenticatedProxyType authenticatedProxyType = new AuthenticatedProxyType();
		BasicConnectionType basicConnectionType = new BasicConnectionType();
		authenticatedProxyType.setBasicConnection(basicConnectionType);
		if (options.jump_proxy_host.isDirty()) {
			basicConnectionType.setHostname(options.jump_proxy_host.Value());
		} else {
			reportMissingMandatoryParameter("jump_proxy_host");
		}
		if (options.jump_proxy_port.isDirty()) {
			basicConnectionType.setPort(Integer.parseInt(options.jump_proxy_port.Value()));
		}
		if (options.jump_proxy_user.isDirty() || options.jump_proxy_password.isDirty()) {
			BasicAuthenticationType basicAuthenticationType = new BasicAuthenticationType();
			authenticatedProxyType.setBasicAuthentication(basicAuthenticationType);
			if (options.jump_proxy_user.isDirty()) {
				basicAuthenticationType.setAccount(options.jump_proxy_user.Value());
			} else {
				reportMissingMandatoryParameter("jump_proxy_user");
			}
			if (options.jump_proxy_password.isDirty()) {
				basicAuthenticationType.setPassword(options.jump_proxy_password.Value());
			}
		}
		return authenticatedProxyType;
	}



	private UnauthenticatedProxyType createUnauthenticatedProxyType(SOSConnection2OptionsAlternate connectionOptions) {
		UnauthenticatedProxyType unauthenticatedProxyType = new UnauthenticatedProxyType();
		BasicConnectionType basicConnectionType = new BasicConnectionType();
		unauthenticatedProxyType.setBasicConnection(basicConnectionType);
		if (connectionOptions.proxy_host.isDirty()) {
			basicConnectionType.setHostname(connectionOptions.proxy_host.Value());
		} else {
			reportMissingMandatoryParameter(connectionOptions.getPrefix() + "proxy_host");
		}
		if (connectionOptions.proxy_port.isDirty()) {
			basicConnectionType.setPort(connectionOptions.proxy_port.value());
		}
		return unauthenticatedProxyType;
	}
	
	private UnauthenticatedProxyType createUnauthenticatedProxyTypeForJump(JADEOptions options) {
		UnauthenticatedProxyType unauthenticatedProxyType = new UnauthenticatedProxyType();
		BasicConnectionType basicConnectionType = new BasicConnectionType();
		unauthenticatedProxyType.setBasicConnection(basicConnectionType);
		if (options.jump_proxy_host.isDirty()) {
			basicConnectionType.setHostname(options.jump_proxy_host.Value());
		} else {
			reportMissingMandatoryParameter("jump_proxy_host");
		}
		if (options.jump_proxy_port.isDirty()) {
			basicConnectionType.setPort(Integer.parseInt(options.jump_proxy_port.Value()));
		}
		return unauthenticatedProxyType;
	}



	private boolean isProxySpecified(SOSConnection2OptionsAlternate connectionOptions) {
		boolean returnValue = false;
		if (connectionOptions.proxy_protocol.isDirty() || connectionOptions.proxy_host.isDirty() || connectionOptions.proxy_port.isDirty() || connectionOptions.proxy_user.isDirty() ||
				connectionOptions.proxy_password.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private SSHAuthenticationType createSSHAuthentication(SOSConnection2OptionsAlternate connectionOptions) {
		SSHAuthenticationType sshAuthentication = new SSHAuthenticationType();
		if (connectionOptions.user.isDirty()) {
			sshAuthentication.setAccount(connectionOptions.user.Value());
		} else {
			reportMissingMandatoryParameter(connectionOptions.getPrefix() + "user");
		}
		if (connectionOptions.ssh_auth_method.Value().equalsIgnoreCase("password")) {
			AuthenticationMethodPassword authenticationMethodPassword = new AuthenticationMethodPassword();
			authenticationMethodPassword.setPassword(connectionOptions.password.Value());
			sshAuthentication.setAuthenticationMethodPassword(authenticationMethodPassword);
		} else if (connectionOptions.ssh_auth_method.Value().equalsIgnoreCase("publickey")) {
			AuthenticationMethodPublickey authenticationMethodPublickey = new AuthenticationMethodPublickey();
			authenticationMethodPublickey.setAuthenticationFile(connectionOptions.ssh_auth_file.Value());
			if (connectionOptions.password.isDirty()) {
				authenticationMethodPublickey.setPassphrase(connectionOptions.password.Value());
				}
			sshAuthentication.setAuthenticationMethodPublickey(authenticationMethodPublickey);
		}
		return sshAuthentication;
	}



	private BasicConnectionType createBasicConnection(SOSConnection2OptionsAlternate connectionOptions) {
		BasicConnectionType basicConnection = new BasicConnectionType();
		basicConnection.setHostname(connectionOptions.host.Value());
		if (connectionOptions.port.isDirty()) {
			basicConnection.setPort(connectionOptions.port.value());
			}
		return basicConnection;
	}



	private SFTPFragmentRef createSFTPFragmentRef(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, String fragmentId) {
		SFTPFragmentRef sftpFragmentRef = new SFTPFragmentRef();
		sftpFragmentRef.setRef(fragmentId);
		// SFTPPreProcessing
		if (isPreProcessingSpecified(connectionOptions)) {
			SFTPPreProcessingType sftpPreProcessing = createSFTPPreProcessing(connectionOptions);
			sftpFragmentRef.setSFTPPreProcessing(sftpPreProcessing);
		}
		// SFTPPostProcessing
		if (isPostProcessingSpecified(connectionOptions)) {
			SFTPPostProcessingType sftpPostProcessing = createSFTPPostProcessing(connectionOptions);
			sftpFragmentRef.setSFTPPostProcessing(sftpPostProcessing);
		}
		// Rename
		if (isRenameSpecified(connectionOptions) || (connectionOptions.getPrefix().equalsIgnoreCase("target_") && isUnprefixedRenameSpecified(options))) {
			RenameType rename = createRename(options, connectionOptions);
			sftpFragmentRef.setRename(rename);
		}
		if (connectionOptions.use_zlib_compression.isDirty()) {
			if (connectionOptions.use_zlib_compression.isTrue()) {
				ZlibCompression zlibCompression = new ZlibCompression();
				sftpFragmentRef.setZlibCompression(zlibCompression);
				if (connectionOptions.zlib_compression_level.isDirty()) {
					zlibCompression.setZlibCompressionLevel(connectionOptions.zlib_compression_level.value());
				}
			} else {
				logger.info("Skipping grouping element 'ZlibCompression' as parameter '" + connectionOptions.getPrefix() + "use_zlib_compression' is set to false or not specified.");
			}
		}
		return sftpFragmentRef;
	}



	private SFTPPostProcessingType createSFTPPostProcessing(SOSConnection2OptionsAlternate connectionOptions) {
		SFTPPostProcessingType sftpPostProcessing = new SFTPPostProcessingType();
		if (connectionOptions.Post_Command.isDirty()) {
			sftpPostProcessing.setCommandAfterFile(connectionOptions.Post_Command.Value());
		}
		if (connectionOptions.PostTransferCommands.isDirty()) {
			sftpPostProcessing.setCommandAfterOperation(connectionOptions.PostTransferCommands.Value());
		}
		if (connectionOptions.TFN_Post_Command.isDirty()) {
			sftpPostProcessing.setCommandBeforeRename(connectionOptions.TFN_Post_Command.Value());
		}
		return sftpPostProcessing;	
	}



	private SFTPPreProcessingType createSFTPPreProcessing(SOSConnection2OptionsAlternate connectionOptions) {
		SFTPPreProcessingType sftpPreProcessing = new SFTPPreProcessingType();
		if (connectionOptions.Pre_Command.isDirty()) {
			sftpPreProcessing.setCommandBeforeFile(connectionOptions.Pre_Command.Value());
		}
		if (connectionOptions.PreTransferCommands.isDirty()) {
			sftpPreProcessing.setCommandBeforeOperation(connectionOptions.PreTransferCommands.Value());
		}
		return sftpPreProcessing;
	}



	private LocalSource createLocalSource(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions) {
		LocalSource localSource = new LocalSource();
		// LocalPreProcessing
		if (isLocalPreProcessingSpecified(connectionOptions)) {
			LocalPreProcessingType localPreProcessing = createLocalPreProcessing(connectionOptions);
			localSource.setLocalPreProcessing(localPreProcessing);
		}
		// LocalPostProcessing
		if (isLocalPostProcessingSpecified(connectionOptions)) {
			LocalPostProcessingType localPostProcessing = createLocalPostProcessing(connectionOptions);
			localSource.setLocalPostProcessing(localPostProcessing);
		}
		// Rename
		if (isRenameSpecified(connectionOptions) || (connectionOptions.getPrefix().equalsIgnoreCase("target_") && isUnprefixedRenameSpecified(options))) {
			RenameType rename = createRename(options, connectionOptions);
			localSource.setRename(rename);
		}
		// Zip
		if (connectionOptions.protocol.Value().equals("zip")) {
			localSource.setZip(true);
		}
		return localSource;
	}



	private HTTPSFragment createHTTPSFragment(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, String fragmentId, ProtocolFragments protocolFragments) {
		HTTPSFragment httpsFragment = new HTTPSFragment();
		httpsFragment.setName(fragmentId);
		// URLConnection
		URLConnectionType urlConnection = createURLConnection(connectionOptions);
		httpsFragment.setURLConnection(urlConnection);
		// AcceptUntrustedCertificate
		if (connectionOptions.accept_untrusted_certificate.isDirty()) {
			httpsFragment.setAcceptUntrustedCertificate(connectionOptions.accept_untrusted_certificate.value());
		}
		// BasicAuthentication
		BasicAuthenticationType basicAuthentication = createBasicAuthenticationForURLConnection(connectionOptions);
		httpsFragment.setBasicAuthentication(basicAuthentication);
		// JumpFragmentRef
		if (isJumpFragmentSpecified(options)) {
			String jumpFragmentId = getRandomFragmentId();
			JumpFragment jumpFragment = createJumpFragment(options, jumpFragmentId);
			protocolFragments.getJumpFragment().add(jumpFragment);
			JumpFragmentRef jumpFragmentRef = createJumpFragmentRef(jumpFragmentId);
			httpsFragment.setJumpFragmentRef(jumpFragmentRef);
		}
		// ProxyForHTTP
		if (isProxySpecified(connectionOptions)) {
			if (connectionOptions.proxy_protocol.Value().equalsIgnoreCase("http") || connectionOptions.proxy_protocol.Value().isEmpty()) {
				if (connectionOptions.proxy_protocol.Value().isEmpty()) {
					logger.info("No value was specified for parameter '" + connectionOptions.getPrefix() + "proxy_protocol'. I assume you meant 'http'.");
				}
				ProxyForHTTP proxyForHTTP = new ProxyForHTTP();
				httpsFragment.setProxyForHTTP(proxyForHTTP);
				// HTTPProxy
				AuthenticatedProxyType httpProxy = createAuthenticatedProxyType(connectionOptions);
				proxyForHTTP.setHTTPProxy(httpProxy);
			} else {
				logger.warn("Invalid value specified for option '" + connectionOptions.getPrefix() + "proxy_protocol'. Only value 'http' is allowed in combination with HTTPS.");
			}
		}
		return httpsFragment;
	}



	private HTTPSFragmentRef createHTTPSFragmentRef(String fragmentId) {
		HTTPSFragmentRef httpsFragmentRef = new HTTPSFragmentRef();
		httpsFragmentRef.setRef(fragmentId);
		return httpsFragmentRef;
	}



	private HTTPFragment createHTTPFragment(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, String fragmentId, ProtocolFragments protocolFragments) {
		HTTPFragment httpFragment = new HTTPFragment();
		httpFragment.setName(fragmentId);
		// URLConnection
		URLConnectionType urlConnection = createURLConnection(connectionOptions);
		httpFragment.setURLConnection(urlConnection);
		// BasicAuthentication
		BasicAuthenticationType basicAuthentication = createBasicAuthenticationForURLConnection(connectionOptions);
		httpFragment.setBasicAuthentication(basicAuthentication);
		// JumpFragmentRef
		if (isJumpFragmentSpecified(options)) {
			String jumpFragmentId = getRandomFragmentId();
			JumpFragment jumpFragment = createJumpFragment(options, jumpFragmentId);
			protocolFragments.getJumpFragment().add(jumpFragment);
			JumpFragmentRef jumpFragmentRef = createJumpFragmentRef(jumpFragmentId);
			httpFragment.setJumpFragmentRef(jumpFragmentRef);
		}
		// ProxyForHTTP
		if (isProxySpecified(connectionOptions)) {
			if (connectionOptions.proxy_protocol.Value().equalsIgnoreCase("http") || connectionOptions.proxy_protocol.Value().isEmpty()) {
				if (connectionOptions.proxy_protocol.Value().isEmpty()) {
					logger.warn("No value was specified for parameter '" + connectionOptions.getPrefix() + "proxy_protocol'. I assume you intended to use 'http'.");
				}
				ProxyForHTTP proxyForHTTP = new ProxyForHTTP();
				httpFragment.setProxyForHTTP(proxyForHTTP);
				// HTTPProxy
				AuthenticatedProxyType httpProxy = createAuthenticatedProxyType(connectionOptions);
				proxyForHTTP.setHTTPProxy(httpProxy);
			} else {
				logger.warn("Invalid value specified for option '" + connectionOptions.getPrefix() + "proxy_protocol'. Only value 'http' is allowed in combination with HTTP.");
			}
		}
		return httpFragment;
	}



	private HTTPFragmentRef createHTTPFragmentRef(String fragmentId) {
		HTTPFragmentRef httpFragmentRef = new HTTPFragmentRef();
		httpFragmentRef.setRef(fragmentId);

		return httpFragmentRef;
	}

	private CredentialStoreFragment createCredentialStoreFragment(SOSConnection2OptionsAlternate connectionOptions, String fragmentId) {
		CredentialStoreFragment credentialStoreFragment = new CredentialStoreFragment();
		credentialStoreFragment.setName(fragmentId);
		SOSCredentialStoreOptions credentialStoreOptions = connectionOptions.getCredentialStore();
		// CSFile
		if (credentialStoreOptions.CS_FileName.isDirty()) {
			credentialStoreFragment.setCSFile(credentialStoreOptions.CS_FileName.Value());
		} else {
			reportMissingMandatoryParameter("CSFile");
		}
		// CSAuthentication
		CSAuthentication csAuthentication = new CSAuthentication();
		credentialStoreFragment.setCSAuthentication(csAuthentication);
		// KeyFileAuthentication
		if (credentialStoreOptions.CS_AuthenticationMethod.Value().equalsIgnoreCase("privatekey")) {
			KeyFileAuthentication keyFileAuthentication = new KeyFileAuthentication();
			csAuthentication.setKeyFileAuthentication(keyFileAuthentication);
			// CSKeyFile
			if (credentialStoreOptions.CS_KeyFileName.isDirty()) {
				keyFileAuthentication.setCSKeyFile(credentialStoreOptions.CS_KeyFileName.Value());
			} else {
				reportMissingMandatoryParameter("CSKeyFile");
			}
			// CSPassword
			if (credentialStoreOptions.CS_password.isDirty()) {
				keyFileAuthentication.setCSPassword(credentialStoreOptions.CS_password.Value());
			}
		// PasswordAuthentication	
		} else if (credentialStoreOptions.CS_AuthenticationMethod.Value().equalsIgnoreCase("password") || !credentialStoreOptions.CS_AuthenticationMethod.isDirty()) {
			if (!credentialStoreOptions.CS_AuthenticationMethod.isDirty()) {
				logger.warn("No value was specified for parameter 'CS_AuthenticationMethod'. I assume you intended to use vale 'password'.");
			}
			PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
			csAuthentication.setPasswordAuthentication(passwordAuthentication);
			// CSPassword
			if (credentialStoreOptions.CS_password.isDirty()) {
				passwordAuthentication.setCSPassword(credentialStoreOptions.CS_password.Value());
			} else {
				reportMissingMandatoryParameter("CSPassword");
			}
		} else {
			logger.info("Skipping grouping element 'CSAuthentication' as parameter 'CS_AuthenticationMethod' is not valid.");
		}
		// CSEntryPath
		if (credentialStoreOptions.CS_KeyPath.isDirty()) {
			credentialStoreFragment.setCSEntryPath(credentialStoreOptions.CS_KeyPath.Value());
		} else {
			reportMissingMandatoryParameter("CSEntryPath");
		}
		// CSExportAttachment
		if (credentialStoreOptions.CS_ExportAttachment.isTrue()) {
			CSExportAttachment csExportAttachment = new CSExportAttachment();
			credentialStoreFragment.setCSExportAttachment(csExportAttachment);
			// CSExportedFile
			if (credentialStoreOptions.CS_ExportAttachment2FileName.isDirty()) {
				csExportAttachment.setCSExportedFile(credentialStoreOptions.CS_ExportAttachment2FileName.Value());
			} else {
				reportMissingMandatoryParameter("CSExportedFile");
			}
			// CSDeleteExportedFileOnExit
			if (credentialStoreOptions.CS_DeleteExportedFileOnExit.isDirty()) {
				csExportAttachment.setCSDeleteExportedFileOnExit(credentialStoreOptions.CS_DeleteExportedFileOnExit.value());
			}
			// CSOverwriteExportedFile
			if (credentialStoreOptions.CS_OverwriteExportedFile.isDirty()) {
				csExportAttachment.setCSOverwriteExportedFile(credentialStoreOptions.CS_OverwriteExportedFile.value());
			}
			// CSPermissionsForExportedFile
			if (credentialStoreOptions.CS_Permissions4ExportedFile.isDirty()) {
				csExportAttachment.setCSPermissionsForExportedFile(credentialStoreOptions.CS_Permissions4ExportedFile.Value());
			}
		}
		// CSStoreType may be neglected as the only implemented StoreType currently is KeePass
		return credentialStoreFragment;
	}

	private FTPSFragment createFTPSFragment(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, String fragmentId, ProtocolFragments protocolFragments) {
		FTPSFragment ftpsFragment = new FTPSFragment();
		ftpsFragment.setName(fragmentId);
		// BasicConnection
		BasicConnectionType basicConnection = new BasicConnectionType();
		ftpsFragment.setBasicConnection(basicConnection);
		basicConnection.setHostname(connectionOptions.host.Value());
		if (connectionOptions.port.isDirty()) {
			basicConnection.setPort(connectionOptions.port.value());
		}
		// BasicAuthentication
		BasicAuthenticationType basicAuthentication = createBasicAuthentication(connectionOptions);
		ftpsFragment.setBasicAuthentication(basicAuthentication);
		// AcceptUntrustedCertificate
		if (connectionOptions.accept_untrusted_certificate.isDirty()) {
			ftpsFragment.setAcceptUntrustedCertificate(connectionOptions.accept_untrusted_certificate.value());
		}
		// FTPSClientSecurity
		if (isFTPSClientSecuritySpecified(connectionOptions)) {
			FTPSClientSecurityType ftpsClientSecurityType = new FTPSClientSecurityType();
			ftpsFragment.setFTPSClientSecurity(ftpsClientSecurityType);
			// SecurityMode
			if (connectionOptions.ftps_client_security.isDirty()) {
				if (connectionOptions.ftps_client_security.equalsIgnoreCase("explicit") || connectionOptions.ftps_client_security.equalsIgnoreCase("implicit")) {
					ftpsClientSecurityType.setSecurityMode(connectionOptions.ftps_client_security.Value());	
				} else {
					logger.warn("Invalid value specified for option 'ftps_client_security'. Only values 'explicit' and 'implicit' are allowed.");
				}
			}
			// KeyStoreType
			if (connectionOptions.keystore_type.isDirty()) {
				if (connectionOptions.keystore_type.Value().equalsIgnoreCase("jks") || connectionOptions.keystore_type.Value().equalsIgnoreCase("jceks") ||
						connectionOptions.keystore_type.Value().equalsIgnoreCase("pkcs12") || connectionOptions.keystore_type.Value().equalsIgnoreCase("pkcs11") || 
						connectionOptions.keystore_type.Value().equalsIgnoreCase("dks")) {
					ftpsClientSecurityType.setKeyStoreType(connectionOptions.keystore_type.Value());
				} else {
					logger.warn("Invalid value specified for option 'keystore_type'. Only values 'JKS', 'JCEKS', 'PKCS12', 'PKCS11' and 'DKS' are allowed.");
				}
			}
			// KeyStoreFile
			if (connectionOptions.keystore_file.isDirty()) {
				ftpsClientSecurityType.setKeyStoreFile(connectionOptions.keystore_file.Value());
			}
			// KeyStorePassword
			if (connectionOptions.keystore_password.isDirty()) {
				ftpsClientSecurityType.setKeyStorePassword(connectionOptions.keystore_password.Value());
			}
		}
		// FTPSProtocol
		if (connectionOptions.FtpS_protocol.isDirty()) {
			ftpsFragment.setFTPSProtocol(connectionOptions.FtpS_protocol.Value());
		}
		// JumpFragmentRef
		if (isJumpFragmentSpecified(options)) {
			String jumpFragmentId = getRandomFragmentId();
			JumpFragment jumpFragment = createJumpFragment(options, jumpFragmentId);
			protocolFragments.getJumpFragment().add(jumpFragment);
			JumpFragmentRef jumpFragmentRef = createJumpFragmentRef(jumpFragmentId);
			ftpsFragment.setJumpFragmentRef(jumpFragmentRef);
		}
		// ProxyForFTPS
		if (isProxySpecified(connectionOptions)) {
			if (connectionOptions.proxy_protocol.Value().equalsIgnoreCase("socks4") || connectionOptions.proxy_protocol.Value().equalsIgnoreCase("socks5") || connectionOptions.proxy_protocol.Value().isEmpty()) {
				ProxyForFTPS proxyForFTPS = new ProxyForFTPS();
				ftpsFragment.setProxyForFTPS(proxyForFTPS);
				if (connectionOptions.proxy_protocol.Value().equalsIgnoreCase("socks4")) {
					UnauthenticatedProxyType socks4Proxy = createUnauthenticatedProxyType(connectionOptions);
					proxyForFTPS.setSOCKS4Proxy(socks4Proxy);
				} else if (connectionOptions.proxy_protocol.Value().equalsIgnoreCase("socks5")) {
					AuthenticatedProxyType socks5Proxy = createAuthenticatedProxyType(connectionOptions);
					proxyForFTPS.setSOCKS5Proxy(socks5Proxy);
				} else if (connectionOptions.proxy_protocol.Value().isEmpty()) {
					if (connectionOptions.proxy_user.isDirty()) {
						logger.warn("No value was specified for parameter '" + connectionOptions.getPrefix() + "proxy_protocol'. As you specified a value for parameter '" + connectionOptions.getPrefix() + "proxy_user' for authentication, I assume you meant 'socks5'.");
						AuthenticatedProxyType socks5Proxy = createAuthenticatedProxyType(connectionOptions);
						proxyForFTPS.setSOCKS5Proxy(socks5Proxy);
					} else {
						logger.info("Skipping grouping element 'ProxyForFTPS' as parameter '" + connectionOptions.getPrefix() + "proxy_protocol' is not specified.");
						ftpsFragment.setProxyForFTPS(null);
					}
				}
			} else {
				logger.warn("Invalid value specified for option '" + connectionOptions.getPrefix() + "proxy_protocol'. Only values 'socks4' and 'socks5' are allowed in combination with FTPS.");
			}
		}
		return ftpsFragment;
	}

	private boolean isFTPSClientSecuritySpecified(SOSConnection2OptionsAlternate connectionOptions) {
		boolean returnValue = false;
		if (connectionOptions.ftps_client_security.isDirty() || connectionOptions.keystore_type.isDirty() || connectionOptions.keystore_file.isDirty() ||
				connectionOptions.keystore_password.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private FTPSFragmentRef createFTPSFragmentRef(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, String fragmentId) {
		FTPSFragmentRef ftpsFragmentRef = new FTPSFragmentRef();
		ftpsFragmentRef.setRef(fragmentId);
		// Rename
		if (isRenameSpecified(connectionOptions) || (connectionOptions.getPrefix().equalsIgnoreCase("target_") && isUnprefixedRenameSpecified(options))) {
			RenameType rename = createRename(options, connectionOptions);
			ftpsFragmentRef.setRename(rename);
		}
		return ftpsFragmentRef;
	}

	private TransferOptions createTransferOptions(JADEOptions options) {
		TransferOptions transferOptions = new TransferOptions();
		if (options.BufferSize.isDirty()) {
			transferOptions.setBufferSize(options.BufferSize.value());
		}
		if (options.ConcurrentTransfer.isTrue()) {
			ConcurrencyType concurrentTransfer = new ConcurrencyType();
			transferOptions.setConcurrentTransfer(concurrentTransfer);
			if (options.MaxConcurrentTransfers.isDirty()) {
				concurrentTransfer.setMaxConcurrentTransfers(options.MaxConcurrentTransfers.value());
			}
		}
		if (options.transactional.isDirty()) {
			transferOptions.setTransactional(options.transactional.value());
		}
		return transferOptions;
	}



	private boolean isTransferOptionsSpecified(JADEOptions options) {
		boolean returnValue = false;
		if (options.BufferSize.isDirty() || options.ConcurrentTransfer.isDirty() || options.transactional.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private TargetFileOptions createTargetFileOptions(JADEOptions options) {
		TargetFileOptions targetFileOptions = new TargetFileOptions();
		// AppendFiles
		if (options.append_files.isDirty()) {
			targetFileOptions.setAppendFiles(options.append_files.value());
		}
		// Atomicity
		if (isAtomicitySpecified(options)) {
			AtomicityType atomicity = new AtomicityType();
			targetFileOptions.setAtomicity(atomicity);
			if (options.atomic_prefix.isDirty()) {
				atomicity.setAtomicPrefix(options.atomic_prefix.Value());
			}
			if (options.atomic_suffix.isDirty()) {
				atomicity.setAtomicSuffix(options.atomic_suffix.Value());
			}
		}
		// CheckSize
		if (options.check_size.isDirty()) {
			targetFileOptions.setCheckSize(options.check_size.value());
		}
		// CumulateFiles
		if (options.CumulateFiles.isTrue()) {
			CumulateFiles cumulateFiles = new CumulateFiles();
			targetFileOptions.setCumulateFiles(cumulateFiles);
			if (options.CumulativeFileSeparator.isDirty()) {
				cumulateFiles.setCumulativeFileSeparator(options.CumulativeFileSeparator.Value());
			}
			if (options.CumulativeFileName.isDirty()) {
				cumulateFiles.setCumulativeFilename(options.CumulativeFileName.Value());
			}
			if (options.CumulativeFileDelete.isDirty()) {
				cumulateFiles.setCumulativeFileDelete(options.CumulativeFileDelete.value());
			}
		} else {
			logger.info("Skipping grouping element 'CumulateFiles' as parameter 'CumulateFiles' is set to false or not specified.");
		}
		// CompressFiles
		if (options.compress_files.isTrue()) {
			CompressFiles compressFiles = new CompressFiles();
			if (options.compressed_file_extension.isDirty()) {
				compressFiles.setCompressedFileExtension(options.compressed_file_extension.Value());
			}
			targetFileOptions.setCompressFiles(compressFiles);
		}
		// TODO CheckIntegrityHash tbd
		if (options.CheckSecurityHash.isTrue() || options.CreateSecurityHashFile.isTrue()) { // CreateSecurityHashFile wird nicht gesetzt
			CheckIntegrityHash checkIntegrityHash = new CheckIntegrityHash();
			if (options.CreateSecurityHashFile.isDirty()) {
				checkIntegrityHash.setCreateIntegrityHashFile(options.CreateSecurityHashFile.value());
			}
			if (options.SecurityHashType.isDirty()) {
				checkIntegrityHash.setHashAlgorithm(options.SecurityHashType.Value());
			}
			targetFileOptions.setCheckIntegrityHash(checkIntegrityHash);
		}
		// KeepModificationDate
		if (options.KeepModificationDate.isDirty()) {
			targetFileOptions.setKeepModificationDate(options.KeepModificationDate.value());
		}
		// DisableMakeDirectories
		if (options.makeDirs.isDirty()) {
			logger.info("Replacing 'makeDirs=" + options.makeDirs.Value() + "' with 'DisableMakeDirectories=" + String.valueOf(!options.makeDirs.value()) + "'");
			targetFileOptions.setDisableMakeDirectories(!options.makeDirs.value());
		}
		// DisableOverwriteFiles
		if (options.overwrite_files.isDirty()) {
			logger.info("Replacing 'overwrite_files=" + options.overwrite_files.Value() + "' with 'DisableOverwriteFiles=" + String.valueOf(!options.overwrite_files.value()) + "'");
			targetFileOptions.setDisableOverwriteFiles(!options.overwrite_files.value());
		}
		return targetFileOptions;
	}



	private boolean isTargetFileOptionsSpecified(JADEOptions options) {
		boolean returnValue = false;
		if (options.append_files.isDirty() || isAtomicitySpecified(options) || options.check_size.isDirty() || options.CumulateFiles.isDirty() || 
				options.compress_files.isDirty() || options.CheckSecurityHash.isDirty() || options.KeepModificationDate.isDirty() || 
				options.makeDirs.isDirty() || options.overwrite_files.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private boolean isAtomicitySpecified(JADEOptions options) {
		boolean returnValue = false;
		if (options.atomic_prefix.isDirty() || options.atomic_suffix.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}


	private LocalPostProcessingType createLocalPostProcessing(SOSConnection2OptionsAlternate connectionOptions) {
		LocalPostProcessingType localPostProcessing = new LocalPostProcessingType();
		if (connectionOptions.Post_Command.isDirty()) {
			localPostProcessing.setCommandAfterFile(connectionOptions.Post_Command.Value());
		}
		if (connectionOptions.PostTransferCommands.isDirty()) {
			localPostProcessing.setCommandAfterOperation(connectionOptions.PostTransferCommands.Value());
		}
		if (connectionOptions.TFN_Post_Command.isDirty()) {
			localPostProcessing.setCommandBeforeRename(connectionOptions.TFN_Post_Command.Value());
		}
		return localPostProcessing;
	}



	private boolean isLocalPostProcessingSpecified(SOSConnection2OptionsAlternate connectionOptions) {
		boolean returnValue = false;
		if (connectionOptions.Post_Command.isDirty() || connectionOptions.PostTransferCommands.isDirty() || connectionOptions.TFN_Post_Command.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private LocalPreProcessingType createLocalPreProcessing(SOSConnection2OptionsAlternate connectionOptions) {
		LocalPreProcessingType localPreProcessing = new LocalPreProcessingType();
		if (connectionOptions.Pre_Command.isDirty()) {
			localPreProcessing.setCommandBeforeFile(connectionOptions.Pre_Command.Value());
		}
		if (connectionOptions.PreTransferCommands.isDirty()) {
			localPreProcessing.setCommandBeforeOperation(connectionOptions.PreTransferCommands.Value());
		}
		return localPreProcessing;
	}



	private boolean isLocalPreProcessingSpecified(SOSConnection2OptionsAlternate connectionOptions) {
		boolean returnValue = false;
		if (connectionOptions.Pre_Command.isDirty() || connectionOptions.PreTransferCommands.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private FTPFragment createFTPFragment(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, String fragmentId, ProtocolFragments protocolFragments) {
		FTPFragment ftpFragment = new FTPFragment();
		ftpFragment.setName(fragmentId);
		// BasicConnection
		BasicConnectionType basicConnection = new BasicConnectionType();
		ftpFragment.setBasicConnection(basicConnection);
		basicConnection.setHostname(connectionOptions.host.Value());
		if (connectionOptions.port.isDirty()) {
			basicConnection.setPort(connectionOptions.port.value());
		}
		// BasicAuthentication
		BasicAuthenticationType basicAuthentication = createBasicAuthentication(connectionOptions);
		ftpFragment.setBasicAuthentication(basicAuthentication);
		// JumpFragmentRef
		if (isJumpFragmentSpecified(options)) {
			String jumpFragmentId = getRandomFragmentId();
			JumpFragment jumpFragment = createJumpFragment(options, jumpFragmentId);
			protocolFragments.getJumpFragment().add(jumpFragment);
			JumpFragmentRef jumpFragmentRef = createJumpFragmentRef(jumpFragmentId);
			ftpFragment.setJumpFragmentRef(jumpFragmentRef);
		}
		// PassiveMode
		if (connectionOptions.passive_mode.isDirty()) {
			ftpFragment.setPassiveMode(connectionOptions.passive_mode.value());
		}
		// TransferMode
		if (connectionOptions.transfer_mode.isDirty()) {
			if (connectionOptions.transfer_mode.Value().equalsIgnoreCase("ascii") || connectionOptions.transfer_mode.Value().equalsIgnoreCase("binary")) {
				ftpFragment.setTransferMode(connectionOptions.transfer_mode.Value());
			} else {
				logger.warn("Invalid value specified for option '" + connectionOptions.getPrefix() + "transfer_mode'. Only values 'ascii' and 'binary' are allowed.");
			}
		}
		// ProxyForFTP
		if (isProxySpecified(connectionOptions)) {
			if (connectionOptions.proxy_protocol.Value().equalsIgnoreCase("http") || connectionOptions.proxy_protocol.Value().equalsIgnoreCase("socks4") || connectionOptions.proxy_protocol.Value().equalsIgnoreCase("socks5") || connectionOptions.proxy_protocol.Value().isEmpty()) {
				ProxyForFTP proxyForFTP = new ProxyForFTP();
				ftpFragment.setProxyForFTP(proxyForFTP);
				if (connectionOptions.proxy_protocol.Value().equalsIgnoreCase("http")) {
					AuthenticatedProxyType httpProxy = createAuthenticatedProxyType(connectionOptions);
					proxyForFTP.setHTTPProxy(httpProxy);
				} else if (connectionOptions.proxy_protocol.Value().equalsIgnoreCase("socks4")) {
					UnauthenticatedProxyType socks4Proxy = createUnauthenticatedProxyType(connectionOptions);
					proxyForFTP.setSOCKS4Proxy(socks4Proxy);
				} else if (connectionOptions.proxy_protocol.Value().equalsIgnoreCase("socks5")) {
					AuthenticatedProxyType socks5Proxy = createAuthenticatedProxyType(connectionOptions);
					proxyForFTP.setSOCKS5Proxy(socks5Proxy);
				} else if (connectionOptions.proxy_protocol.Value().isEmpty()) {
					logger.info("Skipping grouping element 'ProxyForFTP' as parameter '" + connectionOptions.getPrefix() + "proxy_protocol' is not specified.");
					ftpFragment.setProxyForFTP(null);
				}
			} else {
				logger.warn("Invalid value specified for option '" + connectionOptions.getPrefix() + "proxy_protocol'. Only values 'http', 'socks4' and 'socks5' are allowed in combination with FTP.");
			}
		}
		// ControlEncoding is not implemented. see https://kb.sos-berlin.com/display/PKB/JADE+Parameter+Reference+-+FTPFragment
		// CheckServerFeatures is not implemented. see https://kb.sos-berlin.com/display/PKB/JADE+Parameter+Reference+-+FTPFragment
		return ftpFragment;
	}



	private FTPFragmentRef createFTPFragmentRef(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, String fragmentId) {
		FTPFragmentRef ftpFragmentRef = new FTPFragmentRef();
		ftpFragmentRef.setRef(fragmentId);
		// FTPPreProcessing
		if (isPreProcessingSpecified(connectionOptions)) {
			FTPPreProcessingType ftpPreProcessing = createFTPPreProcessing(connectionOptions);
			ftpFragmentRef.setFTPPreProcessing(ftpPreProcessing);
		}
		// FTPPostProcessing
		if (isPostProcessingSpecified(connectionOptions)) {
			FTPPostProcessingType ftpPostProcessing = createFTPPostProcessing(connectionOptions);
			ftpFragmentRef.setFTPPostProcessing(ftpPostProcessing);
		}
		// Rename
		if (isRenameSpecified(connectionOptions) || (connectionOptions.getPrefix().equalsIgnoreCase("target_") && isUnprefixedRenameSpecified(options))) {
			RenameType rename = createRename(options, connectionOptions);
			ftpFragmentRef.setRename(rename);
		}
		return ftpFragmentRef;
	}



	private boolean isUnprefixedRenameSpecified(JADEOptions options) {
		boolean returnValue = false;
		if (options.replacement.isDirty() || options.replacing.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private FTPPreProcessingType createFTPPreProcessing(SOSConnection2OptionsAlternate connectionOptions) {
		FTPPreProcessingType ftpPreProcessing = new FTPPreProcessingType();
		if (connectionOptions.Pre_Command.isDirty()) {
			ftpPreProcessing.setCommandBeforeFile(connectionOptions.Pre_Command.Value());
		}
		if (connectionOptions.PreTransferCommands.isDirty()) {
			ftpPreProcessing.setCommandBeforeOperation(connectionOptions.PreTransferCommands.Value());
		}
		return ftpPreProcessing;
	}



	private boolean isPreProcessingSpecified(SOSConnection2OptionsAlternate connectionOptions) {
		boolean returnValue = false;
		if (connectionOptions.Pre_Command.isDirty() || connectionOptions.PreTransferCommands.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private boolean isRenameSpecified(SOSConnection2OptionsAlternate connectionOptions) {
		boolean returnValue = false;
		if (connectionOptions.ReplaceWhat.isDirty() && connectionOptions.ReplaceWith.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private boolean isPostProcessingSpecified(SOSConnection2OptionsAlternate connectionOptions) {
		boolean returnValue = false;
		if (connectionOptions.Post_Command.isDirty() || connectionOptions.PostTransferCommands.isDirty() || connectionOptions.TFN_Post_Command.isDirty()) {
			returnValue = true;
		}
		return returnValue;
	}



	private RenameType createRename(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions) {
		RenameType rename = new RenameType();
		if (connectionOptions.getPrefix().equalsIgnoreCase("source_")) {
			if (connectionOptions.ReplaceWhat.isDirty()) {
				rename.setReplaceWhat(connectionOptions.ReplaceWhat.Value());
			}
			if (connectionOptions.ReplaceWith.isDirty()) {
				rename.setReplaceWith(connectionOptions.ReplaceWith.Value());
			}
		} else if (connectionOptions.getPrefix().equalsIgnoreCase("target_")) {
			// ReplaceWhat
			if (options.ReplaceWhat.isDirty() && connectionOptions.ReplaceWhat.isDirty()) {
				logger.info("Both parameters 'target_ReplaceWhat' and 'ReplaceWhat' are specifed. Using 'target_ReplaceWhat'.");
			}
			if (connectionOptions.ReplaceWhat.isDirty()) {
				rename.setReplaceWhat(connectionOptions.ReplaceWhat.Value());
			} else if (options.ReplaceWhat.isDirty()) {
				if (!connectionOptions.ReplaceWhat.isDirty()) {
					logger.info("Parameter 'ReplaceWhat' was specified unprefixed. I assume you intended to use parameter 'target_ReplaceWhat'.");
				}
				rename.setReplaceWhat(options.ReplaceWhat.Value());
			}
			// ReplaceWith
			if (options.ReplaceWith.isDirty() && connectionOptions.ReplaceWith.isDirty()) {
				logger.info("Both parameters 'target_ReplaceWith' and 'ReplaceWith' are specifed. Using 'target_ReplaceWith'.");
			}
			if (connectionOptions.ReplaceWith.isDirty()) {
				rename.setReplaceWith(connectionOptions.ReplaceWith.Value());
			} else if (options.ReplaceWith.isDirty()) {
				if (!connectionOptions.ReplaceWith.isDirty()) {
					logger.info("Parameter 'ReplaceWith' was specified unprefixed. I assume you intended to use parameter 'target_ReplaceWith'.");
				}
				rename.setReplaceWith(options.ReplaceWith.Value());
			}
		}
		return rename;
	}



	private FTPPostProcessingType createFTPPostProcessing(SOSConnection2OptionsAlternate connectionOptions) {
		FTPPostProcessingType ftpPostProcessing = new FTPPostProcessingType();
		if (connectionOptions.Post_Command.isDirty()) {
			ftpPostProcessing.setCommandAfterFile(connectionOptions.Post_Command.Value());
		}
		if (connectionOptions.PostTransferCommands.isDirty()) {
			ftpPostProcessing.setCommandAfterOperation(connectionOptions.PostTransferCommands.Value());
		}
		if (connectionOptions.TFN_Post_Command.isDirty()) {
			ftpPostProcessing.setCommandBeforeRename(connectionOptions.TFN_Post_Command.Value());
		}
		return ftpPostProcessing;	
	}
	
	private void reportMissingMandatoryParameter(String parameterName, String message) {
		logger.warn("Mandatory paramter '" + parameterName + "' is missing. " + message);
		missingMandatoryParameter = true;
	}
	
	private void reportMissingMandatoryParameter(String parameterName) {
		reportMissingMandatoryParameter(parameterName, "");
	}
	
	private String getRandomFragmentId() {
		return UUID.randomUUID().toString();
	}



	private void writeXML(Object object, String outFilename) {
		logger.info("Writing converted file '" + outFilename + "'");
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, schemaLocation);

			jaxbMarshaller.marshal(object, new File(outFilename) );
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		logger.info("Done.");
	}
	
	private JADEOptions loadIniFile(String filepath, String profilename) {
		
		JADEOptions options = new JADEOptions();
		options.settings.Value(filepath);
		options.profile.Value(profilename);
		// Verhindert das Substituieren von Variablen (${USERPROFILE}, ${TEMP}, ...)
		options.gflgSubsituteVariables = false;
		options.ReadSettingsFile();
		
		return options;
	}
	
}
