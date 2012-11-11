package at.photoselector;

import java.sql.SQLException;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class Main {
	static Workspace workspace = new Workspace(
			"/home/me/Projekte/pictureselector/playground/w1");
	private static Shell shell;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		shell = new Shell(display);
		shell.setLayout(new RowLayout());
		Button switchWorkspaceButton = new Button(shell, SWT.PUSH);
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

		Button addPhotosButton = new Button(shell, SWT.PUSH);
		addPhotosButton.setText("Add photos");
		addPhotosButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
				workspace.addPhoto(dialog.open());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		Button settingsButton = new Button(shell, SWT.PUSH);
		settingsButton.setText("Settings");
		Button exitButton = new Button(shell, SWT.PUSH);
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
		filterListLabel.setText("Filters");
		final Table list = new Table(filterComposite, SWT.CHECK | SWT.BORDER
				| SWT.V_SCROLL);
		list.setLayoutData(new RowData(100, 150));
		for (String current : workspace.getFilters()) {
			TableItem item = new TableItem(list, SWT.NONE);
			item.setText(current);
		}

		list.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Main.workspace.setCurrentFilter(((TableItem) e.item).getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Button addFilterButton = new Button(filterComposite, SWT.PUSH);
		addFilterButton.setText("add Filter");
		addFilterButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem item = new TableItem(list, SWT.NONE);
				String name = "Filter_" + new Random().nextInt(100);
				item.setText(name);
				workspace.addFilter(name);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		final ScrolledComposite drawerScrollComposite = new ScrolledComposite(
				shell, SWT.V_SCROLL);
		drawerScrollComposite.setLayoutData(new RowData(100, 150));

		final Composite drawerComposite = new Composite(drawerScrollComposite,
				SWT.NONE);
		drawerComposite.setBackground(display
				.getSystemColor(SWT.COLOR_DARK_GRAY));
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.wrap = true;
		drawerComposite.setLayout(layout);

		drawerScrollComposite.setContent(drawerComposite);
		drawerScrollComposite.setExpandHorizontal(true);
		drawerScrollComposite.setExpandVertical(true);

		final Composite tableComposite = new Composite(shell, SWT.NONE);
		tableComposite.setLayoutData(new RowData(1000, 500));
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

				new ImageTile(tableComposite, display, (String) event.data);
			}
		});

		shell.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				fillTable(tableComposite, drawerComposite, display);

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		Database.closeConnection();
		display.dispose();
	}

	public static void fillTable(Composite tableComposite,
			Composite drawerComposite, Display display) {
		try {
			for (String current : workspace.getPhotos()) {
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
