package com.sos.jadevaadincockpit.util;

import java.io.Serializable;

import com.sos.JSHelper.Options.IValueChangedListener;
import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.JSHelper.Options.SOSValidationError;
import com.sos.jadevaadincockpit.view.components.JadeComboBox;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

/**
 * 
 * @author JS
 * @deprecated Functionality moved to UiComponentCreator. Use that class instead.
 */
public class UiComponentHelper implements Serializable {
	private static final long serialVersionUID = -5913922458117089734L;

	public UiComponentHelper(final AbstractField<?> component, final SOSOptionElement optionElement) {
		
			if (component instanceof TextField) {
				component.addValueChangeListener(new ValueChangeListener() {
					private static final long serialVersionUID = 4494994397731811284L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						String value = (String) component.getValue();
						if (value != null) {
							optionElement.Value(value);
						} else {
							optionElement.setNull();
						}
						changeStyleName(component, optionElement.isDefault());
					}
				});
				optionElement
						.addValueChangedListener(new IValueChangedListener() {
							@Override
							public void ValueHasChanged(String pstrNewValue) {
								if (!component.isReadOnly()) {
									((TextField) component)
											.setValue(pstrNewValue);
								}
							}

							@Override
							public void ValidationError(
									SOSValidationError pobjVE) {
								// TODO Auto-generated method stub
							}
						});
			} 
			else if (component instanceof CheckBox) {
				component.addValueChangeListener(new ValueChangeListener() {
					private static final long serialVersionUID = 4494994397731811284L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						Object value = component.getValue();
						if (value != null) {
							optionElement.Value(optionElement
									.boolean2String((Boolean) value));
						} else {
							optionElement.setNull();
						}
						changeStyleName(component, optionElement.isDefault());
					}
				});
				optionElement
						.addValueChangedListener(new IValueChangedListener() {
							@Override
							public void ValueHasChanged(String pstrNewValue) {
								((CheckBox) component).setValue(optionElement
										.String2Bool(pstrNewValue));
							}

							@Override
							public void ValidationError(
									SOSValidationError pobjVE) {
								// TODO Auto-generated method stub
							}
						});
			} else if (component instanceof ComboBox) {
				component.addValueChangeListener(new ValueChangeListener() {
					private static final long serialVersionUID = 4494994397731811284L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						ComboBox combo = (ComboBox) component;
						Object currentItemId = combo.getValue();

						String value = null;
						if (currentItemId != null
								&& !currentItemId.equals("null") && !currentItemId.equals("")) {
							try {
							value = (String) combo
									.getItem(currentItemId)
									.getItemProperty(JadeComboBox.PROPERTY.VALUE)
									.getValue();
							} catch (Exception e) {
								Notification.show("ERROR! currentItemId is: '" + currentItemId.toString() + "'", Notification.Type.ERROR_MESSAGE);
								e.printStackTrace();
							}
							optionElement.Value(value);
						} else {
							optionElement.setNull(); // TODO was passiert dann?
						}
						changeStyleName(component, optionElement.isDefault());
					}
				});
				optionElement
						.addValueChangedListener(new IValueChangedListener() {
							@Override
							public void ValueHasChanged(String pstrNewValue) {
								((ComboBox) component).setValue(pstrNewValue);
							}

							@Override
							public void ValidationError(
									SOSValidationError pobjVE) {
								// TODO Auto-generated method stub
							}
						});
			}
		changeStyleName(component, optionElement.isDefault());
	}
	
	private void changeStyleName(AbstractField<?> component, boolean isDefault) {
		if (isDefault) {
			component.addStyleName("default-option-value");
		} else {
			component.removeStyleName("default-option-value");
		}
	}
}
