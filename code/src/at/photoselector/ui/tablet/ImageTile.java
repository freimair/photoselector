package at.photoselector.ui.tablet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import at.photoselector.model.Photo;
import at.photoselector.ui.ControlsDialog;


class ImageTile extends Composite {
	private Composite imageContainer;
	private Image image;
	private Photo photo;

	public ImageTile(final Composite parent, ControlsDialog dialog,
			Photo currentPhoto) {
		super(parent, SWT.NONE);
		imageContainer = this;

		this.photo = currentPhoto;

		// get image data. scale to fill the biggest possible option
		int boundingBox = (int) Math.max(parent.getBounds().width,
				parent.getBounds().height);
		image = photo.getImage(boundingBox);

		FormData data = new FormData();

		double frameRatio = (double) parent.getBounds().width
				/ parent.getBounds().height;
		double imageRatio = (double) image.getBounds().width
				/ image.getBounds().height;

		if (frameRatio < imageRatio) {
			// fit width
			data.width = parent.getBounds().width;
			data.height = (int) (data.width / imageRatio);

			data.left = new FormAttachment(0, 0);
			data.top = new FormAttachment(0,
					(parent.getBounds().height - data.height) / 2);
		} else { // if (alb < bla) {
			// fit height
			data.height = parent.getBounds().height;
			data.width = (int) (data.height * imageRatio);

			data.top = new FormAttachment(0, 0);
			data.left = new FormAttachment(0,
					(parent.getBounds().width - data.width) / 2);
		}

		imageContainer.setLayoutData(data);


		// draw the image
		imageContainer.addListener(SWT.Paint, new Listener() {

			@Override
			public void handleEvent(Event e) {
				GC gc = e.gc;
				if (!image.isDisposed())
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
