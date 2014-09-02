package at.photoselector.ui;


import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import at.photoselector.ui.drawer.DrawerDialog;
import at.photoselector.ui.stages.StagesDialog;

public class MainWindow extends ApplicationWindow {

	public MainWindow(Shell parentShell) {
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
		final Display display = getShell().getDisplay();

		// create controls
		// Composite mainComposite = parent;
		Composite mainComposite = new Composite(parent, SWT.NONE);
		// RowLayout layout = new RowLayout(SWT.VERTICAL);
		// layout.fill = true;
		mainComposite.setLayout(new GridLayout(2, false));

		ControlsDialog fritz = new ControlsDialog(getParentShell());
		Composite controlsComposite = new Composite(mainComposite, SWT.BORDER);
		controlsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true,
				false));
		fritz.createContents(controlsComposite);

		StagesDialog josef = new StagesDialog(getParentShell(), null);
		Composite stagesComposite = new Composite(mainComposite, SWT.BORDER);
		stagesComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true,
				false));
		josef.createContents(stagesComposite);

		DrawerDialog sepp = new DrawerDialog(getParentShell(), null);
		Composite drawerComposite = new Composite(mainComposite, SWT.BORDER);
		drawerComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true,
				true));
		sepp.createContents(drawerComposite);

		return mainComposite;
	}
}
