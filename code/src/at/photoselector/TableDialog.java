package at.photoselector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class TableDialog extends MyApplicationWindow {

	public TableDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText("Table");
	}

	@Override
	protected Control createContents(Composite parent) {
		final Composite tableComposite = new Composite(parent, SWT.NONE);
		tableComposite.setLayoutData(new RowData(500, 500));
		tableComposite.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_GRAY));

		DropTarget target = new DropTarget(tableComposite, DND.DROP_MOVE
				| DND.DROP_COPY | DND.DROP_LINK);
		target.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		target.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}

				// new ImageTile(tableComposite, parent.getDisplay(), (String)
				// event.data,
				// event.x, event.y);
			}
		});
		return tableComposite;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}
}
