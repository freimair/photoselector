package at.photoselector;

import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolItem;

public class PhotoSelector {
	static Workspace workspace = new Workspace(
	// "/home/me/Projekte/pictureselector/playground/w1");
			"/home/fr/Desktop/pictureselector/playground/w2");
	private static Shell shell;
	public static ProgressBar bar;
	public static Table list;
	private ControlsDialog controlsDialog;

	public PhotoSelector() {
		final Display display = new Display();

		StagesDialog stagesDialog = new StagesDialog(new Shell(display));
		display.asyncExec(stagesDialog);

		// DrawerDialog drawerDialog = new DrawerDialog(new Shell(display));
		// display.asyncExec(drawerDialog);

		// tableDialog = new TableDialog(new Shell(display));
		// display.asyncExec(tableDialog);
		//
		controlsDialog = new ControlsDialog(new Shell(display), stagesDialog);
		display.syncExec(controlsDialog);

		// list.addSelectionListener(new SelectionListener() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// fillTable(photoListContentComposite, list, display,
		// showAcceptedButton, showDeclinedButton);
		// }
		//
		// @Override
		// public void widgetDefaultSelected(SelectionEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		//
		// addPhotosButton.addSelectionListener(new SelectionListener() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
		// workspace.addPhoto(dialog.open());
		// fillTable(photoListContentComposite, list, display,
		// showAcceptedButton, showDeclinedButton);
		// try {
		// bar.setMaximum(workspace.getPhotos(
		// Workspace.UNPROCESSED | Workspace.ACCEPTED
		// | Workspace.DECLINED).size());
		// bar.setSelection(workspace.getPhotos(
		// Workspace.ACCEPTED | Workspace.DECLINED).size());
		// } catch (SQLException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// }
		//
		// @Override
		// public void widgetDefaultSelected(SelectionEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		//
		// showAcceptedButton.addSelectionListener(new SelectionListener() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// fillTable(photoListContentComposite, list, display,
		// showAcceptedButton, showDeclinedButton);
		// }
		//
		// @Override
		// public void widgetDefaultSelected(SelectionEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		//
		// showDeclinedButton.addSelectionListener(new SelectionListener() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// fillTable(photoListContentComposite, list, display,
		// showAcceptedButton, showDeclinedButton);
		// }
		//
		// @Override
		// public void widgetDefaultSelected(SelectionEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		//
		// fillTable(photoListContentComposite, list, display,
		// showAcceptedButton,
		// showDeclinedButton);

		// shell.pack();
		// shell.open();
		// while (!shell.isDisposed()) {
		// if (!display.readAndDispatch())
		// display.sleep();
		// }

		Database.closeConnection();
		display.dispose();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PhotoSelector();
	}

	public static void fillTable(Composite drawerComposite, Table list,
			Display display, ToolItem showAcceptedButton,
			ToolItem showDeclinedButton) {
		try {
			for (Control current : drawerComposite.getChildren())
				current.dispose();
			int filter = Workspace.UNPROCESSED;
			if (showAcceptedButton.getSelection())
				filter |= Workspace.ACCEPTED;
			if (showDeclinedButton.getSelection())
				filter |= Workspace.DECLINED;
			for (String current : workspace.getPhotos(filter)) {
				// new ImageTile(tableComposite, display, current);
				new DrawerItem(drawerComposite, display, current);
			}
			Rectangle r = drawerComposite.getParent().getClientArea();
			((ScrolledComposite) drawerComposite.getParent())
					.setMinSize(drawerComposite.computeSize(r.width,
							SWT.DEFAULT));
			drawerComposite.redraw();
			shell.redraw();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
