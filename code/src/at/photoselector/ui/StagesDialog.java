package at.photoselector.ui;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import at.photoselector.Stage;
import at.photoselector.Workspace;

public class StagesDialog extends UncloseableApplicationWindow {

	private ProgressBar bar;
	private Composite stageListComposite;

	public StagesDialog(Shell parentShell, ControlsDialog dialog) {
		super(parentShell, dialog);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText("Stages");
	}

	@Override
	protected Control createContents(Composite parent) {
		ScrolledComposite scrollableStageListComposite = new ScrolledComposite(
				parent, SWT.BORDER);
		scrollableStageListComposite.setLayout(new FillLayout());
		scrollableStageListComposite.setExpandHorizontal(true);
		scrollableStageListComposite.setExpandVertical(true);

		stageListComposite = new Composite(scrollableStageListComposite,
				SWT.NONE);
		stageListComposite.setBackground(getShell().getDisplay()
				.getSystemColor(SWT.COLOR_WHITE));
		stageListComposite.setLayout(new GridLayout());

		scrollableStageListComposite.setContent(stageListComposite);

		update();

		return stageListComposite;
	}

	private void addSimpleListItem(String text) {
		Label item = new Label(stageListComposite, SWT.NONE);
		item.setText(text);
		item.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		item.setBackground(getShell().getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
	}

	public void update() {
		// update list
		// - clear list
		for (Control current : stageListComposite.getChildren())
			current.dispose();

		// - refill from database
		// -- first element reflecting the basic set without any filters
		addSimpleListItem("all");

		// -- add filter stages
		List<Stage> stages = Stage.getAll();
		for (int i = 0; i < stages.size() - 1; i++) {
			addSimpleListItem(stages.get(i).getName());
		}

		// -- stage in progress is displayed with text and progressbar
		Group itemInProgressComposite = new Group(stageListComposite, SWT.NONE);
		itemInProgressComposite.setLayout(new FillLayout(SWT.VERTICAL));
		itemInProgressComposite.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		itemInProgressComposite.setText("next Stage");
		itemInProgressComposite.setBackground(getShell().getDisplay()
				.getSystemColor(SWT.COLOR_WHITE));
		Text itemInProgressText = new Text(itemInProgressComposite, SWT.BORDER);
		itemInProgressText.setText(Stage.getCurrent().getName());

		bar = new ProgressBar(itemInProgressComposite, SWT.SMOOTH);

		// update progress bar
		try {
			// - maximum
			bar.setMaximum(Workspace.getPhotos(
					Workspace.UNPROCESSED | Workspace.ACCEPTED
							| Workspace.DECLINED).size());

			// - current value
			bar.setSelection(Workspace.getPhotos(
					Workspace.ACCEPTED | Workspace.DECLINED).size());

			itemInProgressComposite.getParent().layout();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
