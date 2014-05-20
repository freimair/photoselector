package at.photoselector.ui.drawer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import at.photoselector.Settings;
import at.photoselector.Workspace;
import at.photoselector.model.Photo;
import at.photoselector.ui.ControlsDialog;
import at.photoselector.ui.UncloseableApplicationWindow;

public class DrawerDialog extends UncloseableApplicationWindow {

	
	private ToolItem showAcceptedButton;
	private ToolItem showDeclinedButton;
	private Composite photoListContentComposite;
	private int boundingBox = 0;
	private ToolItem showControlsButton;

	public DrawerDialog(Shell parentShell, ControlsDialog dialog) {
		super(parentShell, dialog);

		addToolBar(SWT.FLAT);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText("Drawer");
	}

	protected Control createContents(Composite parent) {
		ToolBar drawerToolbar = (ToolBar) getToolBarControl();

		showAcceptedButton = new ToolItem(drawerToolbar, SWT.CHECK);
		showAcceptedButton.setText("accepted");
		showAcceptedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controlsDialog.update();
				updateAll();
			}
		});
		showDeclinedButton = new ToolItem(drawerToolbar, SWT.CHECK);
		showDeclinedButton.setText("declined");
		showDeclinedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controlsDialog.update();
				updateAll();
			}
		});

		ToolItem zoomInButton = new ToolItem(drawerToolbar, SWT.PUSH);
		zoomInButton.setText("+");
		zoomInButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boundingBox += 50;
				updateAll();
			}
		});

		ToolItem zoomOutButton = new ToolItem(drawerToolbar, SWT.PUSH);
		zoomOutButton.setText("-");
		zoomOutButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boundingBox -= 50;
				if (boundingBox < 50)
					boundingBox = 50;
				updateAll();
			}
		});

		showControlsButton = new ToolItem(drawerToolbar, SWT.CHECK);
		showControlsButton.setText("show controls");
		showControlsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// photoListContentComposite.layout();
				photoListContentComposite.redraw();
			}
		});

		ToolItem resetLastPhotoButton = new ToolItem(drawerToolbar, SWT.PUSH);
		resetLastPhotoButton.setText("reset last treated photo");
		resetLastPhotoButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.reset(Workspace.getLastTreated());
				updateAll();
			}
		});

		ScrolledComposite photoListComposite = new ScrolledComposite(parent,
				SWT.V_SCROLL);
		photoListComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		photoListContentComposite = new Composite(
				photoListComposite, SWT.NONE);
		photoListContentComposite.setBackground(parent.getDisplay()
				.getSystemColor(SWT.COLOR_DARK_GRAY));
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.wrap = true;
		photoListContentComposite.setLayout(layout);

		photoListComposite.setContent(photoListContentComposite);
		photoListComposite.setExpandHorizontal(true);
		photoListComposite.setExpandVertical(true);

		photoListContentComposite.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				getShell().setFullScreen(!getShell().getFullScreen());
			}
		});

		parent.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				Rectangle r = photoListContentComposite.getParent().getClientArea();
				((ScrolledComposite) photoListContentComposite.getParent())
						.setMinSize(photoListContentComposite.computeSize(r.width,
								SWT.DEFAULT));
				photoListContentComposite.getParent().redraw();
			}
		});

		update();

		return parent;
	}

	@Override
	protected ToolBarManager createToolBarManager(int style) {
		return super.createToolBarManager(style);
	}

	private Map<String, ListItem> cache;
	
	@Override
	public void update() {
		update(false);
	}

	public void updateAll() {
		update(true);
	}

	public void update(boolean force) {
		Photo lastTreated = Workspace.getLastTreated();
		if (lastTreated != null && !force) {
			ListItem tmp = cache
					.remove(lastTreated.getPath().getAbsolutePath());
			if (null != tmp)
				tmp.dispose();
			photoListContentComposite.update();
		} else {
			for (Control current : photoListContentComposite.getChildren())
				current.dispose();
			int filter = Photo.UNPROCESSED;
			if (showAcceptedButton.getSelection())
				filter |= Photo.ACCEPTED;
			if (showDeclinedButton.getSelection())
				filter |= Photo.DECLINED;
			cache = new HashMap<String, ListItem>();
			for (Photo current : Photo.getFiltered(true, filter)) {
				cache.put(current.getPath().getAbsolutePath(), new ListItem(
						photoListContentComposite,
						this, controlsDialog, current));

				if (Settings.getMaximumDrawerItems() < cache.size()) {
					Button moreButton = new Button(photoListContentComposite,
							SWT.PUSH);
					moreButton.setText("load more");
					moreButton.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							update(true);
						}
					});

					break;
				}
			}
		}

		photoListContentComposite.layout();
		photoListContentComposite.getParent().getParent().redraw();
	}

	public int getBoundingBox() {
		return Settings.getInitialThumbnailSize() + boundingBox;
	}

	public boolean isShowControls() {
		return showControlsButton.getSelection();
	}

}
