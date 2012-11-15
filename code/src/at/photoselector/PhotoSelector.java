package at.photoselector;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PhotoSelector {
	public static Settings settings;

	public PhotoSelector() {
		Workspace.open("/home/me/Desktop/pictureselector/playground/w2");

		Settings.load();

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
