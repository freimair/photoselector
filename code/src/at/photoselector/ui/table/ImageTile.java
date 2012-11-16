package at.photoselector.ui.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
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
	private Image scaled;
	private Photo photo;
	private ControlsDialog controlsDialog;

	public ImageTile(final Composite parent, ControlsDialog dialog,
			Photo currentPhoto, int x, int y) {
		this.photo = currentPhoto;
		controlsDialog = dialog;

		image = new Image(dialog.getShell().getDisplay(), photo.getPath()
				.getAbsolutePath());
		scaled = image;

		imageContainer = new Composite(parent, SWT.NONE);

		// TODO find some smart way to calculate the initial size of the image
		imageContainer.setSize(image.getImageData().width,
				image.getImageData().height);

		// add controls
		imageContainer.setLayout(new RowLayout());
		Point pt = parent.toControl(x, y);
		imageContainer.setLocation(pt.x - imageContainer.getBounds().width / 2,
				pt.y - imageContainer.getBounds().height / 2);

		final Button buttonAccept = new Button(imageContainer, SWT.PUSH);
		buttonAccept.setText("Accept");
		buttonAccept.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.accept(photo);
				controlsDialog.update();
				imageContainer.dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		final Button buttonDecline = new Button(imageContainer, SWT.PUSH);
		buttonDecline.setText("Decline");
		buttonDecline.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.decline(photo);
				controlsDialog.update();
				imageContainer.dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		final Button buttonCancel = new Button(imageContainer, SWT.PUSH);
		buttonCancel.setText("Cancel");
		buttonCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				imageContainer.dispose();
			}
		});

//		imageContainer.addMouseTrackListener(new MouseTrackListener() {
//
//			@Override
//			public void mouseHover(MouseEvent e) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void mouseExit(MouseEvent e) {
//				buttonAccept.setVisible(false);
//				buttonDecline.setVisible(false);
//
//			}
//
//			@Override
//			public void mouseEnter(MouseEvent e) {
//				buttonAccept.setVisible(true);
//				buttonDecline.setVisible(true);
//			}
//		});

		// draw the image
		imageContainer.addListener(SWT.Paint, new Listener() {

			@Override
			public void handleEvent(Event e) {
				GC gc = e.gc;
				gc.drawImage(scaled, 0, 0);
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
				int oldImageWidth = imageContainer.getBounds().width;
				int newImageWidth = (int) (Math.round(oldImageWidth * factor));

				int oldImageHeight = imageContainer.getBounds().height;
				int newImageHeight = (int) (Math.round(oldImageHeight * factor));

				// resize image box
				imageContainer.setSize(newImageWidth, newImageHeight);

				// recenter
				imageContainer.setLocation(imageContainer.getLocation().x
						+ (oldImageWidth - newImageWidth) / 2,
						imageContainer.getLocation().y
								+ (oldImageHeight - newImageHeight) / 2);

				// scale image
				scaled = new Image(Display.getDefault(), newImageWidth,
						newImageHeight);
				GC gc = new GC(scaled);
				gc.drawImage(image, 0, 0, image.getBounds().width,
						image.getBounds().height, 0, 0, newImageWidth,
						newImageHeight);
				gc.dispose();

				// FIXME dispose old scaled! could not figure out how by now
				imageContainer.layout();
			}
		});
		imageContainer.layout();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		image.dispose();
		scaled.dispose();
	}
}
