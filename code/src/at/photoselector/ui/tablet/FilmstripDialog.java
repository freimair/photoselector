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
import org.eclipse.swt.layout.RowData;
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

	private ImageTile currentTile;
	private Composite imageComposite;

	protected Control createContents(final Composite parent) {
		parent.setLayout(new FormLayout());

		for (Control current : parent.getChildren())
			current.dispose();
		
		imageComposite = new Composite(parent, SWT.NONE);
		imageComposite
				.setBackground(new Color(parent.getDisplay(), 75, 75, 75));
		imageComposite.setLayout(new FormLayout());

		// add controls
		Composite controlsComposite = new Composite(parent, SWT.NONE);
		FormData data = new FormData();
		data.right = new FormAttachment(100);
		data.top = new FormAttachment(25);
		controlsComposite.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0);
		data.top = new FormAttachment(0);
		data.right = new FormAttachment(controlsComposite);
		data.bottom = new FormAttachment(100);
		imageComposite.setLayoutData(data);

		controlsComposite.setLayout(new RowLayout(SWT.VERTICAL));

		Button buttonAccept = new Button(controlsComposite, SWT.PUSH);
		buttonAccept.setText("Accept");
		buttonAccept.setLayoutData(new RowData(100, 100));
		buttonAccept.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.accept(currentTile.getPhoto());
				update();
			}
		});

		Button exitButton = new Button(controlsComposite, SWT.PUSH);
		exitButton.setText("exit");
		exitButton.setLayoutData(new RowData(100, 100));
		exitButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});

		Button buttonDecline = new Button(controlsComposite, SWT.PUSH);
		buttonDecline.setText("Decline");
		buttonDecline.setLayoutData(new RowData(100, 100));
		buttonDecline.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.decline(currentTile.getPhoto());
				update();
			}
		});

		Button buttonUndo = new Button(controlsComposite, SWT.PUSH);
		buttonUndo.setText("Undo");
		buttonUndo.setLayoutData(new RowData(100, 100));
		buttonUndo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.reset(Workspace.getLastTreated());
				update();
			}
		});

		imageComposite.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				getShell().setFullScreen(!getShell().getFullScreen());
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
		currentTile = new ImageTile(imageComposite, controlsDialog,
				Photo.get(Photo
				.getFiltered(Stage.getCurrent(), Photo.UNPROCESSED).get(0)
				.getId()));
		imageComposite.layout();
	}

	public void updateAll() {
	}

	@Override
	public boolean close() {
		return super.close();
	}
}
