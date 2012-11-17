package at.photoselector.ui.drawer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

import at.photoselector.Workspace;
import at.photoselector.model.Photo;

class ListItem {
	private Photo photo;
	private Image scaled;
	private int border = 3;
	private DrawerDialog drawerDialog;
	private Composite controlsComposite;

	public ListItem(final Composite parent, final DrawerDialog dialog,
			Photo current) {
		photo = current;
		drawerDialog = dialog;

		final Display display = parent.getDisplay();
		int boundingBox = drawerDialog.getBoundingBox();
		final Composite imageContainer = new Composite(parent, SWT.NONE);
		imageContainer.setLayoutData(new RowData(boundingBox + 2 * border,
				boundingBox + 2 * border));
		imageContainer.setLayout(new RowLayout());
		imageContainer.setBackground(display
				.getSystemColor(SWT.COLOR_DARK_GRAY));

		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				scaled = new Image(display, photo.getImage(drawerDialog
						.getBoundingBox()));

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

		controlsComposite = new Composite(imageContainer, SWT.NONE);
		controlsComposite.setLayout(new RowLayout());
		controlsComposite.setVisible(false);

		final Button buttonAccept = new Button(controlsComposite, SWT.PUSH);
		buttonAccept.setText("OK");
		buttonAccept.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.accept(photo);
				imageContainer.dispose();
				drawerDialog.update();
			}
		});
		buttonAccept.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (SWT.CTRL == e.keyCode) {
					System.out.println("key released");
					controlsComposite.setVisible(false);
				}
			}
		});

		Button buttonDecline = new Button(controlsComposite, SWT.PUSH);
		buttonDecline.setText("X");
		buttonDecline.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace.decline(photo);
				imageContainer.dispose();
				drawerDialog.update();
			}
		});
		
		controlsComposite.layout();

		imageContainer.addMouseTrackListener(new MouseTrackListener() {
			
			@Override
			public void mouseHover(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExit(MouseEvent e) {
//				controlsComposite.setVisible(false);
			}
			
			@Override
			public void mouseEnter(MouseEvent e) {
				System.out.println("enter");
				// if (!controlsComposite.isVisible())
					System.out.println(imageContainer.setFocus());
				System.out.println(imageContainer.isFocusControl());
				if (0 < (SWT.CTRL & e.stateMask)) {
					controlsComposite.setVisible(true);
				}
			}
		});
		
		parent.addMouseTrackListener(new MouseTrackAdapter() {

			@Override
			public void mouseEnter(MouseEvent e) {
				parent.setFocus();
				// controlsComposite.setVisible(false);
			}
		});

		imageContainer.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				controlsComposite.setVisible(false);
				
			}
		});

		imageContainer.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (SWT.CTRL == e.keyCode) {
					System.out.println("key released");
					controlsComposite.setVisible(false);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (SWT.CTRL == e.keyCode) {
					System.out.println("key pressed");
					controlsComposite.setVisible(true);
				}
			}
		});


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
}
