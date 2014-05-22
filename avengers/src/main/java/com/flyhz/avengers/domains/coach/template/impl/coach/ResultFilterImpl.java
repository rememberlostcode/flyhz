
package com.flyhz.avengers.domains.coach.template.impl.coach;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.domains.ResultFilter;
import com.flyhz.avengers.framework.util.image.Image;
import com.flyhz.avengers.framework.util.image.ImageMitiThread;
import com.flyhz.avengers.framework.util.image.ImagePool;

public class ResultFilterImpl implements ResultFilter {
	Logger							log	= LoggerFactory.getLogger(ResultFilterImpl.class);
	private static ImageMitiThread	it;

	public void startDownloadThread() {
		if (it == null) {
			it = new ImageMitiThread();
			it.start();
		}
	}

	public void addImage(Image image) {
		ImagePool.getNextImage(image);
	}

	public LinkedList<Image> getFinshedImage() {
		return ImagePool.getFinshedImage(null);
	}
}
