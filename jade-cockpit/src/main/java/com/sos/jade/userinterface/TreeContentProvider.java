package com.sos.jade.userinterface;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.sos.jade.userinterface.data.JadeTreeViewEntry;
import com.sos.jade.userinterface.data.JadeTreeViewEntry.enuTreeItemType;
import com.sos.jade.userinterface.data.SectionsHandler;

public class TreeContentProvider implements ITreeContentProvider {
	@SuppressWarnings("unused")
	private final Logger	logger			= Logger.getLogger(TreeContentProvider.class);
	public final String		conSVNVersion	= "$Id$";
	private SectionsHandler	model			= null;

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		model = (SectionsHandler) newInput;
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		JadeTreeViewEntry[] objO = new JadeTreeViewEntry[] {};
		if (inputElement == null || inputElement instanceof SectionsHandler) {
			JadeTreeViewEntry objT = new JadeTreeViewEntry(enuTreeItemType.IsRoot);
			switch (objT.getType()) {
				case IsRoot:
					objO = new JadeTreeViewEntry[1];
					objO[0] = objT;
					break;
				case fragments_root:
					objO = model.getFragments();
					break;
				case profiles_root:
					objO = model.getProfiles();
					break;
				default:
					objO = model.getEntries();
					break;
			}
		}
		else {
		}
		return objO;
	}

	@Override
	public Object[] getChildren(final Object parentElement) {
		JadeTreeViewEntry[] objO = new JadeTreeViewEntry[] {};
		if (parentElement instanceof JadeTreeViewEntry) {
			JadeTreeViewEntry objF = (JadeTreeViewEntry) parentElement;
			switch (objF.getType()) {
				case IsRoot:
					objO = new JadeTreeViewEntry[2];
					objO[1] = new JadeTreeViewEntry(enuTreeItemType.profiles_root);
					objO[0] = new JadeTreeViewEntry(enuTreeItemType.fragments_root);
					break;
				case fragments_root:
					objO = model.getFragments();
					break;
				case profiles_root:
					objO = model.getProfiles();
					break;
				default:
					break;
			}
		}
		return objO;
	}

	@Override
	public Object getParent(final Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(final Object element) {
		return getChildren(element).length > 0;
	}
}