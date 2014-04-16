package com.holding.smile.tools;


public class FileManager {

	public static String getSaveFilePath() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "com.smile/files/";
		} else {
			return CommonUtil.getRootFilePath() + "com.smile/files/";
		}
	}
}
