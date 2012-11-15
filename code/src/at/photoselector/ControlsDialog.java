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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class ControlsDialog extends MyApplicationWindow {

	private StagesDialog stagesDialog;

	public ControlsDialog(Shell parentShell, StagesDialog stagesDialog) {
		super(parentShell);
		this.stagesDialog = stagesDialog;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText("PhotoSelector");
	}

	@Override
	protected Control createContents(final Composite parent) {
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

					updateAnything();
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

				updateAnything();
			}
		});

		Button settingsButton = new Button(controlComposite, SWT.PUSH);
		settingsButton.setText("Settings");
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
		return controlComposite;
	}

	private void updateAnything() {
		stagesDialog.update();
		// fillTable(photoListContentComposite, list, display,
		// showAcceptedButton, showDeclinedButton);
	}

	@Override
	public boolean close() {
		stagesDialog.closeApplication();
		return super.close();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
