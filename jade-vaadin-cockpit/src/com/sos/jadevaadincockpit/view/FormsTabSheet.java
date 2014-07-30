package com.sos.jadevaadincockpit.view;

import java.util.HashMap;
import java.util.Map;

import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.jadevaadincockpit.globals.Globals;
import com.sos.jadevaadincockpit.view.forms.BackgroundServiceForm;
import com.sos.jadevaadincockpit.view.forms.ConnectionForm;
import com.sos.jadevaadincockpit.view.forms.FileSelectionForm;
import com.sos.jadevaadincockpit.view.forms.JitlForm;
import com.sos.jadevaadincockpit.view.forms.LoggingForm;
import com.sos.jadevaadincockpit.view.forms.MiscForm;
import com.sos.jadevaadincockpit.view.forms.NotificationForm;
import com.sos.jadevaadincockpit.view.forms.OperationForm;
import com.sos.jadevaadincockpit.view.forms.PollEngineForm;
import com.sos.jadevaadincockpit.viewmodel.ProfileContainer;
import com.vaadin.data.Item;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.Reindeer;

/**
 * 
 * @author JS
 *
 */
public class FormsTabSheet extends TabSheet {
	private static final long serialVersionUID = -8505566611584654428L;
	
	private static Map<Item, FormsTabSheet> instances = new HashMap<Item, FormsTabSheet>();
	
	private final Item profileItem;
	
	private OperationForm operationForm;
	private ConnectionForm sourceConnectionForm;
	private FileSelectionForm fileSelectionForm;
	private ConnectionForm targetConnectionForm;
	private MiscForm miscForm;
	private LoggingForm loggingForm;
	private NotificationForm notificationForm;
	private BackgroundServiceForm backgroundServiceForm;
	private JitlForm jitlForm;
	private PollEngineForm pollEngineForm;
	private OverviewForm overviewForm;
	
	
	public FormsTabSheet(final Item profileItem) {
		this.profileItem = profileItem;
		
		addStyleName(Reindeer.TABSHEET_BORDERLESS);
		
		init();
	}
	
	private void init() {
		
		operationForm = new OperationForm("Operation", profileItem);
		sourceConnectionForm = new ConnectionForm("Source", profileItem, ConnectionForm.ConnectionType.SOURCE);
		fileSelectionForm = new FileSelectionForm("Objects", profileItem);
		targetConnectionForm = new ConnectionForm("Target", profileItem, ConnectionForm.ConnectionType.TARGET);
		miscForm = new MiscForm("Misc", profileItem);
		loggingForm = new LoggingForm("Logging", profileItem);
		notificationForm = new NotificationForm("Notification", profileItem);	
		backgroundServiceForm = new BackgroundServiceForm("BackgroundService", profileItem);
		jitlForm = new JitlForm("JITL", profileItem);
		pollEngineForm = new PollEngineForm("PollEngine", profileItem);
		overviewForm = new OverviewForm("Overview", profileItem);
		
		addComponents(operationForm, sourceConnectionForm, fileSelectionForm, targetConnectionForm, miscForm, loggingForm, notificationForm, backgroundServiceForm, jitlForm, pollEngineForm, overviewForm);

		// --------------------
		HashMap<String, SOSOptionElement> optionElements = (HashMap<String, SOSOptionElement>) profileItem.getItemProperty(ProfileContainer.PROPERTY.OPTIONS).getValue();
		for (String s : Globals.allOptionsFromSettingsFile.keySet()) {
			if (!optionElements.containsKey(s)) {
				Globals.missingOptions.put(s, Globals.allOptionsFromSettingsFile.get(s));
			}
		}
		// --------------------
	}
	
	/** Creates a new FormsTabSheet for the given profileItem or returns the existing one.
	 * @return the formsTabSheet
	 */
	public static FormsTabSheet getFormsTabSheet(Item profileItem) {
		if (instances.get(profileItem) == null) {
			instances.put(profileItem, new FormsTabSheet(profileItem));
		}
		return instances.get(profileItem);
	}
	
	/**
	 * @return the profile
	 */
	public Item getProfileItem() {
		return profileItem;
	}

	/**
	 * @return the operationForm
	 */
	public OperationForm getOperationForm() {
		return operationForm;
	}

	/**
	 * @return the sourceConnectionForm
	 */
	public ConnectionForm getSourceConnectionForm() {
		return sourceConnectionForm;
	}

	/**
	 * @return the fileSelectionForm
	 */
	public FileSelectionForm getFileSelectionForm() {
		return fileSelectionForm;
	}

	/**
	 * @return the targetConnectionForm
	 */
	public ConnectionForm getTargetConnectionForm() {
		return targetConnectionForm;
	}

	/**
	 * @return the miscForm
	 */
	public MiscForm getMiscForm() {
		return miscForm;
	}

	/**
	 * @return the loggingForm
	 */
	public LoggingForm getLoggingForm() {
		return loggingForm;
	}

	/**
	 * @return the notificationForm
	 */
	public NotificationForm getNotificationForm() {
		return notificationForm;
	}

	/**
	 * @return the backgroundServiceForm
	 */
	public BackgroundServiceForm getBackgroundServiceForm() {
		return backgroundServiceForm;
	}

	/**
	 * @return the jitlForm
	 */
	public JitlForm getJitlForm() {
		return jitlForm;
	}

	/**
	 * @return the pollEngineForm
	 */
	public PollEngineForm getPollEngineForm() {
		return pollEngineForm;
	}

	/**
	 * @return the overviewForm
	 */
	public OverviewForm getOverviewForm() {
		return overviewForm;
	}
	
	
}
