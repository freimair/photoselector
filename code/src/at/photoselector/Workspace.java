package at.photoselector;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.photoselector.model.Database;
import at.photoselector.model.Photo;
import at.photoselector.model.Stage;

public class Workspace {

	//################################ STATICS ################################


	private static File location;

	public static void open(String path) {
		new Workspace(path);
		location = new File(path);
	}

	public static File getLocation() {
		return location;
	}

	public static void addPhoto(String path) {
		File location = new File(path);
		if (location.isDirectory()) {
			// find the list of raw files available in the directory
			File[] rawRaws = location.listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						return !name.toLowerCase().matches(".*\\.jpe?g$");
					}
				});
			List<String> raws = new ArrayList<String>();
			for (File current : rawRaws)
				raws.add(current.getAbsolutePath().substring(0,
						current.getAbsolutePath().lastIndexOf(".")));

			// find all files
			File[] files = location.listFiles();
			Arrays.sort(files);
			for (File current : files)
				// if we do not use jpgs that reside besides their raw for the
				// fast image cache later ||
				// if the current file is no jpg anyways ||
				// if there is no raw for this very jpg
				// we add the file to the database
				if (!Settings.isTryFindingJpgBesideRaw()
						|| !current.getName().toLowerCase() 
								.matches(".*\\.jpe?g$")
						|| !raws.contains(current.getAbsolutePath().substring(
								0, current.getAbsolutePath().lastIndexOf("."))))
					addPhoto(current.getAbsolutePath());
		}
		if (location.getName().toLowerCase().matches(".*(\\.jpe?g|\\.cr2)$"))
			Photo.create(location);
	}

	public static boolean accept(Photo photo) {
		photo.setStatus(Photo.ACCEPTED);

		lastTreated = photo;

		photo.setStage(getNextStage());
		photo.setStatus(Photo.UNPROCESSED);

		return isStageCompleted();
	}

	private static Stage getNextStage() {
		if (Stage.getAll().indexOf(Stage.getCurrent()) == Stage.getAll().size() - 1)
			// the current stage was the highest stage -> create a new stage
			return Stage.create("Stage " + Stage.getAll().size());
		else
			// our target stage already exists.
			return Stage.getAll().get(
					Stage.getAll().indexOf(Stage.getCurrent()) + 1);
	}

	public static boolean decline(Photo photo) {
		photo.setStatus(Photo.DECLINED);
		photo.clearCachedImages();

		lastTreated = photo;

		return isStageCompleted();
	}

	public static void reset(Photo photo) {
		photo.setStatus(Photo.UNPROCESSED);
		if (!photo.getStage().equals(Stage.getCurrent()))
			photo.setStage(Stage.getCurrent());
		photo.setStatus(Photo.UNPROCESSED);
	}

	public static boolean isStageCompleted() {
		if (0 == Photo.getFiltered(Stage.getCurrent(), Photo.UNPROCESSED)
				.size()) {
			stageCompleted();
			return true;
		}
		return false;
	}

	public static void stageCompleted() {
		for (Photo current : Photo.getFiltered(Stage.getCurrent(),
				Photo.DECLINED)) {
			// clear cached photos
			current.clearCachedImages();
		}

		Stage.setCurrent(getNextStage());

		lastTreated = null;

	}

	// ################################ NON-STATICS ################################

	private Database db;

	/**
	 * Instantiates a new workspace. I. a. create a connection to the new
	 * database after closing the old one.
	 * 
	 * @param path
	 *            the absolut path to the database file
	 */
	private Workspace(String path) {
		Database.closeConnection();
		db = new Database(path);
		Stage.init(db);
		Photo.init(db);
	}

	static private Photo lastTreated = null;

	public static Photo getLastTreated() {
		return lastTreated;
	}
}
