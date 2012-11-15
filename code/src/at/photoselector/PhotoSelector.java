package at.photoselector;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PhotoSelector {
	public PhotoSelector() {
		Workspace.open("/home/fr/Desktop/pictureselector/playground/w2");

		Display display = new Display();
		ControlsDialog controlsDialog = new ControlsDialog(new Shell(display));
		display.syncExec(controlsDialog);
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
