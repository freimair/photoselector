package at.photoselector.tools;

import java.util.ArrayList;
import java.util.List;

public abstract class Exporter {

	public static List<Exporter> getAvailable() {
		List<Exporter> result = new ArrayList<Exporter>();
		result.add(new RawTherapeeLinuxExporter());
		result.add(new FileSystemExporter());
		result.add(new RawTherapeeExporter());

		return result;
	}

	public abstract void run();

	public String getName() {
		return getClass().getSimpleName();
	}
}
