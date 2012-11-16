package at.photoselector.ui.stages;

import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

import at.photoselector.Workspace;
import at.photoselector.model.Stage;

class ListItem {
	private Stage stage;

	public ListItem(Composite parent, Stage stage, boolean isLast) {
		this.stage = stage;

		if (isLast)
			createAdvancedListItem(parent);
		else
			createSimpleListItem(parent);
	}

	private void createAdvancedListItem(Composite parent) {
		Group itemInProgressComposite = new Group(parent, SWT.NONE);
		itemInProgressComposite.setLayout(new FillLayout(SWT.VERTICAL));
		itemInProgressComposite.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		itemInProgressComposite.setText("new Stage");
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

		// update progress bar
		ProgressBar bar = new ProgressBar(itemInProgressComposite, SWT.SMOOTH);
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

	private void createSimpleListItem(Composite parent) {
		Label item = new Label(parent, SWT.NONE);
		item.setText(stage.getName());
		item.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		item.setBackground(parent.getShell().getDisplay()
				.getSystemColor(SWT.COLOR_WHITE));
	}
}
