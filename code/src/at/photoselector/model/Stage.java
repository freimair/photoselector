package at.photoselector.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Stage {

	// ################################ STATICS ################################

	private static Database database;
	private static Map<Integer, Stage> cache;
	private static int currentStage;

	public static void init(Database db) {
		cache = new HashMap<Integer, Stage>();
		database = db;
		if (0 == getAll().size())
			setCurrent(create("Stage 0"));
		else
			setCurrent(getAll().get(getAll().size() - 1));
	}

	public static Stage create(String name) {
		try {
			database.execute("INSERT INTO filters (name) VALUES ('" + name
					+ "')");

			int newId = database.getInteger("SELECT MAX(fid) FROM filters");
			cache.put(newId, new Stage(newId, name));
			return cache.get(newId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static List<Stage> getAll() {
		updateCache();
		ArrayList<Stage> result = new ArrayList<Stage>(cache.values());
		Collections.sort(result, new Comparator<Stage>() {
			@Override
			public int compare(Stage o1, Stage o2) {
				return Integer.valueOf(o1.getId()).compareTo(o2.getId());
			}
		});
		return result;
	}

	public static void setCurrent(Stage stage) {
		currentStage = stage.id;
	}

	public static Stage getCurrent() {
		updateCache();
		return cache.get(currentStage);
	}

	public static Stage get(int id) {
		updateCache();
		return cache.get(id);
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
