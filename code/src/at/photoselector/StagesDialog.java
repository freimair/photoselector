package at.photoselector;

import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class StagesDialog extends UncloseableApplicationWindow {

	private Table list;
	private ProgressBar bar;

	public StagesDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText("Stages");
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite filterComposite = new Composite(parent, SWT.BORDER);
		filterComposite.setLayout(new RowLayout(SWT.VERTICAL));
		list = new Table(filterComposite, SWT.BORDER | SWT.V_SCROLL);
		list.setLayoutData(new RowData(100, 150));

		bar = new ProgressBar(filterComposite, SWT.SMOOTH);
		try {
			bar.setMaximum(Workspace.getPhotos(
					Workspace.UNPROCESSED | Workspace.ACCEPTED
							| Workspace.DECLINED).size());
			bar.setSelection(Workspace.getPhotos(
					Workspace.ACCEPTED | Workspace.DECLINED).size());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		update();

		return filterComposite;
	}

	public void update() {
		// update list
		// - clear list
		list.clearAll();
		for (TableItem current : list.getItems())
			current.dispose();

		// - refill from database
		TableItem item = new TableItem(list, SWT.NONE);
		String name = "all";
		item.setText(name);
		for (String current : Workspace.getFilters()) {
			item = new TableItem(list, SWT.NONE);
			item.setText(current);
		}

		// update progress bar
		try {
			// - maximum
			bar.setMaximum(Workspace.getPhotos(
					Workspace.UNPROCESSED | Workspace.ACCEPTED
							| Workspace.DECLINED).size());

			// - current value
			bar.setSelection(Workspace.getPhotos(
					Workspace.ACCEPTED | Workspace.DECLINED).size());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
