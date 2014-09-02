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

import at.photoselector.model.Photo;
import at.photoselector.model.Stage;
import at.photoselector.ui.ControlsDialog;
import at.photoselector.ui.MyApplicationWindow;
import at.photoselector.ui.table.TableDialog;

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

	private TableDialog tableDialog;

	protected Control createContents(final Composite parent) {
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
				// Workspace.accept(photo);
				controlsDialog.update();
				// imageContainer.dispose();
				// photo.clearCachedImages();
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
				// Workspace.decline(photo);
				controlsDialog.update();
				// imageContainer.dispose();
				// photo.clearCachedImages();
			}
		});

		Button hundredPercentButton = new Button(controlsComposite, SWT.PUSH);
		hundredPercentButton.setText("100%");
		hundredPercentButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// int y = controlsComposite.getLocation().y
				// + controlsComposite.getSize().y / 2;
				// int x = controlsComposite.getLocation().x
				// + controlsComposite.getSize().x / 2;
				//
				// double factor = (double) photo.getDimensions().x
				// / imageContainer.getSize().x;
				//
				// zoomImageContainer(factor, x, y);
				// controlsComposite.setVisible(false);
			}
		});
		

//		controlsComposite.setVisible(true);
//		Point location = imageContainer.toControl(e.x, e.y);
//		controlsComposite.setLocation(
//				location.x - controlsComposite.getBounds().width / 2,
//				location.y - controlsComposite.getBounds().height / 2);


				new ImageTile(parent, controlsDialog, Photo.get(Photo
						.getFiltered(Stage.getCurrent(), Photo.UNPROCESSED)
						.get(0).getId()));


		parent.layout();

		update();

		return parent;
	}

	
	@Override
	public void update() {


	}

	public void updateAll() {
	}

	@Override
	public boolean close() {
		return super.close();
	}
}
