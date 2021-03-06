package com.sos.jade.converter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.sos.CredentialStore.Options.SOSCredentialStoreOptions;
import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.Options.SOSOptionJadeOperation;
import com.sos.JSHelper.Options.SOSOptionTransferType;
import com.sos.JSHelper.io.Files.JSIniFile;
import com.sos.JSHelper.io.Files.SOSProfileSection;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.jade.converter.generated.*;

import sos.net.mail.options.SOSSmtpMailOptions;

public class IniToXmlConverter {

    private static final String schemaLocation = "http://www.sos-berlin.com/schema/yade/YADE_configuration_v1.0.xsd";
    private String iniFilePath = "";
    private boolean autoDetectProfiles = true;
    private String[] ignoredProfiles = null;
    private String[] forcedProfiles = null;
    private File outputDir = null;
    private File outputFile = null;
    private Set<String> profilesMissingMandatoryParameters = new HashSet<>();
    private Set<String> profilesNotCreated = new HashSet<>();
    private boolean missingMandatoryParameter = false;
    private boolean raiseMandatoryParameter = false;
    private Map<String, Integer> refsCounter = new HashMap<String, Integer>();
    private Set<String> includedFragments = new HashSet<String>();
    private Set<String> profilesWithOperation = new HashSet<String>();
    private Fragments fragments = new Fragments();
    public static Logger logger = LoggerFactory.getLogger(IniToXmlConverter.class);

    public IniToXmlConverter() {
        //
    }

    public static void main(String[] args) {
        try {
            IniToXmlConverter converter = new IniToXmlConverter();
            converter.handleArguments(args);
            if (converter.iniFilePath.isEmpty()) {
                throw new JobSchedulerException("No settings file was specified for conversion.");
            } else if (!new File(converter.iniFilePath).exists()) {
                throw new JobSchedulerException("The settings file specified for conversion could not be found.");
            }
            logger.info(String.format("The settings file specified for conversion is '%1$s'", converter.iniFilePath));
            String[] profilesToConvert = converter.detectProfiles();
            Object object = converter.convertIniFile(profilesToConvert);
            File outFile = converter.getOutFile();
            converter.writeXML(object, outFile);
            System.exit(0);
        } catch (Exception e) {
            logger.error("", e);
            System.exit(1);
        }
    }

    private File getOutFile() {
        if (outputFile != null) {
            return outputFile;
        }
        File iniFile = new File(iniFilePath);
        String outFileName = iniFile.getName().replaceFirst("\\.ini$", "") + ".xml";
        if (outputDir == null) {
            outputDir = iniFile.getParentFile();
        }
        return new File(outputDir, outFileName);
    }

    private void handleArguments(String[] args) {
        for (String argument : args) {
            if (argument.startsWith("-") && argument.contains("=")) {
                int separatorPosition = argument.indexOf("=");
                String argName = argument.substring(1, separatorPosition);
                String argValue = argument.substring(separatorPosition + 1);
                if ("settings".equalsIgnoreCase(argName)) {
                    iniFilePath = argValue;
                } else if ("autoDetectProfiles".equalsIgnoreCase(argName)) {
                    if ("false".equalsIgnoreCase(argValue)) {
                        autoDetectProfiles = false;
                    } else if ("true".equalsIgnoreCase(argValue)) {
                        autoDetectProfiles = true;
                    } else {
                        throw new IllegalArgumentException("Invalid value specified for option 'autoDetectProfiles'");
                    }
                } else if ("ignoredProfiles".equalsIgnoreCase(argName) && !argValue.isEmpty()) {
                    ignoredProfiles = argValue.split(";");
                } else if ("forcedProfiles".equalsIgnoreCase(argName) && !argValue.isEmpty()) {
                    forcedProfiles = argValue.split(";");
                } else if ("outputDir".equalsIgnoreCase(argName) && !argValue.isEmpty()) {
                    File file = new File(argValue);
                    if (file.exists() && file.isDirectory()) {
                        outputDir = file;
                    } else if (!file.exists() && file.mkdirs()) {
                        outputDir = file;
                    } else if (file.exists() && !file.isDirectory()) {
                        throw new IllegalArgumentException(String.format(
                                "Invalid value specified for option %1$s. '%2$s' already exists and it is not a directory.", argName, argValue));
                    }
                    if (!file.exists()) {
                        throw new IllegalArgumentException(String.format("Invalid value specified for option %1$s. '%2$s' doesn't exist.", argName,
                                argValue));
                    }
                } else if ("outputFile".equalsIgnoreCase(argName) && !argValue.isEmpty()) {
                    File file = new File(argValue);
                    File fileParent = file.getParentFile();
                    if (fileParent.exists() && fileParent.isDirectory()) {
                        outputFile = file;
                    } else if (!fileParent.exists() && fileParent.mkdirs()) {
                        outputFile = file;
                    } else if (file.exists() && !file.isDirectory()) {
                        throw new IllegalArgumentException(String.format(
                                "Invalid value specified for option %1$s. '%2$s' already exists and it is not a directory.", argName,
                                fileParent.getAbsolutePath()));
                    }
                    if (!fileParent.exists()) {
                        throw new IllegalArgumentException(String.format("Invalid value specified for option %1$s. '%2$s' doesn't exist.", argName,
                                fileParent.getAbsolutePath()));
                    }
                }
            }
        }
    }

    private Object convertIniFile(String[] profilesToConvert) {
        Configurations configurations = new Configurations();
        configurations.setFragments(fragments);
        ProtocolFragments protocolFragments = new ProtocolFragments();
        fragments.setProtocolFragments(protocolFragments);
        Profiles profiles = new Profiles();
        configurations.setProfiles(profiles);
        List<Profile> profileList = profiles.getProfile();
        for (String profilename : profilesToConvert) {
            raiseMandatoryParameter = false;
            missingMandatoryParameter = false;
            try {
                logger.info("----- Starting conversion of profile '" + profilename + "' -----");
                JADEOptions options = loadIniFile(iniFilePath, profilename);
                String profilenameWithoutPrefix = "";
                if (profilename.contains("@")) {
                    profilenameWithoutPrefix = profilename.substring(profilename.indexOf("@") + 1);
                    String prefix = profilename.substring(0, profilename.indexOf("@") + 1);
                    logger.info("The profile name contains the invalid character '@'. I assume this is caused by conversion from .xml to .ini format."
                            + " The prefix '" + prefix + "' will be removed.");
                }
                Profile profile = new Profile();
                if (profilenameWithoutPrefix.isEmpty()) {
                    profile.setProfileId(profilename);
                } else {
                    profile.setProfileId(profilenameWithoutPrefix);
                }
                Operation operation = new Operation();
                profile.setOperation(operation);
                String operationValue = options.operation.Value();
                if ("copy".equalsIgnoreCase(operationValue)) {
                    Copy copy = new Copy();
                    operation.setCopy(copy);
                    CopySource copySource = createCopySource(options, false);
                    copy.setCopySource(copySource);
                    CopyTarget copyTarget = createCopyTarget(options, false);
                    copy.setCopyTarget(copyTarget);
                    if (isTransferOptionsSpecified(options)) {
                        TransferOptions transferOptions = createTransferOptions(options);
                        copy.setTransferOptions(transferOptions);
                    }
                } else if ("move".equalsIgnoreCase(operationValue)) {
                    // Move
                    Move move = new Move();
                    operation.setMove(move);
                    // MoveSource
                    MoveSource moveSource = createMoveSource(options, false);
                    move.setMoveSource(moveSource);
                    // MoveTarget
                    MoveTarget moveTarget = createMoveTarget(options, false);
                    move.setMoveTarget(moveTarget);
                    // TransferOptions
                    if (isTransferOptionsSpecified(options)) {
                        TransferOptions transferOptions = createTransferOptions(options);
                        move.setTransferOptions(transferOptions);
                    }
                } else if ("remove".equalsIgnoreCase(operationValue)) {
                    // Remove
                    Remove remove = new Remove();
                    operation.setRemove(remove);
                    // RemoveSource
                    RemoveSource removeSource = createRemoveSource(options);
                    remove.setRemoveSource(removeSource);
                } else if ("getlist".equalsIgnoreCase(operationValue)) {
                    // GetList
                    GetList getList = new GetList();
                    operation.setGetList(getList);
                    // GetListSource
                    GetListSource getListSource = createGetListSource(options);
                    getList.setGetListSource(getListSource);
                } else if ("copytointernet".equalsIgnoreCase(operationValue)) {
                    if (options.remove_files.isTrue()) {
                        // Move
                        Move move = new Move();
                        operation.setMove(move);
                        // MoveSource
                        MoveSource moveSource = createMoveSource(options, false);
                        move.setMoveSource(moveSource);
                        // MoveTarget
                        MoveTarget moveTarget = createMoveTarget(options, true); // Jump
                        move.setMoveTarget(moveTarget);
                        // TransferOptions
                        if (isTransferOptionsSpecified(options)) {
                            TransferOptions transferOptions = createTransferOptions(options);
                            move.setTransferOptions(transferOptions);
                        }
                    } else {
                        // Copy
                        Copy copy = new Copy();
                        operation.setCopy(copy);
                        // CopySource
                        CopySource copySource = createCopySource(options, false);
                        copy.setCopySource(copySource);
                        // CopyTarget
                        CopyTarget copyTarget = createCopyTarget(options, true); // Jump
                        copy.setCopyTarget(copyTarget);
                        // TransferOptions
                        if (isTransferOptionsSpecified(options)) {
                            TransferOptions transferOptions = createTransferOptions(options);
                            copy.setTransferOptions(transferOptions);
                        }
                    }
                } else if ("copyfrominternet".equalsIgnoreCase(operationValue)) {
                    if (options.remove_files.isTrue()) {
                        // Move
                        Move move = new Move();
                        operation.setMove(move);
                        // MoveSource
                        MoveSource moveSource = createMoveSource(options, true);
                        move.setMoveSource(moveSource);
                        // MoveTarget
                        MoveTarget moveTarget = createMoveTarget(options, false);
                        move.setMoveTarget(moveTarget);
                        // TransferOptions
                        if (isTransferOptionsSpecified(options)) {
                            TransferOptions transferOptions = createTransferOptions(options);
                            move.setTransferOptions(transferOptions);
                        }
                    } else {
                        // Copy
                        Copy copy = new Copy();
                        operation.setCopy(copy);
                        // CopySource
                        CopySource copySource = createCopySource(options, true);
                        copy.setCopySource(copySource);
                        // CopyTarget
                        CopyTarget copyTarget = createCopyTarget(options, false);
                        copy.setCopyTarget(copyTarget);
                        // TransferOptions
                        if (isTransferOptionsSpecified(options)) {
                            TransferOptions transferOptions = createTransferOptions(options);
                            copy.setTransferOptions(transferOptions);
                        }
                    }
                } else if ("send".equalsIgnoreCase(operationValue)) {
                    convertFromSendOperation(options);
                    if (options.remove_files.isTrue()) {
                        logger.info("Operation 'send' is deprecated. Trying to convert to a profile with operation 'move'.");
                        // Move
                        Move move = new Move();
                        operation.setMove(move);
                        // MoveSource
                        MoveSource moveSource = createMoveSource(options, false);
                        move.setMoveSource(moveSource);
                        // MoveTarget
                        MoveTarget moveTarget = createMoveTarget(options, false);
                        move.setMoveTarget(moveTarget);
                        // TransferOptions
                        if (isTransferOptionsSpecified(options)) {
                            TransferOptions transferOptions = createTransferOptions(options);
                            move.setTransferOptions(transferOptions);
                        }
                    } else {
                        logger.info("Operation 'send' is deprecated. Trying to convert to a profile with operation 'copy'.");
                        // Copy
                        Copy copy = new Copy();
                        operation.setCopy(copy);
                        // CopySource
                        CopySource copySource = createCopySource(options, false);
                        copy.setCopySource(copySource);
                        // CopyTarget
                        CopyTarget copyTarget = createCopyTarget(options, false);
                        copy.setCopyTarget(copyTarget);
                        // TransferOptions
                        if (isTransferOptionsSpecified(options)) {
                            TransferOptions transferOptions = createTransferOptions(options);
                            copy.setTransferOptions(transferOptions);
                        }
                    }
                } else if ("receive".equalsIgnoreCase(operationValue)) {
                    convertFromReceiveOperation(options);
                    if (options.remove_files.isTrue()) {
                        logger.info("Operation 'receive' is deprecated. Trying to convert to a profile with operation 'move'.");
                        // Move
                        Move move = new Move();
                        operation.setMove(move);
                        // MoveSource
                        MoveSource moveSource = createMoveSource(options, false);
                        move.setMoveSource(moveSource);
                        // MoveTarget
                        MoveTarget moveTarget = createMoveTarget(options, false);
                        move.setMoveTarget(moveTarget);
                        // TransferOptions
                        if (isTransferOptionsSpecified(options)) {
                            TransferOptions transferOptions = createTransferOptions(options);
                            move.setTransferOptions(transferOptions);
                        }
                    } else {
                        logger.info("Operation 'receive' is deprecated. Trying to convert to a profile with operation 'copy'.");
                        // Copy
                        Copy copy = new Copy();
                        operation.setCopy(copy);
                        // CopySource
                        CopySource copySource = createCopySource(options, false);
                        copy.setCopySource(copySource);
                        // CopyTarget
                        CopyTarget copyTarget = createCopyTarget(options, false);
                        copy.setCopyTarget(copyTarget);
                        // TransferOptions
                        if (isTransferOptionsSpecified(options)) {
                            TransferOptions transferOptions = createTransferOptions(options);
                            copy.setTransferOptions(transferOptions);
                        }
                    }
                } else if ("zip".equalsIgnoreCase(operationValue)) {
                    logger.info("Value 'zip' is not allowed for parameter 'operation'.");
                    logger.info("----- Skipped conversion of profile '" + profilename + "' -----");
                    continue;
                }
                // Client
                if (options.mandator.isDirty()) {
                    Client client = new Client();
                    profile.setClient(client);
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
                    logger.debug("Skipping grouping element 'JobScheduler' because of parameter 'create_order' is set to false or not specified.");
                }
                // Logging
                if (isLoggingSpecified(options)) {
                    profile.setLogging(createLogging(options));
                }
                // NotificationTriggers
                if (isNotificationTriggersSpecified(options)) {
                    NotificationTriggers notificationTriggers = new NotificationTriggers();
                    profile.setNotificationTriggers(notificationTriggers);
                    // OnSuccess
                    if (options.mail_on_success.value()) {
                        SOSSmtpMailOptions mailOptions = options.getMailOptions().getOptions(SOSSmtpMailOptions.enuMailClasses.MailOnSuccess);
                        notificationTriggers.setOnSuccess(getNotificationTrigger(mailOptions));
                    }
                    // OnError
                    if (options.mail_on_error.value()) {
                        SOSSmtpMailOptions mailOptions = options.getMailOptions().getOptions(SOSSmtpMailOptions.enuMailClasses.MailOnError);
                        notificationTriggers.setOnError(getNotificationTrigger(mailOptions));
                    }
                    // OnEmptyFiles
                    if (options.mail_on_empty_files.value()) {
                        SOSSmtpMailOptions mailOptions = options.getMailOptions().getOptions(SOSSmtpMailOptions.enuMailClasses.MailOnEmptyFiles);
                        notificationTriggers.setOnEmptyFiles(getNotificationTrigger(mailOptions));
                    }
                }
                // Notifications
                if (isNotificationsSpecified(options)) {
                    NotificationType notifications = new NotificationType();
                    profile.setNotifications(notifications);
                    // BackgroundServiceFragmentRefs
                    if (isBackgroundServiceSpecified(options)) {
                        // BackgroundServiceFragment
                        BackgroundServiceFragment backgroundServiceFragment = createBackgroundServiceFragment(options);
                        // BackgroundServiceFragmentRef
                        BackgroundServiceFragmentRef backgroundServiceFragmentRef = new BackgroundServiceFragmentRef();
                        backgroundServiceFragmentRef.setRef(getBackgroundServiceFragmentRefId(backgroundServiceFragment));
                        notifications.setBackgroundServiceFragmentRef(backgroundServiceFragmentRef);
                    }
                    // MailServerFragmentRef
                    if (isMailServerFragmentSpecified(options.getMailOptions()) && isNotificationTriggersSpecified(options)) {
                        // MailServerFragment
                        MailServerFragment mailServerFragment = createMailServerFragment(options.getMailOptions());
                        // MailServerFragmentRef
                        MailServerFragmentRef mailServerFragmentRef = new MailServerFragmentRef();
                        mailServerFragmentRef.setRef(getMailServerFragmentRefId(mailServerFragment));
                        notifications.setMailServerFragmentRef(mailServerFragmentRef);
                    }
                }
                profileList.add(profile);
            } catch (Exception e) {
                logger.error(String.format("error in profile [%1$s]: %2$s", profilename, e.toString()));
            }
            logger.info(String.format("----- Finished conversion of profile: '%1$s' -----", profilename));
            if (missingMandatoryParameter) {
                profilesMissingMandatoryParameters.add(profilename);
            }
            if (raiseMandatoryParameter) {
                profilesNotCreated.add(profilename);
            }
        }
        logger.info("Finished conversion of profiles.");
        if (!profilesNotCreated.isEmpty()) {
            String log = "The following profiles are not created because of missing mandatory parameters:";
            for (String s : profilesNotCreated) {
                log += System.lineSeparator() + s;
            }
            logger.error(log);
        }
        if (!profilesMissingMandatoryParameters.isEmpty()) {
            String log = "The following profiles have missing mandatory parameters and hence may be invalid:";
            for (String s : profilesMissingMandatoryParameters) {
                log += System.lineSeparator() + s;
            }
            logger.warn(log);
        }
        return configurations;
    }

    private ProtocolFragments getProtocolFragments() {
        ProtocolFragments protocolFragments = fragments.getProtocolFragments();
        if (protocolFragments == null) {
            protocolFragments = new ProtocolFragments();
            fragments.setProtocolFragments(protocolFragments);
        }
        return protocolFragments;
    }

    private CredentialStoreFragments getCredentialStoreFragments() {
        CredentialStoreFragments credentialStoreFragments = fragments.getCredentialStoreFragments();
        if (credentialStoreFragments == null) {
            credentialStoreFragments = new CredentialStoreFragments();
            fragments.setCredentialStoreFragments(credentialStoreFragments);
        }
        return credentialStoreFragments;
    }

    private NotificationFragments getNotificationFragments() {
        NotificationFragments notificationFragments = fragments.getNotificationFragments();
        if (notificationFragments == null) {
            notificationFragments = new NotificationFragments();
            fragments.setNotificationFragments(notificationFragments);
        }
        return notificationFragments;
    }

    private MailServerFragments getMailServerFragments() {
        MailServerFragments mailServerFragments = fragments.getMailServerFragments();
        if (mailServerFragments == null) {
            mailServerFragments = new MailServerFragments();
            fragments.setMailServerFragments(mailServerFragments);
        }
        return mailServerFragments;
    }

    private MailServerFragment createMailServerFragment(SOSSmtpMailOptions mailOptions) {
        MailServerFragment mailServerFragment = new MailServerFragment();
        if (isMailHostSpecified(mailOptions)) {
            mailServerFragment.setMailHost(createMailHost(mailOptions));
        }
        if (mailOptions.queue_directory.isDirty()) {
            mailServerFragment.setQueueDirectory(mailOptions.queue_directory.Value());
        }
        return mailServerFragment;
    }

    private BackgroundServiceFragment createBackgroundServiceFragment(JADEOptions options) {
        BackgroundServiceFragment backgroundServiceFragment = new BackgroundServiceFragment();
        if (options.BackgroundServiceHost.isDirty()) {
            backgroundServiceFragment.setBackgroundServiceHost(options.BackgroundServiceHost.Value());
        }
        if (options.BackgroundServicePort.isDirty()) {
            backgroundServiceFragment.setBackgroundServicePort(options.BackgroundServicePort.value());
        }
        if (options.BackgroundServiceJobChainName.isDirty()) {
            backgroundServiceFragment.setBackgroundServiceJobChainName(options.BackgroundServiceJobChainName.Value());
        }
        if (options.Scheduler_Transfer_Method.isDirty()) {
            backgroundServiceFragment.setBackgroundServiceProtocol(options.Scheduler_Transfer_Method.Value());
        }
        return backgroundServiceFragment;
    }

    private NotificationTriggerType getNotificationTrigger(SOSSmtpMailOptions options) {
        NotificationTriggerType notificationTriggerType = new NotificationTriggerType();
        MailFragment mailFragment = createMailFragment(options);
        MailFragmentRef mailFragmentRef = new MailFragmentRef();
        mailFragmentRef.setRef(getMailFragmentRefId(mailFragment));
        notificationTriggerType.setMailFragmentRef(mailFragmentRef);
        return notificationTriggerType;
    }

    private void convertFromSendOperation(JADEOptions options) {
        SOSConnection2OptionsAlternate sourceOptions = options.Source();
        SOSConnection2OptionsAlternate targetOptions = options.Target();
        sourceOptions.protocol.Value(SOSOptionTransferType.enuTransferTypes.local);
        if (options.remove_files.isTrue()) {
            options.operation.Value(SOSOptionJadeOperation.enuJadeOperations.move);
        } else {
            options.operation.Value(SOSOptionJadeOperation.enuJadeOperations.copy);
        }
        if (options.Pre_Command.isDirty()) {
            targetOptions.Pre_Command.Value(options.Pre_Command.Value());
        }
        if (options.PreTransferCommands.isDirty()) {
            targetOptions.PreTransferCommands.Value(options.PreTransferCommands.Value());
        }
        if (options.Post_Command.isDirty()) {
            targetOptions.Post_Command.Value(options.Post_Command.Value());
        }
        if (options.PostTransferCommands.isDirty()) {
            targetOptions.PostTransferCommands.Value(options.PostTransferCommands.Value());
        }
        if (options.TFN_Post_Command.isDirty()) {
            targetOptions.TFN_Post_Command.Value(options.TFN_Post_Command.Value());
        }
        if (options.replacement.isDirty()) {
            targetOptions.replacement.Value(options.replacement.Value());
        }
        if (options.replacing.isDirty()) {
            targetOptions.replacing.Value(options.replacing.Value());
        }
        if (options.local_dir.isDirty()) {
            sourceOptions.Directory.Value(options.local_dir.Value());
        }
        if (options.protocol.isDirty()) {
            targetOptions.protocol.Value(options.protocol.Value());
        } else {
            targetOptions.protocol.Value("ftp");
        }
        if (options.host.isDirty()) {
            targetOptions.host.Value(options.host.Value());
            targetOptions.host.setNotDirty();
        }
        if (options.port.isDirty()) {
            targetOptions.port.value(options.port.value());
            targetOptions.port.setNotDirty();
        }
        if (options.user.isDirty()) {
            targetOptions.user.Value(options.user.Value());
        }
        if (options.password.isDirty()) {
            targetOptions.password.Value(options.password.Value());
        }
        if (options.passive_mode.isDirty()) {
            targetOptions.passive_mode.value(options.passive_mode.value());
        }
        if (options.transfer_mode.isDirty()) {
            targetOptions.transfer_mode.Value(options.transfer_mode.Value());
        }
        if (options.ssh_auth_method.isDirty()) {
            targetOptions.ssh_auth_method.Value(options.ssh_auth_method.Value());
        }
        if (options.ssh_auth_file.isDirty()) {
            targetOptions.ssh_auth_file.Value(options.ssh_auth_file.Value());
        }
        if (options.remote_dir.isDirty()) {
            targetOptions.Directory.Value(options.remote_dir.Value());
        }
        if (options.makeDirs.isDirty()) {
            targetOptions.makeDirs.Value(options.makeDirs.Value());
        }
    }

    private void convertFromReceiveOperation(JADEOptions options) {
        SOSConnection2OptionsAlternate sourceOptions = options.Source();
        SOSConnection2OptionsAlternate targetOptions = options.Target();
        targetOptions.protocol.Value(SOSOptionTransferType.enuTransferTypes.local);
        if (options.remove_files.isTrue()) {
            options.operation.Value(SOSOptionJadeOperation.enuJadeOperations.move);
        } else {
            options.operation.Value(SOSOptionJadeOperation.enuJadeOperations.copy);
        }
        if (options.Pre_Command.isDirty()) {
            targetOptions.Pre_Command.Value(options.Pre_Command.Value());
        }
        if (options.PreTransferCommands.isDirty()) {
            targetOptions.PreTransferCommands.Value(options.PreTransferCommands.Value());
        }
        if (options.Post_Command.isDirty()) {
            targetOptions.Post_Command.Value(options.Post_Command.Value());
        }
        if (options.PostTransferCommands.isDirty()) {
            targetOptions.PostTransferCommands.Value(options.PostTransferCommands.Value());
        }
        if (options.TFN_Post_Command.isDirty()) {
            targetOptions.TFN_Post_Command.Value(options.TFN_Post_Command.Value());
        }
        if (options.replacement.isDirty()) {
            targetOptions.replacement.Value(options.replacement.Value());
        }
        if (options.replacing.isDirty()) {
            targetOptions.replacing.Value(options.replacing.Value());
        }
        if (options.local_dir.isDirty()) {
            targetOptions.Directory.Value(options.local_dir.Value());
        }
        if (options.protocol.isDirty()) {
            sourceOptions.protocol.Value(options.protocol.Value());
        } else {
            sourceOptions.protocol.Value("ftp");
        }
        if (options.host.isDirty()) {
            sourceOptions.host.Value(options.host.Value());
            options.host.setNotDirty();
        }
        if (options.port.isDirty()) {
            sourceOptions.port.value(options.port.value());
            options.port.setNotDirty();
        }
        if (options.user.isDirty()) {
            sourceOptions.user.Value(options.user.Value());
        }
        if (options.password.isDirty()) {
            sourceOptions.password.Value(options.password.Value());
        }
        if (options.passive_mode.isDirty()) {
            sourceOptions.passive_mode.value(options.passive_mode.value());
        }
        if (options.transfer_mode.isDirty()) {
            sourceOptions.transfer_mode.Value(options.transfer_mode.Value());
        }
        if (options.ssh_auth_method.isDirty()) {
            sourceOptions.ssh_auth_method.Value(options.ssh_auth_method.Value());
        }
        if (options.ssh_auth_file.isDirty()) {
            sourceOptions.ssh_auth_file.Value(options.ssh_auth_file.Value());
        }
        if (options.remote_dir.isDirty()) {
            sourceOptions.Directory.Value(options.remote_dir.Value());
        }
        if (options.makeDirs.isDirty()) {
            targetOptions.makeDirs.Value(options.makeDirs.Value());
        }
    }

    private boolean isCreateOrderSpecified(JADEOptions options) {
        boolean returnValue = false;
        if (options.create_order.isTrue() || options.order_jobchain_name.isDirty() || options.create_orders_for_all_files.isDirty()
                || options.next_state.isDirty() || options.MergeOrderParameter.isDirty()) {
            returnValue = true;
        }
        return returnValue;
    }

    private String[] detectProfiles() {
        String[] profilesToConvert = null;
        JSIniFile inifile = new JSIniFile(iniFilePath);
        Set<String> sectionsCaseSensitive = new HashSet<String>();
        for (Map.Entry<String, SOSProfileSection> entry : inifile.Sections().entrySet()) {
            sectionsCaseSensitive.add(entry.getValue().strSectionName);
        }
        if (!autoDetectProfiles) {
            logger.info("Automatic detection of profiles was turned off. Using only manually specified profiles.");
            String profiles = "";
            for (String profile : forcedProfiles) {
                if (sectionsCaseSensitive.contains(profile)) {
                    profiles += ";" + profile;
                } else {
                    logger.warn("Manually specified profile '" + profile + "' could not be found in settings file.");
                }
            }
            if (!profiles.isEmpty()) {
                profilesToConvert = profiles.substring(1).split(";");
            } else {
                logger.warn("No profiles could be detected in settings file.");
            }
            logger.info("The following profiles were specified:" + profiles.replace(";", " "));
        } else {
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
            detectIncludedSectionsAndSectionsWithOperation(sectionsCaseSensitive);
            String profiles = "";
            for (String section : sectionsCaseSensitive) {
                // check if forced
                boolean forced = false;
                if (forcedProfiles != null) {
                    for (String profile : forcedProfiles) {
                        if (profile.equals(section)) {
                            profiles += ";" + profile;
                            forced = true;
                            break;
                        }
                    }
                }
                // check if ignored
                boolean ignored = false;
                if (ignoredProfiles != null && !forced) {
                    for (String profile : ignoredProfiles) {
                        if (profile.equals(section)) {
                            ignored = true;
                            break;
                        }
                    }
                    // The [global] profile may not be detected as a fragment
                    // because it is never explicitly included. For this reason
                    // it will be added to the ignored profiles, but only if it
                    // is not forced
                } else if ("global".equalsIgnoreCase(section) && !forced) {
                    ignored = true;
                }
                if (!forced && !ignored && isProfile(section)) {
                    profiles += ";" + section;
                }
            }
            if (!profiles.isEmpty()) {
                profiles = profiles.substring(1);
                profilesToConvert = profiles.split(";");
                String log = "The following profiles were detected:";
                for (String s : profilesToConvert) {
                    log += System.lineSeparator() + s;
                }
                logger.info(log);
            } else {
                logger.warn("No profiles could be detected in settings file.");
            }
        }
        return profilesToConvert;
    }

    private void detectIncludedSectionsAndSectionsWithOperation(Set<String> sections) {
        for (String section : sections) {
            JADEOptions options = loadIniFile(iniFilePath, section);
            if (options.operation.isDirty()) {
                profilesWithOperation.add(section);
            }
            String[] includes = options.include.Value().split(",");
            for (String include : includes) {
                if (!includedFragments.contains(include.trim())) {
                    includedFragments.add(include.trim());
                }
            }
            includes = options.Source().include.Value().split(",");
            for (String include : includes) {
                if (!includedFragments.contains(include.trim())) {
                    includedFragments.add(include.trim());
                }
            }
            includes = options.Target().include.Value().split(",");
            for (String include : includes) {
                if (!includedFragments.contains(include.trim())) {
                    includedFragments.add(include.trim());
                }
            }
            includes = options.Source().Alternatives().include.Value().split(",");
            for (String include : includes) {
                if (!includedFragments.contains(include.trim())) {
                    includedFragments.add(include.trim());
                }
            }
            includes = options.Target().Alternatives().include.Value().split(",");
            for (String include : includes) {
                if (!includedFragments.contains(include.trim())) {
                    includedFragments.add(include.trim());
                }
            }
        }
    }

    private boolean isProfile(String key) {
        boolean returnValue = true;
        // a section is considered a profile if it is not being referenced by an
        // include and if it contains an operation
        if (profilesWithOperation.contains(key)) {
            // Operation is specified check for include
            if (includedFragments.contains(key)) {
                returnValue = false;
            }
        } else {
            returnValue = false;
        }
        return returnValue;
    }

    private MailFragment createMailFragment(SOSSmtpMailOptions mailOptions) {
        MailFragment mailFragment = new MailFragment();
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
        return mailFragment;
    }

    private MailHost createMailHost(SOSSmtpMailOptions mailOptions) {
        MailHost mailHost = new MailHost();
        BasicConnectionType basicConnectionType = new BasicConnectionType();
        mailHost.setBasicConnection(basicConnectionType);
        // Hostname
        if (mailOptions.SMTPHost.isDirty()) {
            basicConnectionType.setHostname(mailOptions.SMTPHost.Value());
        } else {
            reportMissingMandatoryParameter("smtp_host");
        }
        // Port
        if (mailOptions.port.isDirty()) {
            basicConnectionType.setPort(mailOptions.port.value());
        }
        // BasicAuthentication
        if (mailOptions.smtp_user.isDirty() || mailOptions.smtp_password.isDirty()) {
            BasicAuthenticationType basicAuthenticationType = new BasicAuthenticationType();
            mailHost.setBasicAuthentication(basicAuthenticationType);
            // Account
            if (mailOptions.smtp_user.isDirty()) {
                basicAuthenticationType.setAccount(mailOptions.smtp_user.Value());
            }
            // Password
            if (mailOptions.smtp_password.isDirty()) {
                basicAuthenticationType.setPassword(mailOptions.smtp_password.Value());
            }
        }
        return mailHost;
    }

    private boolean isCredentialStoreFragmentSpecified(SOSConnection2OptionsAlternate connectionOptions) {
        boolean returnValue = false;
        SOSCredentialStoreOptions credentialStoreOptions = connectionOptions.getCredentialStore();
        if (credentialStoreOptions.use_credential_Store.isDirty() || credentialStoreOptions.CS_FileName.isDirty()
                || credentialStoreOptions.CS_password.isDirty() || credentialStoreOptions.CS_KeyFileName.isDirty()
                || credentialStoreOptions.CS_AuthenticationMethod.isDirty() || credentialStoreOptions.CS_KeyPath.isDirty()
                || credentialStoreOptions.CS_ExportAttachment.isDirty() || credentialStoreOptions.CS_ExportAttachment2FileName.isDirty()
                || credentialStoreOptions.CS_DeleteExportedFileOnExit.isDirty() || credentialStoreOptions.CS_OverwriteExportedFile.isDirty()
                || credentialStoreOptions.CS_Permissions4ExportedFile.isDirty() || credentialStoreOptions.CS_StoreType.isDirty()) {
            returnValue = true;
        }
        return returnValue;
    }

    private boolean isNotificationsSpecified(JADEOptions options) {
        boolean returnValue = false;
        if (isBackgroundServiceSpecified(options) || (isMailServerFragmentSpecified(options.getMailOptions()) 
                && isNotificationTriggersSpecified(options))) {
            returnValue = true;
        }
        return returnValue;
    }

    private boolean isNotificationTriggersSpecified(JADEOptions options) {
        boolean returnValue = false;
        if (options.mail_on_success.value() || options.mail_on_error.value() || options.mail_on_empty_files.value()) {
            returnValue = true;
        }
        return returnValue;
    }

    private boolean isBackgroundServiceSpecified(JADEOptions options) {
        boolean returnValue = false;
        if (options.SendTransferHistory.value()) {
            returnValue = true;
        }
        return returnValue;
    }

    private boolean isMailServerFragmentSpecified(SOSSmtpMailOptions mailOptions) {
        boolean returnValue = false;
        if (isMailHostSpecified(mailOptions) || mailOptions.queue_directory.isDirty()) {
            returnValue = true;
        }
        return returnValue;
    }

    private boolean isMailHostSpecified(SOSSmtpMailOptions mailOptions) {
        boolean returnValue = false;
        if (mailOptions.SMTPHost.isDirty() || mailOptions.port.isDirty() || mailOptions.smtp_user.isDirty() || mailOptions.smtp_password.isDirty()) {
            returnValue = true;
        }
        return returnValue;
    }

    private boolean isLoggingSpecified(JADEOptions options) {
        boolean pcl = options.Source().ProtocolCommandListener.isDirty() || options.Target().ProtocolCommandListener.isDirty();
        if (pcl || options.verbose.isDirty() || options.log_filename.isDirty() || options.log4jPropertyFileName.isDirty()) {
            return true;
        }
        return false;
    }

    private LoggingType createLogging(JADEOptions options) {
        LoggingType loggingType = new LoggingType();
        if (options.verbose.isDirty()) {
            loggingType.setDebugLevel(options.verbose.value());
        }
        if (options.log_filename.isDirty()) {
            loggingType.setLogFile(options.log_filename.Value());
        }
        if (options.log4jPropertyFileName.isDirty()) {
            loggingType.setLog4JPropertyFile(options.log4jPropertyFileName.Value());
        }
        if (options.Source().ProtocolCommandListener.value() || options.Target().ProtocolCommandListener.value()) {
            loggingType.setProtocolCommandListener(true);
        }
        return loggingType;
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

    private GetListSource createGetListSource(JADEOptions options) {
        GetListSource getListSource = new GetListSource();
        SOSConnection2OptionsAlternate sourceConnectionOptions = options.Source();
        // GetListSourceFragmentRef
        ListableFragmentRefType getListSourceFragmentRefType = new ListableFragmentRefType();
        setListableFragmentRefType(getListSourceFragmentRefType, options, sourceConnectionOptions, false);
        getListSource.setGetListSourceFragmentRef(getListSourceFragmentRefType);
        // SourceFileOptions
        SourceFileOptions sourceFileOptions = createSourceFileOptions(options);
        getListSource.setSourceFileOptions(sourceFileOptions);
        // Alternatives
        SOSConnection2OptionsAlternate alternativConnectionOptions = sourceConnectionOptions.Alternatives();
        if (alternativConnectionOptions.protocol.isDirty()) {
            AlternativeGetListSourceFragmentRef alternativeGetListSourceFragmentRef = new AlternativeGetListSourceFragmentRef();
            setListableFragmentRefType(alternativeGetListSourceFragmentRef, options, alternativConnectionOptions, false);
            if (alternativConnectionOptions.Directory.isDirty()) {
                alternativeGetListSourceFragmentRef.setDirectory(alternativConnectionOptions.Directory.Value());
            }
            getListSource.getAlternativeGetListSourceFragmentRef().add(alternativeGetListSourceFragmentRef);
        }
        return getListSource;
    }

    private MoveTarget createMoveTarget(JADEOptions options, boolean useJumpHost) {
        MoveTarget moveTarget = new MoveTarget();
        SOSConnection2OptionsAlternate targetConnectionOptions = options.Target();
        // MoveTargetFragmentRef
        WriteableFragmentRefType moveTargetFragmentRef = new WriteableFragmentRefType();
        setWriteableFragmentRefType(moveTargetFragmentRef, options, targetConnectionOptions, useJumpHost);
        moveTarget.setMoveTargetFragmentRef(moveTargetFragmentRef);
        // Directory
        moveTarget.setDirectory(options.Target().Directory.Value());
        // TargetFileOptions
        if (isTargetFileOptionsSpecified(options)) {
            TargetFileOptions targetFileOptions = createTargetFileOptions(options);
            moveTarget.setTargetFileOptions(targetFileOptions);
        }
        // Alternatives
        SOSConnection2OptionsAlternate alternativConnectionOptions = targetConnectionOptions.Alternatives();
        if (alternativConnectionOptions.protocol.isDirty()) {
            AlternativeMoveTargetFragmentRef alternativeCopyTargetFragmentRef = new AlternativeMoveTargetFragmentRef();
            setWriteableFragmentRefType(alternativeCopyTargetFragmentRef, options, alternativConnectionOptions, useJumpHost);
            if (alternativConnectionOptions.Directory.isDirty()) {
                alternativeCopyTargetFragmentRef.setDirectory(alternativConnectionOptions.Directory.Value());
            }
            moveTarget.getAlternativeMoveTargetFragmentRef().add(alternativeCopyTargetFragmentRef);
        }
        return moveTarget;
    }

    private RemoveSource createRemoveSource(JADEOptions options) {
        RemoveSource removeSource = new RemoveSource();
        SOSConnection2OptionsAlternate sourceConnectionOptions = options.Source();
        // RemoveSourceFragmentRef
        WriteableFragmentRefType writeableFragmentRefType = new WriteableFragmentRefType();
        setWriteableFragmentRefType(writeableFragmentRefType, options, sourceConnectionOptions, false);
        removeSource.setRemoveSourceFragmentRef(writeableFragmentRefType);
        // SourceFileOptions
        SourceFileOptions sourceFileOptions = createSourceFileOptions(options);
        removeSource.setSourceFileOptions(sourceFileOptions);
        // Alternatives
        SOSConnection2OptionsAlternate alternativConnectionOptions = sourceConnectionOptions.Alternatives();
        if (alternativConnectionOptions.protocol.isDirty()) {
            AlternativeRemoveSourceFragmentRef alternativeRemoveSourceFragmentRef = new AlternativeRemoveSourceFragmentRef();
            setWriteableFragmentRefType(alternativeRemoveSourceFragmentRef, options, alternativConnectionOptions, false);
            if (alternativConnectionOptions.Directory.isDirty()) {
                alternativeRemoveSourceFragmentRef.setDirectory(alternativConnectionOptions.Directory.Value());
            }
            removeSource.getAlternativeRemoveSourceFragmentRef().add(alternativeRemoveSourceFragmentRef);
        }
        return removeSource;
    }

    private MoveSource createMoveSource(JADEOptions options, boolean useJumpHost) {
        MoveSource moveSource = new MoveSource();
        SOSConnection2OptionsAlternate sourceConnectionOptions = options.Source();
        // MoveSourceFragmentRef
        WriteableFragmentRefType writeableFragmentRefType = new WriteableFragmentRefType();
        setWriteableFragmentRefType(writeableFragmentRefType, options, sourceConnectionOptions, useJumpHost);
        moveSource.setMoveSourceFragmentRef(writeableFragmentRefType);
        // SourceFileOptions
        SourceFileOptions sourceFileOptions = createSourceFileOptions(options);
        moveSource.setSourceFileOptions(sourceFileOptions);
        // Alternatives
        SOSConnection2OptionsAlternate sourceAlternativConnectionOptions = sourceConnectionOptions.Alternatives();
        if (sourceAlternativConnectionOptions.protocol.isDirty()) {
            AlternativeMoveSourceFragmentRef alternativeCopyTargetFragmentRef = new AlternativeMoveSourceFragmentRef();
            setWriteableFragmentRefType(alternativeCopyTargetFragmentRef, options, sourceAlternativConnectionOptions, useJumpHost);
            if (sourceAlternativConnectionOptions.Directory.isDirty()) {
                alternativeCopyTargetFragmentRef.setDirectory(sourceAlternativConnectionOptions.Directory.Value());
            }
            moveSource.getAlternativeMoveSourceFragmentRef().add(alternativeCopyTargetFragmentRef);
        }
        return moveSource;
    }

    private void setWriteableFragmentRefType(WriteableFragmentRefType writeableFragmentRefType, JADEOptions options,
            SOSConnection2OptionsAlternate connectionOptions, boolean useJumpHost) {
        if (connectionOptions.protocol.isNotDirty()) {
            raiseMandatoryParameter = true;
            throw new RuntimeException(String.format("Parameter '%1$sprotocol' is required!", connectionOptions.getPrefix()));
        }
        String sourceProtocolValue = connectionOptions.protocol.Value();
        if ("ftp".equalsIgnoreCase(sourceProtocolValue)) {
            // FTPFragment
            FTPFragment ftpFragment = createFTPFragment(options, connectionOptions, useJumpHost);
            // FTPFragmentRef
            FTPFragmentRef ftpFragmentRef = createFTPFragmentRef(options, connectionOptions, ftpFragment);
            writeableFragmentRefType.setFTPFragmentRef(ftpFragmentRef);
        } else if ("ftps".equalsIgnoreCase(sourceProtocolValue)) {
            // FTPSFragment
            FTPSFragment ftpsFragment = createFTPSFragment(options, connectionOptions, useJumpHost);
            // FTPSFragmentRef
            FTPSFragmentRef ftpsFragmentRef = createFTPSFragmentRef(options, connectionOptions, ftpsFragment);
            writeableFragmentRefType.setFTPSFragmentRef(ftpsFragmentRef);
        } else if ("local".equalsIgnoreCase(sourceProtocolValue) || "zip".equalsIgnoreCase(sourceProtocolValue)) {
            LocalTarget localTarget = createLocalTarget(options, connectionOptions);
            writeableFragmentRefType.setLocalTarget(localTarget);
        } else if ("sftp".equalsIgnoreCase(sourceProtocolValue)) {
            // SFTPFragment
            SFTPFragment sftpFragment = createSFTPFragment(options, connectionOptions, useJumpHost);
            // SFTPFragmentRef
            SFTPFragmentRef sftpFragmentRef = createSFTPFragmentRef(options, connectionOptions, sftpFragment);
            writeableFragmentRefType.setSFTPFragmentRef(sftpFragmentRef);
        } else if ("smb".equalsIgnoreCase(sourceProtocolValue)) {
            // SMBFragment
            SMBFragment smbFragment = createSMBFragment(connectionOptions);
            // SMBFragmentRef
            SMBFragmentRef smbFragmentRef = createSMBFragmentRef(options, connectionOptions, smbFragment);
            writeableFragmentRefType.setSMBFragmentRef(smbFragmentRef);
        } else if ("webdav".equalsIgnoreCase(sourceProtocolValue)) {
            // WebDAVFragment
            WebDAVFragment webDAVFragment = createWebDAVFragment(options, connectionOptions, useJumpHost);
            // WebDAVFragmentRef
            WebDAVFragmentRef webDAVFragmentRef = createWebDAVFragmentRef(options, connectionOptions, webDAVFragment);
            writeableFragmentRefType.setWebDAVFragmentRef(webDAVFragmentRef);
        } else {
            raiseMandatoryParameter = true;
            throw new RuntimeException(String.format("Parameter 'protocol' = %1$s is not supported", connectionOptions.protocol.Value()));
        }
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
        if (isRenameSpecified(connectionOptions) 
                || ("target_".equalsIgnoreCase(connectionOptions.getPrefix()) && isUnprefixedRenameSpecified(options))) {
            RenameType rename = createRename(options, connectionOptions);
            localTarget.setRename(rename);
        }
        return localTarget;
    }

    private CopyTarget createCopyTarget(JADEOptions options, boolean useJumpHost) {
        CopyTarget copyTarget = new CopyTarget();
        SOSConnection2OptionsAlternate targetConnectionOptions = options.Target();
        // CopyTargetFragmentRef
        WriteableFragmentRefType copyTargetFragmentRef = new WriteableFragmentRefType();
        setWriteableFragmentRefType(copyTargetFragmentRef, options, targetConnectionOptions, useJumpHost);
        copyTarget.setCopyTargetFragmentRef(copyTargetFragmentRef);
        // Directory
        copyTarget.setDirectory(targetConnectionOptions.Directory.Value());
        // TargetFileOptions
        if (isTargetFileOptionsSpecified(options)) {
            TargetFileOptions targetFileOptions = createTargetFileOptions(options);
            copyTarget.setTargetFileOptions(targetFileOptions);
        }
        // Alternatives
        SOSConnection2OptionsAlternate targetAlternativConnectionOptions = targetConnectionOptions.Alternatives();
        if (targetAlternativConnectionOptions.protocol.isDirty()) {
            AlternativeCopyTargetFragmentRef alternativeCopyTargetFragmentRef = new AlternativeCopyTargetFragmentRef();
            setWriteableFragmentRefType(alternativeCopyTargetFragmentRef, options, targetAlternativConnectionOptions, useJumpHost);
            if (targetAlternativConnectionOptions.Directory.isDirty()) {
                alternativeCopyTargetFragmentRef.setDirectory(targetAlternativConnectionOptions.Directory.Value());
            }
            copyTarget.getAlternativeCopyTargetFragmentRef().add(alternativeCopyTargetFragmentRef);
        }
        return copyTarget;
    }

    private CopySource createCopySource(JADEOptions options, boolean useJumpHost) {
        CopySource copySource = new CopySource();
        SOSConnection2OptionsAlternate sourceConnectionOptions = options.Source();
        // CopySourceFragmentRef
        ReadableFragmentRefType readableFragmentRefType = new ReadableFragmentRefType();
        setReadableFragmentRefType(readableFragmentRefType, options, sourceConnectionOptions, useJumpHost);
        copySource.setCopySourceFragmentRef(readableFragmentRefType);
        // SourceFileOptions
        SourceFileOptions sourceFileOptions = createSourceFileOptions(options);
        copySource.setSourceFileOptions(sourceFileOptions);
        // Alternatives
        SOSConnection2OptionsAlternate sourceAlternativConnectionOptions = sourceConnectionOptions.Alternatives();
        if (sourceAlternativConnectionOptions.protocol.isDirty()) {
            AlternativeCopySourceFragmentRef alternativeCopyTargetFragmentRef = new AlternativeCopySourceFragmentRef();
            setReadableFragmentRefType(alternativeCopyTargetFragmentRef, options, sourceAlternativConnectionOptions, useJumpHost);
            if (sourceAlternativConnectionOptions.Directory.isDirty()) {
                alternativeCopyTargetFragmentRef.setDirectory(sourceAlternativConnectionOptions.Directory.Value());
            }
            copySource.getAlternativeCopySourceFragmentRef().add(alternativeCopyTargetFragmentRef);
        }
        return copySource;
    }

    private void setReadableFragmentRefType(ReadableFragmentRefType readableFragmentRefType, JADEOptions options,
            SOSConnection2OptionsAlternate connectionOptions, boolean useJumpHost) {
        if (connectionOptions.protocol.isNotDirty()) {
            raiseMandatoryParameter = true;
            throw new RuntimeException(String.format("Parameter '%1$sprotocol' is required!", connectionOptions.getPrefix()));
        }
        // (Protocol)
        String sourceProtocolValue = connectionOptions.protocol.Value();
        if ("ftp".equalsIgnoreCase(sourceProtocolValue)) {
            // FTPFragment
            FTPFragment ftpFragment = createFTPFragment(options, connectionOptions, useJumpHost);
            // FTPFragmentRef
            FTPFragmentRef ftpFragmentRef = createFTPFragmentRef(options, connectionOptions, ftpFragment);
            readableFragmentRefType.setFTPFragmentRef(ftpFragmentRef);
        } else if ("ftps".equalsIgnoreCase(sourceProtocolValue)) {
            // FTPSFragment
            FTPSFragment ftpsFragment = createFTPSFragment(options, connectionOptions, useJumpHost);
            // FTPSFragmentRef
            FTPSFragmentRef ftpsFragmentRef = createFTPSFragmentRef(options, connectionOptions, ftpsFragment);
            readableFragmentRefType.setFTPSFragmentRef(ftpsFragmentRef);
        } else if ("http".equalsIgnoreCase(sourceProtocolValue)) {
            if (isHTTPSFragment(connectionOptions)) {
                // HTTPSFragment
                HTTPSFragment httpsFragment = createHTTPSFragment(options, connectionOptions, useJumpHost);
                // HTTPSFragmentRef
                HTTPSFragmentRef httpsFragmentRef = createHTTPSFragmentRef(httpsFragment);
                readableFragmentRefType.setHTTPSFragmentRef(httpsFragmentRef);
            } else {
                // HTTPFragment
                HTTPFragment httpFragment = createHTTPFragment(options, connectionOptions, useJumpHost);
                // HTTPFragmentRef
                HTTPFragmentRef httpFragmentRef = createHTTPFragmentRef(httpFragment);
                readableFragmentRefType.setHTTPFragmentRef(httpFragmentRef);
            }
        } else if ("local".equalsIgnoreCase(sourceProtocolValue) || "zip".equalsIgnoreCase(sourceProtocolValue)) {
            LocalSource localSource = createLocalSource(options, connectionOptions);
            readableFragmentRefType.setLocalSource(localSource);
        } else if ("sftp".equalsIgnoreCase(sourceProtocolValue)) {
            // SFTPFragment
            SFTPFragment sftpFragment = createSFTPFragment(options, connectionOptions, useJumpHost);
            // SFTPFragmentRef
            SFTPFragmentRef sftpFragmentRef = createSFTPFragmentRef(options, connectionOptions, sftpFragment);
            readableFragmentRefType.setSFTPFragmentRef(sftpFragmentRef);
        } else if ("smb".equalsIgnoreCase(sourceProtocolValue)) {
            // SMBFragment
            SMBFragment smbFragment = createSMBFragment(connectionOptions);
            // SMBFragmentRef
            SMBFragmentRef smbFragmentRef = createSMBFragmentRef(options, connectionOptions, smbFragment);
            readableFragmentRefType.setSMBFragmentRef(smbFragmentRef);
        } else if ("webdav".equalsIgnoreCase(sourceProtocolValue)) {
            // WebDAVFragment
            WebDAVFragment webDAVFragment = createWebDAVFragment(options, connectionOptions, useJumpHost);
            // WebDAVFragmentRef
            WebDAVFragmentRef webDAVFragmentRef = createWebDAVFragmentRef(options, connectionOptions, webDAVFragment);
            readableFragmentRefType.setWebDAVFragmentRef(webDAVFragmentRef);
        } else {
            raiseMandatoryParameter = true;
            throw new RuntimeException(String.format("Parameter 'protocol' = %1$s is not supported", connectionOptions.protocol.Value()));
        }
    }

    private void setListableFragmentRefType(ListableFragmentRefType listableFragmentRefType, JADEOptions options,
            SOSConnection2OptionsAlternate connectionOptions, boolean useJumpHost) {
        if (connectionOptions.protocol.isNotDirty()) {
            raiseMandatoryParameter = true;
            throw new RuntimeException(String.format("Parameter '%1$sprotocol' is required!", connectionOptions.getPrefix()));
        }
        // (Protocol)
        String sourceProtocolValue = connectionOptions.protocol.Value();
        if ("ftp".equalsIgnoreCase(sourceProtocolValue)) {
            // FTPFragment
            FTPFragment ftpFragment = createFTPFragment(options, connectionOptions, useJumpHost);
            // FTPFragmentRef
            FTPFragmentRef ftpFragmentRef = createFTPFragmentRef(options, connectionOptions, ftpFragment);
            listableFragmentRefType.setFTPFragmentRef(ftpFragmentRef);
        } else if ("ftps".equalsIgnoreCase(sourceProtocolValue)) {
            // FTPSFragment
            FTPSFragment ftpsFragment = createFTPSFragment(options, connectionOptions, useJumpHost);
            // FTPSFragmentRef
            FTPSFragmentRef ftpsFragmentRef = createFTPSFragmentRef(options, connectionOptions, ftpsFragment);
            listableFragmentRefType.setFTPSFragmentRef(ftpsFragmentRef);
        } else if ("local".equalsIgnoreCase(sourceProtocolValue) || "zip".equalsIgnoreCase(sourceProtocolValue)) {
            LocalSource localSource = createLocalSource(options, connectionOptions);
            listableFragmentRefType.setLocalSource(localSource);
        } else if ("sftp".equalsIgnoreCase(sourceProtocolValue)) {
            // SFTPFragment
            SFTPFragment sftpFragment = createSFTPFragment(options, connectionOptions, useJumpHost);
            // SFTPFragmentRef
            SFTPFragmentRef sftpFragmentRef = createSFTPFragmentRef(options, connectionOptions, sftpFragment);
            listableFragmentRefType.setSFTPFragmentRef(sftpFragmentRef);
        } else if ("smb".equalsIgnoreCase(sourceProtocolValue)) {
            // SMBFragment
            SMBFragment smbFragment = createSMBFragment(connectionOptions);
            // SMBFragmentRef
            SMBFragmentRef smbFragmentRef = createSMBFragmentRef(options, connectionOptions, smbFragment);
            listableFragmentRefType.setSMBFragmentRef(smbFragmentRef);
        } else if ("webdav".equalsIgnoreCase(sourceProtocolValue)) {
            // WebDAVFragment
            WebDAVFragment webDAVFragment = createWebDAVFragment(options, connectionOptions, useJumpHost);
            // WebDAVFragmentRef
            WebDAVFragmentRef webDAVFragmentRef = createWebDAVFragmentRef(options, connectionOptions, webDAVFragment);
            listableFragmentRefType.setWebDAVFragmentRef(webDAVFragmentRef);
        } else {
            raiseMandatoryParameter = true;
            throw new RuntimeException(String.format("Parameter 'protocol' = %1$s is not supported", connectionOptions.protocol.Value()));
        }
    }

    private boolean isHTTPSFragment(SOSConnection2OptionsAlternate connectionOptions) {
        boolean returnValue = false;
        if (connectionOptions.url.isDirty() || connectionOptions.host.isDirty()) {
            URL url = null;
            try {
                if (connectionOptions.url.isDirty()) {
                    url = new URL(connectionOptions.url.Value());
                } else if (connectionOptions.host.isDirty()) {
                    url = new URL(connectionOptions.host.Value());
                }
            } catch (MalformedURLException | RuntimeException e) {
            }
            if ("https".equalsIgnoreCase(url.getProtocol())) {
                returnValue = true;
            }
        }
        return returnValue;
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
        // SkipFiles? Why? Use case?
        if (options.skip_first_files.isDirty() || options.skip_last_files.isDirty()) {
            SkipFiles skipFiles = new SkipFiles();
            if (options.skip_first_files.isDirty()) {
                skipFiles.setSkipFirstFiles(options.skip_first_files.value());
            }
            if (options.skip_last_files.isDirty()) {
                skipFiles.setSkipLastFiles(options.skip_last_files.value());
            }
            sourceFileOptions.setSkipFiles(skipFiles);
        }
        // MaxFiles
        if (options.MaxFiles.isDirty()) {
            sourceFileOptions.setMaxFiles(options.MaxFiles.value());
        }
        // CheckIntegrityHash
        if (options.CheckIntegrityHash.isTrue()) {
            CheckIntegrityHash checkIntegrityHash = new CheckIntegrityHash();
            if (options.SecurityHashType.isDirty()) {
                checkIntegrityHash.setHashAlgorithm(options.SecurityHashType.Value());
            }
            sourceFileOptions.setCheckIntegrityHash(checkIntegrityHash);
        }
        return sourceFileOptions;
    }

    private ResultSet createResultSet(JADEOptions options) {
        ResultSet resultSet = new ResultSet();
        if (options.result_list_file.isDirty()) {
            resultSet.setResultSetFile(options.result_list_file.Value());
        }
        if (isCheckResultSetSizeSpecified(options)) {
            CheckResultSetCount checkResultSetCount = new CheckResultSetCount();
            checkResultSetCount.setExpectedResultSetCount(options.expected_size_of_result_set.value());
            checkResultSetCount.setRaiseErrorIfResultSetIs(options.raise_error_if_result_set_is.Value());
        }
        if (options.on_empty_result_set.isDirty()) {
            resultSet.setEmptyResultSetState(options.on_empty_result_set.Value());
        }
        return resultSet;
    }

    private boolean isResultSetSpecified(JADEOptions options) {
        boolean returnValue = false;
        if (options.result_list_file.isDirty() || isCheckResultSetSizeSpecified(options) || options.on_empty_result_set.isDirty()) {
            returnValue = true;
        }
        return returnValue;
    }

    private boolean isCheckResultSetSizeSpecified(JADEOptions options) {
        boolean returnValue = false;
        if (options.expected_size_of_result_set.isDirty() || options.raise_error_if_result_set_is.isDirty()) {
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
        if (options.poll_interval.isDirty() || options.poll_timeout.isDirty() || options.poll_minfiles.isDirty()
                || options.pollingWait4SourceFolder.isDirty() || options.PollErrorState.isDirty() || options.PollingServer.isDirty()
                || options.pollingServerDuration.isDirty() || options.PollingServerPollForever.isDirty()) {
            returnValue = true;
        }
        return returnValue;
    }

    private Directives createDirectives(JADEOptions options) {
        Directives directives = new Directives();
        if (options.force_files.isDirty()) {
            logger.info("Replacing 'force_files=" + options.force_files.Value() + "' with 'DisableErrorOnNoFilesFound="
                    + String.valueOf(!options.force_files.value()) + "'");
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
            logger.info("No FileSelection was specified. I assume you intended to use 'file_spec' with value '.*' "
                    + "to select all files from the SourceDirectory.");
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

    private WebDAVFragment createWebDAVFragment(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, boolean useJumpHost) {
        WebDAVFragment webDAVFragment = new WebDAVFragment();
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
        if (useJumpHost && isJumpFragmentSpecified(options)) {
            JumpFragment jumpFragment = createJumpFragment(options);
            JumpFragmentRef jumpFragmentRef = createJumpFragmentRef(jumpFragment);
            webDAVFragment.setJumpFragmentRef(jumpFragmentRef);
        }
        // ProxyForWebDAV
        if (isProxySpecified(connectionOptions)) {
            if ("http".equalsIgnoreCase(connectionOptions.proxy_protocol.Value()) || connectionOptions.proxy_protocol.Value().isEmpty()) {
                if (connectionOptions.proxy_protocol.Value().isEmpty()) {
                    logger.info("No value was specified for parameter '" + connectionOptions.getPrefix()
                            + "proxy_protocol'. I assume you intended to use 'http'.");
                }
                ProxyForWebDAV proxyForWebDAV = new ProxyForWebDAV();
                webDAVFragment.setProxyForWebDAV(proxyForWebDAV);
                // HTTPProxy
                AuthenticatedProxyType httpProxy = createAuthenticatedProxyType(connectionOptions);
                proxyForWebDAV.setHTTPProxy(httpProxy);
            } else {
                logger.warn("Invalid value specified for option '" + connectionOptions.getPrefix()
                        + "proxy_protocol'. Only value 'http' is allowed in combination with WebDAV.");
            }
        }
        // CredentialStore
        if (isCredentialStoreFragmentSpecified(connectionOptions)) {
            CredentialStoreFragment credentialStoreFragment = createCredentialStoreFragment(connectionOptions);
            CredentialStoreFragmentRef credentialStoreFragmentRef = new CredentialStoreFragmentRef();
            credentialStoreFragmentRef.setRef(getCredentialStoreFragmentRefId(credentialStoreFragment));
            webDAVFragment.setCredentialStoreFragmentRef(credentialStoreFragmentRef);
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
                String connection = url.getProtocol() + "://" + url.getHost();
                if (url.getPort() != -1) {
                    connection += ":" + url.getPort();
                } else if (connectionOptions.port.isDirty()) {
                    connection += ":" + connectionOptions.port.value();
                }
                if (!url.getPath().isEmpty()) {
                    connection += url.getPath();
                }
                urlConnectionType.setURL(connection);
                return urlConnectionType;
            } catch (MalformedURLException | RuntimeException e) {
                logger.error("", e);
            }
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
                if (url.getUserInfo() != null || connectionOptions.user.isDirty()) {
                    if (url.getUserInfo() != null) {
                        if (url.getUserInfo().contains(":")) {
                            basicAuthenticationType.setAccount(url.getUserInfo().split(":")[0]);
                            basicAuthenticationType.setPassword(url.getUserInfo().split(":")[1]);
                        } else {
                            basicAuthenticationType.setAccount(url.getUserInfo());
                        }
                    } else if (connectionOptions.user.isDirty()) {
                        basicAuthenticationType.setAccount(connectionOptions.user.Value());
                        if (connectionOptions.password.isDirty()) {
                            basicAuthenticationType.setPassword(connectionOptions.password.Value());
                        }
                    }
                }
            } catch (MalformedURLException | RuntimeException e) {
                logger.error("", e);
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

    private WebDAVFragmentRef createWebDAVFragmentRef(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, WebDAVFragment fragment) {
        WebDAVFragmentRef webDAVFragmentRef = new WebDAVFragmentRef();
        webDAVFragmentRef.setRef(getWebDAVFragmentRefId(fragment));
        // Rename
        if (isRenameSpecified(connectionOptions)
                || ("target_".equalsIgnoreCase(connectionOptions.getPrefix()) && isUnprefixedRenameSpecified(options))) {
            RenameType rename = createRename(options, connectionOptions);
            webDAVFragmentRef.setRename(rename);
        }
        return webDAVFragmentRef;
    }

    private SMBFragment createSMBFragment(SOSConnection2OptionsAlternate connectionOptions) {
        SMBFragment smbFragment = new SMBFragment();
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
        // CredentialStore
        if (isCredentialStoreFragmentSpecified(connectionOptions)) {
            CredentialStoreFragment credentialStoreFragment = createCredentialStoreFragment(connectionOptions);
            CredentialStoreFragmentRef credentialStoreFragmentRef = new CredentialStoreFragmentRef();
            credentialStoreFragmentRef.setRef(getCredentialStoreFragmentRefId(credentialStoreFragment));
            smbFragment.setCredentialStoreFragmentRef(credentialStoreFragmentRef);
        }
        return smbFragment;
    }

    private SMBFragmentRef createSMBFragmentRef(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, SMBFragment fragment) {
        SMBFragmentRef smbFragmentRef = new SMBFragmentRef();
        smbFragmentRef.setRef(getSMBFragmentRefId(fragment));
        // Rename
        if (isRenameSpecified(connectionOptions)
                || ("target_".equalsIgnoreCase(connectionOptions.getPrefix()) && isUnprefixedRenameSpecified(options))) {
            RenameType rename = createRename(options, connectionOptions);
            smbFragmentRef.setRename(rename);
        }
        return smbFragmentRef;
    }

    private SFTPFragment createSFTPFragment(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, boolean useJumpHost) {
        SFTPFragment sftpFragment = new SFTPFragment();
        // BasicConnection
        BasicConnectionType basicConnection = createBasicConnection(connectionOptions);
        sftpFragment.setBasicConnection(basicConnection);
        // SSHAuthentication
        SSHAuthenticationType sshAuthentication = createSSHAuthentication(connectionOptions);
        sftpFragment.setSSHAuthentication(sshAuthentication);
        // JumpFragmentRef
        if (useJumpHost && isJumpFragmentSpecified(options)) {
            JumpFragment jumpFragment = createJumpFragment(options);
            JumpFragmentRef jumpFragmentRef = createJumpFragmentRef(jumpFragment);
            sftpFragment.setJumpFragmentRef(jumpFragmentRef);
        }
        // ProxyForSFTP
        if (isProxySpecified(connectionOptions)) {
            if ("http".equalsIgnoreCase(connectionOptions.proxy_protocol.Value())
                    || "socks4".equalsIgnoreCase(connectionOptions.proxy_protocol.Value())
                    || "socks5".equalsIgnoreCase(connectionOptions.proxy_protocol.Value()) || connectionOptions.proxy_protocol.Value().isEmpty()) {
                ProxyForSFTP proxyForSFTP = new ProxyForSFTP();
                sftpFragment.setProxyForSFTP(proxyForSFTP);
                if ("http".equalsIgnoreCase(connectionOptions.proxy_protocol.Value())) {
                    AuthenticatedProxyType httpProxy = new AuthenticatedProxyType();
                    proxyForSFTP.setHTTPProxy(httpProxy);
                } else if ("socks4".equalsIgnoreCase(connectionOptions.proxy_protocol.Value())) {
                    UnauthenticatedProxyType socks4Proxy = createUnauthenticatedProxyType(connectionOptions);
                    proxyForSFTP.setSOCKS4Proxy(socks4Proxy);
                } else if ("socks5".equalsIgnoreCase(connectionOptions.proxy_protocol.Value())) {
                    AuthenticatedProxyType socks5Proxy = createAuthenticatedProxyType(connectionOptions);
                    proxyForSFTP.setSOCKS5Proxy(socks5Proxy);
                } else if (connectionOptions.proxy_protocol.Value().isEmpty()) {
                    logger.debug("Skipping grouping element 'ProxyForSFTP' because of parameter '" + connectionOptions.getPrefix()
                            + "proxy_protocol' is not specified.");
                    sftpFragment.setProxyForSFTP(null);
                }
            } else {
                logger.warn("Invalid value specified for option '" + connectionOptions.getPrefix()
                        + "proxy_protocol'. Only values 'http','socks4' and 'socks5' are allowed in combination with SFTP.");
            }
        }
        // StrictHostKeyChecking
        if (connectionOptions.strictHostKeyChecking.isDirty()) {
            if ("yes".equalsIgnoreCase(connectionOptions.strictHostKeyChecking.Value())) {
                sftpFragment.setStrictHostkeyChecking(true);
            } else if ("no".equalsIgnoreCase(connectionOptions.strictHostKeyChecking.Value())) {
                sftpFragment.setStrictHostkeyChecking(false);
            } else if ("ask".equalsIgnoreCase(connectionOptions.strictHostKeyChecking.Value())) {
                logger.warn("Value 'ask' is no longer permitted for option '" + connectionOptions.getPrefix()
                        + "StrictHostKeyChecking'. Use either 'yes' or 'no'.");
            }
        }
        // CredentialStore
        if (isCredentialStoreFragmentSpecified(connectionOptions)) {
            CredentialStoreFragment credentialStoreFragment = createCredentialStoreFragment(connectionOptions);
            CredentialStoreFragmentRef credentialStoreFragmentRef = new CredentialStoreFragmentRef();
            credentialStoreFragmentRef.setRef(getCredentialStoreFragmentRefId(credentialStoreFragment));
            sftpFragment.setCredentialStoreFragmentRef(credentialStoreFragmentRef);
        }
        return sftpFragment;
    }

    private JumpFragment createJumpFragment(JADEOptions options) {
        JumpFragment jumpFragment = new JumpFragment();
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
        if ("password".equalsIgnoreCase(options.jump_ssh_auth_method.Value())) {
            // AuthenticationMethodPassword
            AuthenticationMethodPassword authenticationMethodPassword = new AuthenticationMethodPassword();
            sshAuthenticationType.setAuthenticationMethodPassword(authenticationMethodPassword);
            if (options.jump_password.isDirty()) {
                authenticationMethodPassword.setPassword(options.jump_password.Value());
            } else {
                reportMissingMandatoryParameter("jump_password");
            }
        } else if ("publickey".equalsIgnoreCase(options.jump_ssh_auth_method.Value())) {
            // AuthenticationMethodPublicKey
            AuthenticationMethodPublickey authenticationMethodPublickey = new AuthenticationMethodPublickey();
            sshAuthenticationType.setAuthenticationMethodPublickey(authenticationMethodPublickey);
            if (options.jump_ssh_auth_file.isDirty()) {
                authenticationMethodPublickey.setAuthenticationFile(options.jump_ssh_auth_file.Value());
            } else {
                reportMissingMandatoryParameter("jump_ssh_auth_file");
            }
            if (options.jump_password.isDirty()) {
                authenticationMethodPublickey.setPassphrase(options.jump_password.Value());
            }
        }
        // JumpCommand
        if (options.jump_command.isDirty()) {
            jumpFragment.setJumpCommand(options.jump_command.Value());
        }
        // JumpDirectory
        if (options.jump_dir.isDirty()) {
            jumpFragment.setJumpDirectory(options.jump_dir.Value());
        }
        // StrictHostKeyChecking
        if (options.jump_strict_hostkey_checking.isDirty()) {
            jumpFragment.setStrictHostkeyChecking(options.jump_strict_hostkey_checking.value());
        }
        // ProxyForSFTP
        if (isJumpProxySpecified(options)) {
            if ("http".equalsIgnoreCase(options.jump_proxy_protocol.Value()) || "socks4".equalsIgnoreCase(options.jump_proxy_protocol.Value())
                    || "socks5".equalsIgnoreCase(options.jump_proxy_protocol.Value()) || options.jump_proxy_protocol.Value().isEmpty()) {
                ProxyForSFTP proxyForSFTP = new ProxyForSFTP();
                jumpFragment.setProxyForSFTP(proxyForSFTP);
                if ("http".equalsIgnoreCase(options.jump_proxy_protocol.Value())) {
                    AuthenticatedProxyType authenticatedProxyType = createAuthenticatedProxyTypeForJump(options);
                    proxyForSFTP.setHTTPProxy(authenticatedProxyType);
                } else if ("socks4".equalsIgnoreCase(options.jump_proxy_protocol.Value())) {
                    UnauthenticatedProxyType socks4Proxy = createUnauthenticatedProxyTypeForJump(options);
                    proxyForSFTP.setSOCKS4Proxy(socks4Proxy);
                } else if ("socks5".equalsIgnoreCase(options.jump_proxy_protocol.Value())) {
                    AuthenticatedProxyType socks5Proxy = createAuthenticatedProxyTypeForJump(options);
                    proxyForSFTP.setSOCKS5Proxy(socks5Proxy);
                } else if (options.jump_proxy_protocol.Value().isEmpty()) {
                    logger.debug("Skipping grouping element 'ProxyForSFTP' because of parameter 'jump_proxy_protocol' is not specified.");
                    jumpFragment.setProxyForSFTP(null);
                }
            } else {
                logger.warn("Invalid value specified for option 'jump_proxy_protocol'. "
                        + "Only values 'http', 'socks4' and 'socks5' are allowed in combination with Jump.");
            }
        }
        // no CredentialStore for jump implemented
        return jumpFragment;
    }

    private JumpFragmentRef createJumpFragmentRef(JumpFragment jumpFragment) {
        JumpFragmentRef jumpFragmentRef = new JumpFragmentRef();
        jumpFragmentRef.setRef(getJumpFragmentRefId(jumpFragment));
        return jumpFragmentRef;
    }

    private boolean isJumpFragmentSpecified(JADEOptions options) {
        boolean returnValue = false;
        if (options.jump_host.isDirty() || options.jump_port.isDirty() || options.jump_user.isDirty() || options.jump_password.isDirty()
                || options.jump_ssh_auth_file.isDirty() || options.jump_ssh_auth_method.isDirty() || options.jump_dir.isDirty()
                || isJumpProxySpecified(options)) {
            returnValue = true;
        }
        return returnValue;
    }

    private boolean isJumpProxySpecified(JADEOptions options) {
        boolean returnValue = false;
        if (options.jump_proxy_host.isDirty() || options.jump_proxy_port.isDirty() || options.jump_proxy_user.isDirty()
                || options.jump_proxy_password.isDirty()) {
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
        if (connectionOptions.proxy_protocol.isDirty() || connectionOptions.proxy_host.isDirty() || connectionOptions.proxy_port.isDirty()
                || connectionOptions.proxy_user.isDirty() || connectionOptions.proxy_password.isDirty()) {
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
        if ("password".equalsIgnoreCase(connectionOptions.ssh_auth_method.Value())) {
            AuthenticationMethodPassword authenticationMethodPassword = new AuthenticationMethodPassword();
            authenticationMethodPassword.setPassword(connectionOptions.password.Value());
            sshAuthentication.setAuthenticationMethodPassword(authenticationMethodPassword);
        } else if ("publickey".equalsIgnoreCase(connectionOptions.ssh_auth_method.Value())) {
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

    private SFTPFragmentRef createSFTPFragmentRef(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, SFTPFragment fragment) {
        SFTPFragmentRef sftpFragmentRef = new SFTPFragmentRef();
        sftpFragmentRef.setRef(getSFTPFragmentRefId(fragment));
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
        if (isRenameSpecified(connectionOptions)
                || ("target_".equalsIgnoreCase(connectionOptions.getPrefix()) && isUnprefixedRenameSpecified(options))) {
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
                logger.debug("Skipping grouping element 'ZlibCompression' because of parameter '" + connectionOptions.getPrefix()
                        + "use_zlib_compression' is set to false or not specified.");
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
        if (isRenameSpecified(connectionOptions)
                || ("target_".equalsIgnoreCase(connectionOptions.getPrefix()) && isUnprefixedRenameSpecified(options))) {
            RenameType rename = createRename(options, connectionOptions);
            localSource.setRename(rename);
        }
        return localSource;
    }

    private HTTPSFragment createHTTPSFragment(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, boolean useJumpHost) {
        HTTPSFragment httpsFragment = new HTTPSFragment();
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
        if (useJumpHost && isJumpFragmentSpecified(options)) {
            JumpFragment jumpFragment = createJumpFragment(options);
            JumpFragmentRef jumpFragmentRef = createJumpFragmentRef(jumpFragment);
            httpsFragment.setJumpFragmentRef(jumpFragmentRef);
        }
        // ProxyForHTTP
        if (isProxySpecified(connectionOptions)) {
            if ("http".equalsIgnoreCase(connectionOptions.proxy_protocol.Value()) || connectionOptions.proxy_protocol.Value().isEmpty()) {
                if (connectionOptions.proxy_protocol.Value().isEmpty()) {
                    logger.info("No value was specified for parameter '" + connectionOptions.getPrefix()
                            + "proxy_protocol'. I assume you meant 'http'.");
                }
                ProxyForHTTP proxyForHTTP = new ProxyForHTTP();
                httpsFragment.setProxyForHTTP(proxyForHTTP);
                // HTTPProxy
                AuthenticatedProxyType httpProxy = createAuthenticatedProxyType(connectionOptions);
                proxyForHTTP.setHTTPProxy(httpProxy);
            } else {
                logger.warn("Invalid value specified for option '" + connectionOptions.getPrefix()
                        + "proxy_protocol'. Only value 'http' is allowed in combination with HTTPS.");
            }
        }
        // CredentialStore
        if (isCredentialStoreFragmentSpecified(connectionOptions)) {
            CredentialStoreFragment credentialStoreFragment = createCredentialStoreFragment(connectionOptions);
            CredentialStoreFragmentRef credentialStoreFragmentRef = new CredentialStoreFragmentRef();
            credentialStoreFragmentRef.setRef(getCredentialStoreFragmentRefId(credentialStoreFragment));
            httpsFragment.setCredentialStoreFragmentRef(credentialStoreFragmentRef);
        }
        return httpsFragment;
    }

    private HTTPSFragmentRef createHTTPSFragmentRef(HTTPSFragment fragment) {
        HTTPSFragmentRef httpsFragmentRef = new HTTPSFragmentRef();
        httpsFragmentRef.setRef(getHTTPSFragmentRefId(fragment));
        return httpsFragmentRef;
    }

    private HTTPFragment createHTTPFragment(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, boolean useJumpHost) {
        HTTPFragment httpFragment = new HTTPFragment();
        // URLConnection
        URLConnectionType urlConnection = createURLConnection(connectionOptions);
        httpFragment.setURLConnection(urlConnection);
        // BasicAuthentication
        BasicAuthenticationType basicAuthentication = createBasicAuthenticationForURLConnection(connectionOptions);
        httpFragment.setBasicAuthentication(basicAuthentication);
        // JumpFragmentRef
        if (useJumpHost && isJumpFragmentSpecified(options)) {
            JumpFragment jumpFragment = createJumpFragment(options);
            JumpFragmentRef jumpFragmentRef = createJumpFragmentRef(jumpFragment);
            httpFragment.setJumpFragmentRef(jumpFragmentRef);
        }
        // ProxyForHTTP
        if (isProxySpecified(connectionOptions)) {
            if ("http".equalsIgnoreCase(connectionOptions.proxy_protocol.Value()) || connectionOptions.proxy_protocol.Value().isEmpty()) {
                if (connectionOptions.proxy_protocol.Value().isEmpty()) {
                    logger.warn("No value was specified for parameter '" + connectionOptions.getPrefix()
                            + "proxy_protocol'. I assume you intended to use 'http'.");
                }
                ProxyForHTTP proxyForHTTP = new ProxyForHTTP();
                httpFragment.setProxyForHTTP(proxyForHTTP);
                // HTTPProxy
                AuthenticatedProxyType httpProxy = createAuthenticatedProxyType(connectionOptions);
                proxyForHTTP.setHTTPProxy(httpProxy);
            } else {
                logger.warn("Invalid value specified for option '" + connectionOptions.getPrefix()
                        + "proxy_protocol'. Only value 'http' is allowed in combination with HTTP.");
            }
        }
        // CredentialStore
        if (isCredentialStoreFragmentSpecified(connectionOptions)) {
            CredentialStoreFragment credentialStoreFragment = createCredentialStoreFragment(connectionOptions);
            CredentialStoreFragmentRef credentialStoreFragmentRef = new CredentialStoreFragmentRef();
            credentialStoreFragmentRef.setRef(getCredentialStoreFragmentRefId(credentialStoreFragment));
            httpFragment.setCredentialStoreFragmentRef(credentialStoreFragmentRef);
        }
        return httpFragment;
    }

    private HTTPFragmentRef createHTTPFragmentRef(HTTPFragment fragment) {
        HTTPFragmentRef httpFragmentRef = new HTTPFragmentRef();
        httpFragmentRef.setRef(getHTTPFragmentRefId(fragment));

        return httpFragmentRef;
    }

    private CredentialStoreFragment createCredentialStoreFragment(SOSConnection2OptionsAlternate connectionOptions) {
        SOSCredentialStoreOptions credentialStoreOptions = connectionOptions.getCredentialStore();
        CredentialStoreFragment credentialStoreFragment = null;
        if (credentialStoreOptions.use_credential_Store.isTrue()) {
            credentialStoreFragment = new CredentialStoreFragment();
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
            if ("privatekey".equalsIgnoreCase(credentialStoreOptions.CS_AuthenticationMethod.Value())) {
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
            } else if ("password".equalsIgnoreCase(credentialStoreOptions.CS_AuthenticationMethod.Value())
                    || !credentialStoreOptions.CS_AuthenticationMethod.isDirty()) {
                if (!credentialStoreOptions.CS_AuthenticationMethod.isDirty()) {
                    logger.info("No value was specified for parameter 'CS_AuthenticationMethod'. I assume you intended to use value 'password'.");
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
                logger.debug("Skipping grouping element 'CSAuthentication' because of parameter 'CS_AuthenticationMethod' is not valid.");
            }
            // CSEntryPath
            if (credentialStoreOptions.CS_KeyPath.isDirty()) {
                credentialStoreFragment.setCSEntryPath(credentialStoreOptions.CS_KeyPath.Value());
            } else {
                reportMissingMandatoryParameter("CSKeyPath");
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
                    csExportAttachment.setCSKeepExportedFileOnExit(!credentialStoreOptions.CS_DeleteExportedFileOnExit.value());
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
            // CSStoreType may be neglected as the only implemented StoreType
            // currently is KeePass
        } else {
            logger.debug("Skipping grouping element 'CredentialStoreFragment' because of parameter '" + connectionOptions.getPrefix()
                    + "use_credential_store' is set to false or not specified.");
        }
        return credentialStoreFragment;
    }

    private FTPSFragment createFTPSFragment(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, boolean useJumpHost) {
        FTPSFragment ftpsFragment = new FTPSFragment();
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
        // FTPSClientSecurity
        if (isFTPSClientSecuritySpecified(connectionOptions)) {
            FTPSClientSecurityType ftpsClientSecurityType = new FTPSClientSecurityType();
            ftpsFragment.setFTPSClientSecurity(ftpsClientSecurityType);
            // SecurityMode
            if (connectionOptions.ftps_client_security.isDirty()) {
                if ("explicit".equalsIgnoreCase(connectionOptions.ftps_client_security.Value())
                        || "implicit".equalsIgnoreCase(connectionOptions.ftps_client_security. Value())) {
                    ftpsClientSecurityType.setSecurityMode(connectionOptions.ftps_client_security.Value());
                } else {
                    logger.warn("Invalid value specified for option 'ftps_client_security'. Only values 'explicit' and 'implicit' are allowed.");
                }
            }
            // KeyStoreType
            if (connectionOptions.keystore_type.isDirty()) {
                if ("jks".equalsIgnoreCase(connectionOptions.keystore_type.Value())
                        || "jceks".equalsIgnoreCase(connectionOptions.keystore_type.Value())
                        || "pkcs12".equalsIgnoreCase(connectionOptions.keystore_type.Value())
                        || "pkcs11".equalsIgnoreCase(connectionOptions.keystore_type.Value())
                        || "dks".equalsIgnoreCase(connectionOptions.keystore_type.Value())) {
                    ftpsClientSecurityType.setKeyStoreType(connectionOptions.keystore_type.Value());
                } else {
                    logger.warn("Invalid value specified for option 'keystore_type'. Only values 'JKS', 'JCEKS', 'PKCS12', "
                            + "'PKCS11' and 'DKS' are allowed.");
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
        if (useJumpHost && isJumpFragmentSpecified(options)) {
            JumpFragment jumpFragment = createJumpFragment(options);
            JumpFragmentRef jumpFragmentRef = createJumpFragmentRef(jumpFragment);
            ftpsFragment.setJumpFragmentRef(jumpFragmentRef);
        }
        // ProxyForFTPS
        if (isProxySpecified(connectionOptions)) {
            if ("socks4".equalsIgnoreCase(connectionOptions.proxy_protocol.Value())
                    || "socks5".equalsIgnoreCase(connectionOptions.proxy_protocol.Value()) || connectionOptions.proxy_protocol.Value().isEmpty()) {
                ProxyForFTPS proxyForFTPS = new ProxyForFTPS();
                ftpsFragment.setProxyForFTPS(proxyForFTPS);
                if ("socks4".equalsIgnoreCase(connectionOptions.proxy_protocol.Value())) {
                    UnauthenticatedProxyType socks4Proxy = createUnauthenticatedProxyType(connectionOptions);
                    proxyForFTPS.setSOCKS4Proxy(socks4Proxy);
                } else if ("socks5".equalsIgnoreCase(connectionOptions.proxy_protocol.Value())) {
                    AuthenticatedProxyType socks5Proxy = createAuthenticatedProxyType(connectionOptions);
                    proxyForFTPS.setSOCKS5Proxy(socks5Proxy);
                } else if (connectionOptions.proxy_protocol.Value().isEmpty()) {
                    if (connectionOptions.proxy_user.isDirty()) {
                        logger.warn("No value was specified for parameter '" + connectionOptions.getPrefix()
                                + "proxy_protocol'. As you specified a value for parameter '" + connectionOptions.getPrefix()
                                + "proxy_user' for authentication, I assume you meant 'socks5'.");
                        AuthenticatedProxyType socks5Proxy = createAuthenticatedProxyType(connectionOptions);
                        proxyForFTPS.setSOCKS5Proxy(socks5Proxy);
                    } else {
                        logger.debug("Skipping grouping element 'ProxyForFTPS' because of parameter '" + connectionOptions.getPrefix()
                                + "proxy_protocol' is not specified.");
                        ftpsFragment.setProxyForFTPS(null);
                    }
                }
            } else {
                logger.warn("Invalid value specified for option '" + connectionOptions.getPrefix()
                        + "proxy_protocol'. Only values 'socks4' and 'socks5' are allowed in combination with FTPS.");
            }
        }
        // CredentialStore
        if (isCredentialStoreFragmentSpecified(connectionOptions)) {
            CredentialStoreFragment credentialStoreFragment = createCredentialStoreFragment(connectionOptions);
            CredentialStoreFragmentRef credentialStoreFragmentRef = new CredentialStoreFragmentRef();
            credentialStoreFragmentRef.setRef(getCredentialStoreFragmentRefId(credentialStoreFragment));
            ftpsFragment.setCredentialStoreFragmentRef(credentialStoreFragmentRef);
        }
        return ftpsFragment;
    }

    private boolean isFTPSClientSecuritySpecified(SOSConnection2OptionsAlternate connectionOptions) {
        boolean returnValue = false;
        if (connectionOptions.ftps_client_security.isDirty() || connectionOptions.keystore_type.isDirty()
                || connectionOptions.keystore_file.isDirty() || connectionOptions.keystore_password.isDirty()) {
            returnValue = true;
        }
        return returnValue;
    }

    private FTPSFragmentRef createFTPSFragmentRef(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, FTPSFragment fragment) {
        FTPSFragmentRef ftpsFragmentRef = new FTPSFragmentRef();
        ftpsFragmentRef.setRef(getFTPSFragmentRefId(fragment));
        // Rename
        if (isRenameSpecified(connectionOptions)
                || ("target_".equalsIgnoreCase(connectionOptions.getPrefix()) && isUnprefixedRenameSpecified(options))) {
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
        if (isCumulateFilesSpecified(options)) {
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
            }
        } else {
            logger.debug("Skipping grouping element 'CumulateFiles' because of parameter 'CumulateFiles' is set to false or not specified.");
        }
        // CompressFiles
        if (options.compress_files.isTrue()) {
            CompressFiles compressFiles = new CompressFiles();
            if (options.compressed_file_extension.isDirty()) {
                compressFiles.setCompressedFileExtension(options.compressed_file_extension.Value());
            }
            targetFileOptions.setCompressFiles(compressFiles);
        }
        // CreateIntegrityHash
        if (options.CreateSecurityHashFile.isTrue()) {
            CreateIntegrityHashFile createIntegrityHash = new CreateIntegrityHashFile();
            if (options.SecurityHashType.isDirty()) {
                createIntegrityHash.setHashAlgorithm(options.SecurityHashType.Value());
            }
            targetFileOptions.setCreateIntegrityHashFile(createIntegrityHash);
        }
        // KeepModificationDate
        if (options.KeepModificationDate.isDirty()) {
            targetFileOptions.setKeepModificationDate(options.KeepModificationDate.value());
        }
        // DisableMakeDirectories
        if (options.makeDirs.isDirty()) {
            logger.info("Replacing 'makeDirs=" + options.makeDirs.Value() + "' with 'DisableMakeDirectories="
                    + String.valueOf(!options.makeDirs.value()) + "'");
            targetFileOptions.setDisableMakeDirectories(!options.makeDirs.value());
        }
        // DisableOverwriteFiles
        if (options.overwrite_files.isDirty()) {
            logger.info("Replacing 'overwrite_files=" + options.overwrite_files.Value() + "' with 'DisableOverwriteFiles="
                    + String.valueOf(!options.overwrite_files.value()) + "'");
            targetFileOptions.setDisableOverwriteFiles(!options.overwrite_files.value());
        }
        return targetFileOptions;
    }

    private boolean isCumulateFilesSpecified(JADEOptions options) {
        boolean returnValue = false;
        if (options.CumulateFiles.isDirty() || options.CumulativeFileName.isDirty() || options.CumulativeFileSeparator.isDirty()
                || options.CumulativeFileDelete.isDirty()) {
            returnValue = true;
        }
        return returnValue;
    }

    private boolean isTargetFileOptionsSpecified(JADEOptions options) {
        boolean returnValue = false;
        if (options.append_files.isDirty() || isAtomicitySpecified(options) || options.check_size.isDirty() || options.CumulateFiles.isDirty()
                || options.compress_files.isDirty() || options.CheckSecurityHash.isDirty() || options.CreateSecurityHashFile.isDirty()
                || options.KeepModificationDate.isDirty() || options.makeDirs.isDirty() || options.overwrite_files.isDirty()) {
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
        if (connectionOptions.Post_Command.isDirty() || connectionOptions.PostTransferCommands.isDirty()
                || connectionOptions.TFN_Post_Command.isDirty()) {
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

    private FTPFragment createFTPFragment(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, boolean useJumpHost) {
        FTPFragment ftpFragment = new FTPFragment();
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
        if (useJumpHost && isJumpFragmentSpecified(options)) {
            JumpFragment jumpFragment = createJumpFragment(options);
            JumpFragmentRef jumpFragmentRef = createJumpFragmentRef(jumpFragment);
            ftpFragment.setJumpFragmentRef(jumpFragmentRef);
        }
        // PassiveMode
        if (connectionOptions.passive_mode.isDirty()) {
            ftpFragment.setPassiveMode(connectionOptions.passive_mode.value());
        }
        // TransferMode
        if (connectionOptions.transfer_mode.isDirty()) {
            if ("ascii".equalsIgnoreCase(connectionOptions.transfer_mode.Value())
                    || "binary".equalsIgnoreCase(connectionOptions.transfer_mode.Value())) {
                ftpFragment.setTransferMode(connectionOptions.transfer_mode.Value());
            } else {
                logger.warn("Invalid value specified for option '" + connectionOptions.getPrefix()
                        + "transfer_mode'. Only values 'ascii' and 'binary' are allowed.");
            }
        }
        // ProxyForFTP
        if (isProxySpecified(connectionOptions)) {
            if ("http".equalsIgnoreCase(connectionOptions.proxy_protocol.Value())
                    || "socks4".equalsIgnoreCase(connectionOptions.proxy_protocol.Value())
                    || "socks5".equalsIgnoreCase(connectionOptions.proxy_protocol.Value()) || connectionOptions.proxy_protocol.Value().isEmpty()) {
                ProxyForFTP proxyForFTP = new ProxyForFTP();
                ftpFragment.setProxyForFTP(proxyForFTP);
                if ("http".equalsIgnoreCase(connectionOptions.proxy_protocol.Value())) {
                    AuthenticatedProxyType httpProxy = createAuthenticatedProxyType(connectionOptions);
                    proxyForFTP.setHTTPProxy(httpProxy);
                } else if ("socks4".equalsIgnoreCase(connectionOptions.proxy_protocol.Value())) {
                    UnauthenticatedProxyType socks4Proxy = createUnauthenticatedProxyType(connectionOptions);
                    proxyForFTP.setSOCKS4Proxy(socks4Proxy);
                } else if ("socks5".equalsIgnoreCase(connectionOptions.proxy_protocol.Value())) {
                    AuthenticatedProxyType socks5Proxy = createAuthenticatedProxyType(connectionOptions);
                    proxyForFTP.setSOCKS5Proxy(socks5Proxy);
                } else if (connectionOptions.proxy_protocol.Value().isEmpty()) {
                    logger.debug("Skipping grouping element 'ProxyForFTP' because of parameter '" + connectionOptions.getPrefix()
                            + "proxy_protocol' is not specified.");
                    ftpFragment.setProxyForFTP(null);
                }
            } else {
                logger.warn("Invalid value specified for option '" + connectionOptions.getPrefix()
                        + "proxy_protocol'. Only values 'http', 'socks4' and 'socks5' are allowed in combination with FTP.");
            }
        }
        // CredentialStore
        if (isCredentialStoreFragmentSpecified(connectionOptions)) {
            CredentialStoreFragment credentialStoreFragment = createCredentialStoreFragment(connectionOptions);
            CredentialStoreFragmentRef credentialStoreFragmentRef = new CredentialStoreFragmentRef();
            credentialStoreFragmentRef.setRef(getCredentialStoreFragmentRefId(credentialStoreFragment));
            ftpFragment.setCredentialStoreFragmentRef(credentialStoreFragmentRef);
        }
        return ftpFragment;
    }

    private FTPFragmentRef createFTPFragmentRef(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions, FTPFragment fragment) {
        FTPFragmentRef ftpFragmentRef = new FTPFragmentRef();
        ftpFragmentRef.setRef(getFTPFragmentRefId(fragment));
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
        if (isRenameSpecified(connectionOptions)
                || ("target_".equalsIgnoreCase(connectionOptions.getPrefix()) && isUnprefixedRenameSpecified(options))) {
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
        if (connectionOptions.Post_Command.isDirty() || connectionOptions.PostTransferCommands.isDirty()
                || connectionOptions.TFN_Post_Command.isDirty()) {
            returnValue = true;
        }
        return returnValue;
    }

    private String getCredentialStoreFragmentRefId(CredentialStoreFragment fragment) {
        List<CredentialStoreFragment> frags = getCredentialStoreFragments().getCredentialStoreFragment();
        for (CredentialStoreFragment entry : frags) {
            fragment.setName(entry.getName());
            if (elementToString(fragment).equals(elementToString(entry))) {
                return entry.getName();
            }
        }
        String fragmentId = getRandomFragmentId(fragment);
        fragment.setName(fragmentId);
        frags.add(fragment);
        return fragmentId;
    }

    private String getMailServerFragmentRefId(MailServerFragment fragment) {
        List<MailServerFragment> frags = getMailServerFragments().getMailServerFragment();
        for (MailServerFragment entry : frags) {
            fragment.setName(entry.getName());
            if (elementToString(fragment).equals(elementToString(entry))) {
                return entry.getName();
            }
        }
        String fragmentId = getRandomFragmentId(fragment);
        fragment.setName(fragmentId);
        frags.add(fragment);
        return fragmentId;
    }

    private String getMailFragmentRefId(MailFragment fragment) {
        List<MailFragment> frags = getNotificationFragments().getMailFragment();
        for (MailFragment entry : frags) {
            fragment.setName(entry.getName());
            if (elementToString(fragment).equals(elementToString(entry))) {
                return entry.getName();
            }
        }
        String fragmentId = getRandomFragmentId(fragment);
        fragment.setName(fragmentId);
        frags.add(fragment);
        return fragmentId;
    }

    private String getBackgroundServiceFragmentRefId(BackgroundServiceFragment fragment) {
        List<BackgroundServiceFragment> frags = getNotificationFragments().getBackgroundServiceFragment();
        for (BackgroundServiceFragment entry : frags) {
            fragment.setName(entry.getName());
            if (elementToString(fragment).equals(elementToString(entry))) {
                return entry.getName();
            }
        }
        String fragmentId = getRandomFragmentId(fragment);
        fragment.setName(fragmentId);
        frags.add(fragment);
        return fragmentId;
    }

    private String getWebDAVFragmentRefId(WebDAVFragment fragment) {
        List<WebDAVFragment> frags = getProtocolFragments().getWebDAVFragment();
        for (WebDAVFragment entry : frags) {
            fragment.setName(entry.getName());
            if (elementToString(fragment).equals(elementToString(entry))) {
                return entry.getName();
            }
        }
        String fragmentId = getRandomFragmentId(fragment);
        fragment.setName(fragmentId);
        frags.add(fragment);
        return fragmentId;
    }

    private String getFTPFragmentRefId(FTPFragment fragment) {
        List<FTPFragment> frags = getProtocolFragments().getFTPFragment();
        for (FTPFragment entry : frags) {
            fragment.setName(entry.getName());
            if (elementToString(fragment).equals(elementToString(entry))) {
                return entry.getName();
            }
        }
        String fragmentId = getRandomFragmentId(fragment);
        fragment.setName(fragmentId);
        frags.add(fragment);
        return fragmentId;
    }

    private String getSFTPFragmentRefId(SFTPFragment fragment) {
        List<SFTPFragment> frags = getProtocolFragments().getSFTPFragment();
        for (SFTPFragment entry : frags) {
            fragment.setName(entry.getName());
            if (elementToString(fragment).equals(elementToString(entry))) {
                return entry.getName();
            }
        }
        String fragmentId = getRandomFragmentId(fragment);
        fragment.setName(fragmentId);
        frags.add(fragment);
        return fragmentId;
    }

    private String getFTPSFragmentRefId(FTPSFragment fragment) {
        List<FTPSFragment> frags = getProtocolFragments().getFTPSFragment();
        for (FTPSFragment entry : frags) {
            fragment.setName(entry.getName());
            if (elementToString(fragment).equals(elementToString(entry))) {
                return entry.getName();
            }
        }
        String fragmentId = getRandomFragmentId(fragment);
        fragment.setName(fragmentId);
        frags.add(fragment);
        return fragmentId;
    }

    private String getSMBFragmentRefId(SMBFragment fragment) {
        List<SMBFragment> frags = getProtocolFragments().getSMBFragment();
        for (SMBFragment entry : frags) {
            fragment.setName(entry.getName());
            if (elementToString(fragment).equals(elementToString(entry))) {
                return entry.getName();
            }
        }
        String fragmentId = getRandomFragmentId(fragment);
        fragment.setName(fragmentId);
        frags.add(fragment);
        return fragmentId;
    }

    private String getJumpFragmentRefId(JumpFragment fragment) {
        List<JumpFragment> frags = getProtocolFragments().getJumpFragment();
        for (JumpFragment entry : frags) {
            fragment.setName(entry.getName());
            if (elementToString(fragment).equals(elementToString(entry))) {
                return entry.getName();
            }
        }
        String fragmentId = getRandomFragmentId(fragment);
        fragment.setName(fragmentId);
        frags.add(fragment);
        return fragmentId;
    }

    private String getHTTPSFragmentRefId(HTTPSFragment fragment) {
        List<HTTPSFragment> frags = getProtocolFragments().getHTTPSFragment();
        for (HTTPSFragment entry : frags) {
            fragment.setName(entry.getName());
            if (elementToString(fragment).equals(elementToString(entry))) {
                return entry.getName();
            }
        }
        String fragmentId = getRandomFragmentId(fragment);
        fragment.setName(fragmentId);
        frags.add(fragment);
        return fragmentId;
    }

    private String getHTTPFragmentRefId(HTTPFragment fragment) {
        List<HTTPFragment> frags = getProtocolFragments().getHTTPFragment();
        for (HTTPFragment entry : frags) {
            fragment.setName(entry.getName());
            if (elementToString(fragment).equals(elementToString(entry))) {
                return entry.getName();
            }
        }
        String fragmentId = getRandomFragmentId(fragment);
        fragment.setName(fragmentId);
        frags.add(fragment);
        return fragmentId;
    }

    private RenameType createRename(JADEOptions options, SOSConnection2OptionsAlternate connectionOptions) {
        RenameType rename = new RenameType();
        if ("source_".equalsIgnoreCase(connectionOptions.getPrefix())) {
            if (connectionOptions.ReplaceWhat.isDirty()) {
                rename.setReplaceWhat(connectionOptions.ReplaceWhat.Value());
            }
            if (connectionOptions.ReplaceWith.isDirty()) {
                rename.setReplaceWith(connectionOptions.ReplaceWith.Value());
            }
        } else if ("target_".equalsIgnoreCase(connectionOptions.getPrefix())) {
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
        logger.warn("Mandatory parameter '" + parameterName + "' is missing. " + message);
        missingMandatoryParameter = true;
    }

    private void reportMissingMandatoryParameter(String parameterName) {
        reportMissingMandatoryParameter(parameterName, "");
    }

    private String getRandomFragmentId(Object caller) {
        return getRandomFragmentId(caller.getClass().getSimpleName());
    }

    private String getRandomFragmentId(String prefix) {
        if (!refsCounter.containsKey(prefix)) {
            refsCounter.put(prefix, 0);
        } else {
            int counter = refsCounter.get(prefix);
            refsCounter.put(prefix, counter + 1);
        }
        return prefix + ":" + refsCounter.get(prefix);
    }

    private void writeXML(Object object, File outFile) throws JAXBException {
        logger.info("Writing converted file '" + outFile.getAbsolutePath() + "'");
        JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, schemaLocation);
        jaxbMarshaller.marshal(object, outFile);
        logger.info("Done." + System.lineSeparator());
    }

    private String elementToString(Object object) {
        StringWriter w = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.marshal(object, w);
            return w.toString();
        } catch (JAXBException e) {
            logger.error("", e);
            return "";
        } finally {
            try {
                w.close();
            } catch (IOException ee) {
            }
        }

    }

    private JADEOptions loadIniFile(String filepath, String profilename) {
        JADEOptions options = new JADEOptions();
        options.settings.Value(filepath);
        options.profile.Value(profilename);
        options.gflgSubsituteVariables = false;
        options.ReadSettingsFile();
        return options;
    }

}