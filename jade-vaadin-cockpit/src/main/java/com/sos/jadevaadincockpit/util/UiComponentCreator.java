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
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

/**
 * 
 * @author JS
 *
 */
public class UiComponentCreator implements Serializable {
	private static final long serialVersionUID = 752895948467492333L;
	
	private HashMap<String, SOSOptionElement> options;
	
	@SuppressWarnings("unchecked")
	public UiComponentCreator(Item profile) {
		options = (HashMap<String, SOSOptionElement>) profile.getItemProperty(ProfileContainer.PROPERTY.OPTIONS).getValue();
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
//			comp.setDescription(msg.tooltip() + " - " + shortKey);
			comp.setDescription(optionElement.getToolTip());
		}
		
		return comp;
	}

	/**
	 * Creates a component according to the optionElement's component type and connects it to the optionElement.
	 * @param optionElement
	 * @return The component
	 */
	public AbstractField<?> getComponent(SOSOptionElement optionElement) {
		
		AbstractField<?> comp = null;
		
		if (optionElement != null) {
			
			comp = getComponentByType(optionElement);
			
			comp.setData(optionElement);
			
			comp.setWidth("80%");
			comp.setImmediate(true);
			
			handleProtectedField(comp, optionElement.isProtected());
			
			addToOptionsMap(optionElement);
			
		}
		
		return comp;
	}
	
	/**
	 * 
	 * @param comp
	 * @param isProtected
	 */
	private void handleProtectedField(final AbstractField<?> comp, boolean isProtected) {
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
			
			TextField textField = getTextField(optionElement);
//			FileResource resource = new FileResource(new File(Globals.getBasePath() + "/WEB-INF/icons/Delete.gif"));
//			textField.setIcon(resource);
			comp = textField;
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
			public void ValueHasChanged(String pstrNewValue) {
				comboBox.setValue(pstrNewValue);
			}

			@Override
			public void ValidationError(
					SOSValidationError pobjVE) {
				// TODO Auto-generated method stub
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
			public void ValueHasChanged(String pstrNewValue) {
				
				checkBox.setValue(optionElement.String2Bool(pstrNewValue));
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
		
		optionElement.addValueChangedListener(new IValueChangedListener() {
			@Override
			public void ValueHasChanged(String pstrNewValue) {
				
				if (!textField.isReadOnly()) {
					textField.setValue(pstrNewValue);
				}
			}

			@Override
			public void ValidationError(
					SOSValidationError pobjVE) {
				// TODO Auto-generated method stub
			}
		});
		
//		changeStyleName(textField, optionElement.isDefault());
		
		return textField;
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
