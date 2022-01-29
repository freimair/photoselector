package at.photoselector.tools;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import at.photoselector.Workspace;
import at.photoselector.model.Photo;
public class FileSystemExporter extends Exporter {

	@Override
	public void run() {
		for (Photo current : Photo.getAll()) {
			String folderName;
			if (null != current.getStage())
				folderName = current.getStage().getId() + "_"
						+ current.getStage().getName();
			else
				folderName = "final";
			File target = new File(Workspace.getLocation().getParent()
					+ File.separatorChar
					+ "export"
					+ File.separatorChar
					+ folderName
					+ File.separatorChar
					+ current.getPath().getName());
			try {
				target.mkdirs();
				Files.copy(current.getPath().toPath(), target.toPath(),
						REPLACE_EXISTING);
				if (new File(current.getPath() + ".pp3").exists())
					Files.copy(new File(current.getPath() + ".pp3").toPath(), new File(target + ".pp3").toPath(),
							REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
