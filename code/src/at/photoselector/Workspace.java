package at.photoselector;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Workspace {

	private Database db;
	private String currentFilter;

	public Workspace(String path) {
		db = new Database(path);
	}

	public void addPhoto(String path) {
		File location = new File(path);
		if (location.isDirectory()) {
			for (File current : location.listFiles())
				addPhoto(current.getAbsolutePath());
		}
		if (location.getName().matches(".*jpe?g$"))
			try {
				db.execute("INSERT INTO photos (path) VALUES ('"
						+ location.getAbsolutePath() + "')");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public List<String> getPhotos(List<String> list) throws SQLException {
		String sql = "SELECT PATH FROM PHOTOS";
		if (0 < list.size()) {
			sql += " WHERE PID NOT IN (SELECT PID FROM FILTERS_PHOTOS JOIN FILTERS ON FILTERS_PHOTOS.FID=FILTERS.FID WHERE";
			for (String current : list)
				sql += " FILTERS.NAME = '" + current + "' OR";
			sql = sql.substring(0, sql.length() - 3) + ")";
		}

		return db.getStringList(sql);
	}

	public void blacklist(String path) {
		try {
			int pid = db.getInteger("SELECT pid FROM photos WHERE path = '"
					+ path + "'");
			int fid = db.getInteger("SELECT fid FROM filters WHERE name = '"
					+ currentFilter + "'");
			db.execute("INSERT INTO filters_photos (fid, pid) VALUES (" + fid
					+ ", " + pid + ")");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setCurrentFilter(String text) {
		currentFilter = text;
	}

	public void addFilter(String name) {
		try {
			db.execute("INSERT INTO filters (name) VALUES ('" + name + "')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> getFilters() {
		try {
			return db.getStringList("SELECT name FROM filters");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}

}
