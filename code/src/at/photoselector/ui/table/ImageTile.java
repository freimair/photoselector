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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import at.photoselector.Workspace;
import at.photoselector.model.Photo;
import at.photoselector.ui.ControlsDialog;


class ImageTile extends Composite {

	private class ImageDrawer implements Runnable {
		private int myBoundingBox;

		public ImageDrawer(int boundingBox) {
			myBoundingBox = boundingBox;
		}

		@Override
		public void run() {
			// image.dispose();
			image = photo.getImage(myBoundingBox);

			imageContainer.redraw();
		}
	}

	private Point offset;
	private Composite imageContainer;
	private Image image;
	private Photo photo;
	private ControlsDialog controlsDialog;
	private Composite controlsComposite;
	private Label probelabel;
	private Composite zoomBoxContainer;

	public ImageTile(final Composite parent, ControlsDialog dialog,
			Photo currentPhoto, int x, int y) {
		super(parent, SWT.NONE);
		imageContainer = this;

		this.photo = currentPhoto;
		controlsDialog = dialog;

		// TODO find some smart way to calculate the initial size of the image
		int boundingBox = (int) Math.min(parent.getBounds().width / 1.5,
				parent.getBounds().height / 1.5);
		image = photo.getImage(boundingBox);

		imageContainer.setLayout(new RowLayout());
		probelabel = new Label(this, SWT.NONE);

		Rectangle dimensions = photo.scaleAndCenterImage(boundingBox);
		imageContainer.setSize(dimensions.width, dimensions.height);

		Point pt = parent.toControl(x, y);
		imageContainer.setLocation(pt.x - imageContainer.getBounds().width / 2,
				pt.y - imageContainer.getBounds().height / 2);
		imageContainer.moveAbove(null);

		// add zoombox container
		zoomBoxContainer = new Composite(imageContainer, SWT.NONE);
		zoomBoxContainer.setSize(50, 50);
		zoomBoxContainer.moveAbove(null);
		zoomBoxContainer.setVisible(false);

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
				photo.clearCachedImages();
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
				photo.clearCachedImages();
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
				int y = controlsComposite.getLocation().y
						+ controlsComposite.getSize().y / 2;
				int x = controlsComposite.getLocation().x
						+ controlsComposite.getSize().x / 2;

				double factor = (double) photo.getDimensions().x
						/ imageContainer.getSize().x;

				zoomImageContainer(factor, x, y);
				controlsComposite.setVisible(false);
			}
		});

		Button sharpnessComparisonButton = new Button(controlsComposite,
				SWT.PUSH);
		sharpnessComparisonButton.setText("compare sharpness");
		sharpnessComparisonButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				zoomBoxContainer.setVisible(true);
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
				gc.drawImage(image, 0, 0, image.getBounds().width,
						image.getBounds().height, 0, 0,
						imageContainer.getBounds().width,
						imageContainer.getBounds().height);
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
				imageContainer.moveAbove(null);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}
		});

		imageContainer.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent event) {
				imageContainer.forceFocus(); // for win

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
				double factor = 0.8; // 8/10 = finite, 10/8 = finite. :)
				if (event.count > 0)
					factor = 1 / factor;

				zoomImageContainer(factor, event.x, event.y);

				imageContainer.forceFocus(); // for win
			}
		});

		imageContainer.layout();

		zoomBoxContainer.addListener(SWT.Paint, new Listener() {

			@Override
			public void handleEvent(Event e) {
				GC gc = e.gc;
				gc.drawImage(image, 0, 0, image.getBounds().width,
						image.getBounds().height, 0, 0,
						zoomBoxContainer.getBounds().width,
						zoomBoxContainer.getBounds().height);
				gc.dispose();
			}
		});

		zoomBoxContainer.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {

			}

			@Override
			public void mouseDown(MouseEvent event) {
				zoomBoxContainer.setVisible(false);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}
		});
	}

	private void zoomImageContainer(double factor, int x, int y) {
		// do some calculations
		int oldBoundingBox = Math.max(imageContainer.getBounds().width,
				imageContainer.getBounds().height);
		Rectangle oldDimensions = photo.scaleAndCenterImage(oldBoundingBox);

		int newBoundingBox = (int) (oldBoundingBox * factor);
		Rectangle newDimensions = photo.scaleAndCenterImage(newBoundingBox);

		// relocate
		imageContainer
				.setLocation(
						(int) (imageContainer.getLocation().x + (oldDimensions.width - newDimensions.width)
								* (double) x / imageContainer.getSize().x),
						(int) (imageContainer.getLocation().y + (oldDimensions.height - newDimensions.height)
								* (double) y / imageContainer.getSize().y));

		// resize image box
		imageContainer.setSize(newDimensions.width, newDimensions.height);

		// scale image
		Display.getCurrent().asyncExec(new ImageDrawer(newBoundingBox));
		imageContainer.moveAbove(null);
		imageContainer.redraw();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		// image.dispose();
	}

	public Photo getPhoto() {
		return photo;
	}
}
