package at.photoselector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PhotoSelector {
	public static Settings settings;

	public PhotoSelector() {
		Settings.load();

		Display display = new Display();

		// select workspace
		SelectWorkspaceDialog selectWorkspaceDialog = new SelectWorkspaceDialog(
				new Shell(display));
		display.syncExec(selectWorkspaceDialog);
		if (Dialog.CANCEL != selectWorkspaceDialog.getReturnCode()) {
			// start application
			ControlsDialog controlsDialog = new ControlsDialog(new Shell(
					display));
			display.syncExec(controlsDialog);
		}

		display.dispose();
		Database.closeConnection();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PhotoSelector();
	}
}
