package com.sos.jade.userinterface.data;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;

public class SectionsHandler {
	@SuppressWarnings("unused")
	private final String			conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private static final String		conSVNVersion	= "$Id$";
	@SuppressWarnings("unused")
	private final Logger			logger			= Logger.getLogger(this.getClass());
	private HashMap<String, JadeTreeViewEntry>	entries;
	private final SectionsHandler	parent;
	private String					name;
	@SuppressWarnings("unused") private ListenerList			listeners;

	public SectionsHandler(final SectionsHandler parent, final String name) {
		this.name = name;
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public SectionsHandler getParent() {
		return parent;
	}

	public void rename(final String newName) {
		name = newName;
	}

	public void addEntry(final JadeTreeViewEntry entry) {
		if (entries == null) {
			entries = new HashMap<>();
		}
		entries.put(entry.getName(), entry);
	}

	public void removeEntry(final JadeTreeViewEntry entry) {
		if (entries != null) {
			entries.remove(entry);
			if (entries.isEmpty())
				entries = null;
		}
	}

	public JadeTreeViewEntry[] getEntries() {
		if (entries != null) {
			JadeTreeViewEntry[] objF = new JadeTreeViewEntry[entries.size()] ;
			int i = 0;
			for (String strProfileName : entries.keySet()) {
				JadeTreeViewEntry jadeTreeViewEntry = entries.get(strProfileName);
				objF [i++] = jadeTreeViewEntry;
			}
			return objF;
		}
		return new JadeTreeViewEntry[0];
	}

	public JadeTreeViewEntry[] getFragments() {
		return selectEntries(2);
	}

	public JadeTreeViewEntry[] selectEntries(final int intWhat) {
		if (entries != null) {
			Vector<JadeTreeViewEntry> objF = new Vector<>();
			for (String strProfileName : entries.keySet()) {
				JadeTreeViewEntry jadeTreeViewEntry = entries.get(strProfileName);
				switch (intWhat) {
					case 1:
						boolean flgIsProfile = jadeTreeViewEntry.isProfile();
						if (flgIsProfile == true) {
							objF.add(jadeTreeViewEntry);
						}
						break;
					case 2:
						boolean flgIsFragment = jadeTreeViewEntry.isFragment();
						if (flgIsFragment == true) {
							objF.add(jadeTreeViewEntry);
						}
						break;
					default:
						break;
				}
			}
			if (objF.size() > 0) {
				JadeTreeViewEntry[] objJ = new JadeTreeViewEntry[objF.size()];
				for (int i = 0; i < objF.size(); i++) {
					objJ[i] = objF.get(i);
				}
				return objJ;
			}
		}
		return new JadeTreeViewEntry[0];
	}

	public JadeTreeViewEntry[] getProfiles() {
		return selectEntries(1);
	}

//	private void addContactsListener(final IContactsListener listener) {
//		if (parent != null)
//			parent.addContactsListener(listener);
//		else {
//			if (listeners == null)
//				listeners = new ListenerList();
//			listeners.add(listener);
//		}
//	}
//
//	private void removeContactsListener(final IContactsListener listener) {
//		if (parent != null)
//			parent.removeContactsListener(listener);
//		else {
//			if (listeners != null) {
//				listeners.remove(listener);
//				if (listeners.isEmpty())
//					listeners = null;
//			}
//		}
//	}

//	protected void fireContactsChanged(final JadeTreeViewEntry entry) {
//		if (parent != null)
//			parent.fireContactsChanged(entry);
//		else {
//			if (listeners == null) {
//				return;
//			}
//			Object[] rls = listeners.getListeners();
//			for (Object rl : rls) {
//				IContactsListener listener = (IContactsListener) rl;
//				listener.contactsChanged(this, entry);
//			}
//		}
//	}
}
