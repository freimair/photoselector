package at.photoselector.ui;

import org.eclipse.swt.widgets.Shell;

public abstract class UncloseableApplicationWindow extends MyApplicationWindow {
	protected ControlsDialog controlsDialog;

	public UncloseableApplicationWindow(Shell parentShell, ControlsDialog dialog) {
		super(parentShell);
		controlsDialog = dialog;
	}

	@Override
	public boolean close() {
		return false;
	}

	public boolean closeApplication() {
		return super.close();
	}

}
