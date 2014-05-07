package jobs;

import models.ImageFull;
import play.jobs.Every;
import play.jobs.Job;
import utils.ImageQueue;
import utils.ImageQueue.ImageVO;

@Every("1mn")
public class DBStarter extends Job {
	@Override
	public void doJob() throws Exception {
		ImageVO vo = ImageQueue.getInstance().next();
		if (vo != null) {
			save2DB(vo);
		}
		super.doJob();
	}

	private void save2DB(ImageVO value) {
		ImageFull destImage = ImageFull.findImage(value.name, value.size,
				value.bucket);
		if (destImage == null) {
			ImageFull.addThumbnail(value.name, value.size, value.bucket,
					value.width, value.height);
		} else {
			destImage.addVisitedTimes();
		}
	}
}
