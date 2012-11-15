package at.photoselector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class DrawerDialog extends MyApplicationWindow {

	
	public DrawerDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText("Drawer");
	}

	protected Control createContents(Composite parent) {
		final Composite drawerComposite = new Composite(parent, SWT.NONE);
		drawerComposite.setLayout(new RowLayout(SWT.VERTICAL));
		ToolBar drawerToolbar = new ToolBar(drawerComposite, SWT.NONE);
		final ToolItem showAcceptedButton = new ToolItem(drawerToolbar,
				SWT.CHECK);
		showAcceptedButton.setText("accepted");
		final ToolItem showDeclinedButton = new ToolItem(drawerToolbar,
				SWT.CHECK);
		showDeclinedButton.setText("declined");
		final ToolItem zoomInButton = new ToolItem(drawerToolbar, SWT.PUSH);
		zoomInButton.setText("+");
		final ToolItem zoomOutButton = new ToolItem(drawerToolbar, SWT.PUSH);
		zoomOutButton.setText("-");
		drawerToolbar.pack();

		final ScrolledComposite photoListComposite = new ScrolledComposite(
				drawerComposite, SWT.V_SCROLL);
		photoListComposite.setLayoutData(new RowData(330, 500));

		final Composite photoListContentComposite = new Composite(
				photoListComposite, SWT.NONE);
		photoListContentComposite.setBackground(parent.getDisplay()
				.getSystemColor(SWT.COLOR_GRAY));
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.wrap = true;
		photoListContentComposite.setLayout(layout);

		photoListComposite.setContent(photoListContentComposite);
		photoListComposite.setExpandHorizontal(true);
		photoListComposite.setExpandVertical(true);
		
		return drawerComposite;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}
	

}
