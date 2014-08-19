package com.sos.jadevaadincockpit.view;

import java.util.HashMap;

import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.util.UiComponentCreator;
import com.sos.jadevaadincockpit.viewmodel.ParameterContainer;
import com.sos.jadevaadincockpit.viewmodel.ProfileContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;

/**
 * 
 * @author JS
 * 
 */
public class ParameterTable extends Table {
	private static final long serialVersionUID = -9084257772050443433L;

	private ParameterTable table;
	
	private ParameterContainer parameterContainer;
	
	private HashMap<String, Field<?>> fields = new HashMap<String, Field<?>>();

	public ParameterTable() {
		init();
	}

	private void init() {
		table = this;
		parameterContainer = getParameterContainer();
		setContainerDataSource(parameterContainer);
		setSizeFull();
		setSelectable(true);
		setImmediate(true);
		setEditable(true);
		setVisibleColumns(ParameterContainer.PROPERTY.NAME,
				ParameterContainer.PROPERTY.VALUE);
		setColumnHeader(ParameterContainer.PROPERTY.NAME, "Parameter");
		setColumnHeader(ParameterContainer.PROPERTY.VALUE, "Value");

		setTableFieldFactory(new TableFieldFactory() {
			private static final long serialVersionUID = -6361489485204776592L;

			@Override
			public Field<?> createField(Container container, final Object itemId, final Object propertyId, Component uiContext) {

				ProfileTree tree = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree();
				
				Field<?> f = null;

				// entry names must not be changed
				if (!propertyId.equals(ParameterContainer.PROPERTY.NAME)) {
					
					// the item to create a field for
					Item currentTableItem = table.getItem(itemId);
					
					// the item's NAME-property equals the key of the option
					String key = (String) currentTableItem.getItemProperty(ParameterContainer.PROPERTY.NAME).getValue();
					
					// check if the field has already been created. if so, return it
					if (fields.containsKey(key)) {
						f = fields.get(key);
					} else {
						// get the corresponding profileItem
						Object profileItemId = currentTableItem.getItemProperty(ParameterContainer.PROPERTY.PROFILEID).getValue();
						Item profileItem = tree.getItem(profileItemId);
						
						@SuppressWarnings("unchecked") // get the map of all options used in the forms
						HashMap<String, SOSOptionElement> options = (HashMap<String, SOSOptionElement>) profileItem.getItemProperty(ProfileContainer.PROPERTY.OPTIONS).getValue();
						
						
						if (options.containsKey(key)) {
							
							// get the optionElement with the given key
							SOSOptionElement optionElement = options.get(key);
							
							UiComponentCreator componentCreator = new UiComponentCreator(profileItem);
							AbstractField<?> comp = componentCreator.getComponent(optionElement);
							
							f = comp;
							fields.put(key, f);
						}
					}
				}
				return f;
			}
		});
	}
	
	public ParameterContainer getParameterContainer() {
		if (parameterContainer == null) {
			parameterContainer = new ParameterContainer();
		}
		return parameterContainer;
	}
}
