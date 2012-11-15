package at.photoselector;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Shell;

public abstract class MyApplicationWindow extends ApplicationWindow implements
		Runnable {

	public MyApplicationWindow(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		// TODO memorize window locations on shutdown and restore at program
		// startup
		if (this instanceof StagesDialog) {
			shell.setLocation(0, 0);
			shell.setSize(150, 300);
		}
	}

	public void run() {
		// Don't return from open() until window closes
		setBlockOnOpen(true);

		// Open the main window
		open();
	}

	public abstract void update();
}
