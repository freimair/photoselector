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
		if (this instanceof ControlsDialog) {
			shell.setLocation(0, 0);
			shell.setSize(130, 160);
		} else if (this instanceof StagesDialog) {
			shell.setLocation(0, 185);
			shell.setSize(130, 300);
		} else if (this instanceof DrawerDialog) {
			shell.setLocation(130, 0);
			shell.setSize(300, 460);
		} else if (this instanceof TableDialog) {
			shell.setLocation(435, 0);
			shell.setSize(460, 460);
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
