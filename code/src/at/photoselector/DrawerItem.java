package at.photoselector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class DrawerItem {
	private String imagePath;
	private Image scaled;
	private Label label;

	public DrawerItem(final Composite container, Display display, String path) {
		imagePath = path;
		label = new Label(container, SWT.NONE);
		Image image = new Image(display, path);
		scaled = new Image(display, 100, 100);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.drawImage(image, 0, 0, image.getBounds().width,
				image.getBounds().height, 0, 0, 100, 100);
		label.setImage(scaled);
		image.dispose();
		container.layout();

		final DragSource source = new DragSource(label, DND.DROP_MOVE
				| DND.DROP_COPY | DND.DROP_LINK);
		source.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		source.addDragListener(new DragSourceListener() {

			@Override
			public void dragStart(DragSourceEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dragSetData(DragSourceEvent event) {
				// TODO Auto-generated method stub
				event.data = imagePath;
			}

			@Override
			public void dragFinished(DragSourceEvent event) {
				// TODO Auto-generated method stub
				label.dispose();
				container.layout();
				Rectangle r = container.getParent().getClientArea();
				((ScrolledComposite) container.getParent())
						.setMinSize(container.computeSize(r.width, SWT.DEFAULT));
			}
		});
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		scaled.dispose();
	}
}
