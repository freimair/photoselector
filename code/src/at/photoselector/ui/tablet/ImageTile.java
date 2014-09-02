package at.photoselector.ui.tablet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

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

	public ImageTile(final Composite parent, ControlsDialog dialog,
			Photo currentPhoto) {
		super(parent, SWT.NONE);
		imageContainer = this;

		this.photo = currentPhoto;
		controlsDialog = dialog;

		// TODO find some smart way to calculate the initial size of the image
		 int boundingBox = (int) Math.min(parent.getBounds().width,
		 parent.getBounds().height);
		image = photo.getImage(boundingBox);

		// imageContainer.setLayout(new RowLayout());

		 Rectangle dimensions = photo.scaleAndCenterImage(boundingBox);

		imageContainer.setBounds(dimensions);
		FormData data = new FormData();
		data.left = new FormAttachment(0, dimensions.x);
		data.top = new FormAttachment(0, dimensions.y);
		data.width = dimensions.width;
		data.height = dimensions.height;
		imageContainer.setLayoutData(data);


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

		imageContainer.layout();
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
