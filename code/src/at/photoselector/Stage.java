package at.photoselector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stage {

	// ################################ STATICS ################################

	private static Database database;
	private static Map<Integer, Stage> cache;

	public static void init(Database db) {
		cache = new HashMap<Integer, Stage>();
		database = db;
		if (0 == getAll().size())
			create("new Stage");
	}

	public static void create(String name) {
		try {
			database.execute("INSERT INTO filters (name) VALUES ('" + name
					+ "')");

			int newId = database.getInteger("SELECT MAX(fid) FROM filters");
			cache.put(newId, new Stage(newId, name));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<Stage> getAll() {
		updateCache();
		return new ArrayList<Stage>(cache.values());
	}

	public static Stage getCurrent() {
		updateCache();
		try {
			return cache.get(database
					.getInteger("SELECT MAX(fid) FROM filters"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static void updateCache() {
		try {
			List<Integer> allIds = database
					.getIntegerList("SELECT fid FROM filters");
			allIds.removeAll(cache.keySet());

		for (int currentId : allIds)
			cache.put(
					currentId,
					new Stage(currentId, database
							.getString("SELECT name FROM filters WHERE fid = "
									+ currentId)));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ############################## NON-STATICS ##############################

	private int id;
	private String name;

	public Stage(int currentId, String currentName) {
		id = currentId;
		name = currentName;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		try {
			database.execute("UPDATE filters SET name='" + name
					+ "' WHERE fid=" + getId());
			this.name = name;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
