package at.photoselector.ui.table;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import at.photoselector.model.Photo;
import at.photoselector.ui.ControlsDialog;
import at.photoselector.ui.MyApplicationWindow;
import at.photoselector.ui.drawer.DrawerDialog;

public class TableDialog extends MyApplicationWindow {

	private ControlsDialog controlsDialog;
	private DrawerDialog drawerDialog;

	public TableDialog(Shell parentShell, ControlsDialog dialog,
			DrawerDialog drawerDialog) {
		super(parentShell);

		this.controlsDialog = dialog;
		this.drawerDialog = drawerDialog;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText("Table");
	}

	@Override
	protected Control createContents(final Composite parent) {
		parent.setBackground(new Color(parent.getDisplay(), 75, 75, 75));

		for (Control current : parent.getChildren())
			current.dispose();

		DropTarget target = new DropTarget(parent, DND.DROP_MOVE
				| DND.DROP_COPY | DND.DROP_LINK);
		target.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		target.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}

				Photo photo = Photo.get(Integer.valueOf((String) event.data));

				for (Control current : getShell().getChildren()) {
					if (current instanceof ImageTile)
						if (((ImageTile) current)
								.getPhoto()
								.getPath()
								.getAbsolutePath()
								.equalsIgnoreCase(
										photo.getPath().getAbsolutePath())) {
							((ImageTile) current).blink();
							return;
						}
				}
				
				// here to modify the coordinates
				Point pt = parent.toControl(event.x, event.y);
				Point modifiedCoordinates = this.adjustDropCoordinates(Photo.get(Integer.valueOf((String) event.data)),
						pt.x, pt.y);
				double initialScale = this.smartScale(photo);

				new ImageTile(parent, controlsDialog, drawerDialog, photo, modifiedCoordinates.x, modifiedCoordinates.y,
						initialScale);
			}

			/**
			 * TODO move outside of droplistener
			 * 
			 * @param x
			 * @param y
			 * @return Point
			 */
			private Point adjustDropCoordinates(Photo photo, int x, int y) {
				// scale as in smart scaling
				double scale = this.smartScale(photo);

				int ourWidth = (int) (photo.getDimensions().x * scale);
				int ourHeight = (int) (photo.getDimensions().y * scale);

				// get through all existing ImageTiles
				for (Control current : getShell().getChildren()) {
					if (current instanceof ImageTile) {
						Rectangle bounds = ((ImageTile) current).getBounds();

						// - TODO check if window-bounds are compromised
						// check if drop is next to an existing image
						if (y > bounds.y && y < bounds.y + bounds.height) {
							if (x > bounds.x + bounds.width && x < bounds.x + bounds.width + 10 + ourWidth / 2)
								x = bounds.x + bounds.width + 10 + ourWidth / 2;
							else if (x < bounds.x && x > bounds.x - 10 - ourWidth / 2)
								x = bounds.x - 10 - ourWidth / 2;
						}

						if (x > bounds.x && x < bounds.x + bounds.width) {
							if (y > bounds.y + bounds.height && y < bounds.y + bounds.height + 10 + ourHeight / 2)
								y = bounds.y + bounds.height + 10 + ourHeight / 2;
							else if (y < bounds.y && y > bounds.y - 10 - ourHeight / 2)
								y = bounds.y - 10 - ourHeight / 2;
						}
					}
				}

				return new Point(x, y);
			}

			/**
			 * move outside of droplistener
			 * 
			 * @param photo
			 * @return
			 */
			private double smartScale(Photo photo) {
				// TODO merge duplicated code pieces
				return photo.isPortrait() ? parent.getBounds().width / 3.2 / photo.getDimensions().x
						: Math.min(parent.getBounds().height / 2.2 / photo.getDimensions().y, 2.1);
			}

		});

		parent.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				getShell().setFullScreen(!getShell().getFullScreen());
			}
		});

		return parent;
	}

	@Override
	public boolean close() {
		super.close();
		if (null != drawerDialog.getShell())
			drawerDialog.close();
		return true;
	}


	@Override
	public void update() {
		/*
		 * TODO if accept/decline is available in drawer also, we might have to
		 * remove some photos from the table
		 */

	}
}
