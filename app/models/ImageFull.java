package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ImageFull extends BaseModel {
	public long visitedTimes = 1;
	public int width;
	public int height;

	@ManyToOne
	public ImageInfo imageInfo;
	@ManyToOne
	public ImageType imageType;

	public static ImageFull addOriginPic(String originName, String imageName,
			String bucketName, String source, int width, int height) {
		ImageFull imageFull = new ImageFull();
		imageFull.imageInfo = ImageInfo.addPicInfo(originName, imageName,
				bucketName, source);
		imageFull.width = width;
		imageFull.height = height;
		imageFull.imageType = ImageType.addOriginImageType();
		return imageFull.save();
	}

	public static ImageFull addThumbnail(String imageName, String imageSize,
			String bucketName, int width, int height) {
		ImageFull imageFull = findImage(imageName, imageSize, bucketName);

		if (imageFull != null) {
			return imageFull;
		}

		ImageFull newimageFull = new ImageFull();
		newimageFull.imageInfo = ImageInfo.findImageInfo(imageName, bucketName);
		newimageFull.imageType = ImageType.addThumbnailImageType(imageSize);
		newimageFull.width = width;
		newimageFull.height = height;
		return newimageFull.save();
	}

	public static ImageFull findImage(String imageName, String imageSize,
			String bucketName) {
		return find(
				"imageInfo.imageName=?1 and imageInfo.bucketName=?2 and imageType.sizeType=?3",
				imageName, bucketName, imageSize).first();
	}

	public void addVisitedTimes() {
		visitedTimes += 1;
		this.save();
	}

}
