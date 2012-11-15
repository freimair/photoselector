package at.photoselector;

import org.eclipse.swt.widgets.Shell;

public abstract class UncloseableApplicationWindow extends MyApplicationWindow {
	public UncloseableApplicationWindow(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public boolean close() {
		return false;
	}

	public boolean closeApplication() {
		return super.close();
	}

}
