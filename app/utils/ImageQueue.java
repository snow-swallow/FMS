package utils;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import jobs.DBStarter;

public class ImageQueue {
	private Queue<ImageVO> queue;
	private Map<String, ImageVO> index;
	private static ImageQueue imageQueue = new ImageQueue();

	private ImageQueue() {
		this.queue = new ConcurrentLinkedQueue<ImageVO>();
		this.index = new ConcurrentHashMap<String, ImageVO>();
	}

	public static ImageQueue getInstance() {
		return imageQueue;
	}

	public void add(String name, String size, String bucket, int width,
			int height) {
		ImageVO value = new ImageVO(name, size, bucket, width, height);
		this.queue.add(value);
		this.index.put(this.getKey(name, size, bucket), value);
		new DBStarter().now();
	}

	public ImageVO get(String name, String size, String bucket) {
		return this.index.get(this.getKey(name, size, bucket));
	}

	private String getKey(String name, String size, String bucket) {
		return bucket + '/' + size + '/' + name;
	}

	public ImageVO next() {
		ImageVO value = this.queue.poll();
		if (value != null) {
			this.index
					.remove(this.getKey(value.name, value.size, value.bucket));
		}
		return value;
	}

	public boolean hasNext() {
		return !this.queue.isEmpty();
	}

	public String getStatus() {
		StringBuffer sb = new StringBuffer();
		sb.append("Queue size:\t").append(this.queue.size()).append("\n");
		sb.append("Index size:\t").append(this.index.size()).append("\n");
		return sb.toString();
	}

	public class ImageVO {
		public String name;
		public String size;
		public String bucket;
		public int width;
		public int height;

		public ImageVO(String name, String size, String bucket, int width,
				int height) {
			this.name = name;
			this.bucket = bucket;
			this.size = size;
			this.width = width;
			this.height = height;
		}
	}
}
