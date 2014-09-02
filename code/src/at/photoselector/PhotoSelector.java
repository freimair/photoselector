package at.photoselector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import at.photoselector.model.Database;
import at.photoselector.ui.MainWindow;
import at.photoselector.ui.SelectWorkspaceDialog;

public class PhotoSelector {
	public static Settings settings;

	public PhotoSelector() {
		Display display = new Display();

		// select workspace
		SelectWorkspaceDialog selectWorkspaceDialog = new SelectWorkspaceDialog(
				new Shell(display));
		display.syncExec(selectWorkspaceDialog);
		if (Dialog.CANCEL != selectWorkspaceDialog.getReturnCode()) {
			// start application
			MainWindow controlsDialog = new MainWindow(new Shell(
					display));
			controlsDialog.setBlockOnOpen(true);
			controlsDialog.open();
			// display.syncExec(controlsDialog);
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
