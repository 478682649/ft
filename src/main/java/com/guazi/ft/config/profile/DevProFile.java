package com.guazi.ft.config.profile;

/**
 * 本地测试环境
 *
 * @author shichunyang
 */
public class DevProFile implements ProFile {
	@Override
	public String proFile() {
		return "dev环境";
	}
}
