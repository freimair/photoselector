package at.photoselector.tools;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

import at.photoselector.model.Photo;
import at.photoselector.model.Stage;

public class RawTherapeeLinuxExporter extends Exporter {

	/*
	 * copied form RawTherapee source
	 * 
	 * for windows: Glib::ustring fileID= Glib::ustring::compose ("%1-%2-%3-%4",
	 * fileAttr.nFileSizeLow, fileAttr.ftCreationTime.dwHighDateTime,
	 * fileAttr.ftCreationTime.dwLowDateTime, fname );
	 * 
	 * return Glib::Checksum::compute_checksum (Glib::Checksum::CHECKSUM_MD5,
	 * fileID);
	 * 
	 * else:
	 * 
	 * return Glib::Checksum::compute_checksum (Glib::Checksum::CHECKSUM_MD5,
	 * Glib::ustring::compose ("%1%2", fname, info->get_size()));
	 */
	/*
	 * Glib::ustring cfn = Glib::build_filename (baseDir, subdir); Glib::ustring
	 * cname = Glib::path_get_basename (fname) + "." + md5; return
	 * Glib::build_filename (cfn, cname);
	 */

	public void run() {
		// retrieve relative rank
		List<Stage> stages = Stage.getAll();
		int offset = 0;
		if (5 <= stages.size())
			offset = 5 - stages.size();

		// get files
		List<Photo> photos = Photo.getAll();
		// for each
		for (Photo current : photos) {
			// - retrieve cache file name with path
			File cachedFile = getCacheFileName(current.getPath());

			try {
				Wini properties;
				if (!cachedFile.exists()) {
					// FIXME does not work yet.
					/*
					 * open rawtherapee - browse to the photos and let
					 * rawtherapee create the cache data file - navigate to
					 * another folder - export - navigate back and there are
					 * your ratings
					 */
					cachedFile.createNewFile();

					properties = new Wini(cachedFile);
					properties.put("General", "MD5", getMD5Sum(current
							.getPath().getAbsolutePath()
							+ current.getPath().length()));
				} else
					properties = new Wini(cachedFile);

				// - insert rank into cache file
				/*
				 * [General] MD5=d1a83c7d1132006671796b089c841cc3 Version=3.0.0
				 * Supported=true Format=2 Rank=1 InTrash=false
				 * RecentlySaved=false
				 * 
				 * [ExifInfo] Valid=false Lens=Unknown Camera=Unknown
				 * 
				 * [FileInfo] Filetype=jpg
				 */
				Stage currentStage = current.getStage();
				int rank;
				boolean trash = false;
				if(null == currentStage)
					rank = stages.size() + offset; // there is no stage set
													// means this is what was
													// left after selection ->
													// highest rank
				else {
					rank = stages.indexOf(currentStage) + 1 + offset;
					if (rank <= 0) {
						rank = 0;
						trash = true;
					}
				}
				properties.put("General", "Rank", rank);
				properties.put("General", "InTrash", trash ? "true" : "false");
				properties.store();

			} catch (InvalidFileFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private File getCacheFileName(File file) {
		return new File("/home/me/.cache/RawTherapee/data"
				+ System.getProperty("file.separator")
				+ file.getName() + "."
				+ getMD5Sum(file.getAbsolutePath() + file.length()) + ".txt");
	}

	private String getMD5Sum(String input) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] md5hash = new byte[32];
			md5hash = md.digest(input.getBytes());
			return convertToHex(md5hash);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}
}
