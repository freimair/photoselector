package at.photoselector;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class Workspace {

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
				db.execute("INSERT INTO photos (path) VALUES ('"
						+ location.getAbsolutePath() + "')");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public List<String> getPhotos() throws SQLException {
		return db.getList("SELECT path FROM photos");
	}

}
