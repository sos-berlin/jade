package com.sos.jadevaadincockpit.view;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.sos.jadevaadincockpit.i18n.I18NComponent;
import com.sos.jadevaadincockpit.i18n.JadeCockpitMsg;
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
public class FormsTabSheet extends TabSheet implements I18NComponent {
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
		
		operationForm = new OperationForm(new JadeCockpitMsg("JADE_Tab_OperationForm").label(), profileItem); // Operation
		sourceConnectionForm = new ConnectionForm(new JadeCockpitMsg("JADE_Tab_ConnectionForm_Source").label(), profileItem, ConnectionForm.ConnectionType.SOURCE); // Source
		fileSelectionForm = new FileSelectionForm(new JadeCockpitMsg("JADE_Tab_FileSelectionForm").label(), profileItem); // Objects
		targetConnectionForm = new ConnectionForm(new JadeCockpitMsg("JADE_Tab_ConnectionForm_Target").label(), profileItem, ConnectionForm.ConnectionType.TARGET); // Target
		miscForm = new MiscForm(new JadeCockpitMsg("JADE_Tab_MiscForm").label(), profileItem); // Misc
		loggingForm = new LoggingForm(new JadeCockpitMsg("JADE_Tab_LoggingForm").label(), profileItem); // Logging
		notificationForm = new NotificationForm(new JadeCockpitMsg("JADE_Tab_NotificationForm").label(), profileItem); // Notification	
		backgroundServiceForm = new BackgroundServiceForm(new JadeCockpitMsg("JADE_Tab_BackgroundServiceForm").label(), profileItem); // BackgroundService
		jitlForm = new JitlForm(new JadeCockpitMsg("JADE_Tab_JitlForm").label(), profileItem); // JITL
		pollEngineForm = new PollEngineForm(new JadeCockpitMsg("JADE_Tab_PollEngineForm").label(), profileItem); // PollEnginge
		overviewForm = new OverviewForm(new JadeCockpitMsg("JADE_Tab_OverviewForm").label(), profileItem); // Overview
		
		addComponents(operationForm, sourceConnectionForm, fileSelectionForm, targetConnectionForm, miscForm, loggingForm, notificationForm, backgroundServiceForm, jitlForm, pollEngineForm, overviewForm);

		// --------------------
		HashMap<String, SOSOptionElement> optionElements = (HashMap<String, SOSOptionElement>) profileItem.getItemProperty(ProfileContainer.PROPERTY.OPTIONS).getValue();
		for (String s : ApplicationAttributes.allOptionsFromSettingsFile.keySet()) {
			if (!optionElements.containsKey(s)) {
				ApplicationAttributes.missingOptions.put(s, ApplicationAttributes.allOptionsFromSettingsFile.get(s));
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

	public void refreshLocale(Locale newLocale) {
		 getTab(operationForm).setCaption(new JadeCockpitMsg("JADE_Tab_OperationForm").label());
		 getTab(sourceConnectionForm).setCaption(new JadeCockpitMsg("JADE_Tab_ConnectionForm_Source").label());
		 getTab(fileSelectionForm).setCaption(new JadeCockpitMsg("JADE_Tab_FileSelectionForm").label());
		 getTab(targetConnectionForm).setCaption(new JadeCockpitMsg("JADE_Tab_ConnectionForm_Target").label());
		 getTab(miscForm).setCaption(new JadeCockpitMsg("JADE_Tab_MiscForm").label());
		 getTab(loggingForm).setCaption(new JadeCockpitMsg("JADE_Tab_LoggingForm").label());
		 getTab(notificationForm).setCaption(new JadeCockpitMsg("JADE_Tab_NotificationForm").label());
		 getTab(backgroundServiceForm).setCaption(new JadeCockpitMsg("JADE_Tab_BackgroundServiceForm").label());
		 getTab(jitlForm).setCaption(new JadeCockpitMsg("JADE_Tab_JitlForm").label());
		 getTab(pollEngineForm).setCaption(new JadeCockpitMsg("JADE_Tab_PollEngineForm").label());
		 getTab(overviewForm).setCaption(new JadeCockpitMsg("JADE_Tab_OverviewForm").label());
		
	}
	
	
}
