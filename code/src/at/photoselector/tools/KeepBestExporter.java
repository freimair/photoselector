package at.photoselector.tools;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import at.photoselector.Workspace;
import at.photoselector.model.Photo;
import at.photoselector.model.Stage;
public class KeepBestExporter extends Exporter {

	@Override
	public void run() {

		for (Photo current : Photo.getAll()) {
			String folderName;

			// FIXME need to do this because getAll tampers with the Photo's stage and
			// status. no idea why
			current = Photo.get(current.getId());

			if (Stage.getAll().get(Stage.getAll().size() - 1).equals(current.getStage()))
				if (Photo.DECLINED != current.getStatus())
					continue;

			// prepare paths
			if (null != current.getStage())
				// if not final stage
				folderName = current.getStage().getId() + "_"
						+ current.getStage().getName();
			else
				// if final stage
				continue;


			// prepare the filename
			File target = new File(Workspace.getLocation().getParent()
					+ File.separatorChar
					+ "removed"
					+ File.separatorChar
					+ folderName
					+ File.separatorChar
					+ current.getPath().getName());

			// do the copying
			try {
				target.mkdirs();
				Files.move(current.getPath().toPath(), target.toPath(),
						REPLACE_EXISTING);
				if (new File(current.getPath() + ".pp3").exists())
				Files.move(new File(current.getPath() + ".pp3").toPath(),
						new File(target + ".pp3").toPath(),
						REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
