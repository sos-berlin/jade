package com.sos.jade.backgroundservice.view.components.filter;

import java.util.List;

import com.sos.jade.backgroundservice.data.JadeHistoryDetailItem;
import com.vaadin.data.Container;
import com.vaadin.data.Item;

/**
 * This is a Vaadin filter for the details table, which is used when all date is already received. 
 * No extra call to the DB is needed, because the data is received by the history table. 
 * 
 * @author SP
 *
 */
public class DetailFilter implements Container.Filter{
	private static final long serialVersionUID = 1L;
	private List<String> messageKeys;

	public DetailFilter() {
	}

	public DetailFilter(List<String> messageKeys) {
		this.messageKeys = messageKeys;
	}

	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		return messageKeys.contains(((JadeHistoryDetailItem)itemId).getMessageKey());
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
        return "key".equals(propertyId);
	}

}
