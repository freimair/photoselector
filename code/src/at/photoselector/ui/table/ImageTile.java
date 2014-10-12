package at.photoselector.ui.table;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import at.photoselector.Settings;
import at.photoselector.Workspace;
import at.photoselector.model.Photo;
import at.photoselector.ui.ControlsDialog;
import at.photoselector.ui.drawer.DrawerDialog;


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
	private Composite zoomBoxContainer;
	private Point zoomBoxOffset;

	public ImageTile(final Composite parent, ControlsDialog dialog,
			final DrawerDialog drawerDialog, Photo currentPhoto, int x, int y) {
		super(parent, SWT.NONE);
		imageContainer = this;

		this.photo = currentPhoto;
		controlsDialog = dialog;

		// TODO find some smart way to calculate the initial size of the image
		int boundingBox = (int) Math.min(parent.getBounds().width / 1.5,
				parent.getBounds().height / 1.5);
		image = photo.getImage(boundingBox);

		imageContainer.setLayout(new RowLayout());

		Rectangle dimensions = photo.scaleAndCenterImage(boundingBox);
		imageContainer.setSize(dimensions.width, dimensions.height);

		Point pt = parent.toControl(x, y);
		imageContainer.setLocation(pt.x - imageContainer.getBounds().width / 2,
				pt.y - imageContainer.getBounds().height / 2);
		imageContainer.moveAbove(null);

		// add zoombox container
		zoomBoxContainer = new Composite(imageContainer, SWT.BORDER);
		zoomBoxContainer.moveAbove(null);
		zoomBoxContainer.setVisible(false);

		// add controls
		controlsComposite = new Composite(imageContainer, SWT.NONE);
		controlsComposite.setLayout(new GridLayout(3, true));
		controlsComposite.setVisible(false);

		Button buttonAccept = new Button(controlsComposite, SWT.PUSH);
		buttonAccept.setText("Accept");
		buttonAccept.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		buttonAccept.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.accept(photo);
				controlsDialog.update();
				drawerDialog.update();
				imageContainer.dispose();
				photo.clearCachedImages();
			}
		});

		Button buttonCancel = new Button(controlsComposite, SWT.PUSH);
		buttonCancel.setText("Cancel");
		buttonCancel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		buttonCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				imageContainer.dispose();
			}
		});

		Button buttonDecline = new Button(controlsComposite, SWT.PUSH);
		buttonDecline.setText("Decline");
		buttonDecline.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		buttonDecline.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.decline(photo);
				controlsDialog.update();
				drawerDialog.update();
				imageContainer.dispose();
				photo.clearCachedImages();
			}
		});

		Button exitButton = new Button(controlsComposite, SWT.PUSH);
		exitButton.setText("Exit Controls");
		exitButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				3, 1));
		exitButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				controlsComposite.setVisible(false);
			}
		});

		Button hundredPercentButton = new Button(controlsComposite, SWT.PUSH);
		hundredPercentButton.setText("100%");
		hundredPercentButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
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
		sharpnessComparisonButton.setText("compare");
		sharpnessComparisonButton.setLayoutData(new GridData(SWT.FILL,
				SWT.FILL, false, false, 2, 1));
		sharpnessComparisonButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int y = controlsComposite.getLocation().y
						+ controlsComposite.getSize().y / 2;
				int x = controlsComposite.getLocation().x
						+ controlsComposite.getSize().x / 2;

				for (ImageTile current : getCurrentImageTiles())
					current.displayZoomBox(x, y);

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
				zoomBoxContainer.setSize(
						(int) Math.round(Settings.getZoomBoxContainerSize()
								* (imageContainer.getSize().x + imageContainer
										.getSize().y) / 2),
						(int) Math.round(Settings.getZoomBoxContainerSize()
								* (imageContainer.getSize().x + imageContainer
										.getSize().y) / 2));

				if (zoomBoxContainer.isVisible()) {
					// get center of zoomBoxContainer in imageContainer
					// coordinates
					int centerX = zoomBoxContainer.getLocation().x
							+ zoomBoxContainer.getSize().x / 2;
					int centerY = zoomBoxContainer.getLocation().y
							+ zoomBoxContainer.getSize().y / 2;

					// normalize to image size
					double normalizedX = (double) centerX
							/ imageContainer.getSize().x;
					double normalizedY = (double) centerY
							/ imageContainer.getSize().y;

					// calculate "center" for full scale image
					int x = (int) Math.round(normalizedX
							* photo.getDimensions().x);
					int y = (int) Math.round(normalizedY
							* photo.getDimensions().y);

					// calculate bounding box
					int left = x - zoomBoxContainer.getSize().x / 2;
					int top = y - zoomBoxContainer.getSize().y / 2;

					// render
					GC gc = e.gc;
					// use full image as source...
					gc.drawImage(photo.getImage(Math.max(
							photo.getDimensions().x, photo.getDimensions().y)),
							left, top,
							zoomBoxContainer.getSize().x,
							zoomBoxContainer.getSize().y, 0, 0,
							zoomBoxContainer.getBounds().width,
							zoomBoxContainer.getBounds().height);
					gc.dispose();
				}
			}
		});

		zoomBoxContainer.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent event) {
				zoomBoxContainer.forceFocus(); // for win

				for (ImageTile current : getCurrentImageTiles())
					current.doZoomBoxDrag(event.x, event.y);
			}
		});

		zoomBoxContainer.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				for (ImageTile current : getCurrentImageTiles())
					current.resetZoomBoxDrag();
			}

			@Override
			public void mouseDown(MouseEvent event) {
				for (ImageTile current : getCurrentImageTiles())
					if (3 == event.button)
						current.hideZoomBox();
					else
						current.startZoomBoxDrag(event.x, event.y);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				for (ImageTile current : getCurrentImageTiles())
					current.hideZoomBox();
			}
		});
	}

	private List<ImageTile> getCurrentImageTiles() {
		List<ImageTile> result = new ArrayList<ImageTile>();

		for (Control currentControl : imageContainer.getParent().getChildren()) {
			if (currentControl instanceof ImageTile)
				result.add((ImageTile) currentControl);
		}

		return result;
	}

	private void displayZoomBox(int x, int y) {
		zoomBoxContainer.setLocation(x - zoomBoxContainer.getSize().x / 2, y
				- zoomBoxContainer.getSize().y / 2);

		controlsComposite.setVisible(false);
		zoomBoxContainer.setVisible(true);
	}

	private void startZoomBoxDrag(int x, int y) {
		zoomBoxOffset = new Point(x, y);
	}

	private void doZoomBoxDrag(int dx, int dy) {

		if (zoomBoxOffset != null) {
			int newX = zoomBoxContainer.getLocation().x + dx
					- zoomBoxOffset.x;
			int newY = zoomBoxContainer.getLocation().y + dy
					- zoomBoxOffset.y;

			if (-zoomBoxContainer.getSize().x / 2 < newX
					&& -zoomBoxContainer.getSize().y / 2 < newY
					&& imageContainer.getSize().x
							- zoomBoxContainer.getSize().x / 2 > newX
					&& imageContainer.getSize().y
							- zoomBoxContainer.getSize().y / 2 > newY) {
				zoomBoxContainer.setLocation(newX, newY);
				zoomBoxContainer.redraw();
			}
		}
	}

	private void resetZoomBoxDrag() {
		zoomBoxOffset = null;
	}

	private void hideZoomBox() {
		zoomBoxContainer.setVisible(false);
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
