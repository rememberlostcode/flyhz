
package com.flyhz.avengers.parser;

import com.flyhz.avengers.util.image.Image;
import com.flyhz.avengers.util.image.ImageMitiThread;
import com.flyhz.avengers.util.image.ImagePool;

public class TestImageDownload {

	public static void main(String[] rags) {
		ImageMitiThread it = new ImageMitiThread();
		it.start();

		ImagePool.getNextImage(new Image(
				"http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		ImagePool.getNextImage(new Image(
				"http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		ImagePool.getNextImage(new Image(
				"http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		ImagePool.getNextImage(new Image("http://s7d2.scene7.com/is/image/Coa"));
		ImagePool.getNextImage(new Image(
				"http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));

		// it.stopDownloadThread();
	}
}
