package at.photoselector.ui.drawer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
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

class ListItem {
	class CreateCacheImage implements Runnable {

		private Display display;

		public CreateCacheImage(Display display) {
			this.display = display;
		}

		public void run() {
			scaled = photo.getImage(drawerDialog.getBoundingBox());
			display.asyncExec(new Runnable() {
				@Override
				public void run() {

					// draw the image
					imageContainer.addListener(SWT.Paint, new Listener() {

						@Override
						public void handleEvent(Event e) {
							GC gc = e.gc;
							Rectangle dimensions = photo
									.scaleAndCenterImage(drawerDialog
											.getBoundingBox());
							gc.drawImage(scaled, dimensions.x + border,
									dimensions.y + border);
							gc.dispose();
						}
					});
					imageContainer.redraw();
				}
			});
		}
	}

	private Photo photo;
	private Image scaled;
	private int border = 3;
	private DrawerDialog drawerDialog;
	private ControlsDialog controlsDialog;
	private Button buttonAccept;
	private Button buttonDecline;
	private PaintListener paintListener;
	private Button buttonReset;
	private Composite imageContainer;
	private boolean loaded = false;
	private Label labelFiletype;

	public ListItem(final Composite parent, final DrawerDialog dialog,
			ControlsDialog cDialog,
			Photo current) {
		photo = current;
		drawerDialog = dialog;
		this.controlsDialog = cDialog;

		final Display display = parent.getDisplay();
		int boundingBox = drawerDialog.getBoundingBox();
		imageContainer = new Composite(parent, SWT.NONE);
		imageContainer.setLayoutData(new RowData(boundingBox + 2 * border,
				boundingBox + 2 * border));
		imageContainer.setLayout(new RowLayout());
		imageContainer.setBackground(new Color(display,
				Photo.DECLINED == current.getStatus() ? 100 : 75,
				Photo.ACCEPTED == current.getStatus() ? 100 : 75, 75));

		// fetch thumbnails asynchronously
		// if and only if the current thumbnail is visible on screen
		// at the time the execution is performed (we do not need to render
		// thumbnails the user isn't going to see on screen)
		imageContainer.getParent().addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				try {
							if (!loaded) {
								// extract client area of scrolledcomposite
								Rectangle visibleArea = imageContainer
										.getParent().getParent()
										.getClientArea();
								// add scroll offset
								visibleArea.y += -imageContainer.getParent()
										.getLocation().y;

								// check if current thumbnail is visible
								if (visibleArea.contains(imageContainer
										.getLocation())
										|| visibleArea.contains(new Point(
												imageContainer.getLocation().x
														+ imageContainer
																.getSize().x,
												imageContainer.getLocation().y
														+ imageContainer
																.getSize().y))) {
									// memorize that this thumbnail is already
									// fetched
									loaded = true;
									getThumbnail();
								}
							}
				} catch (Exception ex) {
				}
			}

			private void getThumbnail() {
				// fetch thumbnail
				try {
					ExecutorService sepp = Executors.newSingleThreadExecutor();
					sepp.submit(
							new CreateCacheImage(Display.getCurrent()));
					sepp.shutdown();

					} catch (SWTException ex) {

				}
			}
		});

		buttonAccept = new Button(imageContainer, SWT.PUSH);
		buttonAccept.setText("Accept");
		buttonAccept.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.accept(photo);
				// Composite parent = imageContainer.getParent();
				// imageContainer.dispose();
				// parent.layout();
				controlsDialog.update();
				drawerDialog.update();
			}
		});

		buttonDecline = new Button(imageContainer, SWT.PUSH);
		buttonDecline.setText("Decline");
		buttonDecline.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.decline(photo);
				// Composite parent = imageContainer.getParent();
				// imageContainer.dispose();
				// parent.layout();
				controlsDialog.update();
				drawerDialog.update();
			}
		});

		buttonReset = new Button(imageContainer, SWT.PUSH);
		buttonReset.setText("Reset");
		buttonReset.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.reset(photo);
				drawerDialog.update();
			}
		});

		labelFiletype = new Label(imageContainer, SWT.NONE);
		labelFiletype.setText(photo.getPath().getName()
				.substring(photo.getPath().getName().lastIndexOf(".")));
		labelFiletype.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		paintListener = new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				buttonAccept.setVisible(drawerDialog.isShowControls());
				buttonDecline.setVisible(drawerDialog.isShowControls());
				buttonReset.setVisible(drawerDialog.isShowControls());
				labelFiletype.setVisible(drawerDialog.isShowControls());
			}
		};

		parent.addPaintListener(paintListener);
		// remove paint listener on dispose. we cannot wait for gc to finalize
		// the object and therefore remove the paint listener
		imageContainer.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				parent.removePaintListener(paintListener);
			}
		});
		parent.redraw();

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
		source.addDragListener(new DragSourceAdapter() {

			@Override
			public void dragSetData(DragSourceEvent event) {
				event.data = String.valueOf(photo.getId());
			}
		});
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		scaled.dispose();
	}

	public void dispose() {
		imageContainer.dispose();
	}
}
