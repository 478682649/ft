package com.guazi.ft.config.profile;

/**
 * 测试环境
 *
 * @author shichunyang
 */
public class StagProFile implements ProFile {
	@Override
	public String proFile() {
		return "stag环境";
	}
}
