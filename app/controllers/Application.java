package controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import models.ImageFull;
import models.Result;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;

import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Http.Header;
import play.templates.JavaExtensions;
import utils.CommonUtils;
import utils.GenThumbnails;
import utils.ImageQueue;
import utils.ImageQueue.ImageVO;
import utils.LocalFileStore;

import com.google.gson.JsonObject;

public class Application extends Controller {
	public static final int MAX_SIZE = 1650;

	public static void index() {
		Logger.error("----here---");
		for (Map.Entry<String, Header> e : request.headers.entrySet()) {
			System.err.println(e.getKey() + ":" + e.getValue());
		}
		renderText("The server is running now!");
	}

	public static void getThumbnailUrl(String imageName, String imageSize,
			String bucketName) {
		int width, height;
		ImageFull imageFull = ImageFull.findImage(imageName, imageSize,
				bucketName);
		ImageVO imageVO = ImageQueue.getInstance().get(imageName, imageSize,
				bucketName);
		if (imageFull != null) {
			width = imageFull.width;
			height = imageFull.height;
		} else if (imageVO == null) {
			Map<String, String> imageInfo = createThumbnailImage(imageName,
					imageSize, bucketName);
			width = Integer.valueOf(imageInfo.get("width"));
			height = Integer.valueOf(imageInfo.get("height"));
		} else {
			width = imageVO.width;
			height = imageVO.height;
		}
		ImageQueue.getInstance().add(imageName, imageSize, bucketName, width,
				height);
		String url = LocalFileStore.getInstance().getUrl(bucketName,
				CommonUtils.getKey(imageName, imageSize));
		System.err.println("url=" + url);
		redirect(url);
	}

	public static void getImageInfo(String imageName, String imageSize,
			String bucketName) {
		int width, height;
		ImageFull imageFull = ImageFull.findImage(imageName, imageSize,
				bucketName);
		ImageVO imageVO = ImageQueue.getInstance().get(imageName, imageSize,
				bucketName);
		if (imageFull != null) {
			width = imageFull.width;
			height = imageFull.height;
		} else if (imageVO != null) {
			width = imageVO.width;
			height = imageVO.height;
		} else {
			width = 0;
			height = 0;
		}
		JsonObject result = new JsonObject();
		result.addProperty("width", width);
		result.addProperty("height", height);
		renderJSON(result.toString());
	}

	public static void uploadOriginFileByForm(File qqfile, String bucketName,
			String source) throws IOException {
		if (qqfile == null) {
			Logger.error(
					"form upload failed[qqfile:%s,bucketName:%s,source:%s]",
					qqfile, bucketName, source);
		} else {
			String originName = qqfile.getName();
			File dest = new File("tmp/"
					+ RandomStringUtils.random(5, true, true) + "/"
					+ RandomStringUtils.random(5, true, true));
			FileUtils.moveFile(qqfile, dest);
			uploadFile(dest, originName, bucketName, source);
		}
	}

	public static void uploadOriginFile(String qqfile, String bucketName,
			String source) {
		try {
			File image = new File("tmp/"
					+ RandomStringUtils.random(5, true, true) + "/"
					+ RandomStringUtils.random(5, true, true));
			FileUtils.copyInputStreamToFile(request.body, image);
			uploadFile(image, qqfile, bucketName, source);
		} catch (IOException e) {
			Logger.error(
					"stream upload failed[qqfile:%s,bucketName:%s,source:%s]",
					qqfile, bucketName, source, e.getStackTrace());
		}
	}

	private static void uploadFile(File qqfile, String originName,
			String bucketName, String source) {
		System.err.println("originName= " + originName + ", bucketName= "
				+ bucketName + ", source= " + source);
		Map<String, String> picInfoStr = GenThumbnails.getPicInfo(qqfile);
		String newImageName = genFileName(originName);
		int width = Integer.valueOf(picInfoStr.get("width"));
		int height = Integer.valueOf(picInfoStr.get("height"));
		try {
			LocalFileStore.getInstance().upload(bucketName,
					CommonUtils.getKey(newImageName, "origin"), qqfile);
			FileUtils.deleteQuietly(qqfile);
		} catch (IOException e) {
			Logger.error(e, e.toString());
		}

		ImageFull image = ImageFull.addOriginPic(originName, newImageName,
				bucketName, source, width, height);

		response.setHeader("content-type", "text/html; charset=utf-8");
		renderJSON(new Result(true, genUrl(bucketName, image)));
	}

	private static String genUrl(String bucketName, ImageFull image) {
		return Play.configuration.getProperty("application.baseUrl")
				+ bucketName + "/" + image.imageType.sizeType + "/"
				+ image.imageInfo.imageName;
	}

	private static String genFileName(String originName) {
		return System.currentTimeMillis()
				+ RandomStringUtils.randomAlphabetic(5).toLowerCase() + "."
				+ FilenameUtils.getExtension(originName);
	}

	private static Map<String, String> createThumbnailImage(String imageName,
			String imageSize, String bucketName) {
		Logger.error("imageName=" + imageName);
		Logger.error("imageSize=" + imageSize);
		Logger.error("bucketName=" + bucketName);
		try {
			String s = genFileName(imageName);
			File srcImage = new File(new File("tmp/"), s);
			Logger.error("s=" + s);
			URL url = new URL(LocalFileStore.getInstance().getUrl(bucketName,
					CommonUtils.getKey(imageName, "origin")));
			Logger.warn("url.content=" + url.getContent());
			FileUtils.copyURLToFile(url, srcImage);
			// FileUtils
			// .copyFile(
			// new File(LocalFileStore.getInstance().getPath(bucketName,
			// CommonUtils.getKey(imageName, "origin"))), srcImage);

			srcImage = GenThumbnails.thumbnail(srcImage, imageSize);

			LocalFileStore.getInstance().upload(bucketName,
					CommonUtils.getKey(imageName, imageSize), srcImage);

			Map<String, String> picInfo = GenThumbnails.getPicInfo(srcImage);

			FileUtils.deleteQuietly(srcImage);

			return picInfo;
		} catch (MalformedURLException e) {
			Logger.error("url formate error when addThumbnail");
			return null;
		} catch (IOException e) {
			Logger.error("io error when addThumbnail");
			return null;
		}
	}

	public static void getStatus() {
		renderHtml(JavaExtensions.nl2br(ImageQueue.getInstance().getStatus()));
	}

}