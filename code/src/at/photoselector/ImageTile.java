package at.photoselector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

public class ImageTile {

	Point offset;
	final Composite imageContainer;
	Image image;
	Image scaled;
	private String path;

	public ImageTile(final Composite container, Display display,
			String imagePath) {
		path = imagePath;
		image = new Image(display, imagePath);
		scaled = image;

		imageContainer = new Composite(container, SWT.NONE);

		// TODO find some smart way to calculate the initial size of the image
		imageContainer.setSize(image.getImageData().width,
				image.getImageData().height);

		// add controls
		imageContainer.setLayout(new RowLayout());
		Button buttonAccept = new Button(imageContainer, SWT.PUSH);
		buttonAccept.setText("Accept");
		Button buttonDecline = new Button(imageContainer, SWT.PUSH);
		buttonDecline.setText("Decline");
		buttonDecline.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Main.workspace.blacklist(path);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

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

		container.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public synchronized void mouseScrolled(MouseEvent event) {
				Rectangle rect = imageContainer.getBounds();
				if (rect.contains(event.x, event.y)) {
					double factor = 0.9;
					if (event.count > 0)
						factor = 1.1;

					// do some calculations
					int oldImageWidth = imageContainer.getBounds().width;
					int newImageWidth = (int) (Math.round(oldImageWidth
							* factor));

					int oldImageHeight = imageContainer.getBounds().height;
					int newImageHeight = (int) (Math.round(oldImageHeight
							* factor));

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
					gc.setAntialias(SWT.ON);
					gc.setInterpolation(SWT.HIGH);
					gc.drawImage(image, 0, 0, image.getBounds().width,
							image.getBounds().height, 0, 0, newImageWidth,
							newImageHeight);
					gc.dispose();

					// FIXME dispose old scaled! could not figure out how by now
				}
			}
		});
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		image.dispose();
		scaled.dispose();
	}
}
