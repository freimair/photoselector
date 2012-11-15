package at.photoselector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class ControlsDialog extends MyApplicationWindow {

	private StagesDialog stagesDialog;

	public ControlsDialog(Shell parentShell, StagesDialog stagesDialog) {
		super(parentShell);
		this.stagesDialog = stagesDialog;
	}

	@Override
	protected Control createContents(final Composite parent) {
		Composite controlComposite = new Composite(parent, SWT.NONE);
		controlComposite.setLayout(new RowLayout(SWT.VERTICAL));
		Button switchWorkspaceButton = new Button(controlComposite, SWT.PUSH);
		switchWorkspaceButton.setText("Switch Workspace");
		switchWorkspaceButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
				// workspace = new Workspace(dialog.open());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Button addPhotosButton = new Button(controlComposite, SWT.PUSH);
		addPhotosButton.setText("Add photos");

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

	@Override
	public boolean close() {
		stagesDialog.closeApplication();
		return super.close();
	}

}
