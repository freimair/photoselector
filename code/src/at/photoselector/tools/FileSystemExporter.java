package at.photoselector.tools;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import at.photoselector.model.Photo;
public class FileSystemExporter extends Exporter {

	@Override
	public void run() {
		for(Photo current : Photo.getFiltered(false, 0)) {
			String folderName;
			if (null != current.getStage())
				folderName = current.getStage().getId() + "_"
						+ current.getStage().getName();
			else
				folderName = "final";
			File target = new File(current.getPath().getParentFile()
					.getAbsolutePath()
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
