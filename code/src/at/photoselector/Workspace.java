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

	public List<String> getPhotos() throws SQLException {
		return db.getStringList("SELECT path FROM photos");
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
