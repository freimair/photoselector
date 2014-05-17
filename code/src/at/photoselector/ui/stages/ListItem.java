package at.photoselector.ui.stages;

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

import at.photoselector.model.Photo;
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

		// - maximum
		bar.setMaximum(Photo.getFiltered(true,
				Photo.UNPROCESSED | Photo.ACCEPTED | Photo.DECLINED).size());

		// - current value
		bar.setSelection(Photo.getFiltered(true,
				Photo.ACCEPTED | Photo.DECLINED).size());

		Label label = new Label(itemInProgressComposite, SWT.NONE);
		label.setText(bar.getSelection() + "/" + bar.getMaximum());
	}

	private void createSimpleListItem(Composite parent) {
		Label item = new Label(parent, SWT.NONE);
		item.setText(stage.getName());
		item.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		item.setBackground(parent.getShell().getDisplay()
				.getSystemColor(SWT.COLOR_WHITE));
	}
}
