
package com.flyhz.avengers.util.image;


/**
 * 图片下载线程
 * 
 * @author zhangb 2014年5月9日 下午4:33:06
 * 
 */
public class ImageMitiThread extends Thread {

	/**
	 * 开启的线程总数
	 */
	private static final int	threadNumber	= 10;
	private boolean				isRunning		= true;

	@Override
	public void run() {
		startDownloadThread();
	}

	/**
	 * 开始图片下载线程
	 */
	public void startDownloadThread() {
		Image image = null;
		while (isRunning) {
			image = ImagePool.getNextImage(null);
			while (image != null) {
				if (ImagePool.getThreadNum(0) <= threadNumber) {
					new ImageThread("Image download thread", image).start();
					ImagePool.getThreadNum(1);
					image = ImagePool.getNextImage(null);
				} else {
					sleepSeconds();
				}
			}
			sleepSeconds();
		}
	}

	private void sleepSeconds() {
		try {
			System.out.println("waiting...");
			sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 停止图片下载线程（不会马上停止，会等待所有的任务下载完成后再停止）
	 */
	public void stopDownloadThread() {
		isRunning = false;
	}

	public static void main(String[] rags) {
		new ImageMitiThread().start();

		ImagePool.getNextImage(new Image(
				"http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		ImagePool.getNextImage(new Image(
				"http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		ImagePool.getNextImage(new Image(
				"http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		ImagePool.getNextImage(new Image("http://s7d2.scene7.com/is/image/Coa"));
		ImagePool.getNextImage(new Image(
				"http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
	}
}
