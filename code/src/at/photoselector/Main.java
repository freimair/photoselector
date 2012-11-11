package at.photoselector;

import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class Main {
	static Workspace workspace;
	private static Shell shell;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		shell = new Shell(display);
		shell.setLayout(new RowLayout());
		Button switchWorkspaceButton = new Button(shell, SWT.PUSH);
		switchWorkspaceButton.setText("Switch Workspace");
		switchWorkspaceButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				workspace = new Workspace(dialog.open());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Button addPhotosButton = new Button(shell, SWT.PUSH);
		addPhotosButton.setText("Add photos");
		addPhotosButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
				workspace.addPhoto(dialog.open());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		Button settingsButton = new Button(shell, SWT.PUSH);
		settingsButton.setText("Settings");
		Button exitButton = new Button(shell, SWT.PUSH);
		exitButton.setText("Exit");
		exitButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		//
		// final List list = new List (shell, SWT.BORDER | SWT.MULTI |
		// SWT.V_SCROLL);

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new RowData(1000, 500));
		composite.setBackground(display.getSystemColor(SWT.COLOR_GRAY));

		// new ImageTile(composite, display, "../playground/1.jpg");
		// new ImageTile(composite, display, "../playground/2.jpg");

		shell.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				fillTable(composite, display);

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		Database.closeConnection();
		display.dispose();
	}

	public static void fillTable(Composite composite, Display display) {
		try {
			for (String current : workspace.getPhotos())
				new ImageTile(composite, display, current);
			composite.redraw();
			shell.redraw();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
