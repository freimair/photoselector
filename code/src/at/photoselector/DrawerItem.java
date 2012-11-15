package at.photoselector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class DrawerItem {
	private String path;
	private Image scaled;
	private Rectangle dimensions;
	private Image image;

	public DrawerItem(final Composite container, Display display,
			String imagePath) {
		path = imagePath;

		image = new Image(display, imagePath);
		dimensions = scaleAndCenterImage(image.getBounds(), 100);

		final Composite imageContainer = new Composite(container, SWT.NONE);
		imageContainer.setLayoutData(new RowData(106, 106));
		imageContainer.setLayout(new RowLayout());
		imageContainer.setBackground(display
				.getSystemColor(SWT.COLOR_DARK_GRAY));
		// draw image
		scaled = new Image(display, dimensions.width, dimensions.height);

		// draw the image
		imageContainer.addListener(SWT.Paint, new Listener() {

			@Override
			public void handleEvent(Event e) {
				GC gc = e.gc;
				gc.drawImage(image, 0, 0, image.getBounds().width,
						image.getBounds().height, dimensions.x + 3,
						dimensions.y + 3, dimensions.width, dimensions.height);
				gc.dispose();
			}
		});

		final Button buttonAccept = new Button(imageContainer, SWT.PUSH);
		buttonAccept.setText("Accept");
		buttonAccept.setVisible(false);
		buttonAccept.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Main.workspace.accept(path);
				// processed();
				imageContainer.dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		final Button buttonDecline = new Button(imageContainer, SWT.PUSH);
		buttonDecline.setText("Decline");
		buttonDecline.setVisible(false);
		buttonDecline.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// Main.workspace.decline(path);
				// processed();
				imageContainer.dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		// TODO does not work stable. overlapping and tracker loops. find other way.
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

		final DragSource source = new DragSource(imageContainer, DND.DROP_MOVE
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
				event.data = path;
			}

			@Override
			public void dragFinished(DragSourceEvent event) {
				// TODO Auto-generated method stub
				imageContainer.dispose();
				container.layout();
				Rectangle r = container.getParent().getClientArea();
				((ScrolledComposite) container.getParent())
						.setMinSize(container.computeSize(r.width, SWT.DEFAULT));
			}
		});
	}

	private Rectangle scaleAndCenterImage(Rectangle imageDimension,
			int boundingBox) {
		Rectangle result = new Rectangle(0, 0, boundingBox, boundingBox);

		// scale
		if (imageDimension.width > imageDimension.height) {
			result.height = (int) (1.0 * boundingBox / imageDimension.width * imageDimension.height);
			result.y = (int) (1.0 * ((boundingBox - result.height) / 2));
		} else {
			result.width = (int) (1.0 * boundingBox / imageDimension.height * imageDimension.width);
			result.x = (int) (1.0 * ((boundingBox - result.width) / 2));
		}

		return result;

	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		scaled.dispose();
		image.dispose();
	}
}
