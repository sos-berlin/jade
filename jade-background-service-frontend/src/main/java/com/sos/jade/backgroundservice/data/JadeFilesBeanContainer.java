package com.sos.jade.backgroundservice.data;

import java.util.List;

import sos.ftphistory.db.JadeFilesDBItem;

import com.vaadin.data.util.BeanItemContainer;

public class JadeFilesBeanContainer {

	private List<JadeFilesDBItem> items;
	
    public BeanItemContainer<JadeFilesDBItem> createJadeFilesContainer(List<JadeFilesDBItem> items) {
    	this.items = items;
        return createContainer(JadeFilesDBItem.class, new JadeFilesBeanCreator());
    }

    @SuppressWarnings({ "hiding", "unchecked" })
	private <JadeFilesDBItem> BeanItemContainer<JadeFilesDBItem> createContainer(
            Class<? super JadeFilesDBItem> type, BeanCreator<JadeFilesDBItem> creator) {
        BeanItemContainer<JadeFilesDBItem> container = new BeanItemContainer<JadeFilesDBItem>(type);

        for (sos.ftphistory.db.JadeFilesDBItem item : items) {
            container.addBean((JadeFilesDBItem) creator.createItem(item));
        }
        return container;
    }

    private abstract static class BeanCreator<T> {
        public abstract T createItem();

		public JadeFilesDBItem createItem(JadeFilesDBItem item) {
			return item;
		}
    }

    private static class JadeFilesBeanCreator extends BeanCreator<JadeFilesDBItem> {
//    	private List<JadeFilesDBItem> items;
//        
////    	public JadeFilesBeanCreator() {
////        }

        @Override
        public JadeFilesDBItem createItem() {
        	return createItem(null);
        }
        
        public JadeFilesDBItem createItem(JadeFilesDBItem item){
        	return item;
        }
    }
}
