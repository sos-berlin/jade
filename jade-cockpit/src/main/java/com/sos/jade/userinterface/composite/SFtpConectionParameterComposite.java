package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.dialog.classes.SOSCheckBox;
import com.sos.dialog.components.CompositeBaseClass;

public class SFtpConectionParameterComposite extends CompositeBaseClass<SOSConnection2OptionsAlternate> {
	@SuppressWarnings("unused") 	private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused") 	private static final String	conSVNVersion	= "$Id$";
	@SuppressWarnings("unused") 	private final Logger		logger			= Logger.getLogger(this.getClass());

//	public SFtpConectionParameterComposite(final CTabItem parent, final SOSConnection2OptionsAlternate objOptions) {
//		this((Composite) parent.getControl(), objOptions);
//	}
//
	public SFtpConectionParameterComposite(final Composite parent, final SOSConnection2OptionsAlternate objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	private SOSCheckBox btnUseZlibCompression = null; 
	@Override
	public void createComposite() {
		objCC.getControl(objJadeOptions.HostName);
		objCC.getControl(objJadeOptions.port);
		objCC.getControl(objJadeOptions.user);
		objCC.getControl(objJadeOptions.auth_method);
		objCC.getControl(objJadeOptions.password);
		objCC.getControl(objJadeOptions.auth_file);
		objCC.getControl(objJadeOptions.passphrase, 3);
		objCC.getControl(objJadeOptions.StrictHostKeyChecking);
		objCC.getLabel(2);
		btnUseZlibCompression = (SOSCheckBox) objCC.getControl(objJadeOptions.use_zlib_compression);
		Text txtZlibCompressionLevel = (Text) objCC.getControl(objJadeOptions.zlib_compression_level);
		btnUseZlibCompression.addChild(txtZlibCompressionLevel);
		objCC.getControl(objJadeOptions.loadClassName, 3);
		initValues();
	}
	
	private void initValues() {
		btnUseZlibCompression.setEnabledDisabled();
	}

}
