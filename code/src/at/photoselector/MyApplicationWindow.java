package at.photoselector;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Shell;

public class MyApplicationWindow extends ApplicationWindow implements Runnable {

	public MyApplicationWindow(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
	}

	public void run() {
		// Don't return from open() until window closes
		setBlockOnOpen(true);

		// Open the main window
		open();
	}
}
