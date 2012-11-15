package at.photoselector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class Settings {

	private static Properties properties;

	private static final String propertiesPath = System
			.getProperty("user.home") + "/.photoselector";
	private static File file;

	public static final String controlsDialog = "controlsDialog";
	public static final String stagesDialog = "stagesDialog";
	public static final String drawerDialog = "drawerDialog";
	public static final String tableDialog = "tableDialog";

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

	public static List<String> getRecent() {
		return new ArrayList<String>();
	}

	public static void memorizeWindowPosition(String control, Point location,
			Point bounds) {
		properties.setProperty(control, location.x + "," + location.y + ","
				+ bounds.x + "," + bounds.y);
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
