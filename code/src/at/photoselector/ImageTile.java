package at.photoselector;

import java.sql.SQLException;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

public class ImageTile {

	Point offset;
	final Composite imageContainer;
	Image image;
	Image scaled;
	private String path;

	public ImageTile(final Composite container, Display display,
			String imagePath, int x, int y) {
		path = imagePath;
		image = new Image(display, imagePath);
		scaled = image;

		imageContainer = new Composite(container, SWT.NONE);

		// TODO find some smart way to calculate the initial size of the image
		imageContainer.setSize(image.getImageData().width,
				image.getImageData().height);

		// add controls
		imageContainer.setLayout(new RowLayout());
		Point pt = container.toControl(x, y);
		imageContainer.setLocation(pt.x - imageContainer.getBounds().width / 2,
				pt.y - imageContainer.getBounds().height / 2);

		final Button buttonAccept = new Button(imageContainer, SWT.PUSH);
		buttonAccept.setText("Accept");
		buttonAccept.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.accept(path);
				processed();
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
				Workspace.decline(path);
				processed();
				imageContainer.dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

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
		imageContainer.layout();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		image.dispose();
		scaled.dispose();
	}

	private void processed() {
		PhotoSelector.bar.setSelection(PhotoSelector.bar.getSelection() + 1);
		if (PhotoSelector.bar.getSelection() >= PhotoSelector.bar.getMaximum()) {
			Workspace.stageCompleted();

			// update stage list
			for (Control current : PhotoSelector.list.getChildren())
				current.dispose();
			PhotoSelector.list.clearAll();
			TableItem item = new TableItem(PhotoSelector.list, SWT.NONE);
			String name = "all";
			item.setText(name);
			for (String current : Workspace.getFilters()) {
				item = new TableItem(PhotoSelector.list, SWT.NONE);
				item.setText(current);
			}

			// update progress bar
			try {
				PhotoSelector.bar.setMaximum(Workspace.getPhotos(
						Workspace.UNPROCESSED | Workspace.ACCEPTED
								| Workspace.DECLINED).size());
				PhotoSelector.bar.setSelection(Workspace.getPhotos(
						Workspace.ACCEPTED | Workspace.DECLINED).size());
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// update drawer
			// Main.fillTable(tableComposite, drawerComposite, list, display,
			// showAcceptedButton, showDeclinedButton);
		}
	}
}
