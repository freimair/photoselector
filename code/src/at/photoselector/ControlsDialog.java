package at.photoselector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class ControlsDialog extends MyApplicationWindow {

	private StagesDialog stagesDialog;
	private DrawerDialog drawerDialog;
	private TableDialog tableDialog;

	public ControlsDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText("PhotoSelector");
	}

	@Override
	protected Control createContents(final Composite parent) {
		// create other windows
		Display display = getShell().getDisplay();
		stagesDialog = new StagesDialog(new Shell(display), this);
		display.asyncExec(stagesDialog);

		drawerDialog = new DrawerDialog(new Shell(display), this);
		display.asyncExec(drawerDialog);

		tableDialog = new TableDialog(new Shell(display), this);
		display.asyncExec(tableDialog);

		// create controls
		Composite controlComposite = new Composite(parent, SWT.NONE);
		controlComposite.setLayout(new RowLayout(SWT.VERTICAL));

		Button switchWorkspaceButton = new Button(controlComposite, SWT.PUSH);
		switchWorkspaceButton.setText("Switch Workspace");
		switchWorkspaceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
				try {
					Workspace.open(dialog.open());

					update();
				} catch (NullPointerException e) {
					// dialog got cancelled
				}
			}
		});

		Button addPhotosButton = new Button(controlComposite, SWT.PUSH);
		addPhotosButton.setText("Add photos");
		addPhotosButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell(),
						SWT.OPEN);
				Workspace.addPhoto(dialog.open());

				update();
			}
		});

		Button settingsButton = new Button(controlComposite, SWT.PUSH);
		settingsButton.setText("Settings");
		settingsButton.setEnabled(false);

		Button exitButton = new Button(controlComposite, SWT.PUSH);
		exitButton.setText("Exit");
		exitButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		// update();

		return controlComposite;
	}

	@Override
	public boolean close() {
		stagesDialog.closeApplication();
		drawerDialog.closeApplication();
		tableDialog.closeApplication();
		return super.close();
	}

	@Override
	public void update() {
		stagesDialog.update();
		drawerDialog.update();
		tableDialog.update();
	}

}
