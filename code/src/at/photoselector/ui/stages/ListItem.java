package at.photoselector.ui.stages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

import at.photoselector.model.Photo;
import at.photoselector.model.Stage;
import at.photoselector.ui.ControlsDialog;

class ListItem {
	private Stage stage;
	private ControlsDialog controlsdialog;

	public ListItem(Composite parent, Stage stage, boolean isLast,
			ControlsDialog controlsDialog) {
		this.stage = stage;
		this.controlsdialog = controlsDialog;

			createAdvancedListItem(parent);
	}

	private void createAdvancedListItem(Composite parent) {
		Group itemInProgressComposite = new Group(parent, SWT.NONE);
		itemInProgressComposite.setLayout(new FillLayout(SWT.VERTICAL));
		itemInProgressComposite.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		itemInProgressComposite.setText(stage.getName());
		itemInProgressComposite.setBackground(parent.getShell().getDisplay()
				.getSystemColor(SWT.COLOR_WHITE));
		final Text itemInProgressText = new Text(itemInProgressComposite,
				SWT.BORDER);
		itemInProgressText.setText(stage.getName());
		itemInProgressText.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				stage.setName(itemInProgressText.getText());
			}
		});

		// update progress bars

		int max = Photo.getFiltered(stage, true,
				Photo.UNPROCESSED | Photo.ACCEPTED | Photo.DECLINED).size();

		Label label = new Label(itemInProgressComposite, SWT.NONE);
		label.setText("done: ");
		if (0 == max) {
			label = new Label(itemInProgressComposite, SWT.NONE);
			label.setText("stage completed");
		} else {
			ProgressBar todoBar = new ProgressBar(itemInProgressComposite,
					SWT.SMOOTH);

			// - maximum
			todoBar.setMaximum(max);

			// - current value
			todoBar.setSelection(max
					- Photo.getFiltered(stage, Photo.UNPROCESSED).size());

			label = new Label(itemInProgressComposite, SWT.NONE);
			label.setText(todoBar.getSelection() + "/" + todoBar.getMaximum());
		}

		label = new Label(itemInProgressComposite, SWT.NONE);
		label.setText("accepted: ");
		ProgressBar madeItBar = new ProgressBar(itemInProgressComposite, SWT.SMOOTH);

		// - maximum
		madeItBar.setMaximum(max);

		// - current value
		madeItBar.setSelection(madeItBar.getMaximum()
				- Photo.getFiltered(stage, Photo.DECLINED).size());

		final Menu exportMenu = new Menu(parent.getShell(), SWT.POP_UP);
		MenuItem menuItem = new MenuItem(exportMenu, SWT.NONE);
		menuItem.setText("Drawer");
		menuItem.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Stage.setCurrent(stage);
				controlsdialog.launchDrawer(false);
			}
		});
		menuItem = new MenuItem(exportMenu, SWT.NONE);
		menuItem.setText("Filmstrip");
		menuItem.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Stage.setCurrent(stage);
				controlsdialog.launchFilmtrip();
			}
		});
		menuItem = new MenuItem(exportMenu, SWT.NONE);
		menuItem.setText("Drawer/Table");
		menuItem.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Stage.setCurrent(stage);
				controlsdialog.launchDrawer(true);
			}
		});

		Button editButton = new Button(itemInProgressComposite, SWT.PUSH);
		editButton.setText("edit");
		editButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle rect = ((Control) e.widget).getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = ((Control) e.widget).getParent().toDisplay(pt);
				exportMenu.setLocation(pt.x, pt.y);
				exportMenu.setVisible(true);
			}
		});
	}
}
