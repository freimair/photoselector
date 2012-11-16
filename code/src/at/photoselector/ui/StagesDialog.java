package at.photoselector.ui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import at.photoselector.Stage;

public class StagesDialog extends UncloseableApplicationWindow {

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

	public void update() {
		// update list
		// - clear list
		for (Control current : stageListComposite.getChildren())
			current.dispose();

		// - refill from database
		// -- first element reflecting the basic set without any filters

		// -- add filter stages
		List<Stage> stages = Stage.getAll();
		for (int i = 0; i < stages.size(); i++) {
			new ListItem(stageListComposite, stages.get(i),
					i == stages.size() - 1);
		}

		stageListComposite.layout();
	}
}
