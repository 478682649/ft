package com.guazi.ft.common;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * BigDecimalUtil
 *
 * @author shichunyang
 */
public class BigDecimalUtil {

	/**
	 * 加法
	 *
	 * @param addend  加数
	 * @param numbers 加数集合
	 * @return 和
	 */
	public static BigDecimal add(String addend, String... numbers) {

		BigDecimal resultBigDecimal = new BigDecimal(addend);

		for (String number : numbers) {
			resultBigDecimal = resultBigDecimal.add(new BigDecimal(number));
		}

		return resultBigDecimal;
	}

	/**
	 * 减法
	 *
	 * @param minuend    被减数
	 * @param subtrahend 减数
	 * @return 差
	 */
	public static BigDecimal subtract(String minuend, String subtrahend) {
		return new BigDecimal(minuend).subtract(new BigDecimal(subtrahend));
	}

	/**
	 * 乘法
	 *
	 * @param multiplicand 被乘数
	 * @param multiplier   乘数
	 * @return 积
	 */
	public static BigDecimal multiply(String multiplicand, String multiplier) {
		return new BigDecimal(multiplicand).multiply(new BigDecimal(multiplier));
	}

	/**
	 * 除法
	 *
	 * @param dividend 被除数
	 * @param divisor  除数
	 * @param decimals 小数
	 * @return 商
	 */
	public static BigDecimal divide(String dividend, String divisor, int decimals) {
		// 四舍五入
		return new BigDecimal(dividend).divide(new BigDecimal(divisor), decimals, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 除法
	 *
	 * @param dividend 被除数
	 * @param divisor  除数
	 * @return 商和余数
	 */
	public static BigInteger[] divideAndRemainder(String dividend, String divisor) {
		return new BigInteger(dividend).divideAndRemainder(new BigInteger(divisor));
	}

	/**
	 * 计算百分比
	 *
	 * @param dividend   被除数
	 * @param divisor    除数
	 * @param decimals   小数点位数
	 * @param percentage 百分比的小数点位数
	 * @return 百分比字符串
	 */
	public static String percentage(String dividend, String divisor, int decimals, int percentage) {
		return divide(multiply(divide(dividend, divisor, decimals).toString(), "100").toString(), "1", percentage).toString() + "%";
	}

	public static void main(String[] args) {
	}
}
