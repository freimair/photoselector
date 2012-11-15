package at.photoselector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.graphics.Rectangle;

public class Settings {

	private static Properties properties;

	private static final String propertiesPath = System
			.getProperty("user.home") + "/.photoselector";
	private static File file;

	private static final String recent = "recent";
	private static final String recentCount = "recentCount";

	public static void load() {
		properties = new Properties();
		try {
			file = new File(propertiesPath);
			System.out.println(file.getAbsolutePath());
			if (!file.exists())
				file.createNewFile();
			properties.load(new FileInputStream(propertiesPath));
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setRecent(String path) {
		if (getRecent().contains(path)) {
			// exchange
			properties.setProperty(recent + (getRecent().indexOf(path) + 1),
					properties.getProperty(recent + "1"));
			properties.setProperty(recent + "1", path);
		} else {
			// add new
			for (int i = Integer.valueOf(properties.getProperty(recentCount,
					"5")); i > 1; i--)
				properties.setProperty(recent + i,
						properties.getProperty(recent + (i - 1), ""));

			properties.setProperty(recent + "1", path);
		}

		// persist
		save();
	}

	public static List<String> getRecent() {
		List<String> result = new ArrayList<String>();
		for (int i = 1; i <= Integer.valueOf(properties.getProperty(
				recentCount, "5")); i++) {
			String tmp = properties.getProperty(recent + i, "");
			if (!"".equals(tmp))
				result.add(tmp);
		}
		return result;
	}

	public static void memorizeWindowPosition(String control, Rectangle bounds) {
		properties.setProperty(control, bounds.x + "," + bounds.y + ","
				+ bounds.width + "," + bounds.height);

		save();
	}

	private static void save() {
		try {
			properties.store(new FileOutputStream(propertiesPath), "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public void setControlsDialogPosition(Rectangle rectangle) {
	// memorizeWindowPosition(controlsDialog, rectangle);
	// }

	public static Rectangle rememberWindowPosition(String control) {
		String[] tmp = properties.getProperty(control, "0,0,200,200")
				.split(",");
		return new Rectangle(Integer.valueOf(tmp[0].trim()),
				Integer.valueOf(tmp[1].trim()), Integer.valueOf(tmp[2].trim()),
				Integer.valueOf(tmp[3].trim()));
	}

	// public Rectangle getControlsDialogPosition() {
	// return rememberWindowPosition(controlsDialog);
	// }
}
