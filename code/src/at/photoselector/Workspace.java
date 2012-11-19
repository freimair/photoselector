package at.photoselector;

import java.io.File;

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
			for (File current : location.listFiles())
				addPhoto(current.getAbsolutePath());
		}
		if (location.getName().toLowerCase().matches(".*(\\.jpe?g|\\.cr2)$"))
			Photo.create(location);
	}

	public static boolean accept(Photo photo) {
		photo.setStatus(Photo.ACCEPTED);

		return isStageCompleted();
	}

	public static boolean decline(Photo photo) {
		photo.setStatus(Photo.DECLINED);

		return isStageCompleted();
	}

	public static void reset(Photo photo) {
		photo.setStatus(Photo.UNPROCESSED);
	}

	public static boolean isStageCompleted() {
		if (0 == Photo.getFiltered(true, Photo.UNPROCESSED).size()) {
			stageCompleted();
			return true;
		}
		return false;
	}

	public static void stageCompleted() {
		for (Photo current : Photo.getFiltered(true, Photo.DECLINED)) {
			current.setStage(Stage.getCurrent());

			// clear cached photos
			current.clearCachedImages();
		}

		for (Photo current : Photo.getFiltered(true, Photo.ACCEPTED))
			current.setStatus(Photo.UNPROCESSED);

		Stage.create("Stage " + Stage.getAll().size());
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
}
