package at.photoselector.model;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;

import at.photoselector.Workspace;

public class Photo {

	// ################################ STATICS ################################

	public final static int UNPROCESSED = 1 << 0;
	public final static int ACCEPTED = 1 << 1;
	public final static int DECLINED = 1 << 2;

	private static Database database;
	private static Map<Integer, Photo> cache;

	public static void init(Database db) {
		cache = new HashMap<Integer, Photo>();
		database = db;
	}

	public static void create(File path) {

		try {
			database.execute("INSERT INTO photos (path, status) VALUES ('"
				+ path.getAbsolutePath() + "', " + UNPROCESSED + ")");

			// add to cache
			int newId = database.getInteger("SELECT MAX(pid) FROM photos");
			cache.put(newId, new Photo(newId, path, UNPROCESSED));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<Photo> getFiltered(boolean stageless, int filter) {
		updateCache();

		try {
			String sql = "SELECT pid FROM photos";

			String stage = "";
			if (stageless)
				stage += "stage IS NULL";

			String status = "";
			if ((UNPROCESSED & filter) > 0)
				status += "status = " + UNPROCESSED + " OR ";
			if ((ACCEPTED & filter) > 0)
				status += "status = " + ACCEPTED + " OR ";
			if ((DECLINED & filter) > 0)
				status += "status = " + DECLINED + " OR ";

			if (0 < stage.length() || 0 < status.length())
				sql += " WHERE ";

			if (0 < status.length())
				sql += stage;

			if (0 < stage.length() && 0 < status.length())
				sql += " AND ";

			if (0 < status.length())
				sql += "(" + status.substring(0, status.length() - 4)
						+ ")";

			// TODO find better way
			List<Photo> result = new ArrayList<Photo>();
			for (int current : database.getIntegerList(sql))
				result.add(cache.get(current));

			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static Photo get(int id) {
		updateCache();
		return cache.get(id);
	}

	private static void updateCache() {
		try {
			List<Integer> allIds = database
					.getIntegerList("SELECT pid FROM photos");
			allIds.removeAll(cache.keySet());

			for (int currentId : allIds)
				cache.put(
						currentId,
						new Photo(
								currentId,
								new File(
										database.getString("SELECT path FROM photos WHERE pid = "
												+ currentId)),
								database.getInteger("SELECT status FROM photos WHERE pid = "
										+ currentId),
								Stage.get(database
										.getInteger("SELECT stage FROM photos WHERE pid = "
												+ currentId))));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ############################## NON-STATICS ##############################

	private int id;
	private File path;
	private int status;
	private Stage stage;
	private File cacheDir;
	private String delimiter;
	private int width;
	private int height;

	public Photo(int newId, File path, int status) {
		id = newId;
		this.path = path;
		this.status = status;

		delimiter = System.getProperty("file.separator");
		cacheDir = new File(Workspace.getLocation().getParent() + delimiter
				+ ".cache" + delimiter);
		if (!cacheDir.exists())
			cacheDir.mkdir();
	}

	public Photo(int currentId, File path, int status, Stage stage) {
		this(currentId, path, status);
		this.stage = stage;
	}

	private ImageData getCachedImage(int boundingBox) {
		// TODO find better way to get a suitable cache size
		int cachedSize = (int) (500 * Math.ceil((boundingBox - 100) / 500.0) + 100);
		File cachedImage = new File(cacheDir.getPath() + delimiter
				+ path.getName() + "." + cachedSize + ".jpg");

		ImageData scaled;
		if(!cachedImage.exists()) {
			// scale
			ImageData fullImage = new ImageData(getPath().getAbsolutePath());
			width = fullImage.width;
			height = fullImage.height;

			Rectangle scaledDimensions = scaleAndCenterImage(cachedSize);
			scaled = fullImage.scaledTo(scaledDimensions.width,
				scaledDimensions.height);
			// persist
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { scaled };
			imageLoader.save(cachedImage.getAbsolutePath(), SWT.IMAGE_JPEG);
		} else {
			scaled = new ImageData(cachedImage.getAbsolutePath());
			width = scaled.width;
			height = scaled.height;
		}

		return scaled;
	}

	public ImageData getImage(int boundingBox) {
		ImageData cachedImage = getCachedImage(boundingBox);
		Rectangle dimensions = scaleAndCenterImage(boundingBox);
		return cachedImage.scaledTo(dimensions.width,
			dimensions.height);
}

	public Rectangle scaleAndCenterImage(int boundingBox) {
		Rectangle result = new Rectangle(0, 0, boundingBox, boundingBox);

		// scale
		if (width > height) {
			result.height = (int) (1.0 * boundingBox / width * height);
			result.y = (int) (1.0 * ((boundingBox - result.height) / 2));
		} else {
			result.width = (int) (1.0 * boundingBox / height * width);
			result.x = (int) (1.0 * ((boundingBox - result.width) / 2));
		}

		return result;
	}

	public int getId() {
		return id;
	}

	public File getPath() {
		return path;
	}

	public Stage getStage() {
		return stage;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		try {
			database.execute("UPDATE photos SET status=" + status
					+ " WHERE pid=" + getId());
			this.status = status;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setStage(Stage stage) {
		try {
			database.execute("UPDATE photos SET stage=" + stage.getId()
					+ " WHERE pid=" + getId());
			this.stage = stage;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
