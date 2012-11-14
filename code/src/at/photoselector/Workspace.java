package at.photoselector;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Workspace {

	public final static int UNPROCESSED = 0;
	public final static int ACCEPTED = 1;
	public final static int DECLINED = 2;

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
				db.execute("INSERT INTO photos (path, status) VALUES ('"
						+ location.getAbsolutePath() + "', " + UNPROCESSED
						+ ")");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public List<String> getPhotos(int filter) throws SQLException {
		String sql = "SELECT path FROM photos WHERE stage IS NULL AND status = "
				+ UNPROCESSED;
		if ((ACCEPTED & filter) > 0)
			sql += " OR status = " + ACCEPTED;
		if ((DECLINED & filter) > 0)
			sql += " OR status = " + DECLINED;
		return db.getStringList(sql);
	}

	public void blacklist(String path, String filter) {
		// try {
		// int pid = db.getInteger("SELECT pid FROM photos WHERE path = '"
		// + path + "'");
		// int fid = db.getInteger("SELECT fid FROM filters WHERE name = '"
		// + filter + "'");
		// db.execute("INSERT INTO filters_photos (fid, pid) VALUES (" + fid
		// + ", " + pid + ")");
		// } catch (SQLException e) {
		// // TODO AuSWT.CHECK | to-generated catch block
		// e.printStackTrace();
		// }
	}

	public void accept(String path) {
		try {
			db.execute("UPDATE photos SET status=" + ACCEPTED + " WHERE path='"
					+ path + "'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void blacklist(String path) {
		try {
			db.execute("UPDATE photos SET status=" + DECLINED + " WHERE path='"
					+ path + "'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// try {
		// blacklist(
		// path,
		// db.getString("SELECT name FROM filters WHERE fid = (SELECT MAX(fid) FROM filters)"));
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
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
