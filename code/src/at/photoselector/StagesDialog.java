package at.photoselector;

import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class StagesDialog extends UncloseableApplicationWindow {

	public StagesDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite filterComposite = new Composite(parent, SWT.BORDER);
		filterComposite.setLayout(new RowLayout(SWT.VERTICAL));
		Label filterListLabel = new Label(filterComposite, SWT.NONE);
		filterListLabel.setText("Stages");
		Table list = new Table(filterComposite, SWT.BORDER | SWT.V_SCROLL);
		list.setLayoutData(new RowData(100, 150));

		TableItem item = new TableItem(list, SWT.NONE);
		String name = "all";
		item.setText(name);

		// for (String current : workspace.getFilters()) {
		// item = new TableItem(list, SWT.NONE);
		// item.setText(current);
		// }

		ProgressBar bar = new ProgressBar(filterComposite, SWT.SMOOTH);
		try {
			bar.setMaximum(PhotoSelector.workspace.getPhotos(
					Workspace.UNPROCESSED | Workspace.ACCEPTED
							| Workspace.DECLINED).size());
			bar.setSelection(PhotoSelector.workspace.getPhotos(
					Workspace.ACCEPTED | Workspace.DECLINED).size());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return filterComposite;
	}
}
