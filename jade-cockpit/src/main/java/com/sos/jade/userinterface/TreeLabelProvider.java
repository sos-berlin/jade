package com.sos.jade.userinterface;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.sos.dialog.Globals;
import com.sos.dialog.swtdesigner.SWTResourceManager;
import com.sos.jade.userinterface.data.JadeTreeViewEntry;

public class TreeLabelProvider extends LabelProvider implements ILabelProvider, IColorProvider, IFontProvider {
	@SuppressWarnings("unused")
	private final Logger	logger			= Logger.getLogger(TreeLabelProvider.class);
	public final String		conSVNVersion	= "$Id$";

	@Override
	public Image getImage(final Object element) {
		if (element instanceof JadeTreeViewEntry) {
			JadeTreeViewEntry objS = (JadeTreeViewEntry) element;
			// [SP] changed to get image resources dynamically at runtime
			URL url = Thread.currentThread().getContextClassLoader().getResource("Profil.gif");
			return SWTResourceManager.getImage(url.getPath());
//			return SWTResourceManager.getImage("Profil.gif");
		}
		URL url = Thread.currentThread().getContextClassLoader().getResource("BlueCircle.gif");
		return SWTResourceManager.getImage(url.getPath());
//		return SWTResourceManager.getImage("BlueCircle.gif");
	}

	@Override
	public String getText(final Object element) {
		if (element instanceof JadeTreeViewEntry) {
			JadeTreeViewEntry objS = (JadeTreeViewEntry) element;
			return objS.getName();
		}
		if (element instanceof String) {
			String objS = (String) element;
			return objS;
		}
		return "???";
	}

	@Override
	public Font getFont(final Object element) {
		if (element instanceof JadeTreeViewEntry) {
			@SuppressWarnings("unused") JadeTreeViewEntry objS = (JadeTreeViewEntry) element;
		}
		return Globals.stFontRegistry.get("text");
	}

	@Override
	public Color getBackground(final Object arg0) {
		return null;
	}

	@SuppressWarnings("unused") @Override
	public Color getForeground(final Object element) {
		if (element instanceof JadeTreeViewEntry) {
			JadeTreeViewEntry objS = (JadeTreeViewEntry) element;
			return Globals.getMandatoryFieldColor();
		}
		return null;
	}
}