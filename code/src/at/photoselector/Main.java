package at.photoselector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class Main {
	static Workspace workspace = new Workspace(
	// "/home/me/Projekte/pictureselector/playground/w1");
			"/home/fr/Desktop/pictureselector/playground/w2");
	private static Shell shell;
	public static ProgressBar bar;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		shell = new Shell(display);
		shell.setLayout(new RowLayout());
		Composite controlComposite = new Composite(shell, SWT.NONE);
		controlComposite.setLayout(new RowLayout(SWT.VERTICAL));
		Button switchWorkspaceButton = new Button(controlComposite, SWT.PUSH);
		switchWorkspaceButton.setText("Switch Workspace");
		switchWorkspaceButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				workspace = new Workspace(dialog.open());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Button addPhotosButton = new Button(controlComposite, SWT.PUSH);
		addPhotosButton.setText("Add photos");

		Button settingsButton = new Button(controlComposite, SWT.PUSH);
		settingsButton.setText("Settings");
		Button exitButton = new Button(controlComposite, SWT.PUSH);
		exitButton.setText("Exit");
		exitButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Composite filterComposite = new Composite(shell, SWT.BORDER);
		filterComposite.setLayout(new RowLayout(SWT.VERTICAL));
		Label filterListLabel = new Label(filterComposite, SWT.NONE);
		filterListLabel.setText("Stages");
		final Table list = new Table(filterComposite, SWT.BORDER | SWT.V_SCROLL);
		list.setLayoutData(new RowData(100, 150));

		TableItem item = new TableItem(list, SWT.NONE);
		String name = "all";
		item.setText(name);

		for (String current : workspace.getFilters()) {
			item = new TableItem(list, SWT.NONE);
			item.setText(current);
		}

		list.setSelection(list.getItemCount() - 1);

		bar = new ProgressBar(filterComposite, SWT.SMOOTH);
		try {
			bar.setMaximum(workspace.getPhotos(
					Workspace.UNPROCESSED | Workspace.ACCEPTED
							| Workspace.DECLINED).size());
			bar.setSelection(workspace.getPhotos(
					workspace.ACCEPTED | workspace.DECLINED).size());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		final Composite drawerComposite = new Composite(shell, SWT.NONE);
		drawerComposite.setLayout(new RowLayout(SWT.VERTICAL));

		final Button showAcceptedButton = new Button(drawerComposite,
				SWT.TOGGLE);
		showAcceptedButton.setText("accepted");

		final Button showDeclinedButton = new Button(drawerComposite,
				SWT.TOGGLE);
		showDeclinedButton.setText("declined");

		final ScrolledComposite photoListComposite = new ScrolledComposite(
				drawerComposite, SWT.V_SCROLL);
		photoListComposite.setLayoutData(new RowData(330, 500));

		final Composite photoListContentComposite = new Composite(
				photoListComposite, SWT.NONE);
		photoListContentComposite.setBackground(display
				.getSystemColor(SWT.COLOR_DARK_GRAY));
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.wrap = true;
		photoListContentComposite.setLayout(layout);

		photoListComposite.setContent(photoListContentComposite);
		photoListComposite.setExpandHorizontal(true);
		photoListComposite.setExpandVertical(true);

		final Composite tableComposite = new Composite(shell, SWT.NONE);
		tableComposite.setLayoutData(new RowData(500, 500));
		tableComposite.setBackground(display.getSystemColor(SWT.COLOR_GRAY));

		DropTarget target = new DropTarget(tableComposite, DND.DROP_MOVE
				| DND.DROP_COPY | DND.DROP_LINK);
		target.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		target.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}

				new ImageTile(tableComposite, display, (String) event.data,
						event.x, event.y);
			}
		});

		list.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				fillTable(tableComposite, photoListContentComposite, list,
						display, showAcceptedButton, showDeclinedButton);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		addPhotosButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
				workspace.addPhoto(dialog.open());
				fillTable(tableComposite, photoListContentComposite, list,
						display, showAcceptedButton, showDeclinedButton);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		showAcceptedButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				fillTable(tableComposite, photoListContentComposite, list,
						display, showAcceptedButton, showDeclinedButton);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		showDeclinedButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				fillTable(tableComposite, photoListContentComposite, list,
						display, showAcceptedButton, showDeclinedButton);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		fillTable(tableComposite, photoListContentComposite, list, display,
				showAcceptedButton, showDeclinedButton);

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		Database.closeConnection();
		display.dispose();
	}

	public static List<String> getActiveFilters(Table list) {
		List<String> result = new ArrayList<String>();

		for (int i = 0; i <= list.getSelectionIndex(); i++)
			result.add(list.getItem(i).getText());

		return result;
	}

	public static void fillTable(Composite tableComposite,
			Composite drawerComposite, Table list, Display display,
			Button showAcceptedButton, Button showDeclinedButton) {
		try {
			for (Control current : drawerComposite.getChildren())
				current.dispose();
			int filter = Workspace.UNPROCESSED;
			if (showAcceptedButton.getSelection())
				filter |= Workspace.ACCEPTED;
			if (showDeclinedButton.getSelection())
				filter |= Workspace.DECLINED;
			for (String current : workspace.getPhotos(filter)) {
				// new ImageTile(tableComposite, display, current);
				new DrawerItem(drawerComposite, display, current);
			}
			Rectangle r = drawerComposite.getParent().getClientArea();
			((ScrolledComposite) drawerComposite.getParent())
					.setMinSize(drawerComposite.computeSize(r.width,
							SWT.DEFAULT));
			drawerComposite.redraw();
			shell.redraw();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
