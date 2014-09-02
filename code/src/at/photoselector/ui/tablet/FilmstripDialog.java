package at.photoselector.ui.tablet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import at.photoselector.Workspace;
import at.photoselector.model.Photo;
import at.photoselector.model.Stage;
import at.photoselector.ui.ControlsDialog;
import at.photoselector.ui.MyApplicationWindow;

public class FilmstripDialog extends MyApplicationWindow {
	
	private ControlsDialog controlsDialog;

	public FilmstripDialog(Shell parentShell, ControlsDialog dialog) {
		super(parentShell);

		this.controlsDialog = dialog;

	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText("Filmstrip Selector");
	}

	private Composite parent;
	private ImageTile currentTile;

	protected Control createContents(final Composite parent) {
		this.parent = parent;

		parent.setBackground(new Color(parent.getDisplay(), 75, 75, 75));
		parent.setLayout(new FormLayout());

		for (Control current : parent.getChildren())
			current.dispose();

		parent.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				getShell().setFullScreen(!getShell().getFullScreen());
			}
		});
		
		// add controls
		Composite controlsComposite = new Composite(parent, SWT.NONE);
		FormData data = new FormData();
		data.right = new FormAttachment(100);
		data.top = new FormAttachment(25);
		controlsComposite.setLayoutData(data);

		controlsComposite.setLayout(new RowLayout(SWT.VERTICAL));

		Button buttonAccept = new Button(controlsComposite, SWT.PUSH);
		buttonAccept.setText("Accept");
		buttonAccept.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.accept(currentTile.getPhoto());
				update();
			}
		});

		Button exitButton = new Button(controlsComposite, SWT.PUSH);
		exitButton.setText("later");
		exitButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// controlsComposite.setVisible(false);
			}
		});

		Button buttonDecline = new Button(controlsComposite, SWT.PUSH);
		buttonDecline.setText("Decline");
		buttonDecline.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.decline(currentTile.getPhoto());
				update();
			}
		});

		parent.layout();

		update();

		return parent;
	}

	
	@Override
	public void update() {
		if (null != currentTile) {
			currentTile.getPhoto().clearCachedImages();
			currentTile.dispose();
			controlsDialog.update();
		}
		currentTile = new ImageTile(parent, controlsDialog, Photo.get(Photo
				.getFiltered(Stage.getCurrent(), Photo.UNPROCESSED).get(0)
				.getId()));
		parent.layout();
	}

	public void updateAll() {
	}

	@Override
	public boolean close() {
		return super.close();
	}
}
