package utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import models.ImageType;
import models.ImageType.ImageEffect;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;

import com.mchange.v1.io.InputStreamUtils;

public class GenThumbnails {
	private final static String BASEURL = Play.applicationPath.toString()
			+ "/conf/shells/";

	public static File thumbnail(File src, String size, ImageEffect effect) {
		try {
			Process exec = Runtime.getRuntime().exec(
					BASEURL + effect.getCMD() + " " + size + " "
							+ src.getName(), null, src.getParentFile());
			exec.waitFor();
		} catch (IOException e) {
			Logger.error(e, e.getMessage());
		} catch (InterruptedException e) {
			Logger.error(e, e.getMessage());
		}
		return new File(src.toString());
	}

	public static File thumbnail(File src, String size) {
		ImageType imageType = ImageType.getInfoFromSizeStr(size);
		return thumbnail(src, imageType.getSize(), imageType.imageEffect);
	}

	public static Map<String, String> getPicInfo(File src) {
		try {
			Process exec = Runtime.getRuntime().exec(
					BASEURL + "picInfo.sh " + src.getName(), null,
					src.getParentFile());
			String[] output = StringUtils.split(
					StringUtils.trim(InputStreamUtils.getContentsAsString(
							exec.getInputStream(), "utf-8")), " ");
			exec.waitFor();
			System.err.println(src.getName() + ", " + src.getParent() + ", w="
					+ output[0] + ", h=" + output[1]);

			Map<String, String> result = new HashMap<String, String>();
			result.put("width", output[0]);
			result.put("height", output[1]);
			return result;
		} catch (IOException e) {
			Logger.error(e, e.getMessage());
		} catch (InterruptedException e) {
			Logger.error(e, e.getMessage());
		}
		return null;
	}
}
