
package com.flyhz.avengers.template.impl.coach;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.ResultFilter;
import com.flyhz.avengers.util.image.Image;
import com.flyhz.avengers.util.image.ImageMitiThread;
import com.flyhz.avengers.util.image.ImagePool;

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
