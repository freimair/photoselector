package at.photoselector.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import at.photoselector.Settings;

public class SettingsDialog extends Dialog {

	private Text dcrawText;
	private Text imagemagickText;

	protected SettingsDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(3, false));

		Label dcrawLabel = new Label(container, SWT.NONE);
		dcrawLabel.setText("dcraw binary location");
		dcrawText = new Text(container, SWT.BORDER);
		Button dcrawButton = new Button(container, SWT.PUSH);
		dcrawButton.setText("Browse...");
		dcrawButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell());
				dcrawText.setText(dialog.open());
			}
		});

		Label imagemagickLabel = new Label(container, SWT.NONE);
		imagemagickLabel.setText("dcraw binary location");
		imagemagickText = new Text(container, SWT.BORDER);
		Button imagemagickButton = new Button(container, SWT.PUSH);
		imagemagickButton.setText("Browse...");
		imagemagickButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell());
				imagemagickText.setText(dialog.open());
			}
		});

		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void okPressed() {
		// TODO maybe check selection
		Settings.setDCRawLocation(dcrawText.getText());
		Settings.setImageMagicBinaryLocation(imagemagickText.getText());
		super.okPressed();
	}
}
