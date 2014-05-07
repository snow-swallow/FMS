package models;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.io.FilenameUtils;

import play.Logger;

@Entity
public class ImageInfo extends BaseModel {
	@Column(length = 2048)
	public String imageName;
	public String originName;
	public String imageExtension;
	public String bucketName;

	public String source;

	public static ImageInfo addPicInfo(String originName, String imageName,
			String bucketName, String source) {
		ImageInfo imageInfo = new ImageInfo();

		imageInfo.imageName = imageName;
		imageInfo.originName = originName;
		imageInfo.imageExtension = FilenameUtils.getExtension(originName);
		imageInfo.bucketName = bucketName;

		imageInfo.source = source;
		return imageInfo.save();
	}

	public static ImageInfo findImageInfo(String imageName, String bucketName) {
		ImageInfo imageInfo = ImageInfo.find("imageName=?1 and bucketName=?2",
				imageName, bucketName).first();
		if (imageInfo == null) {
			Logger.error(
					"The origin image %s is not exist when try to create thumbnails.",
					imageName);
		}
		return imageInfo;
	}

}
