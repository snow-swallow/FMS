package utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import play.Play;

/**
 * @author MaYan(mayan31370@126.com)
 * 
 */
public final class LocalFileStore {
	private static final String BASE_PIC_DIR = Play.configuration.getProperty(
			"application.baseDIR", "/developworks/fms/");
	private static LocalFileStore instance = new LocalFileStore();

	private LocalFileStore() {
	}

	public static LocalFileStore getInstance() {
		return instance;
	}

	public void upload(String bucketName, String fileName, File src)
			throws IOException {
		File saveFile = new File(BASE_PIC_DIR + bucketName, fileName);
		FileUtils.copyFile(src, saveFile);
		System.out.println("---uploaded---");
	}

	public String getUrl(String bucketName, String fileName) {
		return Play.configuration.getProperty("application.baseUrl") + "pic/"
				+ bucketName + "/" + fileName;
	}

	public String getPath(String bucketName, String fileName) {
		return Play.configuration.getProperty("application.baseDIR") + "/"
				+ bucketName + "/" + fileName;
	}
}
