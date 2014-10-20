package com.sos.jadevaadincockpit.util;

import java.io.Serializable;
import java.util.HashMap;

import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickListener;

import com.sos.JSHelper.Options.IValueChangedListener;
import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.JSHelper.Options.SOSValidationError;
import com.sos.jadevaadincockpit.data.ProfileContainer;
import com.sos.jadevaadincockpit.i18n.JadeCockpitMsg;
import com.sos.jadevaadincockpit.view.components.JadeCheckBox;
import com.sos.jadevaadincockpit.view.components.JadeComboBox;
import com.sos.jadevaadincockpit.view.components.JadeTextField;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Notification;

/**
 * 
 * @author JS
 *
 */
public class UiComponentCreator implements Serializable {
	private static final long serialVersionUID = 752895948467492333L;
	
	private HashMap<String, SOSOptionElement> options;
	private HashMap<String, AbstractField<?>> fields;
	
	@SuppressWarnings("unchecked")
	public UiComponentCreator(Item profile) {
		options = (HashMap<String, SOSOptionElement>) profile.getItemProperty(ProfileContainer.PROPERTY.OPTIONS).getValue();
		fields = new HashMap<String, AbstractField<?>>(); // all fields of the form this ComponentCreator belongs to
	}
	
	/**
	 * Creates a component according to the optionElement's component type, connects it to the optionElement and sets the control's caption and description.
	 * @param optionElement
	 * @return The component
	 */
	public AbstractField<?> getComponentWithCaption(final SOSOptionElement optionElement) {

		final AbstractField<?> comp = getComponent(optionElement);
		
		final String shortKey = optionElement.getShortKey();
		
		if (!(comp instanceof com.sos.jadevaadincockpit.view.components.JadeTextField)) {
			JadeCockpitMsg msg = new JadeCockpitMsg("jade_l_" + shortKey);
			comp.setCaption(msg.label());
			comp.setDescription(msg.tooltip() + " - " + shortKey);
//			comp.setDescription(optionElement.getToolTip());
		}
		
		comp.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 5851279292914178361L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				handleProtectedField(comp, optionElement.isProtected());
			}
		});
		
		return comp;
	}

	/**
	 * Creates a component according to the optionElement's component type and connects it to the optionElement.
	 * @param optionElement
	 * @return The component
	 */
	public AbstractField<?> getComponent(final SOSOptionElement optionElement) {
		
		AbstractField<?> comp = null;
		
		if (optionElement != null) {
			
			comp = getComponentByType(optionElement);
			
			comp.setData(optionElement);
			
			comp.setWidth("80%");
			comp.setImmediate(true);
			
			handleProtectedField(comp, optionElement.isProtected());
			
			addToOptionsMap(optionElement);
			
			addToFieldsMap(comp);
			
		}
		
		return comp;
	}
	
	/**
	 * 
	 * @param comp
	 * @param isProtected
	 */
	public void handleProtectedField(final AbstractField<?> comp, boolean isProtected) {
		// options from includes are protected
		if (isProtected) {
			comp.setReadOnly(true);
			
			ContextMenu protectedFieldContextMenu = new ContextMenu();
			ContextMenuItem editItem = protectedFieldContextMenu.addItem("Overwrite Include");
			
			
			editItem.addItemClickListener(new ContextMenuItemClickListener() {
				
				@Override
				public void contextMenuItemClicked(ContextMenuItemClickEvent event) {
					comp.setReadOnly(false);
					comp.focus();
					((SOSOptionElement) comp.getData()).setProtected(false);
				}
			});

			protectedFieldContextMenu.setAsContextMenuOf(comp);
		} else {
			comp.setReadOnly(false);
		}
		
	}

	/**
	 * Adds the optionElement to the map of all optionElements
	 * @param optionElement
	 */
	private void addToOptionsMap(SOSOptionElement optionElement) {
		
		String shortKey = optionElement.getShortKey();
		
		if (!options.containsKey(shortKey)) {
			options.put(shortKey, optionElement);
		}
	}
	
	/**
	 * Adds the component to the map of all components
	 * @param comp
	 */
	private void addToFieldsMap(AbstractField<?> comp) {
		
		String shortKey = ((SOSOptionElement) comp.getData()).getShortKey();
		
		if (!fields.containsKey(shortKey)) {
			fields.put(shortKey, comp);
		}
	}
	
	/**
	 * Checks the component type and creates an according component.
	 * @param optionElement
	 * @return The component according to the optionElement's component type.
	 */
	private AbstractField<?> getComponentByType(SOSOptionElement optionElement) {
		
		AbstractField<?> comp = null;
		String componentType = optionElement.getControlType();
		
		if (componentType.equalsIgnoreCase("text")) {
			comp = getTextField(optionElement);

		} else if (componentType.equalsIgnoreCase("checkbox")) {
			comp = getCheckBox(optionElement);
			
		} else if (componentType.equalsIgnoreCase("combo")) {
			comp = getComboBox(optionElement);
			
		} else { // component types which are not covered by the previous checks
			
			comp = getTextField(optionElement);
		}
		
		return comp;
	}
	

	/**
	 * Creates a ComboBox with the value list from the optionElement and connects it to the optionElement.
	 * @param optionElement
	 * @return The ComboBox
	 */
	@SuppressWarnings("unchecked")
	private JadeComboBox getComboBox(final SOSOptionElement optionElement) {
		
		String shortKey = optionElement.getShortKey();
		
		JadeCockpitMsg msg = new JadeCockpitMsg("jade_l_" + shortKey);
		
		final JadeComboBox comboBox = new JadeComboBox(msg);
		
		for (String value : optionElement.getValueList()) { // TODO valueList scheint teilweise leer zu sein!
			Item item = comboBox.addItem(value);
			item.getItemProperty(JadeComboBox.PROPERTY.VALUE).setValue(value);
		}
		
		comboBox.setValue(optionElement.Value());
		
		// Value(String value) will throw exception if option is mandatory and set to null. disable null selection
		if (optionElement.isMandatory()) {
			comboBox.setNullSelectionAllowed(false);
		}
		
		comboBox.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 4494994397731811284L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				Object currentItemId = comboBox.getValue();

				String value = null;
				if (currentItemId != null && !currentItemId.equals("null") && !currentItemId.equals("")) {
					
					try {
						value = (String) comboBox.getItem(currentItemId).getItemProperty(JadeComboBox.PROPERTY.VALUE).getValue();
						
					} catch (Exception e) {
						Notification.show("ERROR! currentItemId is: '" + currentItemId.toString() + "'", Notification.Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
					
					optionElement.Value(value);
					
				} else {
//					optionElement.setNull(); // TODO was passiert dann?
					if (!optionElement.isMandatory()) {
						optionElement.Value(null);						
					}
				}
				
//				changeStyleName(comboBox, optionElement.isDefault());
			}
		});
		
		optionElement.addValueChangedListener(new IValueChangedListener() {

			@Override
			public void ValidationError(
					SOSValidationError pobjVE) {
				// TODO Auto-generated method stub
			}

			@Override
			public void ValueHasChanged(SOSOptionElement pobjOptionElement) {
				
				// value of readOnly fields can not be changed
				if (comboBox.isReadOnly()) {
					comboBox.setReadOnly(false);
					comboBox.setValue(pobjOptionElement.Value());
					comboBox.setReadOnly(true);
				} else {
					comboBox.setValue(pobjOptionElement.Value());
				}
				handleProtectedField(comboBox, optionElement.isProtected());
			}
		});
		
//		changeStyleName(comboBox, optionElement.isDefault());
		
		return comboBox;
	}

	/**
	 * Creates a CheckBox and connects it to the optionElement.
	 * @param optionElement
	 * @return The CheckBox
	 */
	private JadeCheckBox getCheckBox(final SOSOptionElement optionElement) {
		
		String shortKey = optionElement.getShortKey();
		
		JadeCockpitMsg msg = new JadeCockpitMsg("jade_l_" + shortKey);
		
		final JadeCheckBox checkBox = new JadeCheckBox(msg);
		
		checkBox.setValue(optionElement.String2Bool(optionElement.Value()));
		
		checkBox.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 4494994397731811284L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				Object value = checkBox.getValue();
				
				if (value != null) {
					optionElement.Value(optionElement.boolean2String((Boolean) value));
					
				} else {
					optionElement.setNull();
				}
				
//				changeStyleName(checkBox, optionElement.isDefault());
			}
		});
		
		optionElement.addValueChangedListener(new IValueChangedListener() {
			@Override
			public void ValueHasChanged(SOSOptionElement pobjOptionElement) {
				
				// value of readOnly fields can not be changed
				if (checkBox.isReadOnly()) {
					checkBox.setReadOnly(false);
					checkBox.setValue(optionElement.String2Bool(pobjOptionElement.Value()));
					checkBox.setReadOnly(true);
				} else {
					checkBox.setValue(optionElement.String2Bool(pobjOptionElement.Value()));
				}
				handleProtectedField(checkBox, optionElement.isProtected());
			}

			@Override
			public void ValidationError(
					SOSValidationError pobjVE) {
				// TODO Auto-generated method stub
			}
		});
		
//		changeStyleName(checkBox, optionElement.isDefault());
		
		return checkBox;
	}

	/**
	 * Creates a TextField and connects it to the optionElement.
	 * @param optionElement
	 * @return The TextField
	 */
	private JadeTextField getTextField(final SOSOptionElement optionElement) {
		
		final String shortKey = optionElement.getShortKey();
		
		JadeCockpitMsg msg = new JadeCockpitMsg("jade_l_" + shortKey);
		
		final JadeTextField textField = new JadeTextField(msg);
		
		textField.setValue(optionElement.Value());
		
		textField.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 4494994397731811284L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				String value = (String) textField.getValue();
				
				if (value != null) {
					optionElement.Value(value);
					
				} else {
					optionElement.setNull();
				}
				
//				changeStyleName(textField, optionElement.isDefault());
			}
		});
		
		optionElement.addValueChangedListener(new IValueChangedListener() { // TODO valueHasChanged wird VOR setzen des Protected flag aufgerufen -> setProtected sollte auch listener benachrichtigen
			@Override
			public void ValueHasChanged(SOSOptionElement pobjOptionElement) {
				
				// value of readOnly fields can not be changed
				if (textField.isReadOnly()) {
					textField.setReadOnly(false);
					textField.setValue(pobjOptionElement.Value());
					textField.setReadOnly(true);
				} else {
					textField.setValue(pobjOptionElement.Value());
				}
				handleProtectedField(textField, optionElement.isProtected());
			}

			@Override
			public void ValidationError(
					SOSValidationError pobjVE) {
				// TODO Auto-generated method stub
			}
		});
		
		return textField;
	}
	
	public HashMap<String, AbstractField<?>> getFields() {
		return fields;
	}
	
	/**
	 * 
	 * @param component
	 * @param isDefault
	 */
//	private void changeStyleName(AbstractField<?> component, boolean isDefault) {
//		if (!isDefault) {
//			component.addStyleName("default-option-value");
//		} else {
//			component.removeStyleName("default-option-value");
//		}
//	}
	
}

//class A extends CustomComponent {
//	GridLayout baseLayout = new GridLayout(2, 1);
//	
//	public A(Label label, AbstractComponent component) {
//		setCompositionRoot(baseLayout);
//		baseLayout.addComponent(label);
//		label.setSizeUndefined();
//		baseLayout.addComponent(component);
//		component.setSizeUndefined();
//		baseLayout.setWidth("100%");
//		baseLayout.setColumnExpandRatio(0, 5);
//		baseLayout.setColumnExpandRatio(1, 2);
//	}
//}
