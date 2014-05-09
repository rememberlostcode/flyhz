
package com.flyhz.avengers.util.image;

/**
 * 测试访问静态文件速度
 * 
 * @author zhangb
 * 
 */
public class ImageMitiThread extends Thread {

	/**
	 * 开启的线程总数
	 */
	private static final int	threadNumber	= 10;

	public static void main(String[] rags) {
		new ImageMitiThread().start();

		ImagePool.addImage(new Image(
				"http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		ImagePool.addImage(new Image(
				"http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		ImagePool.addImage(new Image(
				"http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		ImagePool.addImage(new Image("http://s7d2.scene7.com/is/image/Coa"));
		ImagePool.addImage(new Image(
				"http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
	}

	@Override
	public void run() {
		test();
	}

	public static void test() {
		// 获得线程组
		ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
		while (threadGroup.activeCount() > 0) {
			try {

				Image image = ImagePool.getNextImage();
				while (image != null) {
					if (threadGroup.activeCount() <= threadNumber + 1) {
						new ImageThread("图片下载线程", image).start();
						image = ImagePool.getNextImage();
					}
				}
				System.out.println("等待图片任务...");
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
