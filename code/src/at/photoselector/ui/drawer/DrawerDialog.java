package at.photoselector.ui.drawer;

import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import at.photoselector.Workspace;
import at.photoselector.ui.ControlsDialog;
import at.photoselector.ui.UncloseableApplicationWindow;

public class DrawerDialog extends UncloseableApplicationWindow {

	
	private ToolItem showAcceptedButton;
	private ToolItem showDeclinedButton;
	private Composite photoListContentComposite;
	private ScrolledComposite photoListComposite;

	public DrawerDialog(Shell parentShell, ControlsDialog dialog) {
		super(parentShell, dialog);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText("Drawer");
	}

	protected Control createContents(Composite parent) {
		Composite drawerComposite = new Composite(parent, SWT.NONE);
		drawerComposite.setLayout(new RowLayout(SWT.VERTICAL));
		ToolBar drawerToolbar = new ToolBar(drawerComposite, SWT.NONE);

		showAcceptedButton = new ToolItem(drawerToolbar,
				SWT.CHECK);
		showAcceptedButton.setText("accepted");
		showAcceptedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controlsDialog.update();
			}
		});
		showDeclinedButton = new ToolItem(drawerToolbar,
				SWT.CHECK);
		showDeclinedButton.setText("declined");
		showDeclinedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controlsDialog.update();
			}
		});

		final ToolItem zoomInButton = new ToolItem(drawerToolbar, SWT.PUSH);
		zoomInButton.setText("+");
		final ToolItem zoomOutButton = new ToolItem(drawerToolbar, SWT.PUSH);
		zoomOutButton.setText("-");
		drawerToolbar.pack();

		photoListComposite = new ScrolledComposite(
				drawerComposite, SWT.V_SCROLL);
		photoListComposite.setLayoutData(new RowData(270, 380));

		photoListContentComposite = new Composite(
				photoListComposite, SWT.NONE);
		photoListContentComposite.setBackground(parent.getDisplay()
				.getSystemColor(SWT.COLOR_GRAY));
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.wrap = true;
		photoListContentComposite.setLayout(layout);

		photoListComposite.setContent(photoListContentComposite);
		photoListComposite.setExpandHorizontal(true);
		photoListComposite.setExpandVertical(true);

		update();

		return drawerComposite;
	}

	@Override
	public void update() {
		try {
			for (Control current : photoListContentComposite.getChildren())
				current.dispose();
			int filter = Workspace.UNPROCESSED;
			if (showAcceptedButton.getSelection())
				filter |= Workspace.ACCEPTED;
			if (showDeclinedButton.getSelection())
				filter |= Workspace.DECLINED;
			for (String current : Workspace.getPhotos(filter)) {
				new ListItem(photoListContentComposite, getShell()
						.getDisplay(),
						current);
			}

			// just make sure that there has been a layout calculated before
			photoListContentComposite.getParent().getParent().layout();
			Rectangle r = photoListContentComposite.getParent().getClientArea();
			((ScrolledComposite) photoListContentComposite.getParent())
					.setMinSize(photoListContentComposite.computeSize(r.width,
							SWT.DEFAULT));
			photoListContentComposite.getParent().redraw();

			photoListContentComposite.layout();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
