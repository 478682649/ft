package com.guazi.ft.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页工具类
 *
 * @author shichunyang
 */
@Data
public class Page implements Serializable {
	private static final long serialVersionUID = 1315898743498415987L;

	/**
	 * 默认每页显示5条记录
	 */
	public static final int DEFAULT_LENGTH = 5;
	/**
	 * 默认查询第一页
	 */
	public static final int DEFAULT_PAGE_NUM = 1;

	/**
	 * 每页显示记录数
	 */
	private int length;

	/**
	 * 查询结果总记录数
	 */
	private int maxRow;

	/**
	 * 当前页码(默认为1)
	 */
	private int pageNum;

	/**
	 * 最大页码
	 */
	private int maxPage;


	/**
	 * 初始化分页查询条件。
	 */
	public Page() {
		this.length = DEFAULT_LENGTH;
		this.pageNum = DEFAULT_PAGE_NUM;
	}

	/**
	 * 初始化分页查询条件,每页显示length条。
	 *
	 * @param length 每页显示记录数
	 */
	public Page(int length) {
		this.length = length;
		this.pageNum = DEFAULT_PAGE_NUM;
	}

	/**
	 * 设置查询结果总记录数
	 *
	 * @param maxRow 查询结果总记录数
	 */
	public void setMaxRow(int maxRow) {
		this.maxRow = maxRow;
		// 计算最大页码(向上取整)
		this.setMaxPage((int) Math.ceil((float) maxRow / length));
	}

	/**
	 * 读取起始行数
	 *
	 * @return 起始行数
	 */
	public int getRowNum() {
		// (当前页码-1) * 每页读取长度
		return (this.getPageNum() - 1) * length;
	}

	public static void main(String[] args) {
	}
}
