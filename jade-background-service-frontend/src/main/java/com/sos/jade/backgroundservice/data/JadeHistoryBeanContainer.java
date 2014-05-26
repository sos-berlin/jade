package com.sos.jade.backgroundservice.data;

import java.util.List;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.vaadin.data.util.BeanItemContainer;

public class JadeHistoryBeanContainer {
	private List<JadeFilesHistoryDBItem> items;
	
    public BeanItemContainer<JadeFilesHistoryDBItem> createJadeFilesHistoryContainer(List<JadeFilesHistoryDBItem> items) {
    	this.items = items;
        return createContainer(JadeFilesHistoryDBItem.class, new JadeFilesHistoryBeanCreator());
    }

    @SuppressWarnings({ "hiding", "unchecked" })
	private <JadeFilesHistoryDBItem> BeanItemContainer<JadeFilesHistoryDBItem> createContainer(
            Class<? super JadeFilesHistoryDBItem> type, BeanCreator<JadeFilesHistoryDBItem> creator) {
        BeanItemContainer<JadeFilesHistoryDBItem> container = new BeanItemContainer<JadeFilesHistoryDBItem>(type);

        for (sos.ftphistory.db.JadeFilesHistoryDBItem item : items) {
            container.addBean((JadeFilesHistoryDBItem) creator.createItem(item));
        }
        return container;
    }

    private abstract static class BeanCreator<T> {
        public abstract T createItem();

		public JadeFilesHistoryDBItem createItem(JadeFilesHistoryDBItem item) {
			return item;
		}
    }

    private static class JadeFilesHistoryBeanCreator extends BeanCreator<JadeFilesHistoryDBItem> {
    	private List<JadeFilesHistoryDBItem> items;
        
    	public JadeFilesHistoryBeanCreator() {
        	this.items = items;
        }

        @Override
        public JadeFilesHistoryDBItem createItem() {
        	return createItem(null);
        }
        
        public JadeFilesHistoryDBItem createItem(JadeFilesHistoryDBItem item){
        	return item;
        }
    }
}
