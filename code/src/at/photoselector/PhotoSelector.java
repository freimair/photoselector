package at.photoselector;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class PhotoSelector {
	public static ProgressBar bar;
	public static Table list;
	private ControlsDialog controlsDialog;

	public PhotoSelector() {
		Workspace.open("/home/fr/Desktop/pictureselector/playground/w2");

		final Display display = new Display();

		StagesDialog stagesDialog = new StagesDialog(new Shell(display));
		display.asyncExec(stagesDialog);

		DrawerDialog drawerDialog = new DrawerDialog(new Shell(display));
		display.asyncExec(drawerDialog);

		// tableDialog = new TableDialog(new Shell(display));
		// display.asyncExec(tableDialog);
		//
		controlsDialog = new ControlsDialog(new Shell(display), stagesDialog,
				drawerDialog);
		display.syncExec(controlsDialog);

		Database.closeConnection();
		display.dispose();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PhotoSelector();
	}
}
