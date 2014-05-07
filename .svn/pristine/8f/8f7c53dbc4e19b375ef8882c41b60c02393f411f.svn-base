package utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.libs.Time;

public class CommonUtils {
	private static final int STREAM_BUFFER_LENGTH = 4096;

	public static String getKey(String fileName, String size) {
		return StringUtils.isBlank(size) ? fileName : (size + "_" + fileName);
	}

	public static Date getExpires() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, Time.parseDuration("1h"));
		return calendar.getTime();
	}

	public static String getHmacSha1(String data, String key) {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),
					"HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);
			byte[] text = mac.doFinal(data.getBytes());
			String result = org.apache.commons.codec.binary.Base64
					.encodeBase64String(text);
			return result.trim();
		} catch (Exception e) {
			return "";
		}
	}

	public static String convertMD5(String str) {
		String md5Str = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();
			int i;

			StringBuffer buf = new StringBuffer("");
			for (byte element : b) {
				i = element;
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append('0');
				}
				buf.append(Integer.toHexString(i));
			}
			md5Str = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		return md5Str;
	}

	public static String toGMTString(Date date) {
		SimpleDateFormat df = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df.format(date);
	}

	public static String getMD5Hex(InputStream inputStream) {
		try {
			return toHexString(md5(inputStream));
		} catch (IOException e) {
			Logger.error(e, e.getMessage());
			return null;
		}
	}

	private static byte[] md5(InputStream streamData) throws IOException {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			Logger.error(e, e.getMessage());
			return new byte[0];
		}
		byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
		int read;
		do {
			read = streamData.read(buffer, 0, STREAM_BUFFER_LENGTH);
			if (read > -1) {
				digest.update(buffer, 0, read);
			}
		} while (read > -1);
		return digest.digest();
	}

	private static String toHexString(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			if (Integer.toHexString(0xFF & b).length() == 1) {
				hexString.append("0").append(Integer.toHexString(0xFF & b));
			} else {
				hexString.append(Integer.toHexString(0xFF & b));
			}
		}
		return hexString.toString().toUpperCase();
	}
}