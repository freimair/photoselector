package at.photoselector;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Workspace {

	public final static int UNPROCESSED = 1;
	public final static int ACCEPTED = 2;
	public final static int DECLINED = 4;

	private Database db;

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
		String sql = "SELECT path FROM photos WHERE stage IS NULL";
		String tmp = "";
		if ((UNPROCESSED & filter) > 0)
			tmp += "status = " + UNPROCESSED + " OR ";
		if ((ACCEPTED & filter) > 0)
			tmp += "status = " + ACCEPTED + " OR ";
		if ((DECLINED & filter) > 0)
			tmp += "status = " + DECLINED + " OR ";
		if (0 < tmp.length())
			sql += " AND (" + tmp.substring(0, tmp.length() - 4) + ")";

		return db.getStringList(sql);
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

	public void decline(String path) {
		try {
			db.execute("UPDATE photos SET status=" + DECLINED + " WHERE path='"
					+ path + "'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stageCompleted() {
		try {
			String newFilterName = String.valueOf((new Random()).nextInt(100));

			db.execute("INSERT INTO filters (name) VALUES ('" + newFilterName
					+ "')");
			int fid = db.getInteger("SELECT fid FROM filters WHERE name = '"
					+ newFilterName + "'");

			db.execute("UPDATE photos SET stage=" + fid + " WHERE status="
					+ DECLINED);
			db.execute("UPDATE photos SET status=" + UNPROCESSED
					+ " WHERE status=" + ACCEPTED);
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
