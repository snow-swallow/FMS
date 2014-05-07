package models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class ImageType extends BaseModel {
	private static final Pattern TYPE_PATTERN = Pattern.compile(
			"([\\d]+)_([\\d]+)([cf]?)", Pattern.CASE_INSENSITIVE);
	public int imageHeight;
	public int imageWidth;
	@Enumerated(EnumType.STRING)
	public ImageEffect imageEffect;

	public String sizeType;

	public enum ImageEffect {
		CUT, FILL, PURE;
		@Override
		public String toString() {
			switch (this) {
			case CUT:
				return "c";
			case FILL:
				return "f";
			default:
				return "";
			}
		}

		public String getCMD() {
			switch (this) {
			case CUT:
				return "thumbnailsForCut.sh";
			case FILL:
				return "thumbnailsForFill.sh";
			default:
				return "thumbnailsForNormal.sh";
			}
		}

		public static ImageEffect getByShortName(String shortName) {
			if ("c".equals(shortName)) {
				return ImageEffect.CUT;
			} else if ("f".equals(shortName)) {
				return ImageEffect.FILL;
			} else {
				return ImageEffect.PURE;
			}
		}
	}

	@Override
	public String toString() {
		return this.imageWidth + '_' + this.imageHeight
				+ this.imageEffect.toString();
	}

	public ImageType() {
	}

	private ImageType(int imageWidth, int imageHeight, ImageEffect imageEffect) {
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.imageEffect = imageEffect;
		this.sizeType = (imageWidth == 0 && imageHeight == 0) ? "origin"
				: (imageWidth + "_" + imageHeight + imageEffect.toString());
	}

	public static ImageType addThumbnailImageType(String imageSize) {
		ImageType imageType = getInfoFromSizeStr(imageSize);

		int width = imageType.imageWidth;
		int height = imageType.imageHeight;
		ImageEffect effect = imageType.imageEffect;

		imageType = getImageType(width, height, effect);
		if (imageType == null) {
			imageType = new ImageType(width, height, effect);
			imageType.save();
		}

		return imageType;
	}

	public static ImageType getInfoFromSizeStr(String imageSize) {
		ImageType result = new ImageType();
		Matcher matcher = TYPE_PATTERN.matcher(imageSize);
		if (matcher.find()) {
			result.imageWidth = Integer.valueOf(matcher.group(1));
			result.imageHeight = Integer.valueOf(matcher.group(2));
			result.imageEffect = ImageEffect.getByShortName(matcher.group(3));
		}
		return result;
	}

	public static ImageType addOriginImageType() {
		ImageType imageType = getImageType(0, 0, ImageEffect.PURE);
		if (imageType == null) {
			imageType = new ImageType(0, 0, ImageEffect.PURE);
			imageType.save();
		}

		return imageType;
	}

	private static ImageType getImageType(int imageWidth, int imageHeight,
			ImageEffect imageEffect) {
		return ImageType
				.find("imageWidth=?1 and imageHeight=?2 and imageEffect=?3 and isDeleted=false",
						imageWidth, imageHeight, imageEffect).first();
	}

	public String getSize() {
		if (this.imageWidth == 0) {
			return "x" + this.imageHeight;
		} else if (this.imageHeight == 0) {
			return this.imageWidth + "x";
		} else {
			return this.imageWidth + "x" + this.imageHeight;
		}
	}

}
