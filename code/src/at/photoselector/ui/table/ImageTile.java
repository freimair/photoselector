package at.photoselector.ui.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import at.photoselector.Workspace;
import at.photoselector.model.Photo;
import at.photoselector.ui.ControlsDialog;


class ImageTile {
	private Point offset;
	private Composite imageContainer;
	private Image image;
	private Photo photo;
	private ControlsDialog controlsDialog;
	private Composite controlsComposite;

	public ImageTile(final Composite parent, ControlsDialog dialog,
			Photo currentPhoto, int x, int y) {
		this.photo = currentPhoto;
		controlsDialog = dialog;

		// TODO find some smart way to calculate the initial size of the image
		int boundingBox = Math.min(parent.getBounds().width,
				parent.getBounds().height);
		image = new Image(parent.getDisplay(), photo.getImage(boundingBox));

		imageContainer = new Composite(parent, SWT.NONE);
		imageContainer.setLayout(new RowLayout());

		Rectangle dimensions = photo.scaleAndCenterImage(boundingBox);
		imageContainer.setSize(dimensions.width, dimensions.height);

		Point pt = parent.toControl(x, y);
		imageContainer.setLocation(pt.x - imageContainer.getBounds().width / 2,
				pt.y - imageContainer.getBounds().height / 2);

		// add controls
		controlsComposite = new Composite(imageContainer, SWT.NONE);
		controlsComposite.setLayout(new RowLayout());
		controlsComposite.setVisible(false);

		Button buttonAccept = new Button(controlsComposite, SWT.PUSH);
		buttonAccept.setText("Accept");
		buttonAccept.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.accept(photo);
				controlsDialog.update();
				imageContainer.dispose();
			}
		});

		Button buttonDecline = new Button(controlsComposite, SWT.PUSH);
		buttonDecline.setText("Decline");
		buttonDecline.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.decline(photo);
				controlsDialog.update();
				imageContainer.dispose();
			}
		});

		Button exitButton = new Button(controlsComposite, SWT.PUSH);
		exitButton.setText("Exit Controls");
		exitButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				controlsComposite.setVisible(false);
			}
		});

		Button buttonCancel = new Button(controlsComposite, SWT.PUSH);
		buttonCancel.setText("Cancel");
		buttonCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				imageContainer.dispose();
			}
		});

		Button hundredPercentButton = new Button(controlsComposite, SWT.PUSH);
		hundredPercentButton.setText("100%");
		hundredPercentButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int oldBoundingBox = Math.max(imageContainer.getBounds().width,
						imageContainer.getBounds().height);
				Rectangle oldDimensions = photo
						.scaleAndCenterImage(oldBoundingBox);

				Point newDimensions = photo.getDimensions();

				imageContainer.setSize(newDimensions.x, newDimensions.y);

				// recenter
				imageContainer.setLocation(imageContainer.getLocation().x
						+ (oldDimensions.width - newDimensions.x) / 2,
						imageContainer.getLocation().y
								+ (oldDimensions.height - newDimensions.y) / 2);

				// scale image
				image.dispose();
				image = new Image(Display.getCurrent(), photo.getImage(Math
						.max(newDimensions.x, newDimensions.y)));

				imageContainer.redraw();
				controlsComposite.setVisible(false);
			}
		});

		imageContainer.addMenuDetectListener(new MenuDetectListener() {
			
			@Override
			public void menuDetected(MenuDetectEvent e) {
				controlsComposite.setVisible(true);
				Point location = imageContainer.toControl(e.x, e.y);
				controlsComposite.setLocation(
						location.x - controlsComposite.getBounds().width / 2,
						location.y - controlsComposite.getBounds().height / 2);
			}
		});

		// draw the image
		imageContainer.addListener(SWT.Paint, new Listener() {

			@Override
			public void handleEvent(Event e) {
				GC gc = e.gc;
				gc.drawImage(image, 0, 0);
				gc.dispose();

			}
		});

		imageContainer.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				offset = null;
			}

			@Override
			public void mouseDown(MouseEvent event) {
				offset = new Point(event.x, event.y);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}
		});

		imageContainer.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent event) {
				if (offset != null) {
					imageContainer.setLocation(imageContainer.getLocation().x
							+ event.x - offset.x,
							imageContainer.getLocation().y + event.y - offset.y);
				}
			}
		});

		imageContainer.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public synchronized void mouseScrolled(MouseEvent event) {
				Composite imageContainer = (Composite) event.widget;
				double factor = 0.9;
				if (event.count > 0)
					factor = 1.1;

				// do some calculations
				int oldBoundingBox = Math.max(imageContainer.getBounds().width,
						imageContainer.getBounds().height);
				Rectangle oldDimensions = photo
						.scaleAndCenterImage(oldBoundingBox);

				int newBoundingBox = (int) (oldBoundingBox * factor);
				Rectangle newDimensions = photo
						.scaleAndCenterImage(newBoundingBox);

				imageContainer.setVisible(false);
				imageContainer.setVisible(true);

				// resize image box
				imageContainer.setSize(newDimensions.width,
						newDimensions.height);

				// recenter
				imageContainer.setLocation(imageContainer.getLocation().x
						+ (oldDimensions.width - newDimensions.width) / 2,
						imageContainer.getLocation().y
								+ (oldDimensions.height - newDimensions.height)
								/ 2);

				// scale image
				image.dispose();
				image = new Image(Display.getCurrent(), photo
						.getImage(newBoundingBox));

				imageContainer.redraw();
			}
		});

		imageContainer.layout();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		image.dispose();
	}
}
