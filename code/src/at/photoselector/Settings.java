package at.photoselector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.Rectangle;

public class Settings {

	private static final Preferences preferences = Preferences
			.userNodeForPackage(PhotoSelector.class);

	private static final String recent = "recent";
	private static final String recentCount = "recentCount";

	private static final String dcrawlocation = "dcrawbinary";
	private static final String imagemagicklocation = "imagemagickbinary";

	public static void setRecent(String path) {
		if (getRecent().contains(path)) {
			// exchange
			preferences.put(recent + (getRecent().indexOf(path) + 1),
					preferences.get(recent + "1", ""));
			preferences.put(recent + "1", path);
		} else {
			// add new
			for (int i = Integer.valueOf(preferences.get(recentCount, "5")); i > 1; i--)
				preferences.put(recent + i, preferences.get(recent + (i - 1), ""));

			preferences.put(recent + "1", path);
		}
	}

	public static List<String> getRecent() {
		List<String> result = new ArrayList<String>();
		for (int i = 1; i <= Integer.valueOf(preferences.get(recentCount, "5")); i++) {
			String tmp = preferences.get(recent + i, "");
			if (!"".equals(tmp))
				result.add(tmp);
		}
		return result;
	}

	public static void memorizeWindowPosition(String control, Rectangle bounds) {
		preferences.putByteArray(control, object2Bytes(bounds));
	}

	public static Rectangle rememberWindowPosition(String control) {
		return (Rectangle) bytes2Object(preferences.getByteArray(control,
				object2Bytes(new Rectangle(0, 0, 200, 200))));
	}

	public static String getDCRawLocation() {
		return preferences.get(dcrawlocation, "dcraw");
	}

	public static void setDCRawLocation(String text) {
		preferences.put(dcrawlocation, text);
	}

	public static String getImageMagicBinaryLocation() {
		return preferences.get(imagemagicklocation, "convert");
	}

	public static void setImageMagicBinaryLocation(String text) {
		preferences.put(imagemagicklocation, text);
	}

	static private byte[] object2Bytes(Object o) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			return baos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	static private Object bytes2Object(byte raw[]) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(raw);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object o = ois.readObject();
			return o;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
