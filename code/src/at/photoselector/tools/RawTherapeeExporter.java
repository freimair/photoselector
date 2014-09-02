package at.photoselector.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

import at.photoselector.model.Photo;
import at.photoselector.model.Stage;

public class RawTherapeeExporter extends Exporter {

	public void run() {
		// retrieve relative rank
		List<Stage> stages = Stage.getAll();
		int offset = 0;
		if (5 <= stages.size())
			offset = 5 - stages.size();

		// get files
		List<Photo> photos = Photo.getAll();
		// for each
		for (Photo current : photos) {
			// - create descriptor filename
			File cachedFile = new File(current.getPath().getAbsolutePath()
					+ ".pp3");

			try {
				Wini properties;
				if (!cachedFile.exists())
					cachedFile.createNewFile();
				properties = new Wini(cachedFile);

				// - insert rank into cache file
				Stage currentStage = current.getStage();
				int rank;
				boolean trash = false;
				if(null == currentStage)
					rank = stages.size() + offset; // there is no stage set
													// means this is what was
													// left after selection ->
													// highest rank
				else {
					rank = stages.indexOf(currentStage) + 1 + offset;
					if (rank <= 0) {
						rank = 0;
						trash = true;
					}
				}
				properties.put("General", "Rank", rank);
				properties.put("General", "InTrash", trash ? "true" : "false");
				properties.store();

			} catch (InvalidFileFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
